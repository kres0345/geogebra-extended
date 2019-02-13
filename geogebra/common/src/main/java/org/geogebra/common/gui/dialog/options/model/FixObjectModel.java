package org.geogebra.common.gui.dialog.options.model;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

public class FixObjectModel extends BooleanOptionModel {

	public FixObjectModel(IBooleanOptionListener listener, App app) {
		super(listener, app);
	}

	@Override
	public void apply(int index, boolean value) {
		GeoElement geo = getGeoAt(index);
		geo.setFixed(value);
		geo.updateRepaint();
	}

	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		return geo.isFixable() && (!app.isExamStarted()
				|| !AlgebraItem.isFunctionOrEquationFromUser(geo));
	}

	@Override
	public boolean getValueAt(int index) {
		return getGeoAt(index).isLocked();
	}
}
