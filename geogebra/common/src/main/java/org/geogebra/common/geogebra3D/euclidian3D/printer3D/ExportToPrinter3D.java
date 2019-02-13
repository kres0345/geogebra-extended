package org.geogebra.common.geogebra3D.euclidian3D.printer3D;

import java.util.ArrayList;
import java.util.TreeSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawQuadric3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawSurface3DElements;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.GeometriesSet;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.Geometry;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShadersElementsGlobalBuffer;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.discrete.PolygonTriangulation;
import org.geogebra.common.kernel.discrete.PolygonTriangulation.Convexity;
import org.geogebra.common.kernel.discrete.PolygonTriangulation.TriangleFan;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;

/**
 * Export to 3D printer
 */
public class ExportToPrinter3D {

	/**
	 * 3D export type
	 */
	static public enum Type {
		/** curve */
		CURVE,
		/** closed curve */
		CURVE_CLOSED,
		/** closed surface */
		SURFACE_CLOSED,
		/** point */
		POINT
	}

	/** normal index when same as for vertex */
	public final static int NORMAL_SAME_INDEX = -1;
	/** normal index when not set */
	public final static int NORMAL_NOT_SET = -2;

	private Format format;

	private ManagerShadersElementsGlobalBuffer manager;

	protected EuclidianView3D view;

	private StringBuilder sb;

	private Coords center = null;

	private boolean reverse = false;

	private double xInvScale;
	private Coords tmpNormal = new Coords(3);

	/**
	 * default newline string
	 */
	public final static String NEWLINE = "\n";

	private TreeSet<SegmentIndex> segmentsForThickness;
	private SegmentIndex reverseSegment;

	/**
	 * 
	 * interface for geometries methods used for export
	 *
	 */
	public interface GeometryForExport {

		/**
		 * init the geometry to be ready for export
		 */
		void initForExport();

		/**
		 * 
		 * @return number of vertices/normals in geometry
		 */
		int getLengthForExport();

		/**
		 * 
		 * @return vertices buffer for export
		 */
		GLBuffer getVerticesForExport();

		/**
		 * 
		 * @return normals buffer for export
		 */
		GLBuffer getNormalsForExport();

		/**
		 * 
		 * @return indices buffer for export
		 */
		GLBufferIndices getBufferIndices();

		/**
		 * 
		 * @return number of indices
		 */
		int getIndicesLength();

		/**
		 * 
		 * @return offset in vertices/normals to retrieve it from indices
		 */
		int getElementsOffset();

		/**
		 * 
		 * @return geometry GL type
		 */
		Manager.Type getType();

	}

	private static class SegmentIndex implements Comparable<SegmentIndex> {
		private int v1;
		private int v2;

		public SegmentIndex() {
			set(-1, -1);
		}

		public SegmentIndex(int v1, int v2) {
			set(v1, v2);
		}

		public void set(int v1, int v2) {
			this.v1 = v1;
			this.v2 = v2;
		}

		public void setReverse(int v1, int v2) {
			set(v2, v1);
		}

		public int getV1() {
			return v1;
		}

		public int getV2() {
			return v2;
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof SegmentIndex && ((SegmentIndex) o).v1 == v1
					&& ((SegmentIndex) o).v2 == v2;
		}

		@Override
		public int hashCode() {
			return v1 + 13 * v2;
		}

		@Override
		public int compareTo(SegmentIndex o) {
			if (v1 < o.v1) {
				return -1;
			}
			if (v1 > o.v1) {
				return 1;
			}
			if (v2 < o.v2) {
				return -1;
			}
			if (v2 > o.v2) {
				return 1;
			}
			return 0;
		}

		@Override
		public String toString() {
			return v1 + "-" + v2;
		}

	}

	/**
	 * constructor
	 * 
	 * @param view
	 *            3D view
	 * @param manager
	 *            geometry manager
	 */
	public ExportToPrinter3D(EuclidianView3D view, Manager manager) {
		this.view = view;
		if (manager instanceof ManagerShadersElementsGlobalBuffer) {
			this.manager = (ManagerShadersElementsGlobalBuffer) manager;
		}
		sb = new StringBuilder();
	}

	/**
	 * @param d
	 *            drawable
	 * @param type
	 *            export object type
	 */
	public void export(Drawable3D d, Type type) {
		if (type == Type.POINT) {
			if (d.shouldBePacked()) {
				center = null;
			} else {
				center = ((DrawPoint3D) d).getCenter();
			}
		} else {
			center = null;
		}
		GeoElement geo = d.getGeoElement();
		export(d.getGeometryIndex(), type, geo.getGeoClassType().toString(),
				geo);
	}

	/**
	 * @param geometryIndex
	 *            geometry index
	 * @param type
	 *            export object type
	 * @param geoType
	 *            geo type
	 * @param geo
	 *            construction element
	 */
	public void export(int geometryIndex, Type type, String geoType,
			GeoElement geo) {

		reverse = false;
		GeometriesSet currentGeometriesSet = manager
				.getGeometrySet(geometryIndex);

		if (currentGeometriesSet != null) {
			for (Geometry g : currentGeometriesSet) {

				GeometryForExport geometry = (GeometryForExport) g;
				geometry.initForExport();

				format.getObjectStart(sb, geoType, geo, false, null, 1);

				// object is a polyhedron
				format.getPolyhedronStart(sb);

				// vertices
				boolean notFirst = false;
				format.getVerticesStart(sb, geometry.getLengthForExport());
				GLBuffer fb = geometry.getVerticesForExport();
				for (int i = 0; i < geometry.getLengthForExport(); i++) {
					double x = fb.get();
					double y = fb.get();
					double z = fb.get();
					getVertex(notFirst, x, y, z);
					notFirst = true;
				}
				format.getVerticesEnd(sb);
				fb.rewind();

				// normals
				getNormals(geometry);

				// faces
				GLBufferIndices bi = geometry.getBufferIndices();
				int length = geometry.getIndicesLength() / 3;
				int offset = geometry.getElementsOffset();
				format.getFacesStart(sb, length, false);
				notFirst = false;
				for (int i = 0; i < length; i++) {
					int v1 = bi.get();
					int v2 = bi.get();
					int v3 = bi.get();
					getFaceWithOffset(notFirst, offset, v1, v2, v3);
					notFirst = true;
				}
				bi.rewind();

				if (type == Type.CURVE && format.needsClosedObjects()) {
					// face for start
					for (int i = 1; i < 7; i++) {
						getFace(notFirst, 0, 0, i, i + 1, NORMAL_NOT_SET);
					}

					// update index
					int l = geometry.getLengthForExport();

					// face for end
					for (int i = 2; i < 8; i++) {
						getFace(notFirst, 0, l - 1, l - i, l - i - 1,
								NORMAL_NOT_SET);
					}
				}

				format.getFacesEnd(sb); // end of faces

				// end of polyhedron
				format.getPolyhedronEnd(sb);

			}
		}
	}

	/**
	 * export surface
	 * 
	 * @param d
	 *            surface drawable
	 * @param exportSurface
	 *            says if surface/mesh is to export
	 */
	public void export(DrawSurface3DElements d, boolean exportSurface) {
		if (format.handlesSurfaces()) {
			reverse = false;
			GeoElement geo = d.getGeoElement();
			if (exportSurface) {
				exportSurface(geo, d.getSurfaceIndex(), false);
			} else {
				if (geo.getLineThickness() > 0) {
					export(geo, d.getGeometryIndex(), "SURFACE_MESH", false,
							GColor.BLACK, 1, false);
				}
			}

		} else {
			GeoElement geo = d.getGeoElement();
			if (!geo.isGeoFunctionNVar()) {
				reverse = false;
				if (exportSurface) {
					exportSurface(geo, d.getSurfaceIndex(), true);
				} else {
					if (geo.getLineThickness() > 0) {
						export(d.getGeometryIndex(), Type.CURVE,
								geo.getLabelSimple(), geo);
					}
				}
			}
		}
	}

	/**
	 * @param d
	 *            drawable
	 */
	public void exportSurface(Drawable3D d) {
		if (format.needsClosedObjects()) { // draw only spheres so far
			if (d instanceof DrawQuadric3D) {
				GeoQuadric3D q = (GeoQuadric3D) d.getGeoElement();
				exportSurface(d.getGeoElement(), d.getSurfaceIndex(),
						q.getType() != GeoQuadricNDConstants.QUADRIC_SPHERE);
			} else {
				exportSurface(d.getGeoElement(), d.getSurfaceIndex(), true);
			}
		} else {
			exportSurface(d.getGeoElement(), d.getSurfaceIndex(), false);
		}
	}

	/**
	 * export as surface
	 * 
	 * @param geo
	 *            geo
	 * @param index
	 *            surface index
	 */
	private void exportSurface(GeoElement geo, int index,
			boolean withThickness) {
		double alpha = geo.getAlphaValue();
		reverse = false;
		export(geo, index, "SURFACE", true, null, alpha, withThickness);
		if (!format.needsClosedObjects()) {
			reverse = true;
			export(geo, index, "SURFACE", true, null, alpha, false);
		}
	}

	private void export(GeoElement geo, int geometryIndex, String group,
			boolean transparency, GColor color, double alpha,
			boolean withThickness) {

		if (alpha < 0.001) {
			return;
		}

		GeometriesSet currentGeometriesSet = manager
				.getGeometrySet(geometryIndex);
		if (currentGeometriesSet != null) {
			for (Geometry g : currentGeometriesSet) {

				GeometryForExport geometry = (GeometryForExport) g;
				geometry.initForExport();

				format.getObjectStart(sb, group, geo, transparency, color,
						alpha);

				// object is a polyhedron
				format.getPolyhedronStart(sb);

				// normals
				if (withThickness) {
					getNormals(geometry, withThickness);
				}

				// vertices
				boolean notFirst = false;
				format.getVerticesStart(sb, geometry.getLengthForExport());
				GLBuffer fb = geometry.getVerticesForExport();
				for (int i = 0; i < geometry.getLengthForExport(); i++) {
					double x = fb.get();
					double y = fb.get();
					double z = fb.get();
					getVertex(notFirst, x, y, z, withThickness);
					notFirst = true;
				}
				format.getVerticesEnd(sb);
				fb.rewind();

				// normals
				if (!withThickness) {
					getNormals(geometry);
				}

				// faces
				if (withThickness) {
					initSegmentsForThickness();
				}
				GLBufferIndices bi = geometry.getBufferIndices();
				int offset = geometry.getElementsOffset();
				switch (geometry.getType()) {
				case TRIANGLE_FAN:
					// for openGL we use replace triangle fans by triangle
					// strips, repeating apex
					// every time
					int length = geometry.getIndicesLength() / 2;
					format.getFacesStart(sb, length - 1, false);
					notFirst = false;
					int v3 = bi.get();
					int v4 = bi.get();
					for (int i = 1; i < length; i++) {
						int v1 = v3;
						int v2 = v4;
						v3 = bi.get();
						v4 = bi.get();
						getFaceWithOffset(notFirst, offset, v1, v2, v4,
								withThickness);
						notFirst = true;
					}
					break;
				case TRIANGLE_STRIP:
					length = geometry.getIndicesLength() / 2;
					format.getFacesStart(sb, (length - 1) * 2, false);
					notFirst = false;
					v3 = bi.get();
					v4 = bi.get();
					for (int i = 1; i < length; i++) {
						int v1 = v3;
						int v2 = v4;
						v3 = bi.get();
						v4 = bi.get();
						getFaceWithOffset(notFirst, offset, v1, v2, v3,
								withThickness);
						notFirst = true;
						getFaceWithOffset(notFirst, offset, v2, v4, v3,
								withThickness);
					}
					break;
				case TRIANGLES:
				default:
					length = geometry.getIndicesLength() / 3;
					format.getFacesStart(sb, length, false);
					notFirst = false;
					for (int i = 0; i < length; i++) {
						int v1 = bi.get();
						int v2 = bi.get();
						v3 = bi.get();
						getFaceWithOffset(notFirst, offset, v1, v2, v3,
								withThickness);
						notFirst = true;
					}
					break;
				}
				bi.rewind();

				if (withThickness) {
					for (SegmentIndex si : segmentsForThickness) {
						int v1 = si.getV1();
						int v2 = si.getV2();
						getFace(notFirst, 2 * offset, 2 * v1, 2 * v1 + 1,
								2 * v2 + 1, NORMAL_NOT_SET);
						getFace(notFirst, 2 * offset, 2 * v1, 2 * v2 + 1,
								2 * v2, NORMAL_NOT_SET);
					}
				}

				format.getFacesEnd(sb); // end of faces

				// end of polyhedron
				format.getPolyhedronEnd(sb);

			}

		}
	}

	private void getNormals(GeometryForExport geometry) {
		getNormals(geometry, false);
	}

	private void getNormals(GeometryForExport geometry, boolean withThickness) {
		if (format.handlesNormals()) {
			GLBuffer fb = geometry.getNormalsForExport();
			if (fb != null && !fb.isEmpty() && fb.capacity() > 3) {
				format.getNormalsStart(sb, geometry.getLengthForExport());
				for (int i = 0; i < geometry.getLengthForExport(); i++) {
					double x = fb.get();
					double y = fb.get();
					double z = fb.get();
					getNormal(x, y, z, withThickness);
				}
				format.getNormalsEnd(sb);
				fb.rewind();
			}
		}
	}

	/**
	 * @param polygon
	 *            polygon
	 * @param vertices
	 *            vertex coordinates
	 * @param color
	 *            color
	 * @param alpha
	 *            opacity
	 */
	public void export(GeoPolygon polygon, Coords[] vertices, GColor color,
			double alpha) {

		if (alpha < 0.001) {
			return;
		}

		PolygonTriangulation pt = polygon.getPolygonTriangulation();
		if (pt.getMaxPointIndex() > 2) {
			Coords n = polygon.getMainDirection();
			double delta = 0;
			if (format.needsClosedObjects()) {
				delta = view.getThicknessForSurface();
			}
			if (view.scaleAndNormalizeNormalXYZ(n, tmpNormal)) {
				n = tmpNormal;
			}

			double dx = 0, dy = 0, dz = 0;
			if (format.needsClosedObjects()) {
				dx = n.getX() * delta;
				dy = n.getY() * delta;
				dz = n.getZ() * delta;
			}

			// check if the polygon is convex
			Convexity convexity = polygon.getPolygonTriangulation()
					.checkIsConvex();
			if (convexity != Convexity.NOT) {
				int length = polygon.getPointsLength();

				reverse = polygon.getReverseNormalForDrawing()
						^ (convexity == Convexity.CLOCKWISE);
				if (!format.needsClosedObjects()) {
					reverse = !reverse; // TODO fix that
				}

				format.getObjectStart(sb, polygon.getGeoClassType().toString(),
						polygon, true, color, alpha);

				// object is a polyhedron
				format.getPolyhedronStart(sb);

				// vertices
				boolean notFirst = false;
				format.getVerticesStart(sb, length * 2);
				for (int i = 0; i < length; i++) {
					Coords v = vertices[i];
					double x, y, z;
					x = v.getX() * view.getXscale();
					y = v.getY() * view.getYscale();
					z = v.getZ() * view.getZscale();
					if (format.needsClosedObjects()) {
						getVertex(notFirst, x + dx, y + dy, z + dz);
						notFirst = true;
						getVertex(notFirst, x - dx, y - dy, z - dz);
					} else {
						getVertex(notFirst, x, y, z);
						notFirst = true;
						getVertex(notFirst, x, y, z); // we need it twice for
														// front/back sides
					}
				}
				format.getVerticesEnd(sb);

				// normal
				if (format.handlesNormals()) {
					format.getNormalsStart(sb, 2);
					getNormalHandlingReverse(n.getX(), n.getY(), n.getZ(),
							false);
					getNormalHandlingReverse(-n.getX(), -n.getY(), -n.getZ(),
							false);
					format.getNormalsEnd(sb);
				}

				// faces
				format.getFacesStart(sb, format.needsClosedObjects()
						? (length - 2) * 2 + 2 : (length - 2) * 2, true);
				notFirst = false;

				for (int i = 1; i < length - 1; i++) {
					getFace(notFirst, 0, 2 * i, 2 * (i + 1), 0); // top
					notFirst = true;
					getFace(notFirst, 1, 2 * (i + 1) + 1, 2 * i + 1, 1); // bottom
				}

				if (format.needsClosedObjects()) {
					for (int i = 0; i < length; i++) { // side
						getFace(notFirst, 0, 2 * i, 2 * i + 1,
								(2 * i + 3) % (2 * length), NORMAL_NOT_SET);
						getFace(notFirst, 0, 2 * i,
								(2 * i + 3) % (2 * length),
								(2 * i + 2) % (2 * length), NORMAL_NOT_SET);
					}
				}

				format.getFacesEnd(sb); // end of faces

				// end of polyhedron
				format.getPolyhedronEnd(sb);

			} else {
				int length = polygon.getPointsLength();
				Coords[] verticesWithIntersections = pt
						.getCompleteVertices(vertices, length);
				int completeLength = pt.getMaxPointIndex();
				reverse = false;

				format.getObjectStart(sb, polygon.getGeoClassType().toString(),
						polygon, true, color, alpha);

				// object is a polyhedron
				format.getPolyhedronStart(sb);

				// vertices
				boolean notFirst = false;
				format.getVerticesStart(sb, completeLength);
				for (int i = 0; i < completeLength; i++) {
					Coords v = verticesWithIntersections[i];
					double x, y, z;
					x = v.getX() * view.getXscale();
					y = v.getY() * view.getYscale();
					z = v.getZ() * view.getZscale();
					if (format.needsClosedObjects()) {
						getVertex(notFirst, x + dx, y + dy, z + dz);
						notFirst = true;
						getVertex(notFirst, x - dx, y - dy, z - dz);
					} else {
						getVertex(notFirst, x, y, z);
						notFirst = true;
					}
				}
				format.getVerticesEnd(sb);

				// normal
				if (format.handlesNormals()) {
					format.getNormalsStart(sb, 2);
					getNormalHandlingReverse(n.getX(), n.getY(), n.getZ(),
							false);
					getNormalHandlingReverse(-n.getX(), -n.getY(), -n.getZ(),
							false);
					format.getNormalsEnd(sb);
				}

				// faces
				int size = 0;
				ArrayList<TriangleFan> triFanList = pt.getTriangleFans();
				for (TriangleFan triFan : triFanList) {
					size += format.needsClosedObjects()
							? triFan.size() - 1 + (triFan.size() + 1) * 2
							: triFan.size() - 1;
				}
				format.getFacesStart(sb, size * 2, true);
				notFirst = false;

				if (format.needsClosedObjects()) {
					for (TriangleFan triFan : triFanList) {
						int apex = triFan.getApexPoint();
						int current = triFan.getVertexIndex(0);
						int triFanSize = triFan.size();
						// bottom and top
						for (int i = 1; i < triFanSize; i++) {
							int old = current;
							current = triFan.getVertexIndex(i);
							getFace(notFirst, 0, 2 * apex, 2 * old, 2 * current,
									NORMAL_NOT_SET); // top
							notFirst = true;
							getFace(notFirst, 0, 2 * apex + 1, 2 * current + 1,
									2 * old + 1, NORMAL_NOT_SET); // bottom
						}
						// sides
						current = apex;
						for (int i = 0; i < triFanSize; i++) {
							int old = current;
							current = triFan.getVertexIndex(i);
							getFace(notFirst, 0, 2 * old, 2 * current + 1,
									2 * current, NORMAL_NOT_SET);
							notFirst = true;
							getFace(notFirst, 0, 2 * old, 2 * old + 1,
									2 * current + 1, NORMAL_NOT_SET);
						}
						getFace(notFirst, 0, 2 * current, 2 * apex + 1,
								2 * apex, NORMAL_NOT_SET);
						notFirst = true;
						getFace(notFirst, 0, 2 * current, 2 * current + 1,
								2 * apex + 1, NORMAL_NOT_SET);
					}
				} else {
					for (TriangleFan triFan : triFanList) {
						int apex = triFan.getApexPoint();
						int current = triFan.getVertexIndex(0);
						for (int i = 1; i < triFan.size(); i++) {
							int old = current;
							current = triFan.getVertexIndex(i);
							getFace(notFirst, apex, old, current, 0); // top
							notFirst = true;
							getFace(notFirst, apex, current, old, 1); // bottom
						}
					}
				}

				format.getFacesEnd(sb); // end of faces

				// end of polyhedron
				format.getPolyhedronEnd(sb);
			}
		}
	}

	/**
	 * 
	 * @return 3D printer format
	 */
	public Format getFormat() {
		return format;
	}

	private void getVertex(boolean notFirst, double x0, double y0, double z0) {
		getVertex(notFirst, x0, y0, z0, false);
	}

	private void getVertex(boolean notFirst, double x0, double y0, double z0,
			boolean withThickness) {
		double x = x0;
		double y = y0;
		double z = z0;
		if (center != null) {
			double r = center.getW() * DrawPoint3D.DRAW_POINT_FACTOR;
			x = center.getX() + x * r;
			y = center.getY() + y * r;
			z = center.getZ() + z * r;
		}
		if (notFirst) {
			format.getVerticesSeparator(sb);
		}
		if (withThickness) {
			format.getVertices(sb, x * xInvScale, y * xInvScale, z * xInvScale,
					view.getThicknessForSurface() * xInvScale);
		} else {
			format.getVertices(sb, x * xInvScale, y * xInvScale, z * xInvScale);
		}
	}

	private void getNormal(double x, double y, double z,
			boolean withThickness) {
		if (reverse) {
			getNormalHandlingReverse(-x, -y, -z, withThickness);
		} else {
			getNormalHandlingReverse(x, y, z, withThickness);
		}
	}

	private void getNormalHandlingReverse(double x, double y, double z,
			boolean withThickness) {
		format.getNormal(sb, x, y, z, withThickness);
		format.getNormalsSeparator(sb);
	}

	private boolean getFaceWithOffset(boolean notFirst, int offset, int v1,
			int v2, int v3) {
		return getFace(notFirst, offset, v1, v2, v3, NORMAL_SAME_INDEX);
	}

	private void getFaceWithOffset(boolean notFirst, int offset, int v1, int v2,
			int v3, boolean withThickness) {
		if (withThickness) {
			boolean notReversed = getFaceWithOffset(notFirst, 2 * offset,
					2 * v1, 2 * v2, 2 * v3);
			getFaceWithOffset(notFirst, 2 * offset, 2 * v1 + 1, 2 * v3 + 1,
					2 * v2 + 1);
			if (notReversed) {
				addToSegmentsForThickness(v1, v2);
				addToSegmentsForThickness(v2, v3);
				addToSegmentsForThickness(v3, v1);
			} else {
				addToSegmentsForThickness(v2, v1);
				addToSegmentsForThickness(v3, v2);
				addToSegmentsForThickness(v1, v3);
			}
		} else {
			getFaceWithOffset(notFirst, offset, v1, v2, v3);
		}
	}

	private void addToSegmentsForThickness(int v1, int v2) {
		reverseSegment.setReverse(v1, v2);
		// try first to remove segment if exists in the reverse winding order
		if (!segmentsForThickness.remove(reverseSegment)) {
			segmentsForThickness.add(new SegmentIndex(v1, v2));
		}
	}

	private boolean getFace(boolean notFirst, int offset, int v1, int v2,
			int v3, int normal) {
		return getFace(notFirst, v1 - offset, v2 - offset, v3 - offset, normal);
	}

	private boolean getFace(boolean notFirst, int v1, int v2, int v3,
			int normal) {
		if (notFirst) {
			format.getFacesSeparator(sb);
		}

		if (reverse) {
			return format.getFaces(sb, v1, v3, v2, normal);
		}
		return format.getFaces(sb, v1, v2, v3, normal);
	}

	/**
	 * 
	 * @param format1
	 *            export format
	 * @return export
	 */
	public StringBuilder export(Format format1) {
		this.format = format1;
		// this.format.setNewlineString(
		// view.getApplication().isHTML5Applet() ? NEWLINE_HTML : NEWLINE);
		xInvScale = 1 / view.getXscale();

		sb.setLength(0);
		format1.getScriptStart(sb);
		view.exportToPrinter3D(this);
		format1.getScriptEnd(sb);
		return sb;
	}

	private void initSegmentsForThickness() {
		if (segmentsForThickness == null) {
			segmentsForThickness = new TreeSet<>();
			reverseSegment = new SegmentIndex();
		} else {
			segmentsForThickness.clear();
		}
	}

}
