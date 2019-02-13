package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Drawable for symbolic geos
 */
public class DrawSymbolic extends Drawable {

	private GeoSymbolic symbolic;
	private DrawableND twinDrawable;

	/**
	 * @param ev
	 *            view
	 * @param geo
	 *            symbolic geo
	 */
	public DrawSymbolic(EuclidianView ev, GeoSymbolic geo) {
		this.view = ev;
		this.geo = geo;
		this.symbolic = geo;
		update();
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void update() {
		GeoElementND twinGeo = symbolic.getTwinGeo();
		if (twinGeo == null) {
			twinDrawable = null;
		} else if (twinDrawable != null
				&& twinDrawable.getGeoElement() == twinGeo) {
			twinDrawable.update();
		} else {
			twinDrawable = view.newDrawable(symbolic.getTwinGeo());
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (twinDrawable != null && geo.isEuclidianVisible()) {
			((Drawable) twinDrawable).draw(g2);
		}

	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		if (twinDrawable != null) {
			return ((Drawable) twinDrawable).hit(x, y, hitThreshold);
		}
		return false;
	}

	@Override
	public boolean isInside(GRectangle rect) {
		if (twinDrawable != null) {
			return ((Drawable) twinDrawable).isInside(rect);
		}
		return false;
	}

	@Override
	public BoundingBox getBoundingBox() {
		// TODO Auto-generated method stub
		return null;
	}

}
