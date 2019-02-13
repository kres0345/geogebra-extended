package org.geogebra.common.main.settings;

import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.main.AppConfigDefault;

/**
 * Config for Mebis Notes and GGB Notes
 *
 */
public class AppConfigNotes extends AppConfigDefault {

	@Override
	public String getPreferencesKey() {
		return "_notes";
	}

	@Override
	public String getForcedPerspective() {
		return Perspective.NOTES + "";
	}

}
