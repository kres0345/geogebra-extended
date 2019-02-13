package org.geogebra.web.full.gui.properties;

import org.geogebra.common.gui.dialog.options.model.AnimationStepModel;
import org.geogebra.common.gui.dialog.options.model.ITextFieldListener;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.Feature;
import org.geogebra.web.full.gui.AngleTextFieldW;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class AnimationStepPanelW extends OptionPanel 
implements ITextFieldListener {
	private AnimationStepModel model;
	private Label label;	
	private AngleTextFieldW tfAnimStep;
	private Kernel kernel;

	/**
	 * @param app
	 *            application
	 */
	public AnimationStepPanelW(final AppW app) {
		kernel = app.getKernel();
		model = new AnimationStepModel(this, app);
		setModel(model);
		// text field for animation step
		label = new Label();
		tfAnimStep = new AngleTextFieldW(6, app);
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(label);
		mainPanel.add(tfAnimStep);
		setWidget(mainPanel);

		tfAnimStep.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				doActionPerformed();
			}
		});

		new FocusListenerW(this).init();

		tfAnimStep.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == '\n') {
					doActionPerformed();
				}
			}
		});

		tfAnimStep.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				doActionPerformed();
			}
		});

		tfAnimStep.enableGGBKeyboard();
	}

	private void doActionPerformed() {
		model.applyChanges(tfAnimStep.getText());
		//update(model.getGeos());
	}

	@Override
	public void setText(String text) {
		tfAnimStep.setText(text);
	}

	@Override
	public void setLabels() {
		if (kernel.getApplication().has(Feature.DIALOG_DESIGN)) {
			label.setStyleName("coloredLabel");
		}
		label.setText(kernel.getApplication().has(Feature.DIALOG_DESIGN)
				? kernel.getLocalization().getMenu("AnimationStep")
				: kernel.getLocalization().getMenu("AnimationStep") + ": ");
	}

	public void setPartOfSliderPanel() {
		model.setPartOfSlider(true);
	}

}
