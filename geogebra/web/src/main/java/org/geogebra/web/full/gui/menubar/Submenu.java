package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.resources.SVGResource;

/**
 * Menu that can be embedded in higher-level menu
 *
 * @author Zbynek
 */
public abstract class Submenu extends GMenuBar {

	/**
	 * @param menuTitle
	 *            internal title (for event logging)
	 * @param app
	 *            application
	 */
	public Submenu(String menuTitle, AppW app) {
		super(menuTitle, app);
	}

	/**
	 * @param colorfulIcons
	 *            whether to allow colorful icons (suppress opacity)
	 */
	protected void addExpandableStyleWithColor(boolean colorfulIcons) {
		if (getApp().isUnbundledOrWhiteboard()) {
			addStyleName("matStackPanel");
			if (colorfulIcons) {
				addStyleName("matStackPanelNoOpacity");
			}
		} else {
			addStyleName("GeoGebraMenuBar");
		}
	}

	/**
	 * @return icon
	 */
	public abstract SVGResource getImage();

	/**
	 * @param localization
	 *            localization
	 * @return localized title
	 */
	public String getTitle(LocalizationW localization) {
		return localization.getMenu(getTitleTranslationKey());
	}

	/**
	 * @return translation key
	 */
	protected abstract String getTitleTranslationKey();

	/**
	 * Handle click in header when no subitems are present
	 */
	public void handleHeaderClick() {
		// only for empty menus
	}

	/**
	 * @return whether dragging views should be enabled for this menu
	 */
	protected boolean isViewDraggingMenu() {
		return false;
	}

	/**
	 * Update menu when file content changes
	 */
	public void update() {
		// update actions
	}

	/**
	 * @param action
	 *            menu item action
	 */
	public void addItem(final MenuAction<Void> action) {
		addItem(MainMenu.getMenuBarHtml(action.getImage(),
				action.getTitle(getApp().getLocalization())), true,
				new MenuCommand(getApp()) { // Close

					@Override
					public void doExecute() {
						action.execute(null, (AppWFull) getApp());
					}
				});
	}

}
