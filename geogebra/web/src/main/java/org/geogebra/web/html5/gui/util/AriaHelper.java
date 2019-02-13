package org.geogebra.web.html5.gui.util;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.Browser;

import com.google.gwt.user.client.ui.UIObject;

/**
 * Helper class for accessibility methods
 */
public class AriaHelper {
	/**
	 * Avoid setting title (so that screen reader only reads the image alt) Set
	 * aria-label for desktop screen reader and data-title for visual tooltips.
	 * 
	 * @param ui
	 *            UI element
	 * @param title
	 *            title
	 * @param app
	 *            for feature flag
	 */
	public static void setTitle(UIObject ui, String title, App app) {
		if (app.has(Feature.TOOLTIP_DESIGN) && !Browser.isMobile()) {
			ui.getElement().setAttribute("data-title", title);
		}
		ui.getElement().removeAttribute("title");
		ui.getElement().setAttribute("aria-label", title);
	}

	/**
	 * Adds aria-hidden to given element
	 * 
	 * @param widget
	 *            widget to be hidden
	 */
	public static void hide(UIObject widget) {
		widget.getElement().setAttribute("aria-hidden", "true");
	}

	/**
	 * Adds aria-hidden to given element
	 * 
	 * @param widget
	 *            widget to be hidden
	 * @param hidden
	 *            whether to hide it
	 */
	public static void setHidden(UIObject widget, boolean hidden) {
		widget.getElement().setAttribute("aria-hidden", String.valueOf(hidden));
	}

	/**
	 * @param uiObject
	 *            element
	 * @param label
	 *            localized string
	 */
	public static void setLabel(UIObject uiObject, String label) {
		uiObject.getElement().setAttribute("aria-label", label);
	}
}
