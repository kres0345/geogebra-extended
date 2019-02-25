package org.geogebra.web.full.gui;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.menubar.MyActionListener;
import org.geogebra.common.gui.menubar.RadioButtonMenuBar;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.images.StyleBarResources;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.gui.menubar.RadioButtonMenuBarW;
import org.geogebra.web.full.javax.swing.CheckMarkSubMenu;
import org.geogebra.web.full.javax.swing.GCheckBoxMenuItem;
import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.full.javax.swing.GCollapseMenuItem;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;

/**
 * euclidian view/graphics view context menu
 */
public class ContextMenuGraphicsWindowW extends ContextMenuGeoElementW
		implements MyActionListener {

	/**
	 * x position of popup
	 */
	protected double px;
	/**
	 * y position of popup
	 */
	protected double py;
	private GCollapseMenuItem gridCollapseItem;
	private GCollapseMenuItem rulingCollapseItem;

	/**
	 * @param app
	 *            application
	 */
	protected ContextMenuGraphicsWindowW(AppW app) {
		super(app);
		if (app.isUnbundledOrWhiteboard()) {
			wrappedPopup.getPopupPanel().addStyleName("matMenu");
		}
	}

	/**
	 * @param app
	 *            application
	 * @param px
	 *            x pos of popup
	 * @param py
	 *            y pos of popup
	 */
	public ContextMenuGraphicsWindowW(AppW app, double px, double py) {
		this(app);
		this.px = px;
		this.py = py;
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		OptionType ot = OptionType.EUCLIDIAN;
		if (ev.getEuclidianViewNo() == 2) {
			ot = OptionType.EUCLIDIAN2;
			setTitle(loc.getMenu("DrawingPad2"));
		} else {
			setTitle(loc.getMenu("DrawingPad"));
		}
		if (!app.isWhiteboardActive()) {
			if (app.isUnbundled()) {
				addAxesMenuItem(1);
				addGridMenuItem();
				addSnapToGridMenuItem();
				addClearTraceMenuItem();
			} else {
				addCheckboxes();
			}
			addShowAllObjAndStandView();
		} else {
			addRulingMenuItem();
			addBackgroundMenuItem();
		}
		addMiProperties("DrawingPad", ot);
	}

	private void addCheckboxes() {
		addAxesAndGridCheckBoxes();
		addNavigationBar();
		RadioButtonMenuBar yaxisMenu = new RadioButtonMenuBarW((AppW) this.app,
				false);
		addAxesRatioItems(yaxisMenu);
		AriaMenuItem mi = new AriaMenuItem(
				loc.getMenu("xAxis") + " : " + loc.getMenu("yAxis"), true,
				(AriaMenuBar) yaxisMenu);
		mi.addStyleName("mi_no_image_new");
		if (!app.isUnbundled()) {
			wrappedPopup.addItem(mi);
		}
		if (!app.getActiveEuclidianView().isZoomable()) {
			yaxisMenu.setEnabled(false);
		}
		if (app.getActiveEuclidianView().isLockedAxesRatio()) {
			yaxisMenu.setEnabled(false);
		}
	}

	private void addRulingMenuItem() {
		String htmlString = MainMenu
				.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.grid_black()
								.getSafeUri().asString(),
						loc.getMenu("Ruling"));
		rulingCollapseItem = new GCollapseMenuItem(htmlString,
				loc.getMenu("Ruling"),
				MaterialDesignResources.INSTANCE.expand_black().getSafeUri()
						.asString(),
				MaterialDesignResources.INSTANCE.collapse_black().getSafeUri()
						.asString(),
				false, wrappedPopup);
		wrappedPopup.addItem(rulingCollapseItem);
		RulingSubmenu rulingSubMenu = new RulingSubmenu(rulingCollapseItem);
		rulingSubMenu.update();
		rulingCollapseItem.attachToParent();
	}

	private void addBackgroundMenuItem() {
		AriaMenuItem miBackgroundCol = new AriaMenuItem(
				MainMenu.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.color_black()
								.getSafeUri().asString(),
						loc.getMenu("BackgroundColor")),
				true, new Command() {

					@Override
					public void execute() {
						openColorChooser();
					}
				});
		wrappedPopup.addItem(miBackgroundCol);
	}

	/**
	 * open color chooser dialog to select graphics background
	 */
	protected void openColorChooser() {
		((DialogManagerW) (app.getDialogManager())).showColorChooserDialog(
				app.getSettings().getEuclidian(1).getBackground(),
				new ColorChangeHandler() {

					@Override
					public void onForegroundSelected() {
						// do nothing
					}

					@Override
					public void onColorChange(GColor color) {
						// change graphics background color
						app.getSettings().getEuclidian(1).setBackground(color);
					}

					@Override
					public void onClearBackground() {
						// do nothing
					}

					@Override
					public void onBarSelected() {
						// do nothing
					}

					@Override
					public void onBackgroundSelected() {
						// do nothing
					}

					@Override
					public void onAlphaChange() {
						// do nothing
					}
				});
	}

	private void addClearTraceMenuItem() {
		String imgClearTrace = MaterialDesignResources.INSTANCE.refresh_black()
				.getSafeUri().asString();
		AriaMenuItem miClearTrace = new AriaMenuItem(MainMenu.getMenuBarHtml(
				imgClearTrace, loc.getMenu("ClearTrace")), true,
				new Command() {
			        @Override
					public void execute() {
						app.refreshViews();
			        }
		        });
		wrappedPopup.addItem(miClearTrace);
	}

	private void addShowAllObjAndStandView() {
		String img;
		if (app.isUnbundledOrWhiteboard()) {
			img = MaterialDesignResources.INSTANCE.show_all_objects_black()
					.getSafeUri().asString();
		} else {
			img = AppResources.INSTANCE.empty().getSafeUri().asString();
		}
		AriaMenuItem miShowAllObjectsView = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img, loc.getMenu("ShowAllObjects")),
				true,
				new Command() {

			        @Override
					public void execute() {
				        setViewShowAllObject();
			        }

		        });
		String img2;
		if (app.isUnbundledOrWhiteboard()) {
			img2 = MaterialDesignResources.INSTANCE.home_black().getSafeUri().asString();
		} else {
			img2 = AppResources.INSTANCE.empty().getSafeUri().asString();
		}
		AriaMenuItem miStandardView = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img2, loc.getMenu("StandardView")),
				true,
				new Command() {

			        @Override
					public void execute() {
				        setStandardView();
			        }
				});
		if (!app.getActiveEuclidianView().isZoomable()) {
			miShowAllObjectsView.setEnabled(false);
			miStandardView.setEnabled(false);
		}
		if (!app.isUnbundledOrWhiteboard()) {
			addZoomMenu();
		}
		wrappedPopup.addItem(miShowAllObjectsView);
		if (!app.isUnbundledOrWhiteboard()) {
			wrappedPopup.addItem(miStandardView);
		}
	}

	private void addGridMenuItem() {
		String htmlString = MainMenu
				.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.grid_black()
								.getSafeUri().asString(),
						loc.getMenu("ShowGrid"));
		gridCollapseItem = new GCollapseMenuItem(htmlString,
				loc.getMenu("ShowGrid"),
				MaterialDesignResources.INSTANCE.expand_black().getSafeUri()
						.asString(),
				MaterialDesignResources.INSTANCE.collapse_black().getSafeUri()
						.asString(),
				false, wrappedPopup);
		wrappedPopup.addItem(gridCollapseItem);
		GridSubmenu gridSubMenu = new GridSubmenu(gridCollapseItem);
		gridSubMenu.update();
		gridCollapseItem.attachToParent();
	}

	/**
	 * add snap to grid menu item
	 */
	public void addSnapToGridMenuItem() {
		String img = MaterialDesignResources.INSTANCE.snap_to_grid()
				.getSafeUri().asString();
		final boolean isSnapToGrid = EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC == app
				.getSettings().getEuclidian(1).getPointCapturingMode();
		final GCheckmarkMenuItem snapToGrid = new GCheckmarkMenuItem(
				MainMenu.getMenuBarHtml(img, loc.getMenu("SnapToGrid")),
				MaterialDesignResources.INSTANCE.check_black(), isSnapToGrid);
		snapToGrid.setCommand(new Command() {
			@Override
			public void execute() {

				app.getEuclidianView1().setPointCapturing(isSnapToGrid
						? EuclidianStyleConstants.POINT_CAPTURING_OFF
						: EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC);
				if (app.hasEuclidianView2EitherShowingOrNot(1)) {
					app.getEuclidianView2(1).setPointCapturing(isSnapToGrid
							? EuclidianStyleConstants.POINT_CAPTURING_OFF
							: EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC);
				}
				snapToGrid.setChecked(!isSnapToGrid);
				app.getGuiManager().updatePropertiesView();
				app.storeUndoInfo();
			}
		});
		wrappedPopup.addItem(snapToGrid);
	}

	/**
	 * add axes menu item with check mark
	 * 
	 * @param settingsID
	 *            id of EuclidianSettings
	 */
	protected void addAxesMenuItem(final int settingsID) {
		String img = MaterialDesignResources.INSTANCE.axes_black()
					.getSafeUri().asString();
		final GCheckmarkMenuItem showAxes = new GCheckmarkMenuItem(
				MainMenu.getMenuBarHtml(img, loc.getMenu("ShowAxes")),
				MaterialDesignResources.INSTANCE.check_black(),
				app.getSettings().getEuclidian(settingsID).getShowAxis(0)
						&& app.getSettings().getEuclidian(settingsID)
								.getShowAxis(1));
		showAxes.setCommand(new Command() {
			@Override
			public void execute() {
				boolean axisShown = app.getSettings().getEuclidian(settingsID)
						.getShowAxis(0)
						&& app.getSettings().getEuclidian(settingsID)
								.getShowAxis(1);
				app.getSettings().getEuclidian(settingsID)
						.setShowAxes(!axisShown);
				showAxes.setChecked(!axisShown);
				app.getActiveEuclidianView().repaintView();
				app.storeUndoInfo();
			}
		});
		wrappedPopup.addItem(showAxes);
	}

	/**
	 * show/hide construction protocol navigation
	 */
	void toggleShowConstructionProtocolNavigation() {
		((AppW) app).toggleShowConstructionProtocolNavigation(app
				.getActiveEuclidianView().getViewID());
	}

	/**
	 * @param name
	 *            title
	 * @param type
	 *            of option
	 */
	protected void addMiProperties(String name, final OptionType type) {
		String img;
		if (app.isUnbundledOrWhiteboard()) {
			img = MaterialDesignResources.INSTANCE.gear().getSafeUri()
					.asString();
		} else {
			img = AppResources.INSTANCE.view_properties16().getSafeUri().asString();
		}

		AriaMenuItem miProperties = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img,
						app.isUnbundledOrWhiteboard()
						? loc.getMenu("Settings")
						: loc.getMenu(name) + " ..."),
				true,
		        new Command() {

			        @Override
					public void execute() {
						showOptionsDialog(type);
			        }
		        });
		miProperties.setEnabled(true); // TMP AG
		wrappedPopup.addItem(miProperties);
	}

	/**
	 * @param type
	 *            of option
	 */
	protected void showOptionsDialog(OptionType type) {
		if (app.getGuiManager() != null) {
			app.getDialogManager().showPropertiesDialog(type, null);
		}
	}

	/**
	 * set standard view
	 */
	protected void setStandardView() {
		app.setStandardView();
	}

	/**
	 * set show all objects
	 */
	public void setViewShowAllObject() {
		app.setViewShowAllObjects(false);
	}

	private void addAxesRatioItems(RadioButtonMenuBar menu) {
		double scaleRatio = app.getActiveEuclidianView()
		        .getScaleRatio();
		String[] items = new String[axesRatios.length + 2];
		String[] actionCommands = new String[axesRatios.length + 2];
		boolean separatorAdded = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0, j = 0; i < axesRatios.length; i++, j++) {
			// build text like "1 : 2"
			sb.setLength(0);
			if (axesRatios[i] > 1.0) {
				sb.append((int) axesRatios[i]);
				sb.append(" : 1");
				if (!separatorAdded) {
					// ((MenuBar) menu).addSeparator();
					actionCommands[j] = "0.0";
					items[j++] = "---";
					separatorAdded = true;
				}
			} else { // factor
				if (axesRatios[i] == 1) {
					// ((MenuBar) menu).addSeparator();
					actionCommands[j] = "0.0";
					items[j++] = "---";
				}
				sb.append("1 : ");
				sb.append((int) (1.0 / axesRatios[i]));
			}
			items[j] = sb.toString();
			actionCommands[j] = "" + axesRatios[i];
		}
		int selPos = 0;
		while ((selPos < actionCommands.length)
		        && !DoubleUtil.isEqual(Double.parseDouble(actionCommands[selPos]),
		                scaleRatio)) {
			selPos++;
		}
		menu.addRadioButtonMenuItems(this, items, actionCommands, selPos, false);
	}

	/**
	 * @param axesRatio
	 *            ratio of y axis
	 */
	protected void zoomYaxis(double axesRatio) {
		app.zoomAxesRatio(axesRatio);
	}

	/**
	 * app zoom menu
	 */
	protected void addZoomMenu() {
		// zoom for both axes
		AriaMenuBar zoomMenu = new AriaMenuBar();
		String img;
		if (app.isUnbundledOrWhiteboard()) {
			img = MaterialDesignResources.INSTANCE.zoom_in_black().getSafeUri()
					.asString();
		} else {
			img = AppResources.INSTANCE.zoom16().getSafeUri().asString();
		}
		AriaMenuItem zoomMenuItem = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img,
				loc.getMenu("Zoom")), true, zoomMenu);
		if (!app.isUnbundledOrWhiteboard()) {
			zoomMenuItem.addStyleName("mi_with_image");
		}
		wrappedPopup.addItem(zoomMenuItem);
		addZoomItems(zoomMenu);
		if (!app.getActiveEuclidianView().isZoomable()) {
			zoomMenuItem.setEnabled(false);
		}
	}

	private void addZoomItems(AriaMenuBar menu) {
		int perc;
		AriaMenuItem mi;
		boolean separatorAdded = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getZoomFactorLength(); i++) {
			perc = (int) (getZoomFactor(i) * 100.0);
			// build text like "125%" or "75%"
			sb.setLength(0);
			if ((perc <= 100) && (!separatorAdded)) {
				menu.addSeparator();
				separatorAdded = true;
			}
			sb.append(perc);
			sb.append('%');
			final int index = i;
			// TODO: it is terrible, should be used ONE listener for each
			// menuItem, this kills the memory, if GWT changes this
			// get it right!
			mi = new AriaMenuItem(sb.toString(), false, new Command() {
				@Override
				public void execute() {
					zoom(getZoomFactor(index));
				}
			});
			menu.addItem(mi);
			if (app.isUnbundledOrWhiteboard()) {
				mi.addStyleName("no-image");
			}
		}
	}

	/**
	 * @param zoomFactor
	 *            zoom fasctor
	 */
	protected void zoom(double zoomFactor) {
		app.zoom(px, py, zoomFactor);
	}

	/**
	 * add axes and grid menu items with checkboxes
	 */
	protected void addAxesAndGridCheckBoxes() {
		if (app.getGuiManager() == null) {
			return;
		}
		String img;
		if (app.isUnbundledOrWhiteboard()) {
			img = AppResources.INSTANCE.axes20().getSafeUri().asString();
		} else {
			img = StyleBarResources.INSTANCE.axes().getSafeUri().asString();
		}
		String htmlString = MainMenu.getMenuBarHtml(img, loc.getMenu("Axes"));
		if (!app.isUnbundledOrWhiteboard()) {
			GCheckBoxMenuItem cbMenuItem = new GCheckBoxMenuItem(htmlString,
					((AppW) app).getGuiManager().getShowAxesAction(), true,
					app);
			cbMenuItem.setSelected(app.getActiveEuclidianView().getShowXaxis()
					&& (app.getActiveEuclidianView().getShowYaxis()),
					wrappedPopup.getPopupMenu());
			wrappedPopup.addItem(cbMenuItem);
		} else {
			GCheckmarkMenuItem checkmarkMenuItem = new GCheckmarkMenuItem(
				htmlString,
					MaterialDesignResources.INSTANCE.check_black(),
				true, ((AppW) app).getGuiManager().getShowAxesAction());

			checkmarkMenuItem
					.setChecked(app.getActiveEuclidianView().getShowXaxis()
		        && (app.getActiveEuclidianView().getShowYaxis()));
			wrappedPopup.addItem(checkmarkMenuItem.getMenuItem());
		}
		String img2;
		if (app.isUnbundledOrWhiteboard()) {
			img2 = AppResources.INSTANCE.grid20().getSafeUri().asString();
		} else {
			img2 = StyleBarResources.INSTANCE.grid().getSafeUri().asString();
		}
		htmlString = MainMenu.getMenuBarHtml(img2, loc.getMenu("Grid"));
		if (!app.isUnbundledOrWhiteboard()) {
			GCheckBoxMenuItem cbShowGrid = new GCheckBoxMenuItem(htmlString,
				((AppW) app).getGuiManager().getShowGridAction(), true, app);
			cbShowGrid.setSelected(app.getActiveEuclidianView().getShowGrid(),
					wrappedPopup.getPopupMenu());
			wrappedPopup.addItem(cbShowGrid);
		} else {
			GCheckmarkMenuItem checkmarkMenuItem = new GCheckmarkMenuItem(
					htmlString,
					MaterialDesignResources.INSTANCE.check_black(),
					true, ((AppW) app).getGuiManager().getShowGridAction());

			checkmarkMenuItem
					.setChecked(app.getActiveEuclidianView().getShowGrid());
			wrappedPopup.addItem(checkmarkMenuItem.getMenuItem());
		}
	}

	/**
	 * add navigation bar
	 */
	protected void addNavigationBar() {
		if (app.isUnbundledOrWhiteboard()) {
			return;
		}
		// Show construction protocol navigation bar checkbox item
		Command showConstructionStepCommand = new Command() {
			@Override
			public void execute() {
				toggleShowConstructionProtocolNavigation();
			}
		};
		String htmlString = MainMenu.getMenuBarHtml(AppResources.INSTANCE
				.empty().getSafeUri().asString(), loc.getMenu("NavigationBar"));
		GCheckBoxMenuItem cbShowConstructionStep = new GCheckBoxMenuItem(
				htmlString, showConstructionStepCommand, true, app);
		cbShowConstructionStep.setSelected(app.showConsProtNavigation(app
				.getActiveEuclidianView().getViewID()),
				wrappedPopup.getPopupMenu());
		wrappedPopup.addItem(cbShowConstructionStep);

		wrappedPopup.addSeparator();
	}

	@Override
	public void actionPerformed(String command) {
		try {
			// zoomYaxis(Double.parseDouble(e.getActionCommand()));
			zoomYaxis(Double.parseDouble(command));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void updateEditItems() {
		if (!app.isUnbundledOrWhiteboard()) {
			return;
		}
		updatePasteItem();
	}

	/**
	 * @author csilla expand/collapse submenu for major and minor grid setting
	 *
	 */
	public class GridSubmenu extends CheckMarkSubMenu {
		/**
		 * @param parentMenu
		 *            - parent menu item
		 */
		public GridSubmenu(GCollapseMenuItem parentMenu) {
			super(parentMenu);
		}

		@Override
		protected void initActions() {
			addNoGridItem();
			addGridItem("Grid.Major", EuclidianView.GRID_CARTESIAN);
			addGridItem("Grid.MajorAndMinor",
					EuclidianView.GRID_CARTESIAN_WITH_SUBGRID);
			addGridItem("Polar", EuclidianView.GRID_POLAR);
			addGridItem("Isometric", EuclidianView.GRID_ISOMETRIC);
		}

		/**
		 * @param gridType
		 *            new grid type
		 */
		protected void setGridType(int gridType) {
			app.getSettings().getEuclidian(1)
					.showGrid(
					gridType != EuclidianView.GRID_NOT_SHOWN);
			app.getSettings().getEuclidian(1).setGridType(gridType);
			app.getActiveEuclidianView().setGridType(gridType);
			app.getActiveEuclidianView().repaintView();
			app.storeUndoInfo();
			wrappedPopup.hideMenu();
		}

		private void addGridItem(String key, final int gridType) {
			String text = app.getLocalization().getMenu(key);
			boolean isSelected = app.getSettings().getEuclidian(1)
					.getGridType() == gridType
					&& app.getSettings().getEuclidian(1).getShowGrid();
			addItem(text, isSelected, new Command() {

				@Override
				public void execute() {
					setGridType(gridType);
				}
			}, false);
		}

		private void addNoGridItem() {
			String text = app.getLocalization().getMenu("Grid.No");
			boolean isSelected = !app.getSettings().getEuclidian(1)
					.getShowGrid();
			addItem(text, isSelected, new Command() {

				@Override
				public void execute() {
					setGridType(EuclidianView.GRID_NOT_SHOWN);
				}
			}, false);
		}

		@Override
		public void update() {
			// do nothing now
		}
	}

	/**
	 * expand/collapse menu item with ruling types
	 * 
	 * @author csilla
	 *
	 */
	public class RulingSubmenu extends CheckMarkSubMenu {
		/**
		 * @param parentMenu
		 *            - parent menu item
		 */
		public RulingSubmenu(GCollapseMenuItem parentMenu) {
			super(parentMenu);
		}

		@Override
		protected void initActions() {
			addRulingItem("NoRuling", BackgroundType.NONE);
			addRulingItem("Ruled", BackgroundType.RULER);
			addRulingItem("Squared5", BackgroundType.SQUARE_SMALL);
			addRulingItem("Squared1", BackgroundType.SQUARE_BIG);
			addRulingItem("Elementary12", BackgroundType.ELEMENTARY12);
			addRulingItem("Elementary12WithHouse",
					BackgroundType.ELEMENTARY12_HOUSE);
			addRulingItem("Elementary34", BackgroundType.ELEMENTARY34);
			addRulingItem("Music", BackgroundType.MUSIC);
		}

		/**
		 * @param rulingType
		 *            new ruling type (see BackgroundType)
		 */
		protected void setRulingType(BackgroundType rulingType) {
			app.getSettings().getEuclidian(1).setRulerType(rulingType.value());
			// app.getActiveEuclidianView().setGridType(gridType);
			app.getActiveEuclidianView().repaintView();
			app.storeUndoInfo();
			wrappedPopup.hideMenu();
		}

		private void addRulingItem(String key,
				final BackgroundType rulingType) {
			String text = app.getLocalization().getMenu(key);
			boolean isSelected = app.getSettings().getEuclidian(1)
					.getBackgroundType() == rulingType;
			addItem(text, isSelected, new Command() {

				@Override
				public void execute() {
					setRulingType(rulingType);
				}
			}, false);
		}

		@Override
		public void update() {
			// do nothing now
		}
	}

	/**
	 * focus menu in a deferred way.
	 */
	public void focusDeferred() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				wrappedPopup.getPopupMenu().getElement().focus();
				// first item is the header, so we need the 2nd.
				wrappedPopup.getPopupMenu().moveSelectionDown();
				wrappedPopup.getPopupMenu().moveSelectionDown();

			}
		});
	}
}
