package org.geogebra.web.full.gui.applet;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.HeaderPanelDeck;
import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.full.gui.app.FloatingMenuPanel;
import org.geogebra.web.full.gui.app.GGWMenuBar;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.app.ShowKeyboardButton;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.gui.layout.DockGlassPaneW;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.AlgebraPanelInterface;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.full.gui.pagecontrolpanel.PageListPanel;
import org.geogebra.web.full.gui.toolbar.mow.ToolbarMow;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.util.VirtualKeyboardGUI;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.JsEval;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.ArticleElementInterface;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.html5.util.keyboard.VirtualKeyboardW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Frame for applets with GUI
 *
 */
public class GeoGebraFrameBoth extends GeoGebraFrameW implements
		HeaderPanelDeck, NativePreviewHandler {

	private AppletFactory factory;
	private DockGlassPaneW glass;
	private GGWToolBar ggwToolBar = null;
	private GGWMenuBar ggwMenuBar;
	private KeyboardState keyboardState;
	private final SimplePanel kbButtonSpace = new SimplePanel();
	private GDevice device;
	private boolean[] childVisible = new boolean[0];
	private boolean keyboardShowing = false;
	private ShowKeyboardButton showKeyboardButton;
	private int keyboardHeight;
	private DockPanelW dockPanelKB;
	private HeaderPanel lastBG;
	private ToolbarMow toolbarMow;
	private StandardButton openMenuButton;
	private PageListPanel pageListPanel;

	/**
	 * @param factory
	 *            factory for applets (2D or 3D)
	 * @param laf
	 *            look and feel
	 * @param device
	 *            browser/tablet; if left null, defaults to browser
	 * @param mainTag
	 *            TODO remove, if GGB-2051 released.
	 */
	public GeoGebraFrameBoth(AppletFactory factory, GLookAndFeelI laf,
			GDevice device, boolean mainTag) {
		super(laf, mainTag);
		this.device = device;
		this.factory = factory;
		kbButtonSpace.addStyleName("kbButtonSpace");
		this.add(kbButtonSpace);
		Event.addNativePreviewHandler(this);
	}

	@Override
	protected AppW createApplication(ArticleElementInterface article,
			GLookAndFeelI laf) {
		AppW application = factory.getApplet(article, this, laf, this.device);
		getArticleMap().put(article.getId(), application);
		
		if (app != null && app.has(Feature.SHOW_ONE_KEYBOARD_BUTTON_IN_FRAME)) {
			kbButtonSpace.addStyleName("kbButtonSpace");
			this.add(kbButtonSpace);
		}
		
		if (app != null && app.isUnbundled()) {
			addStyleName("newToolbar");
		}

		this.glass = new DockGlassPaneW();
		this.add(glass);

		return application;
	}

	/**
	 * Main entry points called by geogebra.web.full.Web.startGeoGebra()
	 * 
	 * @param geoGebraMobileTags
	 *            list of &lt;article&gt; elements of the web page
	 * @param factory
	 *            applet factory
	 * @param laf
	 *            look and feel
	 * @param device
	 *            browser/tablet; if left null, defaults to browser
	 */
	public static void main(ArrayList<ArticleElement> geoGebraMobileTags,
			AppletFactory factory, GLookAndFeel laf, GDevice device) {

		for (final ArticleElement articleElement : geoGebraMobileTags) {
			final GeoGebraFrameW inst = new GeoGebraFrameBoth(factory, laf,
					device,
					ArticleElement.getDataParamFitToScreen(articleElement));
			inst.ae = articleElement;
			LoggerW.startLogger(inst.ae);
			inst.createSplash(articleElement);
			RootPanel.get(articleElement.getId()).add(inst);
		}
		if (geoGebraMobileTags.isEmpty()) {
			return;
		}

		if (geoGebraMobileTags.size() > 0) {
		// // now we can create dummy elements before & after each applet
		// // with tabindex 10000, for ticket #5158
		// tackleFirstDummy(geoGebraMobileTags.get(0));
		//
		//
			tackleLastDummy(geoGebraMobileTags
					.get(geoGebraMobileTags.size() - 1));
		// // programFocusEvent(firstDummy, lastDummy);
		}
	}

	/**
	 * @param el
	 *            html element to render into
	 * @param factory
	 *            applet factory
	 * @param laf
	 *            look and feel
	 * @param clb
	 *            call this after rendering
	 */
	public static void renderArticleElement(Element el, AppletFactory factory,
			GLookAndFeel laf, JavaScriptObject clb) {

		GeoGebraFrameW.renderArticleElementWithFrame(el, new GeoGebraFrameBoth(
				factory, laf, null, ArticleElement.getDataParamFitToScreen(el)),
				clb);

		GeoGebraFrameW.reCheckForDummies(el);
	}

	/**
	 * @return glass pane for view moving
	 */
	public DockGlassPaneW getGlassPane() {
		return this.glass;
	}

	/**
	 * Attach glass pane to frame
	 */
	public void attachGlass() {
		if (this.glass != null) {
			this.add(glass);
		}
	}

	/**
	 * @param bg
	 *            full-sized GUI
	 */
	public void showBrowser(MyHeaderPanel bg) {
		keyBoardNeeded(false, null);
		GeoGebraFrameW frameLayout = this;
		ToolTipManagerW.hideAllToolTips();
		final int count = frameLayout.getWidgetCount();
		final int oldHeight = this.getOffsetHeight();
		final int oldWidth = this.getOffsetWidth();
		childVisible = new boolean[count];
		for (int i = 0; i < count; i++) {
			// MOW-531 don't interfere with menu animation
			if (!(frameLayout.getWidget(i) instanceof FloatingMenuPanel)) {
				childVisible[i] = frameLayout.getWidget(i).isVisible();
				frameLayout.getWidget(i).setVisible(false);
			}
		}
		frameLayout.add(bg);
		bg.setHeight(oldHeight + "px");
		bg.setWidth(oldWidth + "px");
		bg.onResize();
		bg.setVisible(true);
		bg.setFrame(this);

		this.lastBG = bg;
		// in Graphing Menu > OPen sets overflow to hidden-> breaks resizing of
		// BrowseGUI
		getElement().getStyle().setOverflow(Overflow.VISIBLE);
		// frameLayout.forceLayout();
	}

	@Override
	public void hideBrowser(MyHeaderPanel bg) {
		if (lastBG == null) {
			return; // MOW-394: childVisible is outdated, return
		}
		remove(bg == null ? lastBG : bg);
		lastBG = null;
		ToolTipManagerW.hideAllToolTips();
		final int count = getWidgetCount();
		for (int i = 0; i < count; i++) {
			// MOW-531 don't interfere with menu animation
			if (childVisible.length > i
					&& !(getWidget(i) instanceof FloatingMenuPanel)) {
				getWidget(i).setVisible(childVisible[i]);
			}
		}
		// frameLayout.setLayout(app);
		// frameLayout.forceLayout();
		if (ae.getDataParamFitToScreen()) {
			setSize(Window.getClientWidth(),
					GeoGebraFrameW.computeHeight(ae,
							AppW.smallScreen(ae)));
		} else {
			app.updateViewSizes();
		}
	}

	@Override
	public void setSize(int width, int height) {
		// setPixelSize(width, height);
		if (lastBG != null) {
			((MyHeaderPanel) lastBG).setPixelSize(width, height);
			((MyHeaderPanel) lastBG).resizeTo(width, height);
		} else {
			super.setSize(width, height);
			app.adjustViews(true, height > width
					|| app.getGuiManager().isVerticalSplit(false));
		}
	}

	@Override
	public void doShowKeyBoard(final boolean show,
			MathKeyboardListener textField) {
		if (keyboardState == KeyboardState.ANIMATING_IN
				|| keyboardState == KeyboardState.ANIMATING_OUT) {
			return;
		}

		if (this.isKeyboardShowing() == show) {
			app.getGuiManager().setOnScreenKeyboardTextField(textField);
			return;
		}

		GuiManagerInterfaceW gm = app.getGuiManager();
		if (gm != null && !show) {
			gm.onScreenEditingEnded();
		}

		// this.mainPanel.clear();
		app.getEuclidianView1().setKeepCenter(false);
		if (show) {
			showZoomPanel(false);
			keyboardState = KeyboardState.ANIMATING_IN;
			app.hideMenu();
			app.persistWidthAndHeight();
			ToolTipManagerW.hideAllToolTips();
			addKeyboard(textField, true);
			if (app.isPortrait()) {
				app.getGuiManager().getLayout().getDockManager()
						.adjustViews(true);
			}
		} else {
			showZoomPanel(true);
			keyboardState = KeyboardState.ANIMATING_OUT;
			app.persistWidthAndHeight();
			showKeyboardButton(textField);
			removeKeyboard(textField);
			keyboardState = KeyboardState.HIDDEN;
		}

		// this.mainPanel.add(this.dockPanel);

		Timer timer = new Timer() {
			@Override
			public void run() {

				scrollToInputField();

			}
		};
		timer.schedule(0);
	}

	private void removeKeyboard(MathKeyboardListener textField) {
		final VirtualKeyboardGUI keyBoard = getOnScreenKeyboard(textField);
		this.setKeyboardShowing(false);

		ToolbarPanel toolbarPanel = ((GuiManagerW) app.getGuiManager())
				.getUnbundledToolbar();
		if (toolbarPanel != null) {
			toolbarPanel.updateMoveButton();
		}
		app.updateSplitPanelHeight();

		keyboardHeight = 0;
		keyBoard.remove(new Runnable() {
			@Override
			public void run() {

				keyBoard.resetKeyboardState();
				getApplication().centerAndResizePopups();

			}
		});
	}

	/**
	 * Show keyboard and connect it to textField
	 * 
	 * @param textField
	 *            keyboard listener
	 * @param animated
	 *            whether to animate the keyboard in
	 */
	void addKeyboard(final MathKeyboardListener textField, boolean animated) {
		final VirtualKeyboardW keyBoard = getOnScreenKeyboard(textField);
		this.setKeyboardShowing(true);

		ToolbarPanel toolbarPanel = ((GuiManagerW) app.getGuiManager())
				.getUnbundledToolbar();
		if (toolbarPanel != null) {
			toolbarPanel.hideMoveFloatingButton();
		}

		keyBoard.prepareShow(animated);
		if (app.has(Feature.KEYBOARD_BEHAVIOUR)) {
			app.addAsAutoHidePartnerForPopups(keyBoard.asWidget().getElement());
		}
		CancelEventTimer.keyboardSetVisible();
		// this.mainPanel.addSouth(keyBoard, keyBoard.getOffsetHeight());
		this.add(keyBoard);
		Runnable callback = new Runnable() {

			@Override
			public void run() {
				// this is async, maybe we canceled the keyboard
				if (!isKeyboardShowing()) {
					remove(keyBoard);
					return;
				}
				final boolean showPerspectivesPopup = getApplication()
						.isPerspectivesPopupVisible();
				onKeyboardAdded(keyBoard);
				if (showPerspectivesPopup) {
					getApplication().showPerspectivesPopup();
				}
				if (getApplication().has(Feature.KEYBOARD_BEHAVIOUR)) {
					if (textField != null) {
						textField.setFocus(true, true);
					}
				}
			}
		};
		if (animated) {
			keyBoard.afterShown(callback);
		} else {
			callback.run();
		}
	}

	// @Override
	// public void showInputField() {
	// Timer timer = new Timer() {
	// @Override
	// public void run() {
	// scrollToInputField();
	// }
	// };
	// timer.schedule(0);
	// }
	/**
	 * Callback for keyboard; takes care of resizing
	 * 
	 * @param keyBoard
	 *            keyboard
	 */
	protected void onKeyboardAdded(final VirtualKeyboardW keyBoard) {
		keyboardHeight = keyBoard.getOffsetHeight();
		if (keyboardHeight == 0) {
			keyboardHeight = estimateKeyboardHeight();
		}

		app.updateSplitPanelHeight();

		// TODO maybe too expensive?
		app.updateCenterPanelAndViews();
		add(keyBoard);
		keyBoard.setVisible(true);
		if (showKeyboardButton != null) {
			showKeyboardButton.hide();
		}
		app.centerAndResizePopups();
		keyboardState = KeyboardState.SHOWN;

	}

	/**
	 * Scroll to the input-field, if the input-field is in the algebraView.
	 */
	void scrollToInputField() {
		if (app.showAlgebraInput()
				&& app.getInputPosition() == InputPosition.algebraView) {
			AlgebraPanelInterface dp = (AlgebraPanelInterface) (app
					.getGuiManager()
					.getLayout().getDockManager().getPanel(App.VIEW_ALGEBRA));

			dp.scrollToActiveItem();
		}
	}

	private void showZoomPanel(boolean show) {
		if (app.isPortrait()) {
			return;
		}

		EuclidianDockPanelW dp = (EuclidianDockPanelW) (app.getGuiManager()
				.getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN));
		if (show) {
			dp.showZoomPanel();
		} else {
			dp.hideZoomPanel();
		}

	}

	@Override
	public boolean showKeyBoard(boolean show, MathKeyboardListener textField,
			boolean forceShow) {
		if (forceShow && isKeyboardWantedFromCookie()) {
			doShowKeyBoard(show, textField);
			return true;
		}
		return keyBoardNeeded(show && isKeyboardWantedFromCookie(), textField);
	}

	@Override
	public boolean keyBoardNeeded(boolean show,
			MathKeyboardListener textField) {
		if (this.keyboardState == KeyboardState.ANIMATING_IN) {
			return true;
		}
		if (this.keyboardState == KeyboardState.ANIMATING_OUT) {
			return false;
		}
		
		if (app.isUnbundled() && !app.isWhiteboardActive()
				&& ((GuiManagerW) app.getGuiManager())
						.getUnbundledToolbar() != null
				&& !((GuiManagerW) app.getGuiManager()).getUnbundledToolbar()
						.isOpen()) {
			return false;
		}
		if (app.getLAF().isTablet()
				|| isKeyboardShowing()
									// showing, we don't have
									// to handle the showKeyboardButton
				|| ((app.getGuiManager() != null
						&& app.getGuiManager().getKeyboardShouldBeShownFlag())
						|| (app.isApplet() && app.isShowToolbar()
								&& app.getActiveEuclidianView()
								.getEuclidianController()
								.modeNeedsKeyboard()))) {
			doShowKeyBoard(show, textField);
			return true;
		}
		showKeyboardButton(textField);
		return false;

	}

	private void showKeyboardButton(MathKeyboardListener textField) {
		if (!appNeedsKeyboard()) {
			return;
		}
		if (showKeyboardButton == null) {
			DockManagerW dm = (DockManagerW) app.getGuiManager().getLayout()
					.getDockManager();
			dockPanelKB = dm.getPanelForKeyboard();

			if (dockPanelKB != null) {
				showKeyboardButton = new ShowKeyboardButton(this, dm,
						dockPanelKB, app);
				dockPanelKB.setKeyBoardButton(showKeyboardButton);
			}

		}

		if (showKeyboardButton != null) {
			if (app.has(Feature.SHOW_ONE_KEYBOARD_BUTTON_IN_FRAME)) {
				this.setKeyboardButton();
			}
			// showKeyboardButton.show(app.isKeyboardNeeded(), textField);
			showKeyboardButton.show(
					app.isKeyboardNeeded(), textField);
			if (app.has(Feature.SHOW_ONE_KEYBOARD_BUTTON_IN_FRAME)) {
				showKeyboardButton.addStyleName("openKeyboardButton2");
			}
		}
	}

	private void setKeyboardButton() {
		//this.keyboardButton = button;
		//kbButtonSpace.add(button);
		this.add(showKeyboardButton);
	}
	
	private boolean appNeedsKeyboard() {
		if (app.showAlgebraInput()
				&& app.getInputPosition() == InputPosition.algebraView
				&& app.showView(App.VIEW_ALGEBRA)) {
			return true;
		}
		if (app.has(Feature.KEYBOARD_BEHAVIOUR)) {
			return (app.showAlgebraInput()
					&& app.getInputPosition() != InputPosition.algebraView)
					|| app.showView(App.VIEW_CAS)
					|| app.showView(App.VIEW_SPREADSHEET)
					|| app.showView(App.VIEW_PROBABILITY_CALCULATOR);
		}
		return app.showView(App.VIEW_CAS);
	}

	/**
	 * @param show
	 *            whether to show keyboard button
	 */
	public void showKeyboardButton(boolean show) {
		if (showKeyboardButton == null) {
			return;
		}
		if (show) {
			showKeyboardButton.setVisible(true);
		} else {
			showKeyboardButton.hide();
		}
	}

	@Override
	public void refreshKeyboard() {
		if (isKeyboardShowing()) {
			final VirtualKeyboardW keyBoard = getOnScreenKeyboard(null);
			if (app.isKeyboardNeeded()) {
				ensureKeyboardDeferred();
				add(keyBoard);
			} else {
				removeKeyboard(null);
				if (this.showKeyboardButton != null) {
					this.showKeyboardButton.hide();
				}
			}
		} else {
			if (app != null && app.isKeyboardNeeded() && appNeedsKeyboard()
					&& isKeyboardWantedFromCookie()) {
				if (!app.isStartedWithFile()
						&& !app.getArticleElement().preventFocus()) {
					if (app.getGuiManager().isKeyboardClosedByUser()) {
						ensureKeyboardEditing();
						return;
					}
					setKeyboardShowing(true);
					app.invokeLater(new Runnable() {

						@Override
						public void run() {
							if (getApplication().isWhiteboardActive()) {
								return;
							}
							getApplication().persistWidthAndHeight();
							addKeyboard(null, false);
							getApplication().getGuiManager()
									.focusScheduled(false, false,
									false);
							ensureKeyboardDeferred();

						}
					});
				} else {
					this.showKeyboardButton(null);
					getOnScreenKeyboard(null).showOnFocus();
					app.adjustScreen(true);
				}

			} else if (app != null && app.isKeyboardNeeded()) {
				if (!isKeyboardWantedFromCookie()) {
					this.showKeyboardButton(null);
				} else {
					this.showKeyboardButton(true);
				}
			}

			else if (app != null && !app.has(Feature.SHOW_ONE_KEYBOARD_BUTTON_IN_FRAME)) {

				if (!app.isKeyboardNeeded()
						&& this.showKeyboardButton != null) {
					this.showKeyboardButton.hide();
				}
			}
		}
	}

	private VirtualKeyboardGUI getOnScreenKeyboard(
			MathKeyboardListener textField) {
		if (app.getGuiManager() != null) {
			return ((GuiManagerW) app.getGuiManager())
				.getOnScreenKeyboard(textField, this);
		}
		return null;
	}

	/**
	 * Schedule keyboard editing in 500ms
	 */
	protected void ensureKeyboardDeferred() {
		new Timer() {

			@Override
			public void run() {
				if (getApplication().getGuiManager().hasAlgebraView()) {
					AlgebraViewW av = (AlgebraViewW) getApplication()
							.getAlgebraView();
					// av.clearActiveItem();
					av.setDefaultUserWidth();
				}

				ensureKeyboardEditing();
			}

		}.schedule(500);
	}

	/**
	 * Make sure keyboard is editing
	 */
	protected void ensureKeyboardEditing() {
		DockManagerW dm = (DockManagerW) app.getGuiManager().getLayout()
				.getDockManager();
		MathKeyboardListener ml = app.getGuiManager()
				.getKeyboardListener(dm.getPanelForKeyboard());
		dm.setFocusedPanel(dm.getPanelForKeyboard());

		((GuiManagerW) app.getGuiManager())
					.setOnScreenKeyboardTextField(ml);

		if (ml != null) {
			ml.setFocus(true, true);
			ml.ensureEditing();
		}
	}

	@Override
	public boolean isKeyboardShowing() {
		return this.keyboardShowing;
	}

	@Override
	public void showKeyboardOnFocus() {
		if (app != null) {
			getOnScreenKeyboard(null).showOnFocus();
		}
	}

	@Override
	public void updateKeyboardHeight() {
		if (isKeyboardShowing()) {
			int newHeight = getOnScreenKeyboard(null)
					.getOffsetHeight();
			if (newHeight == 0) {
				newHeight = estimateKeyboardHeight();
			}
			if (newHeight > 0) {

				app.updateSplitPanelHeight();
				keyboardHeight = newHeight;
				app.updateCenterPanelAndViews();
				add(getOnScreenKeyboard(null));
			}
		}
	}

	private int estimateKeyboardHeight() {
		int newHeight = app.needsSmallKeyboard() ? TabbedKeyboard.SMALL_HEIGHT
				: TabbedKeyboard.BIG_HEIGHT;
		// add switcher height
		newHeight += 40;

		return newHeight;
	}

	@Override
	public double getKeyboardHeight() {
		return isKeyboardShowing() ? keyboardHeight : 0;
	}

	private static boolean isKeyboardWantedFromCookie() {
		String wanted = Cookies.getCookie("GeoGebraKeyboardWanted");
		if ("false".equals(wanted)) {
			return false;
		}
		return true;
	}

	/**
	 * Adds menu; if toolbar is missing also add it
	 * 
	 * @param app1
	 *            application
	 */
	public void attachMenubar(AppW app1) {
		if (app1.isUnbundled() || app1.isWhiteboardActive()) {
			return;
		}
		if (ggwToolBar == null) {
			ggwToolBar = new GGWToolBar();
			ggwToolBar.init(app1);
			insert(ggwToolBar, 0);
		}
		ggwToolBar.attachMenubar();
	}

	/**
	 * Adds toolbar
	 * 
	 * @param app1
	 *            application
	 */
	public void attachToolbar(AppW app1) {
		if (app1.isWhiteboardActive()) {
			attachToolbarMow(app1);
			attachOpenMenuButton();
			initPageControlPanel(app1);
			return;
		}

		if (app1.isUnbundled()) {
			// do not attach old toolbar
			return;
		}
		// reusing old toolbar is probably a good decision
		if (ggwToolBar == null) {
			ggwToolBar = new GGWToolBar();
			ggwToolBar.init(app1);
		} else {
			ggwToolBar.updateClassname(app1.getToolbarPosition());
		}

		if (app1.getToolbarPosition() == SwingConstants.SOUTH) {
			add(ggwToolBar);
		} else {
			insert(ggwToolBar, 0);
		}
	}

	private void attachToolbarMow(AppW app1) {
		if (toolbarMow == null) {
			toolbarMow = new ToolbarMow(app1);
		}
		if (app1.getToolbarPosition() == SwingConstants.SOUTH) {
			add(toolbarMow);
		} else {
			insert(toolbarMow, 0);
		}
		add(toolbarMow.getUndoRedoButtons());
		add(toolbarMow.getPageControlButton());
	}

	/**
	 * @return MOW toolbar
	 */
	public ToolbarMow getToolbarMow() {
		return toolbarMow;
	}

	@Override
	public GGWToolBar getToolbar() {
		return ggwToolBar;
	}

	@Override
	public void setMenuHeight(boolean linearInputbar) {
		// TODO in app mode we need to change menu height when inputbar is
		// visible
	}

	/**
	 * @param app1
	 *            application
	 * @return menubar
	 */
	public GGWMenuBar getMenuBar(AppW app1) {
		if (ggwMenuBar == null) {
			ggwMenuBar = new GGWMenuBar();
			((GuiManagerW) app1.getGuiManager()).setGgwMenubar(ggwMenuBar);
		}
		return ggwMenuBar;
	}

	/**
	 * Close all popups and if event was not from menu, also close menu
	 * 
	 * @param event
	 *            browser event
	 */
	public void closePopupsAndMaybeMenu(NativeEvent event) {
		Log.debug("closePopups");
		// app.closePopups(); TODO
		if (app.isMenuShowing()
				&& !Dom.eventTargetsElement(event, ggwMenuBar.getElement())
				&& !Dom.eventTargetsElement(event, getToolbarMenuElement())
				&& !getGlassPane().isDragInProgress()
				&& !app.isUnbundled() && lastBG == null) {
			app.toggleMenu();
		}
	}

	private Element getToolbarMenuElement() {
		return getToolbar() == null ? null
				: getToolbar().getOpenMenuButtonElement();
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (app == null || !app.getUseFullGui()) {
			return;
		}
		final int eventType = DOM.eventGetType(event);
		if (eventType == Event.ONMOUSEDOWN || eventType == Event.ONTOUCHSTART) {
			closePopupsAndMaybeMenu(event);
		}
	}

	/**
	 * Attach keyboard button
	 */
	public void attachKeyboardButton() {
		if (showKeyboardButton != null) {
			add(this.showKeyboardButton);
		}
	}

	@Override
	public boolean isHeaderPanelOpen() {
		return lastBG != null;
	}

	private void attachOpenMenuButton() {
		openMenuButton = new StandardButton(
				MaterialDesignResources.INSTANCE.menu_black_whiteBorder(), null,
				24, app);
		/*
		 * openMenuButton.getUpHoveringFace()
		 * .setImage(MOWToolbar.getImage(pr.menu_header_open_menu_hover(), 32));
		 */
		openMenuButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				onMenuButtonPressed();
				if (getApplication().isWhiteboardActive()) {
					deselectDragBtn();
				}
			}
		});

		openMenuButton.addDomHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					getApplication().toggleMenu();
				}
				// if (event.getNativeKeyCode() == KeyCodes.KEY_LEFT) {
				// GGWToolBar.this.selectMenuButton(0);
				// }
				// if (event.getNativeKeyCode() == KeyCodes.KEY_RIGHT) {
				// GGWToolBar.this.toolBar.selectMenu(0);
				// }
			}
		}, KeyUpEvent.getType());

		openMenuButton.addStyleName("mowOpenMenuButton");
		add(openMenuButton);
	}

	/**
	 * Actions performed when menu button is pressed
	 */
	protected void onMenuButtonPressed() {
		if (app.has(Feature.MOW_VIDEO_TOOL)) {
			app.getActiveEuclidianView().getEuclidianController().widgetsToBackground();
		}
		app.hideKeyboard();
		app.closePopups();
		app.toggleMenu();
		if (app.has(Feature.MOW_MULTI_PAGE)) {
			pageListPanel.close();
		}
	}

	/**
	 * Update undo/redo in MOW toolbar
	 */
	public void updateUndoRedoMOW() {
		if (toolbarMow == null) {
			return;
		}
		toolbarMow.updateUndoRedoActions();
	}

	/**
	 * deselect drag button
	 */
	public void deselectDragBtn() {
		if (((AppWFull) app).getZoomPanelMow() != null
				&& app.getMode() == EuclidianConstants.MODE_TRANSLATEVIEW) {
			((AppWFull) app).getZoomPanelMow().deselectDragBtn();
		}
	}

	/**
	 * @param mode
	 *            new mode for MOW toolbar
	 */
	public void setToorbarMowMode(int mode) {
		if (toolbarMow == null) {
			return;
		}
		toolbarMow.setMode(mode);
	}

	private void setKeyboardShowing(boolean keyboardShowing) {
		this.keyboardShowing = keyboardShowing;
	}

	/**
	 * Create page control panel if needed
	 * 
	 * @param app1
	 *            app
	 */
	@Override
	public void initPageControlPanel(AppW app1) {
		if (!app1.isWhiteboardActive()) {
			return;
		}
		if (pageListPanel == null) {
			pageListPanel = new PageListPanel(app1);
		}
	}

	/**
	 * 
	 * @return pageControlPanel
	 */
	public PageListPanel getPageControlPanel() {
		return pageListPanel;
	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		if (event.getTypeInt() == Event.ONMOUSEDOWN
				|| event.getTypeInt() == Event.ONTOUCHSTART) {

			JavaScriptObject js = event.getNativeEvent().getEventTarget();
			JsEval.callNativeJavaScriptMultiArg("hideAppPicker",
					js);
		}
	}

}