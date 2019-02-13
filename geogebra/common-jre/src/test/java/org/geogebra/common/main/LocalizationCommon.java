package org.geogebra.common.main;

import org.geogebra.common.jre.main.LocalizationJre;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Common Localization class, used for testing.
 */
public class LocalizationCommon extends LocalizationJre {

    /**
     * Construct a LocalizationCommon object.
     *
     * @param dimension the dimension of the app
     */
    LocalizationCommon(int dimension) {
        super(dimension);
    }

    @Override
    protected ResourceBundle createBundle(String key, Locale locale) {
        return null;
    }

    @Override
    protected String getMenuRessourcePath() {
        return null;
    }

    @Override
    protected String getCommandRessourcePath() {
        return null;
    }

    @Override
    protected String getColorRessourcePath() {
        return null;
    }

    @Override
    protected String getErrorRessourcePath() {
        return null;
    }

    @Override
    protected String getSymbolRessourcePath() {
        return null;
    }
}
