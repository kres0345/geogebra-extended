package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.ExportToPrinter3D.Type;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;

/**
 * @author ggb3D
 * 
 *         Drawable for GeoCurveCartesian3D
 * 
 */
public class DrawCurve3D extends Drawable3DCurves {

	/** handle to the curve */
	private CurveEvaluable curve;
	private GeoPoint3D hittingPoint;
	private Coords project;
	private double[] lineCoords;

	private Coords boundsMin = new Coords(3);
	private Coords boundsMax = new Coords(3);

	/**
	 * @param a_view3d
	 *            the 3D view where the curve is drawn
	 * @param curve
	 *            the 3D curve to draw
	 */
	public DrawCurve3D(EuclidianView3D a_view3d, CurveEvaluable curve) {
		super(a_view3d, (GeoElement) curve);
		this.curve = curve;

	}

	@Override
	public void drawGeometry(Renderer renderer) {

		renderer.getGeometryManager().draw(getGeometryIndex());

	}

	@Override
	protected boolean updateForItSelf() {

		EuclidianView3D view = getView3D();

		Renderer renderer = view.getRenderer();

		setPackCurve(true);
		Manager manager = renderer.getGeometryManager();
		PlotterBrush brush = manager.getBrush();
		brush.start(getReusableGeometryIndex());
		brush.setThickness(getGeoElement().getLineThickness(),
				(float) view.getScale());
		brush.setAffineTexture(0f, 0f);
		brush.setLength(1f);

		boundsMin.setPositiveInfinity();
		boundsMax.setNegativeInfinity();
		manager.setBoundsRecorders(boundsMin, boundsMax);

		double min, max;
		if (curve instanceof GeoFunction) {
			if (((GeoFunction) curve).hasInterval()) {
				min = ((GeoFunction) curve).getIntervalMin();
				max = ((GeoFunction) curve).getIntervalMax();
				double minView = view.getXmin();
				double maxView = view.getXmax();
				if (min < minView) {
					min = minView;
				}
				if (max > maxView) {
					max = maxView;
				}
			} else {
				min = view.getXmin();
				max = view.getXmax();
			}
		} else {
			min = curve.getMinParameter();
			max = curve.getMaxParameter();
		}

		// Log.debug(min+","+max);

		CurvePlotter.plotCurve(curve, min, max, view, brush, false,
				CurvePlotter.Gap.MOVE_TO);

		setGeometryIndex(brush.end());
		endPacking();

		manager.setNoBoundsRecorders();

		return true;

	}

	@Override
	public void exportToPrinter3D(ExportToPrinter3D exportToPrinter3D, boolean exportSurface) {
		if (isVisible()) {
			exportToPrinter3D.export(this, Type.CURVE);
		}
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByZoom()
				|| getView3D().viewChangedByTranslate()) {
			updateForItSelf();
		}
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_PATH;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_CLIPPED_CURVES);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_CLIPPED_CURVES);
	}

	@Override
	public boolean hit(Hitting hitting) {

		if (waitForReset) { // prevent NPE
			return false;
		}

		if (hittingPoint == null) {
			hittingPoint = new GeoPoint3D(getGeoElement().getConstruction());
			project = new Coords(4);
			lineCoords = new double[2];
		}

		hittingPoint.setWillingCoords(hitting.origin);
		hittingPoint.setWillingDirection(hitting.direction);

		((Path) curve).pointChanged(hittingPoint);

		Coords closestPoint = hittingPoint.getInhomCoordsInD3();
		closestPoint.projectLine(hitting.origin, hitting.direction, project,
				lineCoords);

		// Log.debug("\n" + hitting.origin + "\nclosest point:\n" +
		// closestPoint
		// + "\nclosest point on line:\n" + project);

		// check if point on line is visible
		if (!hitting.isInsideClipping(project)) {
			return false;
		}

		double d = getView3D().getScaledDistance(project, closestPoint);
		if (d <= getGeoElement().getLineThickness() + 2) {
			double z = -lineCoords[0];
			double dz = getGeoElement().getLineThickness()
					/ getView3D().getScale();
			setZPick(z + dz, z - dz, hitting.discardPositiveHits());
			return true;
		}

		return false;

	}

	@Override
	public boolean hitForList(Hitting hitting) {
		if (hasGeoElementVisible() && getGeoElement().isPickable()) {
			return hit(hitting);
		}

		return false;
	}

	@Override
	protected void updateGeometriesColor() {
		updateGeometriesColor(false);
	}

	@Override
	protected void setGeometriesVisibility(boolean visible) {
		setGeometriesVisibilityNoSurface(visible);
	}

	@Override
	public void enlargeBounds(Coords min, Coords max, boolean reduceWhenClipped) {
		if (!Double.isNaN(boundsMin.getX())) {
			if (reduceWhenClipped) {
				reduceBounds(boundsMin, boundsMax);
			}
			enlargeBounds(min, max, boundsMin, boundsMax);
		}
	}

}
