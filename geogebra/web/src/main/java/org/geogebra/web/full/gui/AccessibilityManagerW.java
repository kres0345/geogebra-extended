package org.geogebra.web.full.gui;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.gui.AccessibilityManagerNoGui;
import org.geogebra.common.gui.SliderInput;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.GUITabs;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelWAbstract;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.view.algebra.LatexTreeItemController;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.ZoomPanel;
import org.geogebra.web.html5.gui.voiceInput.SpeechRecognitionPanel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Web implementation of AccessibilityManager.
 *
 * @author laszlo
 *
 */
public class AccessibilityManagerW implements AccessibilityManagerInterface {
	private GuiManagerW gm;
	private AppW app;
	private boolean tabOverGeos = false;
	private SelectionManager selection;
	private Widget anchor;
	private SliderInput activeButton;

	/**
	 * Constructor.
	 *
	 * @param app
	 *            The application.
	 */
	public AccessibilityManagerW(AppW app) {
		this.app = app;
		gm = (GuiManagerW) app.getGuiManager();
		selection = app.getSelectionManager();
	}

	private void focusFirst() {
		focusFirstElement();
	}

	@Override
	public void focusNext(Object source) {
		if (source == null) {
			focusFirst();
		} else if (source instanceof LatexTreeItemController) {
			nextFromInput();
		} else if (source instanceof ZoomPanel) {
			nextFromZoomPanel(((ZoomPanel) source).getViewID());
		} else if (isSpeechButton(source)) {
			nextFromSpeechRecognitionPanel(
					((SpeechRecognitionPanel) source).getViewID());
		} else if (source instanceof FocusWidget) {
			nextFromWidget((FocusWidget) source);
		} else if (source instanceof GeoElement) {
			nextFromLastGeo();
		}
	}

	private boolean isSpeechButton(Object source) {
		return app.has(Feature.SPEECH_RECOGNITION)
				&& source instanceof SpeechRecognitionPanel;
	}

	@Override
	public void focusPrevious(Object source) {
		if (source instanceof LatexTreeItemController) {
			previousFromInput();
		} else if (source instanceof ZoomPanel) {
			previousFromZoomPanel(((ZoomPanel) source).getViewID());
		} else if (isSpeechButton(source)) {
			previousFromSpeechRecognition(
					((SpeechRecognitionPanel) source).getViewID());
		} else if (source instanceof FocusWidget) {
			previousFromWidget((FocusWidget) source);
		} else if (source instanceof GeoElement) {
			previousFromFirstGeo();
		}
	}

	private void previousFromInput() {
		if (focusLastGeo()) {
			return;
		}
		focusMenu();
	}

	private void previousFromSpeechRecognition(int viewID) {
		EuclidianDockPanelWAbstract dp = getEuclidianPanel(viewID);
		if (dp != null) {
			focusZoomPanel(false, viewID);
		} else {
			focusLastGeo();
		}
	}

	private void previousFromFirstGeo() {
		focusLastZoomOrSpeech(prevID(-1));
	}

	private void focusLastZoomOrSpeech(int prevID) {
		if (app.has(Feature.SPEECH_RECOGNITION)) {
			if (!focusSpeechRec(prevID)) {
				focusLastGeo();
			}
		} else {
			if (!focusZoomPanel(false, prevID)) {
				focusLastGeo();
			}
		}
	}

	private void nextFromInput() {
		focusMenu();
	}

	private void nextFromZoomPanel(int viewID) {
		if (app.has(Feature.SPEECH_RECOGNITION)) {
			focusSpeechRec(viewID);
		} else {
			if (!focusZoomPanel(true, nextID(viewID))) {
				focusFirstElement();
			}
		}
	}

	private int nextID(int viewID) {
		DockManagerW dm = (DockManagerW) app.getGuiManager().getLayout()
				.getDockManager();
		return nextID(dm.getPanels(), viewID);
	}

	private int prevID(int viewID) {
		DockManagerW dm = (DockManagerW) app.getGuiManager().getLayout()
				.getDockManager();
		DockPanelW[] reversePanels = new DockPanelW[dm.getPanels().length];
		for (int i = 0; i < reversePanels.length; i++) {
			reversePanels[i] = dm.getPanels()[dm.getPanels().length - 1 - i];
		}
		return nextID(reversePanels, viewID);
	}

	private static int nextID(DockPanelW[] panels, int viewID) {
		boolean returnNext = viewID == -1;
		for (DockPanelW panel : panels) {
			EuclidianDockPanelWAbstract ev = isEuclidianViewWithZoomPanel(
					panel);
			if (ev != null && returnNext) {
				return ev.getViewId();
			}
			if (ev != null && ev.getViewId() == viewID) {
				returnNext = true;
			}
		}
		return -1;
	}

	private void nextFromSpeechRecognitionPanel(int viewId) {
		if (focusZoomPanel(true, nextID(viewId))) {
			return;
		}

		focusFirstElement();
	}

	private void nextFromLastGeo() {
		focusFirstWidget();
	}

	private boolean focusSpeechRec(int viewID) {
		EuclidianDockPanelWAbstract dp = getEuclidianPanel(viewID);
		if (dp != null) {
			dp.focusSpeechRecBtn();
			return true;
		}
		return false;
	}

	private boolean focusZoomPanel(boolean first, int viewID) {
		EuclidianDockPanelWAbstract ev = isEuclidianViewWithZoomPanel(
				getEuclidianPanel(viewID));
		if (ev != null) {
			setTabOverGeos(false);
			if (first) {
				ev.focusNextGUIElement();
			} else {
				ev.focusLastZoomButton();
			}
			return true;
		}

		return false;
	}

	private static EuclidianDockPanelWAbstract isEuclidianViewWithZoomPanel(
			DockPanelW panel) {
		if (!(panel instanceof EuclidianDockPanelWAbstract)) {
			return null;
		}

		EuclidianDockPanelWAbstract ev = (EuclidianDockPanelWAbstract) panel;

		boolean zoomButtons = ev.isAttached() && ev.hasZoomButtons();
		return zoomButtons ? ev : null;
	}

	private void focusFirstElement() {
		if (gm.getUnbundledToolbar() != null) {
			focusMenu();
		} else {
			if (app.is3DViewEnabled()) {
				setTabOverGeos(true);
				if (!focusFirstGeo()) {
					nextFromLastGeo();
				}
			}
		}
	}

	@Override
	public boolean focusInput(boolean force) {
		if (gm.getUnbundledToolbar() != null) {
			return gm.getUnbundledToolbar().focusInput(force);
		}
		return false;
	}

	private void focusFirstWidget() {
		ToolbarPanel toolbar = gm.getUnbundledToolbar();
		if (toolbar != null) {
			if (toolbar.focusInput(false)) {
				return;
			}

			gm.getUnbundledToolbar().focusMenu();
			return;
		}

		focusZoomPanel(true, nextID(-1));
	}

	private void previousFromZoomPanel(int viewID) {
		if (focusSettings(viewID)) {
			return;
		}

		if (isPlayVisible(viewID)) {
			setPlaySelectedIfVisible(true, viewID);
			setTabOverGeos(true);
			return;
		}
		int prevView = prevID(viewID);
		if (prevView == -1) {
			focusLastGeo();
		} else if (app.has(Feature.SPEECH_RECOGNITION)) {
			focusSpeechRec(prevView);
		} else {
			focusZoomPanel(false, prevView);
		}
	}

	private void nextFromWidget(FocusWidget source) {
		if (app.isMenuShowing()) {
			return;
		}

		if (source.getTabIndex() == GUITabs.SETTINGS) {
			focusZoomPanel(true, nextID(-1));
		}
	}

	private void previousFromWidget(FocusWidget source) {
		if (app.isMenuShowing()) {
			return;
		}

		if (source.getTabIndex() == GUITabs.MENU) {
			if (!focusInput(false)) {
				focusZoomPanel(false, prevID(-1));
			}
		}
	}

	private EuclidianDockPanelWAbstract getEuclidianPanel(int viewId) {
		return (EuclidianDockPanelWAbstract) gm.getLayout().getDockManager()
				.getPanel(viewId);
	}

	private boolean focusSettings(int viewID) {
		if (getEuclidianPanel(viewID).focusLastGUIElement()) {
			setTabOverGeos(false);
			return true;
		}
		return false;
	}

	private boolean focusFirstGeo() {
		Construction cons = app.getKernel().getConstruction();
		if (cons.isEmpty()) {
			return false;
		}

		focusGeo(app.getSelectionManager().getTabbingSet().first());
		return true;
	}

	private boolean focusLastGeo() {
		Construction cons = app.getKernel().getConstruction();
		if (cons.isEmpty()) {
			return false;
		}

		GeoElement last = app.getSelectionManager().getEVFilteredTabbingSet().last();
		focusGeo(last);
		return true;
	}

	@Override
	public void focusMenu() {
		if (gm.getUnbundledToolbar() != null) {
			gm.getUnbundledToolbar().focusMenu();
		} else {
			focusFirstElement();
		}
	}

	@Override
	public boolean isTabOverGeos() {
		return tabOverGeos;
	}

	@Override
	public void setTabOverGeos(boolean tabOverGeos) {
		this.tabOverGeos = tabOverGeos;
	}

	@Override
	public boolean isCurrentTabExitGeos(boolean isShiftDown) {
		if (selection.getSelectedGeos().size() != 1 || !app.isUnbundled()) {
			return false;
		}
		GeoElement geo = selection.getSelectedGeos().get(0);
		boolean exitOnFirst = selection.isFirstGeoSelected() && isShiftDown;
		boolean exitOnLast = selection.isLastGeoSelected() && !isShiftDown;
		this.activeButton = null;
		if (exitOnFirst) {
			focusPrevious(geo);
		} else if (exitOnLast) {
			focusNext(geo);
		}

		if (exitOnFirst || exitOnLast) {
			selection.clearSelectedGeos();
			return true;
		}
		return false;
	}

	@Override
	public void focusGeo(GeoElement geo) {
		if (geo != null) {
			app.getSelectionManager().addSelectedGeo(geo);
			setTabOverGeos(true);
			app.getActiveEuclidianView().requestFocus();
		} else {
			ToolbarPanel tp = ((GuiManagerW) app.getGuiManager())
					.getUnbundledToolbar();
			if (tp != null) {
				tp.focusMenu();
			}
		}
	}

	@Override
	public void setAnchor(Object anchor) {
		this.anchor = anchor instanceof Widget ? (Widget) anchor : null;
	}

	@Override
	public Object getAnchor() {
		return anchor;
	}

	@Override
	public void focusAnchor() {
		if (anchor == null) {
			return;
		}
		anchor.getElement().focus();
		cancelAnchor();
	}

	@Override
	public void focusAnchorOrMenu() {
		if (anchor == null) {
			focusMenu();
		} else {
			focusAnchor();
		}
	}

	@Override
	public void cancelAnchor() {
		anchor = null;
	}

	@Override
	public boolean handleTabExitGeos(boolean forward) {
		if (!app.has(Feature.TAB_ON_EV_PLAY)) {
			return false;
		}
		int firstViewId = nextID(-1);
		if (!forward && selection.isFirstGeoSelected()) {
			focusLastZoomOrSpeech(firstViewId);
		}
		boolean voiceover = Browser.isiOS();
		if (app.getKernel().needToShowAnimationButton()) {
			this.activeButton = null;
			setPlaySelectedIfVisible(true, firstViewId);
			return true;
		}
		if (app.getActiveEuclidianView().getDimension() == 3 && voiceover
				&& forward) {
			this.activeButton = SliderInput.ROTATE_Z;
			return true;
		}
		return voiceover;
	}

	private void exitGeosFromPlayButton() {
		int firstViewId = nextID(-1);
		setPlaySelectedIfVisible(false, firstViewId);
		focusZoomPanel(true, firstViewId);
		tabOverGeos = false;
	}

	@Override
	public void setPlaySelectedIfVisible(boolean b, int viewID) {
		if (isPlayVisible(viewID)) {
			app.getActiveEuclidianView().setAnimationButtonSelected(b);
		}
	}

	private boolean isPlayVisible(int viewID) {
		EuclidianDockPanelWAbstract panel = getEuclidianPanel(viewID);
		return app.getKernel().needToShowAnimationButton()
				&& panel != null && panel.getEuclidianView()
						.drawPlayButtonInThisView();
	}

	@Override
	public boolean tabEuclidianControl(boolean forward) {
		if (app.getActiveEuclidianView().isAnimationButtonSelected()) {
			if (forward) {
				exitGeosFromPlayButton();
			} else {
				focusLastGeo();
				this.activeButton = null;
			}
			setPlaySelectedIfVisible(false,
					app.getActiveEuclidianView().getViewID());
			return true;
		}
		if (app.getActiveEuclidianView().getDimension() == 3 && forward
				&& activeButton == SliderInput.ROTATE_Z) {
			activeButton = SliderInput.TILT;
			return true;
		}
		if (app.getActiveEuclidianView().getDimension() == 3 && !forward
				&& activeButton == null && this.getSelectedGeo() == null) {
			activeButton = SliderInput.TILT;
			return true;
		}
		if (app.getActiveEuclidianView().getDimension() == 3 && !forward
				&& activeButton == SliderInput.TILT) {
			activeButton = SliderInput.ROTATE_Z;
			return true;
		}
		if (app.getActiveEuclidianView().getDimension() == 3 && !forward
				&& activeButton == SliderInput.ROTATE_Z) {
			activeButton = null;
			return false;
		}
		if (app.getActiveEuclidianView().getDimension() == 3 && forward
				&& activeButton == SliderInput.TILT) {
			activeButton = null;
			return true; // tilt is the last => leave
		}

		return false;
	}

	@Override
	public String getSpaceAction() {
		if (app.getActiveEuclidianView().isAnimationButtonSelected()) {
			return app.getLocalization().getMenu("Animation");
		}

		GeoElement sel = getSelectedGeo();
		if (sel instanceof GeoButton || sel instanceof GeoBoolean) {
			return sel.getCaption(StringTemplate.screenReader);
		}
		if (sel != null && sel.getScript(EventType.CLICK) != null) {
			return ScreenReader.getAuralText(sel);
		}

		return null;
	}

	/**
	 *
	 * @return the geo that is currently selected.
	 */
	public GeoElement getSelectedGeo() {
		return AccessibilityManagerNoGui.getSelectedGeo(app);
	}

	@Override
	public SliderInput getSliderAction() {
		return activeButton;
	}

	@Override
	public void sliderChange(double step) {
		if (activeButton == SliderInput.ROTATE_Z) {
			app.getEuclidianView3D().rememberOrigins();
			app.getEuclidianView3D().shiftRotAboutZ(step);
			app.getEuclidianView3D().repaintView();
		}
		if (activeButton == SliderInput.TILT) {
			app.getEuclidianView3D().rememberOrigins();
			app.getEuclidianView3D().shiftRotAboutY(step);
			app.getEuclidianView3D().repaintView();
		}
	}

	@Override
	public void onEmptyConstuction(boolean forward) {
		focusZoomPanel(forward, forward ? nextID(-1) : prevID(-1));
	}

	@Override
	public boolean onSelectFirstGeo(boolean forward) {
		if (!forward) {
			int lastViewId = prevID(-1);
			setPlaySelectedIfVisible(false, lastViewId);
			focusLastZoomOrSpeech(lastViewId);
			return true;
		}

		return handleTabExitGeos(true);
	}

	private boolean focusPlay(int viewID) {
		if (isPlayVisible(viewID)) {
			setPlaySelectedIfVisible(true, viewID);
			return true;
		}
		return false;
	}

	@Override
	public boolean onSelectLastGeo(boolean forward) {
		if (forward) {
			if (focusPlay(app.getActiveEuclidianView().getViewID())) {
				return true;
			}
			int viewID = nextID(-1);
			focusPlay(viewID);
			setTabOverGeos(false);
			if (!focusZoomPanel(true, viewID)) {
				nextFromZoomPanel(viewID);
			}
			return true;
		}
		return handleTabExitGeos(false);
	}
}