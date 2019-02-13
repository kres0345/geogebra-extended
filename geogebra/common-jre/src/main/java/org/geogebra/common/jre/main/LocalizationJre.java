package org.geogebra.common.jre.main;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;

/**
 * common jre localization
 */
public abstract class LocalizationJre extends Localization {

	private ResourceBundle rbmenu;
	private ResourceBundle rbmenuTT;
	private ResourceBundle rbcommand;
	private ResourceBundle rbcommandOld;
	private ResourceBundle rberror;
	private ResourceBundle rbcolors;
	private ResourceBundle rbsymbol;

	private Locale tooltipLocale = null;
	/** application */
	protected App app;
	private boolean tooltipFlag = false;
	// supported GUI languages (from properties files)
	protected ArrayList<Locale> supportedLocales = null;

	/**
	 * @param dimension
	 *            3 for 3D
	 */
	public LocalizationJre(int dimension) {
		this(dimension, 15);
	}

	/**
	 * @param dimension
	 *            3 for 3D, 2 otherwise
	 * @param maxFigures
	 *            maximum digits
	 */
	public LocalizationJre(int dimension, int maxFigures) {
		super(dimension, maxFigures);
	}

	/**
	 * @param app
	 *            application
	 */
	final public void setApp(App app) {
		this.app = app;
	}

	@Override
	final public void setTooltipFlag() {
		if (tooltipLocale != null) {
			tooltipFlag = true;
		}
	}

	/**
	 * Stop forcing usage of tooltip locale for translations
	 */
	@Override
	final public void clearTooltipFlag() {
		tooltipFlag = false;
	}

	@Override
	final public String getCommand(String key) {

		app.initTranslatedCommands();

		try {
			return rbcommand.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	@Override
	final public String getMenu(String key) {

		if (tooltipFlag) {
			return getMenuTooltip(key);
		}

		if (rbmenu == null) {
			rbmenu = createBundle(getMenuRessourcePath(), currentLocale);
		}

		try {
			return rbmenu.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	/**
	 * 
	 * @param key
	 *            key name
	 * @param locale
	 *            locale
	 * @return bundle for key & locale
	 */
	abstract protected ResourceBundle createBundle(String key, Locale locale);

	/** @return path of Menu bundle */
	abstract protected String getMenuRessourcePath();

	/** @return path of Command bundle */
	abstract protected String getCommandRessourcePath();

	/** @return path of Color bundle */
	abstract protected String getColorRessourcePath();

	/** @return path of Error bundle */
	abstract protected String getErrorRessourcePath();

	/** @return path of Symbol bundle */
	abstract protected String getSymbolRessourcePath();

	@Override
	final public String getMenuTooltip(String key) {

		if (tooltipLocale == null) {
			return getMenu(key);
		}

		if (rbmenuTT == null) {
			rbmenuTT = createBundle(getMenuRessourcePath(), tooltipLocale);
		}

		try {
			return rbmenuTT.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	@Override
	final public String getError(String key) {
		if (rberror == null) {
			rberror = createBundle(getErrorRessourcePath(), currentLocale);
		}

		try {
			return rberror.getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	@Override
	final public String getSymbol(int key) {
		if (rbsymbol == null) {
			initSymbolResourceBundle();
		}

		String ret = null;

		try {
			ret = rbsymbol.getString("S." + key);
		} catch (Exception e) {
			// do nothing
		}

		if ("".equals(ret)) {
			return null;
		}
		return ret;
	}

	@Override
	final public String getLanguage() {
		return getLocale().getLanguage();
	}

	@Override
	final public String getLocaleStr() {
		return getLocale().toString();
	}

	@Override
	final public String getSymbolTooltip(int key) {
		if (rbsymbol == null) {
			initSymbolResourceBundle();
		}

		String ret = null;

		try {
			ret = rbsymbol.getString("T." + key);
		} catch (Exception e) {
			// do nothing
		}

		if ("".equals(ret)) {
			return null;
		}
		return ret;
	}

	private void initSymbolResourceBundle() {
		rbsymbol = createBundle(getSymbolRessourcePath(), currentLocale);
	}

	@Override
	final public void initCommand() {
		if (rbcommand == null) {
			rbcommand = createBundle(getCommandRessourcePath(), getCommandLocale());
		}

	}

	private void initColorsResourceBundle() {
		rbcolors = createBundle(getColorRessourcePath(), currentLocale);
	}

	@Override
	final protected void updateResourceBundles() {
		if (rbmenu != null) {
			rbmenu = createBundle(getMenuRessourcePath(), currentLocale);
		}
		if (rberror != null) {
			rberror = createBundle(getErrorRessourcePath(), currentLocale);
		}

		if (rbcommand != null) {
			rbcommand = createBundle(getCommandRessourcePath(), getCommandLocale());
		}
		if (rbcolors != null) {
			rbcolors = createBundle(getColorRessourcePath(), currentLocale);
		}
		if (rbsymbol != null) {
			rbsymbol = createBundle(getSymbolRessourcePath(), currentLocale);
		}
	}

	/**
	 * @return whether properties bundles were initiated (at least plain)
	 */
	final public boolean propertiesFilesPresent() {
		return rbmenu != null;
	}

	/**
	 * @param s
	 *            language for tooltips
	 * @return success
	 */
	final public boolean setTooltipLanguage(String s) {
		Locale locale = null;

		for (int i = 0; i < getSupportedLocales().size(); i++) {
			if (getSupportedLocales().get(i).toString().equals(s)) {
				locale = getSupportedLocales().get(i);
				break;
			}
		}

		boolean updateNeeded = (rbmenuTT != null);

		rbmenuTT = null;

		if (locale == null) {
			tooltipLocale = null;
		} else if (currentLocale.toString().equals(locale.toString())) {
			tooltipLocale = null;
		} else {
			tooltipLocale = locale;
		}
		return updateNeeded;
	}

	/**
	 * @return tootlip loacle
	 */
	final public Locale getTooltipLocale() {
		return tooltipLocale;
	}

	@Override
	final public String getTooltipLanguageString() {
		if (tooltipLocale == null) {
			return null;
		}
		return tooltipLocale.toString();
	}

	@Override
	final public String getColor(String key) {

		if (key == null) {
			return "";
		}

		if ((key.length() == 5)
				&& StringUtil.toLowerCaseUS(key).startsWith("gray")) {
			return StringUtil.getGrayString(key.charAt(4), this);
		}

		if (rbcolors == null) {
			initColorsResourceBundle();
		}

		try {
			return rbcolors.getString(StringUtil.toLowerCaseUS(key));
		} catch (Exception e) {
			return key;
		}
	}

	@Override
	final public String reverseGetColor(String locColor) {
		String str = StringUtil.removeSpaces(StringUtil.toLowerCaseUS(locColor));
		if (rbcolors == null) {
			initColorsResourceBundle();
		}

		try {

			Enumeration<String> enumer = rbcolors.getKeys();
			while (enumer.hasMoreElements()) {
				String key = enumer.nextElement();
				if (str.equals(StringUtil.removeSpaces(
						StringUtil.toLowerCaseUS(rbcolors.getString(key))))) {
					return key;
				}
			}

			return str;
		} catch (Exception e) {
			return str;
		}
	}

	@Override
	protected ArrayList<Locale> getSupportedLocales() {
		return getSupportedLocales(app.has(Feature.ALL_LANGUAGES));
	}

	@Override
	public ArrayList<Locale> getSupportedLocales(boolean prerelease) {
		if (supportedLocales == null) {
			supportedLocales = super.getSupportedLocales(prerelease);
		}
		return supportedLocales;
	}

	@Override
	final protected boolean isCommandChanged() {
		return rbcommandOld != rbcommand;
	}

	@Override
	final protected void setCommandChanged(boolean b) {
		rbcommandOld = rbcommand;

	}

	@Override
	final protected boolean isCommandNull() {
		return rbcommand == null;
	}

	@Override
	final protected String getLanguage(Locale locale) {
		return locale.getLanguage();
	}

	@Override
	final protected String getCountry(Locale locale) {
		return locale.getCountry();
	}

	@Override
	protected String getVariant(Locale locale) {
		return locale.getVariant();
	}

	@Override
	protected Locale createLocale(String language, String country) {
		return new Locale(language, country);
	}
}
