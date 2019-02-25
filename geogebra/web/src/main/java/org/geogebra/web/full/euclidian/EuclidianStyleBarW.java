package org.geogebra.web.full.euclidian;

import java.util.ArrayList;
import java.util.HashMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.full.gui.color.ColorPopupMenuButton;
import org.geogebra.web.full.gui.color.FillingStyleButton;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.images.StyleBarResources;
import org.geogebra.web.full.gui.util.ButtonPopupMenu;
import org.geogebra.web.full.gui.util.GeoGebraIconW;
import org.geogebra.web.full.gui.util.MyCJButton;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.full.gui.util.PointStylePopup;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.full.gui.util.StyleBarW2;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;

/**
 * StyleBar for euclidianView
 */
@SuppressWarnings("javadoc")
public class EuclidianStyleBarW extends StyleBarW2
		implements org.geogebra.common.euclidian.EuclidianStyleBar,
		ValueChangeHandler<Boolean> {

	private enum StyleBarMethod {
		NONE, UPDATE, UPDATE_STYLE
	}

	private static ButtonPopupMenu currentPopup = null;
	private static PopupMenuButtonW currentPopupBtn = null;
	private EuclidianController ec;
	protected EuclidianView ev;
	private Construction cons;

	protected HashMap<Integer, Integer> defaultGeoMap;
	private ArrayList<GeoElement> defaultGeos;
	private GeoElement oldDefaultGeo;

	// flags and constants
	public int mode = -1;
	private boolean isIniting;
	private Integer oldDefaultMode;
	private boolean modeChanged = true;
	private boolean firstPaint = true;

	// button-specific fields
	// TODO: create button classes so these become internal

	protected ArrayList<GeoElement> activeGeoList;
	private boolean visible;

	// // buttons and lists of buttons
	private ColorPopupMenuButton btnBgColor;
	private ColorPopupMenuButton btnTextBgColor;
	private ColorPopupMenuButton btnTextColor;
	private PopupMenuButtonW btnTextSize;
	private PopupMenuButtonW btnLabelStyle;
	private PopupMenuButtonW btnAngleInterval;
	private PopupMenuButtonW btnShowGrid;
	protected PopupMenuButtonW btnPointCapture;
	protected PopupMenuButtonW btnChangeView;
	protected FillingStyleButton btnFilling;

	private MyToggleButtonW btnShowAxes;
	MyToggleButtonW btnBold;
	MyToggleButtonW btnItalic;

	private MyToggleButtonW btnFixPosition;
	private MyToggleButtonW btnFixObject;

	protected MyCJButton btnStandardView;
	protected MyCJButton btnCloseView;

	private MyToggleButtonW[] toggleBtnList;
	private MyToggleButtonW[] btnDeleteSizes = new MyToggleButtonW[3];
	private PopupMenuButtonW[] popupBtnList;

	private StyleBarMethod waitingOperation = StyleBarMethod.NONE;
	Localization loc;
	private ContextMenuPopup btnContextMenu = null;
	private StandardButton btnDelete;
	private MyToggleButtonW btnCrop;
	private LabelSettingsPopup btnLabel;

	/**
	 * @param ev
	 *            {@link EuclidianView}
	 * @param viewID
	 *            id of the panel
	 */
	public EuclidianStyleBarW(EuclidianView ev, int viewID) {
		super((AppW) ev.getApplication(), viewID);
		this.loc = ev.getApplication().getLocalization();
		isIniting = true;
		this.ev = ev;
		ec = ev.getEuclidianController();
		cons = app.getKernel().getConstruction();
		// init handling of default geos
		createDefaultMap();
		defaultGeos = new ArrayList<>();

		initGUI();
		isIniting = false;
		setMode(ev.getMode()); // this will also update the stylebar
		setToolTips();

		setOptionType();
	}

	protected void setOptionType() {
		if (ev.equals(app.getEuclidianView1())) {
			optionType = OptionType.EUCLIDIAN;
		} else {
			optionType = OptionType.EUCLIDIAN2;
		}
	}

	/**
	 * create default map between default geos and modes
	 */
	protected void createDefaultMap() {
		defaultGeoMap = EuclidianStyleBarStatic.createDefaultMap();
	}

	/**
	 * 
	 * @return euclidian view attached
	 */
	public EuclidianView getView() {
		return ev;
	}

	@Override
	public void updateButtonPointCapture(int captureMode) {
		if (captureMode > 3) {
			return;
		}
		int captureModeIndex = captureMode;
		if (captureModeIndex == 3 || captureModeIndex == 0) {
			captureModeIndex = 3 - captureModeIndex; // swap 0 and 3
		}
		btnPointCapture.setSelectedIndex(captureModeIndex);
	}

	@Override
	public void setMode(int mode) {
		if (this.mode == mode) {
			modeChanged = false;
			return;
		}
		modeChanged = true;
		this.mode = mode;

		// MODE_TEXT temporarily switches to MODE_SELECTION_LISTENER
		// so we need to ignore this.
		if (mode == EuclidianConstants.MODE_SELECTION_LISTENER) {
			modeChanged = false;
			return;
		}

		if (getCurrentPopup() != null) {
			getCurrentPopup().hide();
		}

		updateStyleBar();
	}

	protected boolean isVisibleInThisView(GeoElement geo) {
		return geo.isVisibleInView(ev.getViewID());
	}

	@Override
	public void restoreDefaultGeo() {
		if (oldDefaultGeo != null) {
			oldDefaultGeo = cons.getConstructionDefaults()
					.getDefaultGeo(oldDefaultMode);
		}
	}

	@Override
	public void setOpen(boolean visible) {
		this.visible = visible;
		if (visible) {
			switch (this.waitingOperation) {
			default:
				// do nothing
				break;
			case UPDATE:
				updateStyleBar();
				break;
			case UPDATE_STYLE:
				updateButtons();
				break;
			}
			this.waitingOperation = StyleBarMethod.NONE;
		}
	}

	protected boolean hasVisibleGeos(ArrayList<GeoElement> geoList) {
		for (GeoElement geo : geoList) {
			if (isVisibleInThisView(geo) && geo.isEuclidianVisible()
					&& !geo.isAxis()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Updates the state of the stylebar buttons and the defaultGeo field.
	 */
	@Override
	public void updateStyleBar() {
		if (!visible) {
			this.waitingOperation = StyleBarMethod.UPDATE;
			return;
		}

		// -----------------------------------------------------
		// Create activeGeoList, a list of geos the stylebar can adjust.
		// These are either the selected geos or the current default geo.
		// Each button uses this list to update its gui and set visibility
		// -----------------------------------------------------
		activeGeoList = new ArrayList<>();

		// -----------------------------------------------------
		// MODE_MOVE case: load activeGeoList with all selected geos
		// -----------------------------------------------------
		if (EuclidianConstants.isMoveOrSelectionMode(mode)) {

			boolean hasGeosInThisView = false;
			SelectionManager selection = ev.getApplication()
					.getSelectionManager();
			hasGeosInThisView = hasVisibleGeos(selection.getSelectedGeos());
			if (!hasGeosInThisView) {
				for (GeoElement geo : ec.getJustCreatedGeos()) {
					if (isVisibleInThisView(geo) && geo.isEuclidianVisible()) {
						hasGeosInThisView = true;
						break;
					}
				}
			}
			if (hasGeosInThisView) {
				activeGeoList = selection.getSelectedGeos();
				// we also update stylebars according to just created geos
				activeGeoList.addAll(ec.getJustCreatedGeos());
			}
		}

		// -----------------------------------------------------
		// MODE_PEN: for the pen mode the default construction is
		// saved in EuclidianPen
		// All other modes: load activeGeoList with current default geo
		// -----------------------------------------------------
		else if (defaultGeoMap.containsKey(mode)
				|| mode == EuclidianConstants.MODE_PEN) {
			// Save the current default geo state in oldDefaultGeo.
			// Stylebar buttons can temporarily change a default geo, but this
			// default
			// geo is always restored to its previous state after a mode change.
			if (oldDefaultGeo != null && modeChanged) {
				// add oldDefaultGeo to the default map so that the old default
				// is restored
				cons.getConstructionDefaults().addDefaultGeo(oldDefaultMode,
						oldDefaultGeo);
				oldDefaultGeo = null;
				oldDefaultMode = null;
			}

			// get the current default geo
			ArrayList<GeoElement> justCreatedGeos = ec.getJustCreatedGeos();
			Integer type = defaultGeoMap.get(mode);
			if (mode == EuclidianConstants.MODE_PEN) {
				GeoElement geo = ec.getPen().defaultPenLine;
				if (geo != null) {
					activeGeoList.add(geo);
				}
			} else {
				if (type.equals(
						ConstructionDefaults.DEFAULT_POINT_ALL_BUT_COMPLEX)
						&& justCreatedGeos.size() == 1) {
					GeoElement justCreated = justCreatedGeos.get(0);
					if (justCreated.isGeoPoint()) {
						// get default type regarding what type of point has
						// been created
						if (((GeoPointND) justCreated).isPointOnPath()) {
							type = ConstructionDefaults.DEFAULT_POINT_ON_PATH;
						} else if (((GeoPointND) justCreated).hasRegion()) {
							type = ConstructionDefaults.DEFAULT_POINT_IN_REGION;
						} else if (!((GeoPointND) justCreated)
								.isIndependent()) {
							type = ConstructionDefaults.DEFAULT_POINT_DEPENDENT;
						} else {
							type = ConstructionDefaults.DEFAULT_POINT_FREE;
						}
					}
				}

				if (type.equals(
						ConstructionDefaults.DEFAULT_POINT_ALL_BUT_COMPLEX)) {
					// add all non-complex default points
					activeGeoList
							.add(cons.getConstructionDefaults().getDefaultGeo(
									ConstructionDefaults.DEFAULT_POINT_FREE));
					activeGeoList
							.add(cons.getConstructionDefaults().getDefaultGeo(
									ConstructionDefaults.DEFAULT_POINT_ON_PATH));
					activeGeoList
							.add(cons.getConstructionDefaults().getDefaultGeo(
									ConstructionDefaults.DEFAULT_POINT_IN_REGION));
					activeGeoList
							.add(cons.getConstructionDefaults().getDefaultGeo(
									ConstructionDefaults.DEFAULT_POINT_DEPENDENT));
				} else {
					GeoElement geo = cons.getConstructionDefaults()
							.getDefaultGeo(type);
					if (geo != null) {
						activeGeoList.add(geo);
					}
				}

				if (btnContextMenu != null) {
					btnContextMenu.hideMenu();
				}
			}

			// update the defaultGeos field (needed elsewhere for adjusting
			// default geo state)
			defaultGeos = activeGeoList;

			// update oldDefaultGeo
			if (modeChanged) {
				if (defaultGeos.size() == 0) {
					oldDefaultGeo = null;
					oldDefaultMode = -1;
				} else {
					oldDefaultGeo = defaultGeos.get(0);
					oldDefaultMode = type;
				}
			}

			// we also update stylebars according to just created geos
			activeGeoList.addAll(ec.getJustCreatedGeos());
		}
		updateButtons();
		// show the pen delete button
		// TODO: handle pen mode in code above
		// btnPenDelete.setVisible((mode == EuclidianConstants.MODE_PEN));
		addButtons();
	}

	protected void updateButtons() {
		// -----------------------------------------------------
		// update the buttons
		// note: this must always be done, even when activeGeoList is empty
		// -----------------------------------------------------
		if (activeGeoList == null) {
			return;
		}

		Object[] geos;
		if (app.has(Feature.DYNAMIC_STYLEBAR)) {
			if (!isDynamicStylebar()
					&& (this.getView() instanceof EuclidianViewW)
					&& app.isUnbundledOrWhiteboard()) {
				// in view stylebar won't be appeared object stylebar
				geos = new Object[0];
			} else if (!isDynamicStylebar()
					&& (this.getView() instanceof EuclidianView3DInterface)
					&& (!EuclidianConstants
							.isMoveOrSelectionMode(app.getMode()))
					&& (app.getMode() != EuclidianConstants.MODE_PEN)
					&& app.isUnbundledOrWhiteboard()) {
				// show the object stylebar in 3D view, when the user selects a
				// tool
				geos = activeGeoList.toArray();
			} else {
				ArrayList<GeoElement> geoList = new ArrayList<>();
				for (GeoElement geo0 : activeGeoList) {
					if (geo0.isEuclidianVisible()) {
						geoList.add(geo0);
					}
				}
				geos = geoList.toArray();
			}
		} else {
			geos = activeGeoList.toArray();
		}

		for (int i = 0; i < popupBtnList.length; i++) {
			if (popupBtnList[i] != null) { // null pointer fix until necessary
				popupBtnList[i].update(geos);
			}
		}
		for (int i = 0; i < toggleBtnList.length; i++) {
			if (toggleBtnList[i] != null) { // null pointer fix until necessary
				toggleBtnList[i].update(geos);
			}
		}
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		if (activeGeoList != null && activeGeoList.contains(geo)) {
			if (!visible) {
				this.waitingOperation = StyleBarMethod.UPDATE_STYLE;
				return;
			}
			updateButtons();
		}
	}

	// =====================================================
	// Init GUI
	// =====================================================

	private void initGUI() {
		createButtons();
		setActionCommands();
		addButtons();
		popupBtnList = newPopupBtnList();
		toggleBtnList = newToggleBtnList();
		ClickStartHandler.initDefaults(this, true, true);
	}

	protected void setActionCommands() {
		setActionCommand(btnShowAxes, "showAxes");
		setActionCommand(btnPointCapture, "pointCapture");
	}

	/**
	 * adds/removes buttons (must be called on updates so that separators are
	 * drawn only when needed)
	 */
	private void addButtons() {
		clear();
		// --- order matters here
		// button for closing extra views
		if (app.has(Feature.DYNAMIC_STYLEBAR)
				&& App.VIEW_EUCLIDIAN_FOR_PLANE_START <= viewID
				&& viewID <= App.VIEW_EUCLIDIAN_FOR_PLANE_END) {
			addCloseViewButton();
		}
		// add graphics decoration buttons
		addGraphicsDecorationsButtons();
		add(btnPointCapture);

		// add color and style buttons
		add(btnColor);
		add(btnBgColor);
		add(btnTextBgColor);
		add(btnTextColor);
		if (app.has(Feature.MOW_COLOR_FILLING_LINE) && btnFilling != null) {
			add(btnFilling);
		}
		add(btnLineStyle);
		add(btnPointStyle);
		if (app.has(Feature.MOW_COLOR_FILLING_LINE)) {
			// update language of descriptions in color, line style and point
			// style dialogs
			btnColor.setLabels();
			btnLineStyle.setLabels();
			btnPointStyle.setLabels();
		}
		if (app.isUnbundledOrWhiteboard()) {
			// order of button changed
			add(btnTextSize);
		}

		// add text decoration buttons
		if (btnBold.isVisible() && !app.isUnbundledOrWhiteboard()) {
			addSeparator();
		}

		add(btnBold);
		add(btnItalic);
		if (!app.isUnbundledOrWhiteboard()) {
			add(btnTextSize);
		}

		add(btnAngleInterval);
		add(btnLabelStyle);

		if (btnFixPosition.isVisible() || btnFixObject.isVisible()) {
			addSeparator();
		}

		add(btnFixPosition);
		add(btnFixObject);

		for (int i = 0; i < 3; i++) {
			add(btnDeleteSizes[i]);
		}

		if (!app.has(Feature.DYNAMIC_STYLEBAR) || !isDynamicStylebar()
				|| !app.isUnbundledOrWhiteboard()) {
			addMenuButton();
		}

		if (!app.has(Feature.DYNAMIC_STYLEBAR)) {
			if (getViewButton() == null) {
				addViewButton();
			} else {
				add(getViewButton());
			}
		}

		if (!app.isWhiteboardActive() && isDynamicStylebar()) {
			add(getLabelPopup());
		}

		if (app.isUnbundledOrWhiteboard()) {
			if (app.isWhiteboardActive() && isImageGeoSelected()
					&& ev.getMode() != EuclidianConstants.MODE_SELECT) {
				addCropButton();
			}
			addDeleteButton();
		}

		if (app.has(Feature.DYNAMIC_STYLEBAR) && hasActiveGeos()
				&& isContextMenuNeeded()) {
			addContextMenuButton();
		}
	}

	/**
	 * @return true if geo needs a 3-dot button.
	 */
	protected boolean isContextMenuNeeded() {
		if (!ev.getEuclidianController().getAppSelectedGeos().isEmpty()
				&& (getFirstGeo().isGeoAudio() || getFirstGeo().isGeoVideo()
						|| getFirstGeo() instanceof GeoEmbed)) {
			this.addStyleName("noContextBtn");
			if (Browser.isIPad()) {
				btnDelete.addStyleName("delete");
			}
			return false;
		}
		return true;
	}

	private GeoElementND getFirstGeo() {
		return ev.getEuclidianController().getAppSelectedGeos().get(0);
	}

	private boolean isImageGeoSelected() {
		return ev.getEuclidianController().getAppSelectedGeos().size() == 1
				&& getFirstGeo().isGeoImage();
	}

	private boolean hasActiveGeos() {
		return !ev.getEuclidianController().getAppSelectedGeos().isEmpty();
	}

	private void createContextMenuButton() {
		if (!hasActiveGeos()) {
			return;
		}
		btnContextMenu = new ContextMenuPopup(app);
		// btnContextMenu.setIcon(new
		// ImageOrText(AppResources.INSTANCE.dots()));
		// btnContextMenu.addStyleName("MyCanvasButton-borderless");
		// btnContextMenu.addClickHandler(new ClickHandler() {
		//
		// public void onClick(ClickEvent event) {
		// if (ec.isObjectMenuActive()) {
		// app.closePopups();
		// btnContextMenu.setIcon(
		// new ImageOrText(AppResources.INSTANCE.dots()));
		//
		// } else {
		// btnContextMenu.setIcon(
		// new ImageOrText(AppResources.INSTANCE.dots_active()));
		// ec.showObjectContextMenu(0, 0);
		// }
		// }
		// });
	}

	protected void addCropButton() {
		btnCrop = new MyToggleButtonW(new NoDragImage(
				MaterialDesignResources.INSTANCE.crop_black(), 24));
		btnCrop.addStyleName("btnCrop");
		btnCrop.setDown(app.getActiveEuclidianView().getBoundingBox() != null
				&& app.getActiveEuclidianView().getBoundingBox().isCropBox());
		ClickStartHandler.init(btnCrop, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				toggleCrop(!getBtncrop().isDown());
			}
		});
		btnCrop.setTitle(loc.getMenu("stylebar.Crop"));
		add(btnCrop);
	}

	protected void toggleCrop(boolean val) {
		if (getBtncrop() != null) {
			Drawable dr = ((Drawable) app.getActiveEuclidianView()
					.getDrawableFor(activeGeoList.get(0)));
			dr.getBoundingBox().setCropBox(val);
			app.getActiveEuclidianView().repaintView();
			updateStyleBar();
		}
	}

	public MyToggleButtonW getBtncrop() {
		return btnCrop;
	}

	/**
	 * add delete button to dynamic stylebar
	 */
	protected void addDeleteButton() {
		btnDelete = new StandardButton(
				MaterialDesignResources.INSTANCE.delete_black(), null, 24, app);
		btnDelete.setStyleName("MyCanvasButton");
		FastClickHandler btnDelHandler = new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				// force closing keyboard
				getFrame().showKeyBoard(false, null, true);
				boolean deletePoints = true;
				for (int i = activeGeoList.size() - 1; i >= 0; i--) {
					if (!(activeGeoList.get(i) instanceof GeoPoint)) {
						activeGeoList.get(i).remove();
						deletePoints = false;
					}
				}
				if (deletePoints) {
					app.deleteSelectedObjects(false);
				} else {
					app.storeUndoInfo();
				}

				app.getActiveEuclidianView().getEuclidianController()
						.clearSelectionAndRectangle();
			}
		};
		btnDelete.addFastClickHandler(btnDelHandler);
		add(btnDelete);
	}

	/**
	 * @return the frame with casting.
	 */
	GeoGebraFrameBoth getFrame() {
		return (((AppWFull) app).getAppletFrame());
	}

	protected void closeLabelPopup() {
		if (getLabelPopup().getMyPopup().isShowing()) {
			getLabelPopup().getMyPopup().hide();
		}
	}

	// TODO instead of addViewButton() we need a new function addContextMenu()
	// that uses the same icon (3 dots) as ViewButton but instead opens the
	// context menu
	protected void addContextMenuButton() {
		if (!isBackground() && app.isUnbundledOrWhiteboard()) {
			if (btnContextMenu == null) {
				createContextMenuButton();
			}
			if (!app.isUnbundledOrWhiteboard()) {
				btnContextMenu.addStyleName("dynStyleContextButton");
			} else {
				btnContextMenu.addStyleName("matDynStyleContextButton");
			}
			add(btnContextMenu);
		} else if (!isBackground()) {
			if (getViewButton() == null) {
				addViewButton();
			} else {
				add(getViewButton());
			}
		}
	}

	protected ContextMenuPopup getContextMenuButton() {
		return btnContextMenu;
	}

	/*
	 * Some style button removed from dynamic stylebar. Those will be shown in
	 * the default stylebar yet.
	 */
	boolean showAllStyleButtons() {
		return !app.has(Feature.DYNAMIC_STYLEBAR) || (!isDynamicStylebar()
				&& !(this.getView() instanceof EuclidianView3DInterface))
				|| !app.isUnbundledOrWhiteboard();
	}

	protected boolean isDynamicStylebar() {
		return false;
	}

	protected boolean isBackground() {
		return (btnShowGrid != null && btnShowGrid.isVisible());
	}

	/**
	 * add axes, grid, ... buttons
	 */
	protected void addGraphicsDecorationsButtons() {
		addAxesAndGridButtons();
		addChangeViewButtons();
		addBtnRotateView();
	}

	/**
	 * add axes and grid buttons
	 */
	protected void addAxesAndGridButtons() {
		add(btnShowAxes);

		if (mode != EuclidianConstants.MODE_ERASER) {
			add(btnShowGrid);
		}
	}

	/**
	 * add standard view, show all objects, etc. buttons
	 */
	protected void addChangeViewButtons() {
		add(btnChangeView);
	}

	protected void addCloseViewButton() {
		add(btnCloseView);
	}

	/**
	 * add automatic rotate 3D view button
	 */
	protected void addBtnRotateView() {
		// used in 3D
	}

	protected MyToggleButtonW getAxesOrGridToggleButton() {
		return btnShowAxes;
	}

	protected PopupMenuButtonW getAxesOrGridPopupMenuButton() {
		return btnShowGrid;
	}

	protected MyToggleButtonW[] newToggleBtnList() {
		return new MyToggleButtonW[] { getAxesOrGridToggleButton(), btnBold,
				btnItalic, btnFixPosition, btnFixObject, btnDeleteSizes[0],
				btnDeleteSizes[1], btnDeleteSizes[2] };
	}

	protected PopupMenuButtonW[] newPopupBtnList() {
		return new PopupMenuButtonW[] { getAxesOrGridPopupMenuButton(),
				btnColor, btnBgColor, btnTextColor, btnTextBgColor, btnFilling,
				btnLineStyle, btnPointStyle, btnTextSize, btnAngleInterval,
				btnLabelStyle, btnPointCapture, btnChangeView };
	}

	// =====================================================
	// Create Buttons
	// =====================================================

	protected void createButtons() {
		// TODO: fill in
		createAxesAndGridButtons();
		createStandardViewBtn();
		createLineStyleBtn();
		createPointStyleBtn(mode);
		createLabelStyleBtn();
		createAngleIntervalBtn();
		createPointCaptureBtn();
		createDeleteSiztBtn();

		if (app.has(Feature.MOW_COLOR_FILLING_LINE)) {
			createColorBtn();
			createFillingBtn();
		} else {
			createColorBtn();
		}
		createBgColorBtn();
		createTextColorBtn();
		createTextBgColorBtn();
		createTextBoldBtn();
		createTextItalicBtn();
		createFixPositionBtn();
		createFixObjectBtn();
		createTextSizeBtn();
		createChangeViewButtons();
		if (app.has(Feature.DYNAMIC_STYLEBAR)) {
			createCloseViewBtn();
		}
	}

	private LabelSettingsPopup getLabelPopup() {
		if (btnLabel == null) {
			btnLabel = new LabelSettingsPopup(app);
		}

		return btnLabel;
	}

	protected class ProjectionPopup extends PopupMenuButtonW {

		public ProjectionPopup(AppW app, ImageOrText[] projectionIcons) {
			super(app, projectionIcons, 1, projectionIcons.length,
					SelectionTable.MODE_ICON, true, false, null, false);
		}

		@Override
		public void update(Object[] geos) {
			if (app.isUnbundledOrWhiteboard()) {
				super.setVisible(geos.length == 0);
			} else {
				super.setVisible(geos.length == 0
						&& mode != EuclidianConstants.MODE_PEN);
			}
		}
		/*
		 * @Override public Point getToolTipLocation(MouseEvent e) { return new
		 * Point(TOOLTIP_LOCATION_X, TOOLTIP_LOCATION_Y); }
		 */
	}

	protected void createChangeViewButtons() {
		ImageOrText[] directionIcons = ImageOrText
				.convert(
						new ImageResource[] {
								StyleBarResources.INSTANCE.standard_view(),
								StyleBarResources.INSTANCE.view_all_objects() },
						24);

		btnChangeView = new ProjectionPopup(app, directionIcons);
		btnChangeView.setIcon(
				new ImageOrText(StyleBarResources.INSTANCE.standard_view()));
		btnChangeView.addPopupHandler(this);
	}

	protected void createAxesAndGridButtons() {
		// ========================================
		// show axes button
		btnShowAxes = new MyToggleButtonWforEV(
				StyleBarResources.INSTANCE.axes(), this);
		btnShowAxes.setSelected(ev.getShowXaxis());
		btnShowAxes.addValueChangeHandler(this);

		// ========================================
		// show grid button
		ImageOrText[] grids = new ImageOrText[4];
		for (int i = 0; i < 4; i++) {
			grids[i] = GeoGebraIconW
					.createGridStyleIcon(EuclidianView.getPointStyle(i));
		}
		btnShowGrid = new GridPopup(app, grids, ev);
		btnShowGrid.addPopupHandler(this);
	}

	private void createDeleteSiztBtn() {
		ImageResource[] delBtns = new ImageResource[] {
				StyleBarResources.INSTANCE.stylingbar_delete_small(),
				StyleBarResources.INSTANCE.stylingbar_delete_medium(),
				StyleBarResources.INSTANCE.stylingbar_delete_large() };
		for (int i = 0; i < 3; i++) {
			btnDeleteSizes[i] = new MyToggleButtonW(delBtns[i]) {

				@Override
				public void update(Object[] geos) {
					// always show this button unless in pen mode
					super.setVisible(mode == EuclidianConstants.MODE_DELETE
							|| mode == EuclidianConstants.MODE_ERASER);
				}
			};
			btnDeleteSizes[i].addValueChangeHandler(this);
		}
	}

	private void createPointCaptureBtn() {
		ImageOrText[] strPointCapturing = ImageOrText.convert(new String[] {
				loc.getMenu("Labeling.automatic"), loc.getMenu("SnapToGrid"),
				loc.getMenu("FixedToGrid"), loc.getMenu("off") });
		btnPointCapture = new PopupMenuButtonW(app, strPointCapturing, -1, 1,
				SelectionTable.MODE_TEXT, false) {

			@Override
			public void update(Object[] geos) {
				if (app.isUnbundledOrWhiteboard()) {
					super.setVisible(geos.length == 0);
				} else {
					// same as axes
					super.setVisible(
							geos.length == 0 && !EuclidianView.isPenMode(mode)
									&& mode != EuclidianConstants.MODE_DELETE
									&& mode != EuclidianConstants.MODE_ERASER);
				}
			}

			@Override
			public ImageOrText getButtonIcon() {
				return this.getIcon();
			}
		};

		// it is not needed, must be an Image preloaded like others.
		ImageResource ptCaptureIcon = StyleBarResources.INSTANCE.magnet();
		// must be done in callback btnPointCapture.setIcon(ptCaptureIcon);
		ImgResourceHelper.setIcon(ptCaptureIcon, btnPointCapture);
		btnPointCapture.addPopupHandler(this);
		btnPointCapture.setKeepVisible(false);
	}

	private void createLabelStyleBtn() {
		ImageOrText[] captionArray = ImageOrText
				.convert(new String[] { loc.getMenu("stylebar.Hidden"), // index
						// 4
						loc.getMenu("Name"), // index 0
						loc.getMenu("NameAndValue"), // index 1
						loc.getMenu("Value"), // index 2
						loc.getMenu("Caption") // index 3
		});

		btnLabelStyle = new PopupMenuButtonW(app, captionArray, -1, 1,
				SelectionTable.MODE_TEXT, false) {

			@Override
			public void update(Object[] geos) {
				GeoElement geo = EuclidianStyleBarStatic
						.checkGeosForCaptionStyle(geos, mode, app);
				boolean geosOK = geo != null && showAllStyleButtons();
				super.setVisible(geosOK);
				if (geosOK) {
					setSelectedIndex(EuclidianStyleBarStatic
							.getIndexForLabelMode(geo, app));
				}
			}

			@Override
			public ImageOrText getButtonIcon() {
				return this.getIcon();
			}
		};
		ImageResource ic = AppResources.INSTANCE.mode_showhidelabel_16();
		// must be done with callback btnLabelStyle.setIcon(ic);
		ImgResourceHelper.setIcon(ic, btnLabelStyle);
		btnLabelStyle.addPopupHandler(this);
		btnLabelStyle.setKeepVisible(false);
	}

	private void createAngleIntervalBtn() {
		String[] angleIntervalString = new String[GeoAngle
				.getIntervalMinListLength() - 1];
		for (int i = 0; i < GeoAngle.getIntervalMinListLength() - 1; i++) {
			angleIntervalString[i] = app.getLocalization().getPlain(
					"AngleBetweenAB.short", GeoAngle.getIntervalMinList(i),
					GeoAngle.getIntervalMaxList(i));
		}

		ImageOrText[] angleIntervalArray = ImageOrText
				.convert(angleIntervalString);

		btnAngleInterval = new PopupMenuButtonW(app, angleIntervalArray, -1, 1,
				SelectionTable.MODE_TEXT, false) {

			@Override
			public void update(Object[] geos) {
				GeoElement geo = EuclidianStyleBarStatic
						.checkGeosForAngleInterval(geos);
				boolean geosOK = (geo != null
						&& !app.isUnbundledOrWhiteboard());
				super.setVisible(geosOK);
				if (geosOK) {
					setSelectedIndex(((AngleProperties) geo).getAngleStyle()
							.getXmlVal());
				}
			}

			@Override
			public ImageOrText getButtonIcon() {
				return this.getIcon();
			}
		};
		ImageResource ic = AppResources.INSTANCE.stylingbar_angle_interval();
		// must be done with callback btnLabelStyle.setIcon(ic);
		ImgResourceHelper.setIcon(ic, btnAngleInterval);
		btnAngleInterval.addPopupHandler(this);
		btnAngleInterval.setKeepVisible(false);
	}

	private void createStandardViewBtn() {
		btnStandardView = new MyCJButton();
		ImageOrText icon = new ImageOrText(
				StyleBarResources.INSTANCE.standard_view());
		btnStandardView.setIcon(icon);
		btnStandardView.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setEvStandardView();
			}
		});
	}

	private void createCloseViewBtn() {
		btnCloseView = new MyCJButton();
		ImageOrText icon = new ImageOrText(
				GuiResources.INSTANCE.dockbar_close());
		btnCloseView.setIcon(icon);
		btnCloseView.addStyleName("StylebarCloseViewButton");
		btnCloseView.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				closeView();
			}
		});
	}

	protected void closeView() {
		app.getGuiManager().setShowView(false, viewID);
	}

	/**
	 * set EV to standard view
	 */
	protected void setEvStandardView() {
		getView().setStandardView(true);
	}

	private void createColorBtn() {
		btnColor = new ColorPopupMenuButton(app,
				ColorPopupMenuButton.COLORSET_DEFAULT, true) {

			@Override
			public void update(Object[] geos) {
				if (mode == EuclidianConstants.MODE_FREEHAND_SHAPE) {
					Log.debug(
							"MODE_FREEHAND_SHAPE not working in StyleBar yet");
				} else {
					boolean geosOK = (geos.length > 0
							|| EuclidianView.isPenMode(mode));
					boolean hasOpacity = true;
					for (int i = 0; i < geos.length; i++) {
						GeoElement geo = ((GeoElement) geos[i])
								.getGeoElementForPropertiesDialog();
						if (geo instanceof GeoText || geo instanceof GeoButton
								|| geo.isGeoAudio() || geo.isGeoVideo()
								|| geo instanceof GeoEmbed) {
							geosOK = false;
							break;
						}
						if (geos[i] instanceof GeoLocusStroke) {
							hasOpacity = false;
						}
					}

					super.setVisible(geosOK);
					if (geosOK) {
						// get color from first geo
						GColor geoColor;
						geoColor = geos.length > 0
								? ((GeoElement) geos[0]).getObjectColor()
								: GColor.BLACK;
						// check if selection contains a fillable geo
						// if true, then set slider to first fillable's alpha
						// value
						double alpha = 1.0;
						boolean hasFillable = false;
						for (int i = 0; i < geos.length; i++) {
							if (((GeoElement) geos[i]).isFillable()) {
								hasFillable = true;
								alpha = ((GeoElement) geos[i]).getAlphaValue();
								break;
							}
							if (geos[i] instanceof GeoPolyLine
									&& EuclidianView.isPenMode(mode)) {
								hasFillable = true;
								alpha = ((GeoElement) geos[i]).getLineOpacity();

								break;
							}
						}

						if (!app.isUnbundled()) {
							if (hasFillable) {
								if (app.isWhiteboardActive()
										&& geos[0] instanceof GeoImage) {
									if (hasOpacity) {
										setTitle(loc.getMenu("Opacity"));
									} else {
										super.setVisible(false);
									}
								} else {
									setTitle(loc.getMenu(
											"stylebar.ColorTransparency"));
								}
							} else {
								setTitle(loc.getMenu("stylebar.Color"));
							}
						}

						setSliderVisible(hasFillable && hasOpacity);

						if (EuclidianView.isPenMode(mode)) {
							setSliderValue(
									(int) Math.round((alpha * 100) / 255));
						} else {
							setSliderValue((int) Math.round(alpha * 100));
						}

						updateColorTable();
						setEnableTable(geos.length > 0
								&& !(geos[0] instanceof GeoImage));
						// find the geoColor in the table and select it
						int index = this.getColorIndex(geoColor);
						setSelectedIndex(index);
						if (EuclidianView.isPenMode(mode)) {
							setDefaultColor(alpha / 255, geoColor);
						} else {
							setDefaultColor(alpha, geoColor);
						}

						this.setKeepVisible(!app.isUnbundledOrWhiteboard()
								&& EuclidianConstants
										.isMoveOrSelectionMode(mode));
					}
				}
			}

			@Override
			public void onClickAction() {
				if (getBtncrop() != null) {
					getBtncrop().setDown(false);
					toggleCrop(false);
				}
			}
		};
		btnColor.addPopupHandler(this);
	}

	private void createFillingBtn() {
		btnFilling = new FillingStyleButton(app) {
			@Override
			public void update(Object[] geos) {

				if (mode == EuclidianConstants.MODE_FREEHAND_SHAPE) {
					Log.debug(
							"MODE_FREEHAND_SHAPE not working in StyleBar yet");
				} else {
					boolean geosOK = (geos.length > 0 || (EuclidianView
							.isPenMode(mode)
							&& !app.isUnbundledOrWhiteboard()));
					for (int i = 0; i < geos.length; i++) {
						GeoElement geo = ((GeoElement) geos[i])
								.getGeoElementForPropertiesDialog();
						if (geo instanceof GeoText || geo instanceof GeoButton
								|| geo instanceof GeoPoint
								|| geo instanceof GeoLocusStroke
								|| geo instanceof GeoLine || geo.isGeoVideo()
								|| geo.isGeoAudio() || geo.isGeoImage()) {
							geosOK = false;
							break;
						}
					}
					super.setVisible(geosOK);

					if (geosOK) {

						// check if selection contains a fillable geo
						// if true, then set slider to first fillable's alpha
						// value
						// double alpha = 1.0;
						boolean hasFillable = false;
						boolean alphaOnly = false;
						FillType fillType = null;
						for (int i = 0; i < geos.length; i++) {
							GeoElement geo = (GeoElement) geos[i];
							if (geo.isFillable()) {
								alphaOnly = geo.isAngle() || geo.isGeoImage();
								hasFillable = true;
								// alpha = geo.getAlphaValue();
								fillType = geo.getFillType();
								break;
							}
							if (geos[i] instanceof GeoPolyLine
									&& EuclidianView.isPenMode(mode)) {
								hasFillable = true;
								// alpha = ((GeoElement)
								// geos[i]).getLineOpacity();

								break;
							}
						}

						setTitle(loc.getMenu("stylebar.Filling"));

						boolean enableFill = hasFillable && !alphaOnly;
						super.setVisible(enableFill);
						setFillEnabled(enableFill);
						if (enableFill) {
							setFillType(fillType);
						}

						this.setKeepVisible(
								EuclidianConstants.isMoveOrSelectionMode(mode));
					}
				}
			}

			@Override
			public void onClickAction() {
				if (getBtncrop() != null) {
					getBtncrop().setDown(false);
					toggleCrop(false);
				}
			}
		};
		btnFilling.setFixedIcon(btnFilling.getButtonIcon());
		btnFilling.addPopupHandler(this);
	}

	private void createBgColorBtn() {
		btnBgColor = new ColorPopupMenuButton(app,
				ColorPopupMenuButton.COLORSET_BGCOLOR, false) {

			@Override
			public void update(Object[] geos) {

				boolean geosOK = (geos.length > 0
						&& !((GeoElement) geos[0]).isGeoAudio()
						&& !((GeoElement) geos[0]).isGeoVideo());
				for (int i = 0; i < geos.length; i++) {
					GeoElement geo = ((GeoElement) geos[i])
							.getGeoElementForPropertiesDialog();
					if (!(geo instanceof GeoText) && !(geo instanceof GeoButton
							|| geo.isGeoAudio() || geo.isGeoVideo())) {
						geosOK = false;
						break;
					}

				}

				super.setVisible(geosOK);

				if (geosOK) {
					// get color from first geo
					GColor geoColor;
					geoColor = ((GeoElement) geos[0]).getBackgroundColor();

					/*
					 * // check if selection contains a fillable geo // if true,
					 * then set slider to first fillable's alpha value float
					 * alpha = 1.0f; boolean hasFillable = false; for (int i =
					 * 0; i < geos.length; i++) { if (((GeoElement)
					 * geos[i]).isFillable()) { hasFillable = true; alpha =
					 * ((GeoElement) geos[i]).getAlphaValue(); break; } }
					 * getMySlider().setVisible(hasFillable);
					 * setSliderValue(Math.round(alpha * 100));
					 */
					double alpha = 1.0;
					updateColorTable();

					// find the geoColor in the table and select it
					int index = getColorIndex(geoColor);
					setSelectedIndex(index);
					setDefaultColor(alpha, geoColor);

					// if nothing was selected, set the icon to show the
					// non-standard color
					if (index == -1) {
						this.setIcon(GeoGebraIconW.createColorSwatchIcon(alpha,
								geoColor, null));
					}
				}
			}
		};

		btnBgColor.setEnableTable(true);
		btnBgColor.setKeepVisible(app.isUnbundledOrWhiteboard() ? false : true);
		btnBgColor.addPopupHandler(this);
	}

	private void createTextColorBtn() {
		btnTextColor = new ColorPopupMenuButton(app,
				ColorPopupMenuButton.COLORSET_DEFAULT, false) {

			private GColor geoTextColor;

			@Override
			public void update(Object[] geos) {
				boolean geosOK = checkTextNoMedia(geos);
				super.setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();
					geoTextColor = geo.getObjectColor();
					updateColorTable();

					// find the geoColor in the table and select it
					int index = this.getColorIndex(geoTextColor);
					setSelectedIndex(index);

					// if nothing was selected, set the icon to show the
					// non-standard color
					if (index == -1) {
						this.setIcon(getButtonIcon());
					}
				}
			}

			@Override
			public ImageOrText getButtonIcon() {
				return new ImageOrText(
						MaterialDesignResources.INSTANCE.text_color(), 24);
			}
		};
		btnTextColor.setEnableTable(true);
		btnTextColor.addStyleName("btnTextColor");
		btnTextColor.addPopupHandler(this);
	}

	protected boolean checkTextNoMedia(Object[] geos) {
		boolean geosOK = checkGeoText(geos);
		for (Object obj : geos) {
			GeoElement geo0 = (GeoElement) obj;
			if (geo0.isGeoInputBox() || geo0.isGeoAudio()
					|| geo0.isGeoVideo()) {
				return false;
			}
		}
		return geosOK;
	}

	private void createTextBgColorBtn() {
		btnTextBgColor = new ColorPopupMenuButton(app,
				ColorPopupMenuButton.COLORSET_DEFAULT, false) {

			private GColor geoColor;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = !app.isUnbundledOrWhiteboard()
						&& checkGeoText(geos);
				super.setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();
					geoColor = geo.getObjectColor();
					updateColorTable();

					// find the geoColor in the table and select it
					int index = this.getColorIndex(geoColor);
					setSelectedIndex(index);

					// if nothing was selected, set the icon to show the
					// non-standard color
					if (index == -1) {
						this.setIcon(getButtonIcon());
					}
				}
			}

			@Override
			public ImageOrText getButtonIcon() {
				return GeoGebraIconW.createTextSymbolIcon("A",
						getSelectedColor(), null);
			}
		};
		btnTextBgColor.setEnableTable(true);
		btnTextBgColor.addStyleName("btnTextColor");
		btnTextBgColor.addPopupHandler(this);
	}

	private void createTextBoldBtn() {
		if (app.isUnbundledOrWhiteboard()) {
			btnBold = new MyToggleButtonW(new NoDragImage(
					MaterialDesignResources.INSTANCE.text_bold_black(), 24)) {
				@Override
				public void update(Object[] geos) {
					boolean geosOK = checkTextNoMedia(geos);
					super.setVisible(geosOK);
					if (geosOK) {
						GeoElement geo = ((GeoElement) geos[0])
								.getGeoElementForPropertiesDialog();
						int style = ((TextProperties) geo).getFontStyle();
						btnBold.setValue((style & GFont.BOLD) != 0);
					}
				}
			};
		} else {
			btnBold = new MyToggleButtonW(loc.getMenu("Bold.Short")) {

				@Override
				public void update(Object[] geos) {

					boolean geosOK = checkGeoText(geos)
							&& !((GeoElement) geos[0]).isGeoInputBox();
					super.setVisible(geosOK);
					if (geosOK) {
						GeoElement geo = ((GeoElement) geos[0])
								.getGeoElementForPropertiesDialog();
						int style = ((TextProperties) geo).getFontStyle();
						btnBold.setValue((style & GFont.BOLD) != 0);
					}
				}
			};
		}
		btnBold.addStyleName("btnBold");
		btnBold.addValueChangeHandler(this);
	}

	private void createFixPositionBtn() {
		btnFixPosition = new MyToggleButtonW(
				StyleBarResources.INSTANCE.fixPosition()) {

			@Override
			public void update(Object[] geos) {

				boolean geosOK = EuclidianStyleBarStatic
						.checkGeosForFixPosition(geos) && showAllStyleButtons();
				super.setVisible(geosOK);
				if (geosOK) {
					this.setValue(EuclidianStyleBarStatic
							.checkSelectedFixPosition((GeoElement) geos[0]));
				}
			}
		};
		// btnFixPosition.addStyleName("btnFixPosition");
		btnFixPosition.addValueChangeHandler(this);
	}

	private void createFixObjectBtn() {
		btnFixObject = new MyToggleButtonW(
				StyleBarResources.INSTANCE.objectUnfixed(),
				StyleBarResources.INSTANCE.objectFixed()) {

			@Override
			public void update(Object[] geos) {

				boolean geosOK = EuclidianStyleBarStatic
						.checkGeosForFixObject(geos) && showAllStyleButtons();
				super.setVisible(geosOK);
				if (geosOK) {
					this.setValue(EuclidianStyleBarStatic
							.checkSelectedFixObject((GeoElement) geos[0]));
				}
			}
		};
		btnFixObject.addValueChangeHandler(this);
	}

	private void createTextItalicBtn() {
		if (app.isUnbundledOrWhiteboard()) {
			btnItalic = new MyToggleButtonW(new NoDragImage(
					MaterialDesignResources.INSTANCE.text_italic_black(), 24)) {

				@Override
				public void update(Object[] geos) {
					boolean geosOK = checkTextNoMedia(geos);
					super.setVisible(geosOK);
					if (geosOK) {
						GeoElement geo = ((GeoElement) geos[0])
								.getGeoElementForPropertiesDialog();
						int style = ((TextProperties) geo).getFontStyle();
						btnItalic.setValue((style & GFont.ITALIC) != 0);
					}
				}
			};
		} else {
			btnItalic = new MyToggleButtonW(loc.getMenu("Italic.Short")) {

				@Override
				public void update(Object[] geos) {

					boolean geosOK = checkGeoText(geos)
							&& !((GeoElement) geos[0]).isGeoInputBox();
					super.setVisible(geosOK);
					if (geosOK) {
						GeoElement geo = ((GeoElement) geos[0])
								.getGeoElementForPropertiesDialog();
						int style = ((TextProperties) geo).getFontStyle();
						btnItalic.setValue((style & GFont.ITALIC) != 0);
					}
				}
			};
		}
		btnItalic.addStyleName("btnItalic");
		btnItalic.addValueChangeHandler(this);
	}

	private void createTextSizeBtn() {
		// ========================================
		// text size button
		ImageOrText[] textSizeArray = ImageOrText
				.convert(app.getLocalization().getFontSizeStrings());

		btnTextSize = new PopupMenuButtonW(app, textSizeArray, -1, 1,
				SelectionTable.MODE_TEXT, false) {

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos)
						&& !((GeoElement) geos[0]).isGeoAudio()
						&& !((GeoElement) geos[0]).isGeoVideo();
				super.setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();
					setSelectedIndex(GeoText.getFontSizeIndex(
							((TextProperties) geo).getFontSizeMultiplier())); // font
																				// size
																				// ranges
																				// from
					// -4 to 4, transform
					// this to 0,1,..,4
				}
			}
		};
		btnTextSize.addPopupHandler(this);
		btnTextSize.setKeepVisible(false);
		btnTextSize.setIcon(app.isUnbundledOrWhiteboard()
				? new ImageOrText(
						MaterialDesignResources.INSTANCE.text_size_black(), 24)
				: new ImageOrText(StyleBarResources.INSTANCE.font_size()));
		btnTextSize.addStyleName("withIcon");
		btnTextSize.getMyPopup().removeStyleName("matPopupPanel");
		btnTextSize.getMyPopup().addStyleName("textSizePopupPanel");
	}

	// =====================================================
	// Event Handlers
	// =====================================================

	@Override
	public void updateGUI() {
		if (isIniting) {
			return;
		}
		updateButtonPointCapture(ev.getPointCapturingMode());

		updateAxesAndGridGUI();
	}

	protected void updateAxesAndGridGUI() {
		btnShowAxes.removeValueChangeHandler();
		btnShowAxes.setSelected(ev.getShowXaxis());
		btnShowAxes.addValueChangeHandler(this);

		btnShowGrid.setSelectedIndex(gridIndex(ev));
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		Object source = event.getSource();
		handleEventHandlers(source);
	}

	@Override
	protected void handleEventHandlers(Object source) {
		needUndo = false;

		ArrayList<GeoElement> targetGeos = new ArrayList<>();
		targetGeos.addAll(ec.getJustCreatedGeos());
		if (!EuclidianConstants.isMoveOrSelectionMode(mode)) {
			targetGeos.addAll(defaultGeos);
			Previewable p = ev.getPreviewDrawable();
			if (p != null) {
				GeoElement geo = p.getGeoElement();
				if (geo != null) {
					targetGeos.add(geo);
				}
			}
		} else {
			targetGeos.addAll(app.getSelectionManager().getSelectedGeos());
		}

		processSource(source, targetGeos);

		if (needUndo) {
			app.storeUndoInfo();
			needUndo = false;
		}
	}

	static boolean checkGeoText(Object[] geos) {
		boolean geosOK = (geos.length > 0);
		for (int i = 0; i < geos.length; i++) {
			if (!(((GeoElement) geos[i])
					.getGeoElementForPropertiesDialog() instanceof TextProperties)) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	protected boolean processSourceForAxesAndGrid(Object source) {
		if (source == btnShowGrid) {
			if (btnShowGrid.getSelectedValue() != null) {
				setGridType(ev, btnShowGrid.getSelectedIndex());
			}
			return true;
		}
		return false;
	}

	/**
	 * process the action performed
	 * 
	 * @param source
	 *            event source
	 * @param targetGeos
	 *            selected objects
	 */
	@Override
	protected boolean processSource(Object source,
			ArrayList<GeoElement> targetGeos) {
		if ((source instanceof Widget)
				&& (EuclidianStyleBarStatic.processSourceCommon(
						getActionCommand((Widget) source), targetGeos, ev))) {
			return true;
		}

		// processes btnColor, btnLineStyle and btnPointStyle
		if (super.processSource(source, targetGeos)) {
			return true;
		}
		if (source.equals(btnChangeView)) {
			int si = btnChangeView.getSelectedIndex();
			switch (si) {
			case 0: // standard view
				setEvStandardView();
				break;
			case 1: // show all objects
				getView().setViewShowAllObjects(true, false);
				break;
			default:
				setDirection(si);
				break;
			}
		} else if (source == btnBgColor) {
			if (btnBgColor.getSelectedIndex() >= 0) {
				GColor color = btnBgColor.getSelectedColor();
				if (color == null) {
					if (app.isWhiteboardActive()) {
						openColorDialog(targetGeos, true);
					} else {
						openPropertiesForColor(true);
					}
					return false;
				}
				double alpha = btnBgColor.getSliderValue() / 100.0;
				needUndo = EuclidianStyleBarStatic.applyBgColor(targetGeos,
						color, alpha);
			}
		} else if (source == btnTextColor) { 
			if (btnTextColor.getSelectedIndex() >= 0) {
				GColor color = btnTextColor.getSelectedColor();
				if (color == null) {
					if (app.isWhiteboardActive()) {
						openColorDialog(targetGeos, false);
					} else {
						openPropertiesForColor(false);
					}
					return false;
				}
				needUndo = EuclidianStyleBarStatic.applyTextColor(targetGeos,
						color);
			}
		} else if (source == btnTextBgColor) {
			if (btnTextBgColor.getSelectedIndex() >= 0) {
				GColor color = btnTextBgColor.getSelectedColor();
				if (color == null) {
					if (app.isWhiteboardActive()) {
						openColorDialog(targetGeos, false);
					} else {
						openPropertiesForColor(false);
					}
					return false;
				}
				needUndo = EuclidianStyleBarStatic.applyTextColor(targetGeos,
						color);
			}
		} else if (source == btnFilling) {
			FillType fillType = btnFilling.getSelectedFillType();
			EuclidianStyleBarStatic.applyFillType(targetGeos, fillType);

		} else if (source == btnBold) {
			needUndo = EuclidianStyleBarStatic.applyFontStyle(targetGeos,
					GFont.ITALIC, btnBold.isDown() ? GFont.BOLD : GFont.PLAIN);
		} else if (source == btnItalic) {
			needUndo = EuclidianStyleBarStatic.applyFontStyle(targetGeos,
					GFont.BOLD,
					btnItalic.isDown() ? GFont.ITALIC : GFont.PLAIN);
		} else if (source == btnTextSize) {
			needUndo = EuclidianStyleBarStatic.applyTextSize(targetGeos,
					btnTextSize.getSelectedIndex());
		} else if (source == btnAngleInterval) {
			needUndo = EuclidianStyleBarStatic.applyAngleInterval(targetGeos,
					btnAngleInterval.getSelectedIndex());
		} else if (source == btnLabelStyle) {
			needUndo = EuclidianStyleBarStatic.applyCaptionStyle(targetGeos,
					mode, btnLabelStyle.getSelectedIndex());
		} else if (source == btnFixPosition) {
			needUndo = EuclidianStyleBarStatic.applyFixPosition(targetGeos,
					btnFixPosition.isSelected(), ev) != null;
		} else if (source == btnFixObject) {
			needUndo = EuclidianStyleBarStatic.applyFixObject(targetGeos,
					btnFixObject.isSelected(), ev) != null;
			btnFixObject.update(targetGeos.toArray());
		} else if (!processSourceForAxesAndGrid(source)) {
			for (int i = 0; i < 3; i++) {
				if (source == btnDeleteSizes[i]) {
					setDelSize(i);
					return true;
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * For 3D
	 * 
	 * @param si
	 *            direction
	 */
	protected void setDirection(int si) {
		// nothing to do here
	}

	/**
	 * Update grid type.
	 * 
	 * @param ev
	 *            view
	 * @param val
	 *            grid type
	 */
	public static void setGridType(EuclidianView ev, int val) {
		EuclidianSettings evs = ev.getSettings();
		boolean gridChanged = false;
		if (val == 0) {
			gridChanged = evs.showGrid(false);
		} else {
			evs.beginBatch();
			gridChanged = evs.showGrid(true);
			switch (val) {
			case 2:
				evs.setGridType(EuclidianView.GRID_POLAR);
				break;
			case 3:
				evs.setGridType(EuclidianView.GRID_ISOMETRIC);
				break;
			default:
				evs.setGridType(EuclidianView.GRID_CARTESIAN_WITH_SUBGRID);
			}
			evs.endBatch();
		}
		if (gridChanged) {
			ev.getApplication().storeUndoInfo();
		}
	}

	/**
	 * Update axes style of a view.
	 * 
	 * @param ev
	 *            view
	 * @param val
	 *            axes style
	 */
	public static void setAxesLineType(EuclidianView ev, int val) {
		EuclidianSettings evs = ev.getSettings();
		boolean axesChanged = false;
		if (val == 0) {
			axesChanged = evs.setShowAxes(false);
		} else {
			evs.beginBatch();
			axesChanged = evs.setShowAxes(true);
			switch (val) {
			case 2:
				evs.setAxesLineStyle(
						EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS_FILLED);
				break;
			case 3:
				evs.setAxesLineStyle(
						EuclidianStyleConstants.AXES_LINE_TYPE_FULL);
				break;
			default:
				evs.setAxesLineStyle(
						EuclidianStyleConstants.AXES_LINE_TYPE_ARROW_FILLED);
			}
			evs.endBatch();
		}
		if (axesChanged) {
			ev.getApplication().storeUndoInfo();
		}
	}

	private void setDelSize(int s) {
		ev.getSettings().setDeleteToolSize(EuclidianSettings.DELETE_SIZES[s]);
		for (int i = 0; i < 3; i++) {
			btnDeleteSizes[i].setDown(i == s);
			btnDeleteSizes[i].setEnabled(i != s);
		}
	}

	@Override
	public int getPointCaptureSelectedIndex() {
		return btnPointCapture.getSelectedIndex();
	}

	protected void setActionCommand(Widget widget, String actionCommand) {
		widget.getElement().setAttribute("actionCommand", actionCommand);
	}

	private static String getActionCommand(Widget widget) {
		return widget.getElement().getAttribute("actionCommand");
	}

	/**
	 * Get index of selected grid type icon for a view.
	 * 
	 * @param ev
	 *            view
	 * @return which icon should be selected
	 */
	public static int gridIndex(EuclidianView ev) {
		if (!ev.getShowGrid()) {
			return 0;
		}
		if (ev.getGridType() == EuclidianView.GRID_POLAR) {
			return 2;
		}
		if (ev.getGridType() == EuclidianView.GRID_ISOMETRIC) {
			return 3;
		}
		return 1;
	}

	/**
	 * @param ev
	 *            view
	 * @return current axis type
	 */
	public static int axesIndex(EuclidianView ev) {
		if (!ev.getShowAxis(0) && !ev.getShowAxis(1)) {
			return 0;
		}
		int type;
		switch (ev.getAxesLineStyle()) {
		case EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS:
		case EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS_FILLED:
			type = 2;
			break;
		case EuclidianStyleConstants.AXES_LINE_TYPE_FULL:
			type = 3;
			break;

		// EuclidianStyleConstants.AXES_LINE_TYPE_ARROW,
		// EuclidianStyleConstants.AXES_LINE_TYPE_ARROW_FILLED,...
		default:
			type = 1;
		}
		return type;
	}

	@Override
	public void hidePopups() {
		if (EuclidianStyleBarW.getCurrentPopup() != null) {
			EuclidianStyleBarW.getCurrentPopup().hide();
		}
	}

	@Override
	public void resetFirstPaint() {
		firstPaint = true;
	}

	@Override
	public void onAttach() {
		if (firstPaint) {
			firstPaint = false;
			updateGUI();
		}
		super.onAttach();
	}

	public PointStylePopup getBtnPointStyle() {
		return btnPointStyle;
	}

	@Override
	public void setLabels() {
		super.setLabels();
		// set labels for popups
		this.btnPointCapture.getMyTable()
				.updateText(ImageOrText.convert(new String[] {
						loc.getMenu("Labeling.automatic"),
						loc.getMenu("SnapToGrid"), loc.getMenu("FixedToGrid"),
						loc.getMenu("off") }));
		this.btnLabelStyle.getMyTable()
				.updateText(ImageOrText
						.convert(new String[] { loc.getMenu("stylebar.Hidden"), // index
																				// 4
								loc.getMenu("Name"), // index 0
								loc.getMenu("NameAndValue"), // index 1
								loc.getMenu("Value"), // index 2
								loc.getMenu("Caption") // index 3
		}));

		String[] angleIntervalArray = new String[GeoAngle
				.getIntervalMinListLength() - 1];
		for (int i = 0; i < GeoAngle.getIntervalMinListLength() - 1; i++) {
			angleIntervalArray[i] = app.getLocalization().getPlain(
					"AngleBetweenAB.short", GeoAngle.getIntervalMinList(i),
					GeoAngle.getIntervalMaxList(i));
		}

		this.btnAngleInterval.getMyTable()
				.updateText(ImageOrText.convert(angleIntervalArray));

		this.btnTextSize.getMyTable().updateText(ImageOrText
				.convert(app.getLocalization().getFontSizeStrings()));

		// set labels for buttons with text e.g. button "bold" or "italic"
		this.btnBold.getDownFace().setText(loc.getMenu("Bold.Short"));
		this.btnItalic.getDownFace().setText(loc.getMenu("Italic.Short"));
		this.btnBold.getUpFace().setText(loc.getMenu("Bold.Short"));
		this.btnItalic.getUpFace().setText(loc.getMenu("Italic.Short"));
		getLabelPopup().setLabels();
		btnLineStyle.setLabels();
		btnColor.setLabels();
		if (btnCrop != null) {
			btnCrop.setText(loc.getMenu("stylebar.Crop"));
		}
		// set labels for ToolTips
		setToolTips();
	}

	protected void setAxesAndGridToolTips(Localization loc) {
		btnShowGrid.setToolTipText(loc.getPlainTooltip("stylebar.Grid"));
		btnShowAxes.setToolTipText(loc.getPlainTooltip("stylebar.Axes"));
	}

	/**
	 * set tool tips
	 */
	protected void setToolTips() {
		if (app.isUnbundled()) {
			return;
		}
		setAxesAndGridToolTips(loc);
		setToolTipText(btnStandardView, "stylebar.ViewDefault");
		setToolTipText(btnLabelStyle, "stylebar.Label");
		setToolTipText(btnAngleInterval, "AngleBetween");
		setToolTipText(btnColor, "stylebar.Color");
		setToolTipText(btnBgColor, "stylebar.BgColor");
		setToolTipText(btnLineStyle, "stylebar.LineStyle");
		setToolTipText(btnPointStyle, "stylebar.PointStyle");
		setToolTipText(btnFilling, "stylebar.Filling");
		setToolTipText(btnTextBgColor, "stylebar.TextColor");
		setToolTipText(btnTextSize, "stylebar.TextSize");
		setToolTipText(btnBold, "stylebar.Bold");
		setToolTipText(btnItalic, "stylebar.Italic");
		setToolTipText(btnPointCapture, "stylebar.Capture");
		setToolTipText(btnBold, "stylebar.Bold");
		setToolTipText(btnItalic, "stylebar.Italic");
		setToolTipText(btnFixPosition, "AbsoluteScreenLocation");
		setToolTipText(btnFixObject, "FixObject");
		setToolTipText(btnTextColor, "stylebar.Color");
	}

	private void setToolTipText(MyCJButton btn, String key) {
		if (btn != null) {
			btn.setToolTipText(loc.getPlainTooltip(key));
		}
	}

	private void setToolTipText(MyToggleButtonW btn, String key) {
		if (btn != null) {
			btn.setToolTipText(loc.getPlainTooltip(key));
		}
	}

	@Override
	public void reinit() {
		// nothing to do here
	}

	public static ButtonPopupMenu getCurrentPopup() {
		return currentPopup;
	}

	public static void setCurrentPopup(ButtonPopupMenu currentPopup) {
		EuclidianStyleBarW.currentPopup = currentPopup;
	}

	public static PopupMenuButtonW getCurrentPopupButton() {
		return currentPopupBtn;
	}

	public static void setCurrentPopupButton(PopupMenuButtonW currentPopupBtn) {
		EuclidianStyleBarW.currentPopupBtn = currentPopupBtn;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
		super.setVisible(visible);
		if (btnContextMenu != null) {
			btnContextMenu.close();
		}
	}

	@Override
	public boolean isVisible() {
		return visible;
	}
}
