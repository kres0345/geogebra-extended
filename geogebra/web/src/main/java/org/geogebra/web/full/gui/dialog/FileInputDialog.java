package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FileInputDialog extends GPopupPanel implements ClickHandler {

	private FileUpload inputWidget;
	protected Button btCancel;

	/**
	 * @param app
	 *            application
	 */
	public FileInputDialog(AppW app) {
		super(false, true, app.getPanel(), app);
		// createGUI();
		addStyleName("GeoGebraPopup");
		setGlassEnabled(true);
	}

	/**
	 * Build the UI.
	 */
	protected void createGUI() {
		setInputWidget(new FileUpload());
		// addGgbChangeHandler(inputWidget.getElement(), app);

		btCancel = new Button(app.getLocalization().getMenu("Cancel"));
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btCancel.addClickHandler(this);
		VerticalPanel centerPanel = new VerticalPanel();
		centerPanel.add(getInputWidget());
		centerPanel.add(btCancel);

		setWidget(centerPanel);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == btCancel) {
			hideAndFocus();
		}
	}

	/**
	 * @return JS callback that hides this
	 */
	public native JavaScriptObject getNativeHideAndFocus() /*-{
		return function() {
			this.@org.geogebra.web.full.gui.dialog.FileInputDialog::hideAndFocus()();
		}
	}-*/;

	/**
	 * Hide this and focus app
	 */
	public void hideAndFocus() {
		hide();
		app.getActiveEuclidianView().requestFocusInWindow();
	}

	/**
	 * @return input widget
	 */
	public FileUpload getInputWidget() {
		return inputWidget;
	}

	private void setInputWidget(FileUpload inputWidget) {
		this.inputWidget = inputWidget;
	}
}
