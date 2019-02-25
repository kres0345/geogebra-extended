package org.geogebra.web.html5.main;

import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.Browser;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window.Location;

public class UserPreferredLanguage {
	private static final String DATA_TRANS_KEY = "data-trans-key";

	/**
	 * Gets user preferred language in a specific order
	 * 
	 * @param app {@link AppW}
	 * @return the preferred language.
	 */
	public static String get(AppW app) {
		LogInOperation op = app.getLoginOperation();
		boolean loggedIn = op != null && op.isLoggedIn();
		if (loggedIn) {
			String userLang = op.getUserLanguage();
			if (!StringUtil.empty(userLang)) {
				return userLang;
			}
		}

		String cookieLang = Cookies.getCookie("GeoGebraLangUI");
		if (!StringUtil.empty(cookieLang)) {
			return cookieLang;
		}

		Storage localStorage = Storage.getLocalStorageIfSupported();
		if (localStorage != null) {
			String storageLang = localStorage.getItem("GeoGebraLangUI");
			if (!StringUtil.empty(storageLang)) {
				return storageLang;
			}
		}


		String urlLang = app.getArticleElement().getDataParamApp() ? Location.getParameter("lang") : "";

		if (!StringUtil.empty(urlLang) && !loggedIn) {
			return urlLang;
		}

		return Browser.navigatorLanguage();
	}
	
	/**
	 * Translates an element recursively using data-trans-key attribute.
	 * 
	 * @param app  {@link AppW}
	 * @param elem HTML element to translate.
	 */
	public static void translate(AppW app, Element elem) {
		for (int i = 0; i < elem.getChildCount(); i++) {
			Node child = elem.getChild(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) child;
				if (e.hasAttribute(DATA_TRANS_KEY)) {
					e.setInnerText(app.getLocalization().getMenu(e.getAttribute(DATA_TRANS_KEY)));
				} else {
					translate(app, e);
				}
			}
		}
	}
}
