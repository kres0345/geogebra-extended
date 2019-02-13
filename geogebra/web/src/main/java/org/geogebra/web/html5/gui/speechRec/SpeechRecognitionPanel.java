package org.geogebra.web.html5.gui.speechRec;

import org.geogebra.web.full.gui.layout.GUITabs;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.TabHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel containing the speech recognition button
 *
 * @author csilla
 *
 */
public class SpeechRecognitionPanel extends FlowPanel implements TabHandler {

	private SpeechRecognitionController specRecContr;
	private StandardButton speechBtn;
	private int viewID;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public SpeechRecognitionPanel(AppW app, int viewID) {
		specRecContr = new SpeechRecognitionController(app);
		this.viewID = viewID;
		buildGui(app);
	}

	private void buildGui(AppW app) {
		this.setStyleName("speechBtnPanel");
		speechBtn = new StandardButton(
				GuiResourcesSimple.INSTANCE.record(), null, 24, app);
		speechBtn.setStyleName("speechBtn");
		speechBtn.setTabIndex(GUITabs.SPEECH_REC + app.getActiveEuclidianView().getViewID());
		speechBtn.addTabHandler(this);
		speechBtn.setTitle(
				"Speech recognition button.");
		speechBtn.setAltText(
				"Speech recognition button. Press enter to give command."
						+ " Press tab to select next object");
		speechBtn.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				getSpecRecController().initSpeechSynth(
						"Please give the command.",
						"command");
			}
		});
		this.add(speechBtn);
	}

	/**
	 * @return the speech recognition controller
	 */
	public SpeechRecognitionController getSpecRecController() {
		return specRecContr;
	}

	/** Sets focus to speech rec btn */
	public void focusSpeechRec() {
		if (speechBtn != null) {
			speechBtn.getElement().focus();
		}
	}

	@Override
	public boolean onTab(Widget source, boolean shiftDown) {
		if (shiftDown) {
			specRecContr.getAppW().getAccessibilityManager()
					.focusPrevious(this);
			return true;
		}
		specRecContr.getAppW().getAccessibilityManager().focusNext(this);
		return true;
	}

	public int getViewID() {
		return viewID;
	}
}
