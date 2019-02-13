package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.validator.TableValuesDialogValidator;
import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author csilla
 * 
 *         dialog opened from av context menu of functions and lines by clicking
 *         on Table of values
 *
 */
public class InputDialogTableView extends OptionDialog
		implements SetLabels, HasKeyboardPopup {
	private ComponentInputField startValue;
	private ComponentInputField endValue;
	private ComponentInputField step;
	private GeoElement geo;
	private Label errorLabel;
	private TableValuesDialogValidator validator;

	/**
	 * Create new dialog. NOT modal to make sure onscreen keyboard still works.
	 * 
	 * @param app
	 *            see {@link AppW}
	 */
	public InputDialogTableView(final AppW app) {
		super(app.getPanel(), app, false);
		buildGui();
		setPrimaryButtonEnabled(true);
		validator = new TableValuesDialogValidator(app);
		this.addCloseHandler(new CloseHandler<GPopupPanel>() {
			@Override
			public void onClose(CloseEvent<GPopupPanel> event) {
				app.unregisterPopup(InputDialogTableView.this);
				app.hideKeyboard();
			}
		});
		setGlassEnabled(true);
	}

	/**
	 * @return input field for start value
	 */
	public ComponentInputField getStartField() {
		return startValue;
	}

	private void buildGui() {
		addStyleName("tableOfValuesDialog");
		FlowPanel contentPanel = new FlowPanel();
		errorLabel = new Label();
		errorLabel.setStyleName("globalErrorLabel");
		buildTextFieldPanel(contentPanel);
		contentPanel.add(errorLabel);
		buildButtonPanel(contentPanel);
		add(contentPanel);
		setLabels();
	}

	private void buildTextFieldPanel(FlowPanel root) {
		startValue = addTextField("StartValueX", root);
		endValue = addTextField("EndValueX", root);
		step = addTextField("Step", root);
		// last input text field shouldn't have any bottom margin
		step.addStyleName("noBottomMarg");
	}

	private ComponentInputField addTextField(String labelText, FlowPanel root) {
		final ComponentInputField field = new ComponentInputField((AppW) app,
				null, labelText, null, "", 20);
		root.add(field);
		return field;
	}

	private void buildButtonPanel(FlowPanel root) {
		root.add(getButtonPanel());
	}

	@Override
	public void setLabels() {
		getCaption().setText(app.getLocalization().getMenu("TableOfValues"));
		startValue.setLabels();
		endValue.setLabels();
		step.setLabels();
		updateButtonLabels("OK");
	}

	@Override
	public void show() {
		endValue.resetInputField();
		step.resetInputField();
		super.show();
		centerAndResize(
				((AppW) app).getAppletFrame().getKeyboardHeight());
		focusDeferred(startValue);
	}

	private static void focusDeferred(final ComponentInputField inputField) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				inputField.getTextField().getTextComponent()
						.setFocus(true);
			}
		});
	}

	/**
	 * @param functionGeo
	 *            function
	 */
	public void show(GeoElement functionGeo) {
		this.geo = functionGeo;
		TableValuesView tv = (TableValuesView) app.getGuiManager().getTableValuesView();
		startValue.setInputText(tv.getValuesMinStr());
		endValue.setInputText(tv.getValuesMaxStr());
		step.setInputText(tv.getValuesStepStr());
		errorLabel.setText("");
		((AppW) app).registerPopup(this);
		show();
	}

	private void openTableView() {
		double[] inputFieldValues = validator.getDoubles(startValue, endValue, step);
		if (startValue.hasError()) {
			focusDeferred(startValue);
		} else if (endValue.hasError()) {
			focusDeferred(endValue);
		} else if (step.hasError()) {
			focusDeferred(step);
		}
		if (inputFieldValues != null) {
			try {
				initTableValuesView(inputFieldValues[0], inputFieldValues[1], inputFieldValues[2]);
				hide();
			} catch (InvalidValuesException ex) {
				errorLabel
						.setText(ex.getLocalizedMessage(app.getLocalization()));
				focusPrimaryButton();
			}
		} else {
			errorLabel.setText("");
		}
	}

	@Override
	protected void processInput() {
		openTableView();
	}

	/**
	 * Initializes Table View
	 * 
	 * @param min
	 *            min x-value.
	 * @param max
	 *            max x-value.
	 * @param stepVal
	 *            x step value.
	 * @throws InvalidValuesException
	 *             if (max-min)/step is too big
	 */
	private void initTableValuesView(double min, double max, double stepVal)
			throws InvalidValuesException {
		GuiManagerW gui = (GuiManagerW) app.getGuiManager();
		gui.getTableValuesView().setValues(min, max, stepVal);
		if (geo != null) {
			gui.addGeoToTableValuesView(geo);
			app.getKernel().attach(gui.getTableValuesView());
		} else {
			gui.getUnbundledToolbar().resize();
		}
	}
}