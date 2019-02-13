package org.geogebra.common.euclidian.modes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.algos.AlgoAttachCopyToView;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoLocusStroke;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;

/**
 * Delete mode controller for locus based penstrokes
 */
public class ModeDeleteLocus extends ModeDelete {
	private EuclidianView view;
	private EuclidianController ec;
	private boolean objDeleteMode = false;
	private boolean penDeleteMode = false;
	private ArrayList<GPoint2D> interPoints;
	private GRectangle rect = AwtFactory.getPrototype().newRectangle(0, 0, 100,
			100);

	/**
	 * @param view
	 *            EV
	 */
	public ModeDeleteLocus(EuclidianView view) {
		super(view);
		this.ec = view.getEuclidianController();
		this.view = view;
		this.interPoints = new ArrayList<>();
	}

	@Override
	public void handleMouseDraggedForDelete(AbstractEvent e, int deleteSize,
			boolean forceOnlyStrokes) {
		if (e == null) {
			return;
		}

		int eventX = e.getX();
		int eventY = e.getY();
		rect.setBounds(eventX - deleteSize / 2, eventY - deleteSize / 2,
				deleteSize, deleteSize);
		view.setDeletionRectangle(rect);
		view.setIntersectionHits(rect);
		Hits h = view.getHits();
		if (!this.objDeleteMode && !this.penDeleteMode) {
			updatePenDeleteMode(h);
		}
		boolean onlyStrokes = forceOnlyStrokes || this.penDeleteMode;

		// hide cursor, the new "cursor" is the deletion rectangle
		view.setCursor(EuclidianCursor.TRANSPARENT);

		Iterator<GeoElement> it = h.iterator();

		resetAlgoSet();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			// delete tool should delete the object for dragging
			// at whiteboard
			// see MOW-97
			if (view.getApplication().has(Feature.ERASER)
					&& ec.getMode() == EuclidianConstants.MODE_DELETE) {
				geo.removeOrSetUndefinedIfHasFixedDescendent();
			} else if (geo instanceof GeoLocusStroke) {
				GeoLocusStroke gps = (GeoLocusStroke) geo;

				// we need two arrays for the case that AlgoAttachCopyToView is
				// involved
				// the original points (dataPoints) are saved, but will be
				// translated
				// and everything by the algorithm so that the
				// GeoLocusStroke-output
				// holds the points which are really drawn (and should be used
				// for
				// hit detection).

				List<MyPoint> dataPoints;

				if (geo.getParentAlgorithm() != null && (geo
						.getParentAlgorithm() instanceof AlgoAttachCopyToView)) {
					AlgoElement ae = geo.getParentAlgorithm();
					for (int i = 0; i < ae.getInput().length; i++) {
						if (ae.getInput()[i] instanceof GeoLocusStroke) {
							gps = (GeoLocusStroke) ae.getInput()[i];
						}
					}
				}

				if (gps.getParentAlgorithm() != null
						&& gps.getParentAlgorithm() instanceof AlgoLocusStroke) {
					if (view.getApplication().has(Feature.MOW_PEN_SMOOTHING)) {
						dataPoints = ((AlgoLocusStroke) gps
								.getParentAlgorithm())
										.getPointsWithoutControl();
					} else {
						dataPoints = ((AlgoLocusStroke) gps
								.getParentAlgorithm()).getPoints();
					}
				} else {
					dataPoints = gps.getPoints();
				}
				boolean hasVisiblePart = false;
				if (dataPoints.size() > 0) {
					for (int i = 0; i < dataPoints.size(); i++) {
						MyPoint p = dataPoints.get(i);
						if (p.isDefined() && Math.max(
								Math.abs(eventX
										- view.toScreenCoordXd(p.getX())),
								Math.abs(eventY - view.toScreenCoordYd(
										p.getY()))) <= deleteSize / 2.0) {
							// end point of segment is in rectangle
							if ((i - 1 >= 0
									&& dataPoints.get(i - 1).isDefined())) {
								// get intersection point
								interPoints.clear();
								interPoints = getAllIntersectionPoint(
										dataPoints.get(i - 1),
										dataPoints.get(i),
											rect);
								if (!interPoints.isEmpty()
										&& interPoints.size() == 1) {
									i = handleEraserAtJoinPointOrEndOfSegments(

											dataPoints, i);

								}
								// no intersection point
								else {
									i = handleEraserAtPoint(dataPoints, i);
								}
							}
							// start point of segment is in rectangle
							else if (i - 1 >= 0
									&& !dataPoints.get(i - 1).isDefined()
									&& i + 1 < dataPoints.size()
									&& dataPoints.get(i + 1).isDefined()) {
								handleEraserAtStartPointOfSegment(
										dataPoints, i);
							}
							// handle first/last/single remained point
							else {
								handleLastFirstOrSinglePoints(dataPoints, i);
							}
						}
						// eraser is between the endpoints of segment
						else {
							if (i < dataPoints.size() - 1
									&& dataPoints.get(i).isDefined()
									&& dataPoints.get(i + 1).isDefined()) {
								i = handleEraserBetweenPointsOfSegment(
										dataPoints, i); // TODO
								// if (newDataAndRealPoint != null
								// && !newDataAndRealPoint.isEmpty()) {
								// i = i + 2;
								// }
							}
						}

						if (!hasVisiblePart && dataPoints.get(i).isDefined()) {
							hasVisiblePart = true;
						}
					}

					deleteUnnecessaryUndefPoints(dataPoints);

					updatePolyLineDataPoints(dataPoints, gps);
					if (view.getApplication().has(Feature.MOW_PEN_SMOOTHING)
							&& gps.getParentAlgorithm() != null
							&& gps.getParentAlgorithm() instanceof AlgoLocusStroke) {
						((AlgoLocusStroke) gps.getParentAlgorithm())
								.updatePointArray(dataPoints, 0,
										view.getScale(0));
					}

				} else {
					Log.debug(
							"Can't delete points on stroke: output & input sizes differ.");
				}
				if (hasVisiblePart) { // still something visible, don't delete
					it.remove(); // remove this Stroke from hits
				}
			} else {
				if (!this.penDeleteMode) {
					this.objDeleteMode = true;
				}
				if (onlyStrokes) {
					it.remove();
				}
			}
		}
		// do not delete images using eraser
		h.removeImages();
		ec.deleteAll(h);
		updateAlgoSet();
	}

	private void updateAlgoSet() {
		// TODO Auto-generated method stub

	}

	private void resetAlgoSet() {
		// TODO Auto-generated method stub

	}

	private static void deleteUnnecessaryUndefPoints(List<MyPoint> dataPoints) {
		ArrayList<MyPoint> dataPointList = new ArrayList<>(
				dataPoints.size());
		int i = 1;
		while (i < dataPoints.size()) {
			if ((!dataPoints.get(i).isDefined()
					&& !dataPoints.get(i - 1).isDefined())) {
				i++;
			} else {
				dataPointList.add(dataPoints.get(i - 1));
				i++;
			}
		}
		if (dataPoints.get(i - 1).isDefined()) {
			dataPointList.add(dataPoints.get(i - 1));
		}
		if (dataPointList.size() != dataPoints.size()) {
			dataPoints.clear();
			dataPoints.addAll(dataPointList);
		}
	}

	// add new undefined points and update old points coordinates
	private static List<MyPoint> getNewPolyLinePoints(
			List<MyPoint> dataPoints, int newSize,
			int i, int indexInter1, int indexUndef, int indexInter2,
			double[] realCoords) {
		MyPoint[] newDataPoints = Arrays.copyOf(
				dataPoints.toArray(new MyPoint[0]),
				dataPoints.size() + newSize);

		if (newSize == 1) {
			for (int j = dataPoints.size(); j > i + 1; j--) {
				newDataPoints[j + newSize - 1] = dataPoints.get(j - 1);
			}
		} else if (newSize == -1) {
			for (int j = dataPoints.size(); j > i; j--) {
				newDataPoints[j] = dataPoints.get(j - 1);
			}
		} else {
			for (int j = dataPoints.size(); j > i - newSize + 3; j--) {
				newDataPoints[j + newSize - 1] = dataPoints.get(j - 1);
			}
		}
		newDataPoints[indexInter1] = ngp(realCoords[0], realCoords[1]);
		newDataPoints[indexUndef] = ngp();
		newDataPoints[indexInter2] = ngp(realCoords[2], realCoords[3]);

		return Arrays.asList(newDataPoints);
	}

	private static MyPoint ngp() {
		return new MyPoint(Double.NaN, Double.NaN, SegmentType.LINE_TO);
	}

	private static MyPoint ngp(double d, double e) {
		return new MyPoint(d, e, SegmentType.LINE_TO);
	}

	private void updatePenDeleteMode(Hits h) {
		// if we switched to pen deletion just now, some geos may still need
		// removing
		Iterator<GeoElement> it2 = h.iterator();
		while (it2.hasNext()) {
			GeoElement geo2 = it2.next();
			if (geo2 instanceof GeoLocusStroke) {
				this.penDeleteMode = true;
			}
		}
	}

	/**
	 * @param point1
	 *            start point of segment
	 * @param point2
	 *            end point of segment
	 * @param rectangle
	 *            eraser
	 * @return intersection point with top of rectangle (if there is any)
	 */
	public GPoint2D getTopIntersectionPoint(MyPoint point1, MyPoint point2,
			GRectangle rectangle) {
		// Top line
		return getIntersectionPoint(point1, point2,
				rectangle.getX(), rectangle.getY(),
						rectangle.getX() + rectangle.getWidth(),
				rectangle.getY());
	}

	/**
	 * @param point1
	 *            start point of segment
	 * @param point2
	 *            end point of segment
	 * @param rectangle
	 *            eraser
	 * @return intersection point with bottom of rectangle (if there is any)
	 */
	public GPoint2D getBottomIntersectionPoint(MyPoint point1, MyPoint point2,
			GRectangle rectangle) {
		// Bottom line
		return getIntersectionPoint(point1, point2,
				rectangle.getX(),
						rectangle.getY() + rectangle.getHeight(),
						rectangle.getX() + rectangle.getWidth(),
				rectangle.getY() + rectangle.getHeight());
	}

	/**
	 * @param point1
	 *            start point of segment
	 * @param point2
	 *            end point of segment
	 * @param rectangle
	 *            eraser
	 * @return intersection point with left side of rectangle (if there is any)
	 */
	public GPoint2D getLeftIntersectionPoint(MyPoint point1, MyPoint point2,
			GRectangle rectangle) {
		// Left side
		return getIntersectionPoint(point1, point2,
				rectangle.getX(), rectangle.getY(),
						rectangle.getX(),
				rectangle.getY() + rectangle.getHeight());
	}

	/**
	 * @param point1
	 *            start point of segment
	 * @param point2
	 *            end point of segment
	 * @param rectangle
	 *            eraser
	 * @return intersection point with right side of rectangle (if there is any)
	 */
	public GPoint2D getRightIntersectionPoint(MyPoint point1, MyPoint point2,
			GRectangle rectangle) {
		// Right side
		return getIntersectionPoint(point1, point2,
				rectangle.getX() + rectangle.getWidth(),
						rectangle.getY(),
						rectangle.getX() + rectangle.getWidth(),
				rectangle.getY() + rectangle.getHeight());
	}

	/**
	 * method to get all intersection points of a segment with the eraser (with
	 * each side of rectangle)
	 * 
	 * @param point1
	 *            start point of segment
	 * @param point2
	 *            end point of segment
	 * @param rectangle
	 *            eraser
	 * @return list of intersection points
	 */
	public ArrayList<GPoint2D> getAllIntersectionPoint(MyPoint point1,
			MyPoint point2,
			GRectangle rectangle) {
		ArrayList<GPoint2D> interPointList = new ArrayList<>();
		// intersection points
		GPoint2D topInter = getTopIntersectionPoint(point1, point2, rectangle);
		if (topInter != null) {
			interPointList.add(topInter);
		}
		GPoint2D bottomInter = getBottomIntersectionPoint(point1, point2,
				rectangle);
		if (bottomInter != null) {
			interPointList.add(bottomInter);
		}
		GPoint2D leftInter = getLeftIntersectionPoint(point1, point2,
				rectangle);
		if (leftInter != null) {
			interPointList.add(leftInter);
		}
		GPoint2D rightInter = getRightIntersectionPoint(point1, point2,
				rectangle);
		if (rightInter != null) {
			interPointList.add(rightInter);
		}

		return interPointList;
	}

	/**
	 * method to get the intersection point of two segment (not line)
	 * 
	 * @param point1
	 *            start point of first segment
	 * @param point2
	 *            end point of first segment
	 * @param startPointX
	 *            start coord of start point of second segment
	 * @param startPointY
	 *            end coord of start point of second segment
	 * @param endPointX
	 *            start coord of end point of second segment
	 * @param endPointY
	 *            end coord of end point of second segment
	 * @return intersection point
	 */
	public GPoint2D getIntersectionPoint(MyPoint point1, MyPoint point2,
			double startPointX, double startPointY, double endPointX,
			double endPointY) {
		double x1 = view.toScreenCoordXd(point1.getX());
		double y1 = view.toScreenCoordYd(point1.getY());
		double x2 = view.toScreenCoordXd(point2.getX());
		double y2 = view.toScreenCoordYd(point2.getY());

		double x3 = startPointX;
		double y3 = startPointY;
		double x4 = endPointX;
		double y4 = endPointY;
		GPoint2D p = null;

		double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
		// are not parallel
		if (d != 0.0) {
			// coords of intersection point with line
			double xi = ((x3 - x4) * (x1 * y2 - y1 * x2)
					- (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
			double yi = ((y3 - y4) * (x1 * y2 - y1 * x2)
					- (y1 - y2) * (x3 * y4 - y3 * x4)) / d;
			// needed to get only the intersection points with segment
			// and not with line
			if (onSegment(Math.round(x1), Math.round(y1), xi, yi,
					Math.round(x2), Math.round(y2))
					&& onSegment(Math.round(x3), Math.round(y3), xi, yi,
							Math.round(x4), Math.round(y4))) {
				p = new GPoint2D.Double(xi, yi);
			}
		}
		return p;
	}

	// check if intersection point is on segment
	private static boolean onSegment(double segStartX, double segStartY,
			double interPointX, double interPointY, double segEndX,
			double segEndY) {
		return onSegmentCoord(segStartX, interPointX, segEndX)
				&& onSegmentCoord(segStartY, interPointY, segEndY);
	}

	private static boolean onSegmentCoord(double segStartX, double interPointX,
			double segEndX) {
		return (interPointX <= Math.max(segStartX, segEndX)
				&& interPointX >= Math.min(segStartX, segEndX))
				|| (segStartX == segEndX);
	}

	// check if the two intersection point is close enough
	private static boolean areClose(GPoint2D point1, GPoint2D point2) {
		double distance = Math.hypot(point1.getX() - point2.getX(),
				point1.getY() - point2.getY());
		return distance < 20;
	}

	private double[] getInterRealCoords(MyPoint point) {
		double[] coords = new double[4];

		double realX1 = view.toRealWorldCoordX(interPoints.get(0).getX());
		double realY1 = view.toRealWorldCoordY(interPoints.get(0).getY());
		double realX2 = view.toRealWorldCoordX(interPoints.get(1).getX());
		double realY2 = view.toRealWorldCoordY(interPoints.get(1).getY());

		double distance1 = Math.hypot(point.getX() - realX1,
				point.getY() - realY1);

		double distance2 = Math.hypot(point.getX() - realX2,
				point.getY() - realY2);

		// we need to decide the order of intersection points
		// in order to set correct the intersection points
		if (distance1 < distance2) {
			coords[0] = realX1;
			coords[1] = realY1;
			coords[2] = realX2;
			coords[3] = realY2;
		} else {
			coords[0] = realX2;
			coords[1] = realY2;
			coords[2] = realX1;
			coords[3] = realY1;
		}
		return coords;
	}

	@Override
	public void mousePressed(PointerEventType type) {
		this.objDeleteMode = false;
		this.penDeleteMode = false;
	}

	private static void updatePolyLineDataPoints(List<MyPoint> dataPoints,
			GeoLocusStroke gps) {
			if (gps.getParentAlgorithm() != null
					&& gps.getParentAlgorithm() instanceof AlgoLocusStroke) {
				((AlgoLocusStroke) gps.getParentAlgorithm())
						.updateFrom(dataPoints);
			gps.notifyUpdate();
			}
	}

	@Override
	public boolean process(Hits hits, boolean control,
			boolean selPreview) {
		if (hits.isEmpty() || this.penDeleteMode) {
			return false;
		}
		ec.addSelectedGeo(hits, 1, false, selPreview);
		if (ec.selGeos() == 1) {
			GeoElement[] geos = ec.getSelectedGeos();
			resetAlgoSet();
			// delete only parts of GeoLocusStroke, not the whole object
			// when eraser tool is used
			if (geos[0] instanceof GeoLocusStroke
					&& ec.getMode() == EuclidianConstants.MODE_ERASER) {
				updatePenDeleteMode(hits);
				int eventX = 0;
				int eventY = 0;
				if (ec.getMouseLoc() != null) {
					eventX = ec.getMouseLoc().getX();
					eventY = ec.getMouseLoc().getY();
					rect.setBounds(eventX - ec.getDeleteToolSize() / 2,
							eventY - ec.getDeleteToolSize() / 2,
							ec.getDeleteToolSize(), ec.getDeleteToolSize());
				} else {
					return false;
				}
				GeoLocusStroke gps = (GeoLocusStroke) geos[0];
				List<MyPoint> dataPoints;

				if (geos[0].getParentAlgorithm() != null && (geos[0]
						.getParentAlgorithm() instanceof AlgoAttachCopyToView)) {
					AlgoElement ae = geos[0].getParentAlgorithm();
					for (int i = 0; i < ae.getInput().length; i++) {
						if (ae.getInput()[i] instanceof GeoLocusStroke) {
							gps = (GeoLocusStroke) ae.getInput()[i];
						}
					}
				}
				if (gps.getParentAlgorithm() != null
						&& gps.getParentAlgorithm() instanceof AlgoLocusStroke) {
					if (view.getApplication().has(Feature.MOW_PEN_SMOOTHING)) {
						dataPoints = ((AlgoLocusStroke) gps
								.getParentAlgorithm())
										.getPointsWithoutControl();
					} else {
						dataPoints = ((AlgoLocusStroke) gps
							.getParentAlgorithm())
									.getPoints();
					}
				} else {
					dataPoints = gps.getPoints();
				}

				boolean hasVisiblePart = false;
				if (dataPoints.size() > 0) {
					for (int i = 0; i < dataPoints.size(); i++) {
						MyPoint p = dataPoints.get(i);
						if (p.isDefined() && Math.max(
								Math.abs(eventX
										- view.toScreenCoordXd(p.getX())),
								Math.abs(eventY
										- view.toScreenCoordYd(p.getX()))) <= ec
												.getDeleteToolSize() / 2.0) {
							// end point of segment is in rectangle
							if ((i - 1 >= 0
									&& dataPoints.get(i - 1).isDefined())) {
								// get intersection point
								interPoints.clear();
								interPoints = getAllIntersectionPoint(
										dataPoints.get(i - 1),
										dataPoints.get(i), rect);
								// one intersection point
								if (!interPoints.isEmpty()
										&& interPoints.size() == 1) {
									i = handleEraserAtJoinPointOrEndOfSegments(
											dataPoints, i);
								}
								// no intersection point
								else {
									i = handleEraserAtPoint(dataPoints, i);
								}
							}
							// start point of segment is in rectangle
							else if (i - 1 >= 0
									&& !dataPoints.get(i - 1).isDefined()
									&& i + 1 < dataPoints.size()
									&& dataPoints.get(i + 1).isDefined()) {
								handleEraserAtStartPointOfSegment(
										dataPoints, i);
							}
							// handle first/last/single remained point
							else {
								handleLastFirstOrSinglePoints(
										dataPoints, i);
							}
						}
						// eraser is between the points of segment
						else {
							if (i < dataPoints.size() - 1
									&&
									dataPoints.get(i).isDefined()
									&& dataPoints.get(i + 1).isDefined()) {
								i = handleEraserBetweenPointsOfSegment(
										dataPoints, i); // TODO
								// if (newDataAndRealPoint != null
								// && !newDataAndRealPoint.isEmpty()) {
								// i = i + 2;
								// }
							}
						}

						if (!hasVisiblePart && dataPoints.get(i).isDefined()) {
							hasVisiblePart = true;
						}
					}

					deleteUnnecessaryUndefPoints(dataPoints);

					updatePolyLineDataPoints(dataPoints, gps);
					if (view.getApplication().has(Feature.MOW_PEN_SMOOTHING)
							&& gps.getParentAlgorithm() != null
							&& gps.getParentAlgorithm() instanceof AlgoLocusStroke) {
						((AlgoLocusStroke) gps.getParentAlgorithm())
								.updatePointArray(dataPoints, 0, 0);
					}

				} else {
					Log.debug(
							"Can't delete points on stroke: input & output sizes differ.");
				}
				if (!hasVisiblePart) { // still something visible, don't delete
					// remove this Stroke
					geos[0].removeOrSetUndefinedIfHasFixedDescendent();
				}
				updateAlgoSet();
			}
			// delete this object
			else {
				if (!(geos[0] instanceof GeoImage)) {
					geos[0].removeOrSetUndefinedIfHasFixedDescendent();
				}
			}
			return true;
		}
		return false;
	}

	private static void handleLastFirstOrSinglePoints(List<MyPoint> dataPoints,
			int i) {
		if ((i == 0 && ((i + 1 < dataPoints.size()
				&& !dataPoints.get(i + 1).isDefined())
				|| (i + 1 == dataPoints.size())))
				|| (i - 1 >= 0 && !dataPoints.get(i - 1).isDefined()
						&& i + 1 == dataPoints.size())) {
			dataPoints.get(i).setUndefined();
		}
		// handle single remained point
		else if (i - 1 >= 0 && !dataPoints.get(i - 1).isDefined()
				&& i + 1 < dataPoints.size()
				&& !dataPoints.get(i + 1).isDefined()) {
			dataPoints.get(i).setUndefined();
		}
	}

	private void handleEraserAtStartPointOfSegment(List<MyPoint> dataPoints,
			int i) {
		// get intersection points
		interPoints.clear();
		interPoints = getAllIntersectionPoint(dataPoints.get(i),
				dataPoints.get(i + 1),
				rect);
		if (!interPoints.isEmpty() && interPoints.size() == 1) {
			double realX = view.toRealWorldCoordX(interPoints.get(0).getX());
			double realY = view.toRealWorldCoordY(interPoints.get(0).getY());
			// switch old point with intersection point
			dataPoints.get(i).setCoords(realX, realY);
		}
		// no intersection
		else if (interPoints.isEmpty()) {
			double pointX = view.toScreenCoordXd(dataPoints.get(i + 1).getX());
			double pointY = view.toScreenCoordYd(dataPoints.get(i + 1).getY());
			GPoint2D point = AwtFactory.getPrototype().newPoint2D(pointX,
					pointY);
			// if end point is also inside of the
			// rectangle
			if (rect.contains(point)) {
				// we can set point the start point at
				// undefined
				dataPoints.get(i).setUndefined();
			}
		}
		// 2 intersection points
		else {
			if (areClose(interPoints.get(0), interPoints.get(1))) {
				double realX = view
						.toRealWorldCoordX(interPoints.get(0).getX());
				double realY = view
						.toRealWorldCoordY(interPoints.get(0).getY());
				// switch old point with intersection
				// point
				dataPoints.get(i).setCoords(realX, realY);
			} else {
				dataPoints.get(i).setUndefined();
			}
		}
	}

	private int handleEraserAtPoint(List<MyPoint> dataPoints, int i) {
		int index = i;
		// no intersection points
		if (interPoints.isEmpty()) {
			double pointX = view.toScreenCoordXd(dataPoints.get(i - 1).getX());
			double pointY = view.toScreenCoordYd(dataPoints.get(i - 1).getY());
			GPoint2D point = AwtFactory.getPrototype().newPoint2D(pointX,
					pointY);
			// if the first point is also inside of
			// rectangle
			if (rect.contains(point)) {
				// we can set the end point to undefined
				dataPoints.get(i).setUndefined();
			}
		}
		// two intersection points
		else {
			if (areClose(interPoints.get(0), interPoints.get(1))) {
				double realX = view
						.toRealWorldCoordX(interPoints.get(0).getX());
				double realY = view
						.toRealWorldCoordY(interPoints.get(0).getY());
				// switch old point with intersection
				// point
				dataPoints.get(i).setCoords(realX, realY);
			} else {
				double[] realCoords = getInterRealCoords(
						dataPoints.get(i - 1));
				swap(dataPoints, getNewPolyLinePoints(
						dataPoints, 1, i, i - 1, i, i + 1,
						realCoords));

				index = i + 2;
			}
		}
		return index;
	}

	private int handleEraserAtJoinPointOrEndOfSegments(List<MyPoint> dataPoints,
			int i) {
		int index = i;
		ArrayList<GPoint2D> secondInterPoints;
		if (i + 1 < dataPoints.size() && dataPoints.get(i + 1).isDefined()) {
			// see if there is intersection point with next segment
			secondInterPoints = getAllIntersectionPoint(dataPoints.get(i),
					dataPoints.get(i + 1), rect);
			// case point is the join point of 2 segments
			if (!secondInterPoints.isEmpty() && secondInterPoints.size() == 1) {
				interPoints.add(secondInterPoints.get(0));
				double[] realCoords = getInterRealCoords(
						dataPoints.get(i - 1));
				if (i + 2 < dataPoints.size()
						&& dataPoints.get(i + 2).isDefined()
						&& i - 2 > 0 && dataPoints.get(i - 2).isDefined()) {
					// switch old point with
					// intersection point
					dataPoints.get(i - 1).setCoords(realCoords[0],
							realCoords[1]);
					dataPoints.get(i).setUndefined();
					// switch old point with
					// intersection point
					dataPoints.get(i + 1).setCoords(realCoords[2],
							realCoords[3]);
					index = i + 2;
				} else if (i + 2 < dataPoints.size()
						&& !dataPoints.get(i + 2).isDefined() && i - 2 > 0
						&& dataPoints.get(i - 2).isDefined()) {
					swap(dataPoints, getNewPolyLinePoints(dataPoints, 1, i,
							i - 1, i, i + 1, realCoords));

					index = i + 2;
				} else if (i - 2 > 0 && !dataPoints.get(i - 2).isDefined()
						&& i + 2 < dataPoints.size()
						&& dataPoints.get(i + 2).isDefined()) {
					swap(dataPoints, getNewPolyLinePoints(dataPoints, 1, i, i,
							i + 1, i + 2, realCoords));

					index = i + 2;
				} else if (i - 2 > 0 && !dataPoints.get(i - 2).isDefined()
						&& i + 2 < dataPoints.size()
						&& !dataPoints.get(i + 2).isDefined()) {
					swap(dataPoints, getNewPolyLinePoints(dataPoints, 2, i, i,
							i + 1, i + 2, realCoords));
					index = i + 3;
				} else {
					swap(dataPoints, getNewPolyLinePoints(dataPoints, 1, i, i,
							i + 1, i + 2, realCoords));
					index = i + 2;
				}
			}
		}
		// point is endpoint of segment
		else {
			double realX = view.toRealWorldCoordX(interPoints.get(0).getX());
			double realY = view.toRealWorldCoordY(interPoints.get(0).getY());
			// switch old point with
			// intersection point
			dataPoints.get(i).setCoords(realX, realY);
		}
		return index;
	}

	private void swap(List<MyPoint> dataPoints,
			List<MyPoint> newPolyLinePoints) {
		dataPoints.clear();
		dataPoints.addAll(newPolyLinePoints);
	}

	private int handleEraserBetweenPointsOfSegment(
			List<MyPoint> dataPoints, int i) {
		int index = i;
		interPoints.clear();
		interPoints = getAllIntersectionPoint(dataPoints.get(i),
				dataPoints.get(i + 1),
				rect);
		if (interPoints.size() >= 2) {
			double[] realCoords = getInterRealCoords(dataPoints.get(i));
			// case ?,(A),(B),? or ?,(A),(B)
			if (i - 1 > 0 && !dataPoints.get(i - 1).isDefined()
					&& ((i + 2 < dataPoints.size()
							&& !dataPoints.get(i + 2).isDefined())
							|| i + 1 == dataPoints.size() - 1)) {
				swap(dataPoints, getNewPolyLinePoints(dataPoints, 3, i, i + 1,
						i + 2, i + 3, realCoords));
				index += 4;
			}
			// case ?,(A),(B),...
			else if (i - 1 > 0 && !dataPoints.get(i - 1).isDefined()
					&& i + 1 != dataPoints.size() - 1) {
				swap(dataPoints, getNewPolyLinePoints(dataPoints, 2, i, i + 1,
						i + 2, i + 3, realCoords));
				index += 3;
			}
			// case ...,(A),(B),?,... or ...,(A),(B)
			else if (i + 1 == dataPoints.size() - 1
					|| (i + 2 < dataPoints.size()
							&& !dataPoints.get(i + 2).isDefined())) {
				swap(dataPoints, getNewPolyLinePoints(dataPoints, 2, i, i,
						i + 1, i + 2, realCoords));
				index += 3;
			}
			// otherwise
			else {
				swap(dataPoints, getNewPolyLinePoints(dataPoints, 1, i, i,
						i + 1, i + 2, realCoords));
				index += 2;
			}
		}
		return index;
	}
}
