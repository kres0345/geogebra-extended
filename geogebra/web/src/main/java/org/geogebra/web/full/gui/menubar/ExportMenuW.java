package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.geogebra3D.euclidian3D.printer3D.FormatJscad;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.FormatSTL;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.HTML5Export;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.web.full.gui.dialog.ExportImageDialog;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author bencze The "Export Image" menu, part of the "File" menu.
 */
public class ExportMenuW extends AriaMenuBar implements MenuBarI {

	/**
	 * Constructs the "Insert Image" menu
	 * 
	 * @param app
	 *            Application instance
	 */
	public ExportMenuW(AppW app) {
		super();

		addStyleName("GeoGebraMenuBar");
		MainMenu.addSubmenuArrow(this, app.isUnbundledOrWhiteboard());
		if (app.isUnbundled()) {
			addStyleName("floating-Popup");
		}
		initActions(this, app);
	}

	/**
	 * @param menu
	 *            menu
	 * @param app
	 *            application
	 */
	protected static void initActions(final MenuBarI menu, final AppW app) {

		menu.addItem(app.isWhiteboardActive()
				? menuText(app.getLocalization().getMenu("Slides") + " ("
						+ app.getFileExtension().substring(1) + ")")
				: menuText(app.getFileExtension().substring(1)), true,
				new MenuCommand(app) {

					@Override
					public void doExecute() {
						menu.hide();
						dialogEvent(app, "exportGGB");
						app.getFileManager().export(app);
					}
				});

		menu.addItem(app.isWhiteboardActive()
				? menuText(app.getLocalization().getMenu("Image") + " (png)")
				: menuText("png"), true, new MenuCommand(app) {

					@Override
					public void execute() {
						menu.hide();
						app.toggleMenu();
						app.getSelectionManager().clearSelectedGeos();

						String url = ExportImageDialog.getExportDataURL(app);

						app.getFileManager().showExportAsPictureDialog(url,
								app.getExportTitle(), "png", "ExportAsPicture",
								app);
						dialogEvent(app, "exportPNG");
					}
				});

		menu.addItem(
				app.isWhiteboardActive() ? menuText(
						app.getLocalization().getMenu("VectorGraphics")
								+ " (svg)")
						: menuText("svg"),
				true, new MenuCommand(app) {

					@Override
					public void execute() {
						menu.hide();
						app.toggleMenu();
						app.getSelectionManager().clearSelectedGeos();

						String svg = Browser
								.encodeSVG(((EuclidianViewWInterface) app
										.getActiveEuclidianView())
												.getExportSVG(1, false));

						app.getFileManager().showExportAsPictureDialog(svg,
								app.getExportTitle(), "svg", "ExportAsPicture",
								app);
						dialogEvent(app, "exportSVG");
					}
				});
		menu.addItem(app.isWhiteboardActive()
				? menuText(app.getLocalization().getMenu("pdf"))
				: menuText("pdf"), true, new MenuCommand(app) {

					@Override
					public void execute() {

						menu.hide();
						app.toggleMenu();
						app.getSelectionManager().clearSelectedGeos();

						String pdf = app.getGgbApi().exportPDF(1, null, null);

						app.getFileManager().showExportAsPictureDialog(pdf,
								app.getExportTitle(), "pdf", "ExportAsPicture",
								app);
						dialogEvent(app, "exportPDF");
					}
				});
		// TODO add gif back when ready
		// if (!app.getLAF().isTablet()) {
		// addItem(menuText(
		// app.getLocalization().getMenu("AnimatedGIF")), true,
		// new MenuCommand(app) {
		// @Override
		// public void doExecute() {
		// hide();
		// dialogEvent("exportGIF");
		// ((DialogManagerW) app.getDialogManager())
		// .showAnimGifExportDialog();
		// }
		// });
		// }
		if (!app.isWhiteboardActive()) {
			menu.addItem(menuText("PSTricks"), true, new MenuCommand(app) {

				@Override
				public void execute() {
					app.getActiveEuclidianView().setSelectionRectangle(null);
					app.getSelectionManager().clearSelectedGeos();

					menu.hide();
					app.getGgbApi()
							.exportPSTricks(exportCallback("Pstricks", app));
				}
			});

			menu.addItem(menuText("PGF/TikZ"), true, new MenuCommand(app) {

				@Override
				public void execute() {
					app.getActiveEuclidianView().getEuclidianController()
							.clearSelectionAndRectangle();
					menu.hide();
					app.getGgbApi().exportPGF(exportCallback("PGF", app));
				}
			});

			menu.addItem(
					menuText(app.getLocalization()
							.getMenu("ConstructionProtocol") + " ("
							+ FileExtensions.HTML + ")"),
					true, new MenuCommand(app) {
						@Override
						public void doExecute() {
							menu.hide();
							app.exportStringToFile("html",
									app.getGgbApi().exportConstruction("color",
											"name", "definition", "value"));
						}
					});

			menu.addItem(
					menuText(app.getLocalization()
							.getMenu("DynamicWorksheetAsWebpage") + " ("
							+ FileExtensions.HTML + ")"),
					true, new MenuCommand(app) {
						@Override
						public void doExecute() {
							menu.hide();
							app.exportStringToFile("html",
									HTML5Export.getFullString(app));
						}
					});

			menu.addItem(menuText("Asymptote"), true, new MenuCommand(app) {

				@Override
				public void execute() {
					app.getActiveEuclidianView().getEuclidianController()
							.clearSelectionAndRectangle();
					menu.hide();
					app.getGgbApi()
							.exportAsymptote(exportCallback("Asymptote", app));
				}
			});

			if (app.has(Feature.EXPORT_SCAD_IN_MENU) && app.is3D()) {
				menu.addItem(menuText("OpenSCAD"), true, new MenuCommand(app) {
					@Override
					public void doExecute() {
						menu.hide();
						app.setExport3D(new FormatJscad());
					}
				});
			}

			if (app.has(Feature.MOB_EXPORT_STL)) {
				menu.addItem(menuText("STL"), true, new MenuCommand(app) {
					@Override
					public void doExecute() {
						menu.hide();
						app.setExport3D(new FormatSTL());
					}
				});
			}

			if (app.has(Feature.EXPORT_COLLADA_IN_MENU) && app.is3D()) {
				menu.addItem(menuText("Collada"), true, new MenuCommand(app) {
					@Override
					public void doExecute() {
						menu.hide();
						app.exportCollada(false);
					}
				});

				menu.addItem(menuText("Collada (html)"), true,
						new MenuCommand(app) {
							@Override
							public void doExecute() {
								menu.hide();
								app.exportCollada(true);
							}
						});
			}

			if (app.has(Feature.EXPORT_OBJ_IN_MENU) && app.is3D()) {
				menu.addItem(menuText("Obj"), true, new MenuCommand(app) {
					@Override
					public void doExecute() {
						menu.hide();
						app.exportObj();
					}
				});
			}
		}
	}

	/**
	 * @param string
	 *            file type (for event logging)
	 * @param app
	 *            application
	 * @return callback for saving text export / images
	 */
	protected static AsyncOperation<String> exportCallback(final String string,
			final AppW app) {
		return new AsyncOperation<String>() {

			@Override
			public void callback(String obj) {
				String url = Browser.addTxtMarker(obj);
				app.getFileManager().showExportAsPictureDialog(url,
						app.getExportTitle(), "txt", "Export", app);
				dialogEvent(app, "export" + string);
			}
		};
	}

	private static String menuText(String string) {
		return MainMenu.getMenuBarHtmlNoIcon(string);
	}

	/**
	 * Fire dialog open event
	 * 
	 * @param app
	 *            application to receive the evt
	 * 
	 * @param string
	 *            dialog name
	 */
	protected static void dialogEvent(AppW app, String string) {
		app.dispatchEvent(new org.geogebra.common.plugin.Event(
				EventType.OPEN_DIALOG, null, string));
	}

	/** hide the submenu */
	@Override
	public void hide() {
		Widget p = getParent();
		if (p instanceof GPopupPanel) {
			((GPopupPanel) p).hide();
		}
	}
}
