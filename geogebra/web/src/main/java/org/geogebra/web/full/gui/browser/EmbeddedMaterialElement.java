package org.geogebra.web.full.gui.browser;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.models.GeoGebraTubeAPIW;
import org.geogebra.web.shared.ggtapi.models.MaterialCallback;

public class EmbeddedMaterialElement extends MaterialListElement {

	public EmbeddedMaterialElement(final Material m, final AppW app,
			final boolean isLocal) {
		super(m, app, isLocal);
	}

	@Override
	public String getInsertWorksheetTitle(Material m) {
		return m.getType() == MaterialType.book ? null : "insert_worksheet";
	}

	@Override
	public void onView() {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI())
				.getItem(getMaterial().getId() + "", new MaterialCallback() {

					@Override
					public void onLoaded(List<Material> parseResponse,
							ArrayList<Chapter> meta) {
						loadNative(parseResponse.get(0).toJson().toString());
					}
				});
	}

	/**
	 * @param data
	 *            JSON material data
	 */
	protected native void loadNative(String data) /*-{
		if ($wnd.loadWorksheet) {
			$wnd.loadWorksheet(JSON.parse(data));
		}
	}-*/;

}
