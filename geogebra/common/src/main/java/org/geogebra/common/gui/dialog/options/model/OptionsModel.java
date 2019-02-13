package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;

@SuppressWarnings("deprecation")
@Deprecated
/*
* This class is not UI independent that's why it can not be used by mobile. Logic needs changes later to
* be commonly usable - like ObjectSettingsModel
* */
public abstract class OptionsModel {
	private Object[] geos; // currently selected geos

	protected final App app;

	public OptionsModel(App app) {
		this.app = app;
	}

	public Object[] getGeos() {
		return geos;
	}

	public void setGeos(Object[] geos) {
		this.geos = geos;
	}

	public Object getObjectAt(int i) {
		return geos[i];
	}

	public GeoElement getGeoAt(int i) {
		return (GeoElement) geos[i];
	}

	public int getGeosLength() {
		return geos.length;
	}

	public boolean hasGeos() {
		return (geos != null && geos.length > 0);
	}

	protected abstract boolean isValidAt(int index);

	public abstract void updateProperties();

	public boolean checkGeos() {
		boolean geosOK = true;
		for (int i = 0; i < getGeosLength(); i++) {
			if (!isValidAt(i)) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	public boolean hasPreview() {
		boolean geosOK = true;
		for (int i = 0; i < getGeosLength(); i++) {
			if (!getGeoAt(i).isGeoText()) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}

	/**
	 * Used for displaying angle properties only, if elements of a list are
	 ** angles
	 */
	public static boolean isAngleList(GeoElement geo) {
		if (geo.isGeoList()) {
			GeoClass elemType = ((GeoList) geo).getElementType();
			return (elemType == GeoClass.ANGLE || elemType == GeoClass.ANGLE3D);
		}

		return false;
	}

	public abstract PropertyListener getListener();

	public final boolean updateMPanel(Object[] geos2) {
		if (getListener() == null) {
			setGeos(geos2);
			return this.checkGeos();
		}
		return getListener().updatePanel(geos2) != null;
	}

	public void storeUndoInfo() {
		if (app != null) {
			app.getKernel().getConstruction().getUndoManager()
					.storeUndoInfo(false);
		}
	}
}
