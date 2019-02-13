package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Helper methods for creating new AV items
 */
public class ItemFactory {
	private boolean slidersEnabled = true;

	/**
	 * 
	 * @param geo
	 *            geo element
	 * @return if geo matches to SliderTreeItem.
	 */
	public boolean matchSlider(GeoElement geo) {
		return slidersEnabled && geo instanceof GeoNumeric
				&& ((GeoNumeric) geo).isShowingExtendedAV() && geo.isSimple()
				&& MyDouble.isFinite(((GeoNumeric) geo).value);
	}

	/**
	 * @param geo
	 *            element
	 * @return if geo matches to CheckboxTreeItem.
	 */
	public static boolean matchCheckbox(GeoElement geo) {
		return geo instanceof GeoBoolean && geo.isSimple();
	}

	/**
	 * @param ob
	 *            geo element
	 * @return AV item
	 */
	public final RadioTreeItem createAVItem(final GeoElement ob) {
		RadioTreeItem ti = null;
		if (matchSlider(ob)) {
			ti = new SliderTreeItemRetex(ob);
		} else if (matchCheckbox(ob)) {
			ti = new CheckboxTreeItem(ob);
		} else if (TextTreeItem.match(ob)) {
			ti = new TextTreeItem(ob);
		} else {
			ti = new RadioTreeItem(ob);
		}
		ti.setUserObject(ob);
		ti.addStyleName("avItem");
		return ti;
	}

	/**
	 * @param slidersEnabled
	 *            whether to allow creation of sliders
	 */
	public void setSlidersEnabled(boolean slidersEnabled) {
		this.slidersEnabled = slidersEnabled;
	}

}
