package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesian2D;

/**
 * Draws 2D parametric surface
 * 
 * @author Zbynek
 */
public class DrawSurface extends Drawable {

	private GeoSurfaceCartesian2D surface;
	private GeneralPathClippedForCurvePlotter gp;

	/**
	 * @param ev
	 *            view
	 * @param geo
	 *            surface
	 */
	public DrawSurface(EuclidianView ev, GeoSurfaceCartesian2D geo) {
		this.view = ev;
		this.surface = geo;
		this.geo = geo;
		update();
	}

	private static class SurfaceCurve implements CurveEvaluable {

		private GeoSurfaceCartesian2D surface;
		private double val;
		private boolean fixed;

		public SurfaceCurve(GeoSurfaceCartesian2D surface, double i,
				boolean fixed) {
			this.surface = surface;
			this.val = i;
			this.fixed = fixed;
		}

		@Override
		public double getMinParameter() {
			// TODO Auto-generated method stub
			return -10;
		}

		@Override
		public double getMaxParameter() {
			// TODO Auto-generated method stub
			return -10;
		}

		@Override
		public double[] newDoubleArray() {
			// TODO Auto-generated method stub
			return new double[2];
		}

		@Override
		public double distanceMax(double[] p1, double[] p2) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void evaluateCurve(double t, double[] out) {
			double u = val, v = t;
			if (fixed) {
				u = t;
				v = val;
			}
			out[0] = surface.getFunctions()[0].evaluate(u, v);
			out[1] = surface.getFunctions()[1].evaluate(u, v);

		}

		@Override
		public double[] getDefinedInterval(double a, double b) {
			// TODO Auto-generated method stub
			return new double[] { a, b };
		}

		@Override
		public boolean getTrace() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isClosedPath() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isFunctionInX() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public GeoElement toGeoElement() {
			// TODO Auto-generated method stub
			return surface;
		}
	}

	@Override
	public void update() {
		boolean labelVisible = geo.isLabelVisible();

		if (gp == null) {
			gp = new GeneralPathClippedForCurvePlotter(view);
		}
		gp.reset();
		if (!geo.isEuclidianVisible()) {
			return;
		}
		for (double i = surface.getMinParameter(0); i <= surface
				.getMaxParameter(0); i += 1) {
			SurfaceCurve curve = new SurfaceCurve(surface, i, false);
			CurvePlotter.plotCurve(curve, surface.getMinParameter(1),
					surface.getMaxParameter(1), view, gp, labelVisible,
					CurvePlotter.Gap.MOVE_TO);
		}
		for (double i = surface.getMinParameter(1); i <= surface
				.getMaxParameter(1); i++) {
			SurfaceCurve curve = new SurfaceCurve(surface, i, true);
			CurvePlotter.plotCurve(curve, surface.getMinParameter(0),
					surface.getMaxParameter(0), view, gp, labelVisible,
				CurvePlotter.Gap.MOVE_TO);
		}

	}

	@Override
	public void draw(GGraphics2D g2) {
		g2.setPaint(getObjectColor());
		if (gp != null) {
			g2.draw(gp);
		}
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInside(GRectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public BoundingBox getBoundingBox() {
		// TODO Auto-generated method stub
		return null;
	}

}
