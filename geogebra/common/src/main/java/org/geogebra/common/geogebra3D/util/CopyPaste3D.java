package org.geogebra.common.geogebra3D.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoCircle3DThreePoints;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoConicFivePoints3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoEllipseHyperbolaFociPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoJoinPoints3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPolyLine3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPolygonRegular3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoQuadricLimitedConicHeightCone;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoQuadricLimitedConicHeightCylinder;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoQuadricLimitedPointPointRadiusCone;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoQuadricLimitedPointPointRadiusCylinder;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoVector3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedronNet;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.CopyPaste;

/**
 * 3D extension of copy paste utility
 */
public class CopyPaste3D extends CopyPaste {

	@Override
	protected void addSubGeos(ArrayList<ConstructionElement> geos) {
		// even in 3D, there may be a lot of 2D objects
		super.addSubGeos(geos);

		GeoElement geo;
		for (int i = geos.size() - 1; i >= 0; i--) {
			geo = (GeoElement) geos.get(i);
			if (geo.getParentAlgorithm() == null) {
				continue;
			}

			if (geo.isGeoElement3D()) {
				// TODO: implementation!

				if (geo.isGeoPolyhedron()) {
					TreeSet<GeoElement> ancestors = getAllIndependentPredecessors(
							geo);

					// there are many kinds of algorithm to create a
					// GeoPolyhedron,
					// but the essence is that its faces, edges and points
					// should
					// be shown in any case when they have common parent algo
					// inputs
					Iterator<GeoPolygon3D> polysit = ((GeoPolyhedron) geo)
							.getPolygons().iterator();
					GeoPolygon3D psnext;
					while (polysit.hasNext()) {
						psnext = polysit.next();
						if (!geos.contains(psnext)
								&& predecessorsCovered(psnext, ancestors)) {
							geos.add(psnext);
						}
					}
					Iterator<GeoPolygon> ps2 = ((GeoPolyhedron) geo)
							.getPolygonsLinked().iterator();
					GeoPolygon ps2n;
					while (ps2.hasNext()) {
						ps2n = ps2.next();
						if (!geos.contains(ps2n)
								&& predecessorsCovered(ps2n, ancestors)) {
							geos.add(ps2n);
						}
					}
					GeoSegment3D[] segm = ((GeoPolyhedron) geo).getSegments3D();
					for (int j = 0; j < segm.length; j++) {
						if (!geos.contains(segm[j])
								&& predecessorsCovered(segm[j], ancestors)) {
							geos.add(segm[j]);
							GeoPointND[] pspoints2 = { segm[j].getStartPoint(),
									segm[j].getEndPoint() };
							for (int k = 0; k < pspoints2.length; k++) {
								if (!geos.contains(pspoints2[k])
										&& predecessorsCovered(pspoints2[k],
												ancestors)) {
									geos.add((GeoElement) (pspoints2[k]));
								}
							}
						}
					}
				} else if (geo instanceof GeoPolyhedronNet) {
					TreeSet<GeoElement> ancestors = getAllIndependentPredecessors(
							geo);

					Iterator<GeoPolygon3D> polysit = ((GeoPolyhedronNet) geo)
							.getPolygons().iterator();
					GeoPolygon3D psnext;
					while (polysit.hasNext()) {
						psnext = polysit.next();
						if (!geos.contains(psnext)
								&& predecessorsCovered(psnext, ancestors)) {
							geos.add(psnext);
						}
					}
					Iterator<GeoPolygon> ps2 = ((GeoPolyhedronNet) geo)
							.getPolygonsLinked().iterator();
					GeoPolygon ps2n;
					while (ps2.hasNext()) {
						ps2n = ps2.next();
						if (!geos.contains(ps2n)
								&& predecessorsCovered(ps2n, ancestors)) {
							geos.add(ps2n);
						}
					}
					GeoSegment3D[] segm = ((GeoPolyhedronNet) geo)
							.getSegments3D();
					for (int j = 0; j < segm.length; j++) {
						if (!geos.contains(segm[j])
								&& predecessorsCovered(segm[j], ancestors)) {
							geos.add(segm[j]);
							GeoPointND[] pspoints2 = { segm[j].getStartPoint(),
									segm[j].getEndPoint() };
							for (int k = 0; k < pspoints2.length; k++) {
								if (!geos.contains(pspoints2[k])
										&& predecessorsCovered(pspoints2[k],
												ancestors)) {
									geos.add((GeoElement) (pspoints2[k]));
								}
							}
						}
					}
				} else if (geo instanceof GeoQuadric3DLimited) {
					AlgoElement algo = geo.getParentAlgorithm();
					if (algo instanceof AlgoQuadricLimitedPointPointRadiusCone
							|| algo instanceof AlgoQuadricLimitedPointPointRadiusCylinder
							|| algo instanceof AlgoQuadricLimitedConicHeightCone
							|| algo instanceof AlgoQuadricLimitedConicHeightCylinder) {
						TreeSet<GeoElement> ancestors = getAllIndependentPredecessors(
								geo);

						GeoElement[] pgeos = geo.getParentAlgorithm()
								.getInput();
						for (int j = 0; j < pgeos.length; j++) {
							if (!geos.contains(pgeos[j]) && predecessorsCovered(
									pgeos[j], ancestors)) {
								geos.add(pgeos[j]);
							}
						}
						pgeos = geo.getParentAlgorithm().getOutput();
						for (int j = 0; j < pgeos.length; j++) {
							if (!geos.contains(pgeos[j]) && predecessorsCovered(
									pgeos[j], ancestors)) {
								geos.add(pgeos[j]);
							}
						}
					}

				} else if ((geo.isGeoLine()
						&& geo.getParentAlgorithm() instanceof AlgoJoinPoints3D)
						|| (geo.isGeoVector() && geo
								.getParentAlgorithm() instanceof AlgoVector3D)) {

					if (!geos
							.contains(geo.getParentAlgorithm().getInput()[0])) {
						geos.add(geo.getParentAlgorithm().getInput()[0]);
					}
					if (!geos
							.contains(geo.getParentAlgorithm().getInput()[1])) {
						geos.add(geo.getParentAlgorithm().getInput()[1]);
					}
				} else if (geo instanceof GeoPolygon3D) {

					if (geo.getParentAlgorithm() instanceof AlgoPolygon3D) {
						GeoPointND[] points = ((AlgoPolygon3D) (geo
								.getParentAlgorithm())).getPoints();
						for (int j = 0; j < points.length; j++) {
							if (!geos.contains(points[j])) {
								geos.add((GeoElement) points[j]);
							}
						}
						GeoElement[] ogeos = ((AlgoPolygon3D) (geo
								.getParentAlgorithm())).getOutput();
						for (int j = 0; j < ogeos.length; j++) {
							if (!geos.contains(ogeos[j])
									&& ogeos[j].isGeoSegment()) {
								geos.add(ogeos[j]);
							}
						}
					} else if (geo
							.getParentAlgorithm() instanceof AlgoPolygonRegular3D) {
						GeoElement[] pgeos = ((geo.getParentAlgorithm()))
								.getInput();
						for (int j = 0; j < pgeos.length; j++) {
							if (!geos.contains(pgeos[j])
									&& pgeos[j].isGeoPoint() && j < 3) {
								geos.add(pgeos[j]);
							}
						}
						GeoElement[] ogeos = ((geo.getParentAlgorithm()))
								.getOutput();
						for (int j = 0; j < ogeos.length; j++) {
							if (!geos.contains(ogeos[j])
									&& (ogeos[j].isGeoSegment()
											|| ogeos[j].isGeoPoint())) {
								geos.add(ogeos[j]);
							}
						}
					}
				} else if (geo instanceof GeoPolyLine3D) {
					if (geo.getParentAlgorithm() instanceof AlgoPolyLine3D) {
						GeoPointND[] pgeos = ((AlgoPolyLine3D) (geo
								.getParentAlgorithm())).getPointsND();
						for (int j = 0; j < pgeos.length; j++) {
							if (!geos.contains(pgeos[j])) {
								geos.add((GeoElement) pgeos[j]);
							}
						}
					}
				} else if (geo.isGeoConic()) {
					// different, harder!
					/*
					 * if (geo.getParentAlgorithm() instanceof
					 * AlgoCircleTwoPoints) { GeoElement[] pgeos =
					 * geo.getParentAlgorithm().getInput(); if
					 * (!geos.contains(pgeos[0])) geos.add(pgeos[0]); if
					 * (!geos.contains(pgeos[1])) geos.add(pgeos[1]); } else
					 */
					AlgoElement algo = geo.getParentAlgorithm();
					if (algo instanceof AlgoCircle3DThreePoints
							|| algo instanceof AlgoEllipseHyperbolaFociPoint3D) {
						GeoElement[] pgeos = geo.getParentAlgorithm()
								.getInput();
						if (!geos.contains(pgeos[0])) {
							geos.add(pgeos[0]);
						}
						if (!geos.contains(pgeos[1])) {
							geos.add(pgeos[1]);
						}
						if (!geos.contains(pgeos[2])) {
							geos.add(pgeos[2]);
						}
					} else if (geo
							.getParentAlgorithm() instanceof AlgoConicFivePoints3D) {
						GeoElement[] pgeos = geo.getParentAlgorithm()
								.getInput();
						for (int j = 0; j < pgeos.length; j++) {
							if (!geos.contains(pgeos[j])) {
								geos.add(pgeos[j]);
							}
						}
					} /*
						 * else if (geo.getParentAlgorithm() instanceof
						 * AlgoCirclePointRadius) { GeoElement[] pgeos =
						 * geo.getParentAlgorithm().getInput(); if
						 * (!geos.contains(pgeos[0])) geos.add(pgeos[0]); }
						 */
				}
			}
		}
	}

	private static TreeSet<GeoElement> getAllIndependentPredecessors(
			GeoElement geo) {
		TreeSet<GeoElement> ancestors = new TreeSet<>();
		geo.addPredecessorsToSet(ancestors, true);
		return ancestors;
	}

	private static boolean predecessorsCovered(GeoElementND ps2n,
			TreeSet<GeoElement> ancestors) {
		return ancestors.containsAll(
				getAllIndependentPredecessors(ps2n.toGeoElement()));
	}
}
