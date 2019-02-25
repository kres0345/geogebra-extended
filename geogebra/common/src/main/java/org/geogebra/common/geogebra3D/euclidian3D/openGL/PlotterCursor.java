package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Class that describes the geometry of the 3D cursor
 * 
 * @author ggb3D
 * 
 */
public class PlotterCursor {

	public static final int TYPE_CROSS2D = 0;
	public static final int TYPE_DIAMOND = 1;
	public static final int TYPE_CYLINDER = 2;
	public static final int TYPE_CROSS3D = 3;
	public static final int TYPE_ALREADY_XY = 4;
	public static final int TYPE_ALREADY_Z = 5;
	public static final int TYPE_ALREADY_XYZ = 6;
	public static final int TYPE_CUBE = 7;
	public static final int TYPE_SPHERE = 8;
	public static final int TYPE_TARGET_CIRCLE = 9;
	public static final int TYPE_ROTATION = 10;

	static private int TYPE_LENGTH = 11;

	static private float size = 12f;
	static private float thickness = 1.25f;
	static private float thickness2 = 1.25f;
	static private float depth = 1f;

	static private float size_start_move = 7f;
	static private float size_move = 40f;
	static private float thickness3 = 2 * thickness;

	static private float size_cube = size_start_move;

	static private float TARGET_DOT_ALPHA = 0.87f;

	static private float TARGET_CIRCLE_THICKNESS = EuclidianStyleConstants.DEFAULT_LINE_THICKNESS
			* PlotterBrush.LINE3D_THICKNESS / 2f;
	static private float TARGET_CIRCLE_RADIUS = 50f;
	static private float TARGET_CIRCLE_ALPHA = 0.38f;

	private int[] index;

	private Manager manager;

	private float r;
	private float g;
	private float b;
	private float a;

	/**
	 * common constructor
	 * 
	 * @param manager
	 *            geometry manager
	 */
	public PlotterCursor(Manager manager) {

		this.manager = manager;

		manager.setScalerIdentity();

		index = new int[TYPE_LENGTH];

		// crosses
		for (int i = 0; i < 4; i++) {
			index[i] = manager.startNewList(-1, true);
			manager.startGeometry(Manager.Type.TRIANGLES);
			cursor(i);
			manager.endGeometry();
			manager.endList();
		}

		// moving cursors
		PlotterBrush brush = manager.getBrush();

		brush.setArrowType(PlotterBrush.ARROW_TYPE_SIMPLE);

		// sets the thickness for arrows
		brush.setThickness(1, 1f);

		brush.setAffineTexture(0.5f, 0.125f);

		// xy
		brush.start(-1);
		brush.setColor(GColor.GRAY);
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(size_start_move, 0, 0, 1),
				new Coords(size_move, 0, 0, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(-size_start_move, 0, 0, 1),
				new Coords(-size_move, 0, 0, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(0, size_start_move, 0, 1),
				new Coords(0, size_move, 0, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(0, -size_start_move, 0, 1),
				new Coords(0, -size_move, 0, 1));
		index[TYPE_ALREADY_XY] = brush.end();

		// z
		brush.start(-1);
		brush.setColor(GColor.GRAY);
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(0, 0, size_start_move, 1),
				new Coords(0, 0, size_move, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(0, 0, -size_start_move, 1),
				new Coords(0, 0, -size_move, 1));
		index[TYPE_ALREADY_Z] = brush.end();

		// xyz
		brush.start(-1);
		brush.setColor(GColor.GRAY);
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(size_start_move, 0, 0, 1),
				new Coords(size_move, 0, 0, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(-size_start_move, 0, 0, 1),
				new Coords(-size_move, 0, 0, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(0, size_start_move, 0, 1),
				new Coords(0, size_move, 0, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(0, -size_start_move, 0, 1),
				new Coords(0, -size_move, 0, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(0, 0, size_start_move, 1),
				new Coords(0, 0, size_move, 1));
		brush.setThickness(thickness3); // re sets the thickness
		brush.segment(new Coords(0, 0, -size_start_move, 1),
				new Coords(0, 0, -size_move, 1));
		index[TYPE_ALREADY_XYZ] = brush.end();

		brush.setArrowType(PlotterBrush.ARROW_TYPE_NONE);

		// cube
		index[TYPE_CUBE] = manager.startNewList(-1, true);
		manager.startGeometry(Manager.Type.TRIANGLES);
		color(0.5f, 0.5f, 0.5f);
		// up
		manager.normal(0, 0, 1);
		quad(size_cube, size_cube, size_cube, -size_cube, size_cube, size_cube,
				-size_cube, -size_cube, size_cube, size_cube, -size_cube,
				size_cube);
		// down
		manager.normal(0, 0, -1);
		quad(size_cube, size_cube, -size_cube, size_cube, -size_cube,
				-size_cube, -size_cube, -size_cube, -size_cube, -size_cube,
				size_cube, -size_cube);
		// right
		manager.normal(1, 0, 0);
		quad(size_cube, size_cube, size_cube, size_cube, -size_cube, size_cube,
				size_cube, -size_cube, -size_cube, size_cube, size_cube,
				-size_cube);
		// left
		manager.normal(-1, 0, 0);
		quad(-size_cube, size_cube, size_cube, -size_cube, size_cube,
				-size_cube, -size_cube, -size_cube, -size_cube, -size_cube,
				-size_cube, size_cube);
		// back
		manager.normal(0, 1, 0);
		quad(size_cube, size_cube, size_cube, size_cube, size_cube, -size_cube,
				-size_cube, size_cube, -size_cube, -size_cube, size_cube,
				size_cube);
		// front
		manager.normal(0, -1, 0);
		quad(size_cube, -size_cube, size_cube, -size_cube, -size_cube,
				size_cube, -size_cube, -size_cube, -size_cube, size_cube,
				-size_cube, -size_cube);

		manager.endGeometry();
		manager.endList();

		// sphere
		index[TYPE_SPHERE] = manager.startNewList(-1, true);
		manager.startGeometry(Manager.Type.TRIANGLES);
		cursorSphere(1f, TARGET_DOT_ALPHA);
		manager.endGeometry();
		manager.endList();

		// circle for target
		brush.start(-1);
		brush.setColor(GColor.WHITE, TARGET_CIRCLE_ALPHA);
		brush.setThickness(TARGET_CIRCLE_THICKNESS);
		brush.circle(Coords.O, Coords.VX, Coords.VY, TARGET_CIRCLE_RADIUS, 64);
		index[TYPE_TARGET_CIRCLE] = brush.end();

		// rotation
		brush.start(-1);
		brush.setColor(GColor.GRAY);
		brush.setThickness(thickness3); // re sets the thickness
		brush.arcExtendedWithArrows(new Coords(0, 0, 0, 1),
				new Coords(1, 0, 0, 0), new Coords(0, 1, 0, 0), size_move / 2,
				-Math.PI * 0.6, Math.PI * 1.2, 64);
		index[TYPE_ROTATION] = brush.end();

		manager.setScalerView();
	}

	private void color(float red, float green, float blue, float alpha) {
		this.r = red;
		this.g = green;
		this.b = blue;
		this.a = alpha;
	}

	private void color(float red, float green, float blue) {
		color(red, green, blue, 1f);
	}

	private void vertex(float x, float y, float z) {
		manager.color(r, g, b, a);
		manager.vertex(x, y, z);
	}

	private void quad(float x1, float y1, float z1, float x2, float y2,
			float z2, float x3, float y3, float z3, float x4, float y4,
			float z4) {

		vertex(x1, y1, z1);
		vertex(x2, y2, z2);
		vertex(x3, y3, z3);

		vertex(x1, y1, z1);
		vertex(x3, y3, z3);
		vertex(x4, y4, z4);
	}

	/**
	 * used to say if light is on or not
	 * 
	 * @param type
	 *            type
	 * @return true it type is of "already" (xy or z)
	 */
	public static final boolean isTypeAlready(int type) {
		return type == TYPE_ALREADY_XY || type == TYPE_ALREADY_Z
				|| type == TYPE_ALREADY_XYZ || type == TYPE_CUBE
				|| type == TYPE_ROTATION;
	}

	// ////////////////////////////////
	// INDEX
	// ////////////////////////////////

	/**
	 * return geometry index for each type of cursor
	 * 
	 * @param i
	 *            type
	 * @return geometry index for each type of cursor
	 */
	public int getIndex(int i) {
		return index[i];
	}

	// ////////////////////////////////
	// GEOMETRIES
	// ////////////////////////////////

	private void cursor(int i) {

		switch (i) {
		default:
		case 0:
			cursorCross2D();
			break;
		case 1:
			cursorDiamond();
			break;
		case 2:
			cursorCylinder();
			break;
		case 3:
			cursorCross3D();
			break;
		}
	}

	private void cursorCross2D() {

		// white parts
		color(1, 1, 1);

		// up
		quad(thickness, size, depth, -thickness, size, depth, -thickness, -size,
				depth, thickness, -size, depth);

		quad(size, thickness, depth, thickness, thickness, depth, thickness,
				-thickness, depth, size, -thickness, depth);

		quad(-size, thickness, depth, -size, -thickness, depth, -thickness,
				-thickness, depth, -thickness, thickness, depth);

		// down
		quad(thickness, size, -depth, thickness, -size, -depth, -thickness,
				-size, -depth, -thickness, size, -depth);

		quad(size, thickness, -depth, size, -thickness, -depth, thickness,
				-thickness, -depth, thickness, thickness, -depth);

		quad(-size, thickness, -depth, -thickness, thickness, -depth,
				-thickness, -thickness, -depth, -size, -thickness, -depth);

		// black parts
		color(0, 0, 0);

		// up and down
		quadSymxOyRotOz90SymOz(thickness, thickness, depth,
				thickness + thickness2, thickness + thickness2, depth,
				thickness + thickness2, size + thickness2, depth, thickness,
				size, depth);

		quadSymxOyRotOz90SymOz(thickness, -thickness, depth, thickness, -size,
				depth, thickness + thickness2, -size - thickness2, depth,
				thickness + thickness2, -thickness - thickness2, depth);

		quadSymxOyRotOz90SymOz(size, thickness, depth, size, -thickness, depth,
				size + thickness2, -thickness - thickness2, depth,
				size + thickness2, thickness + thickness2, depth);

		// edges
		quadSymxOyRotOz90SymOz(thickness + thickness2, thickness + thickness2,
				-depth, thickness + thickness2, size + thickness2, -depth,
				thickness + thickness2, size + thickness2, depth,
				thickness + thickness2, thickness + thickness2, depth);

		quadSymxOyRotOz90SymOz(thickness + thickness2, -thickness - thickness2,
				-depth, thickness + thickness2, -thickness - thickness2, depth,
				thickness + thickness2, -size - thickness2, depth,
				thickness + thickness2, -size - thickness2, -depth);

		quadRotOz90SymOz(size + thickness2, thickness + thickness2, -depth,
				size + thickness2, thickness + thickness2, depth,
				size + thickness2, -thickness - thickness2, depth,
				size + thickness2, -thickness - thickness2, -depth);

	}

	private void cursorCross3D() {

		float t = (float) (thickness / Math.tan(Math.PI / 8));

		float size2 = size + thickness2;

		// white parts
		color(1, 1, 1);

		quadSymxOyRotOz90SymOz(thickness, t, t, -thickness, t, t, -thickness, t,
				size2, thickness, t, size2);

		quadSymxOyRotOz90SymOz(thickness, t, t, thickness, size2, t, -thickness,
				size2, t, -thickness, t, t);

		quadRotOz90SymOz(t, t, thickness, t, t, -thickness, t, size2,
				-thickness, t, size2, thickness);

		quadRotOz90SymOz(-t, t, thickness, -t, size2, thickness, -t, size2,
				-thickness, -t, t, -thickness);

		quadRotOz90SymOz(thickness, size2 + t - thickness, -thickness,
				-thickness, size2 + t - thickness, -thickness, -thickness,
				size2 + t - thickness, thickness, thickness,
				size2 + t - thickness, thickness);

		quadSymxOyRotOz90SymOz(thickness, -thickness, size2 + t - thickness,
				thickness, thickness, size2 + t - thickness, -thickness,
				thickness, size2 + t - thickness, -thickness, -thickness,
				size2 + t - thickness);

		// black parts
		color(0, 0, 0);

		quadSymxOyRotOz90SymOz(t, t, t, t, t, size2, t, thickness, size2, t,
				thickness, t);

		quadSymxOyRotOz90SymOz(thickness, t, t, thickness, t, size2, t, t,
				size2, t, t, t);

		quadSymxOyRotOz90SymOz(t, t, t, t, t, thickness, t, size2, thickness, t,
				size2, t);

		quadSymxOyRotOz90SymOz(thickness, t, t, t, t, t, t, size2, t, thickness,
				size2, t);

		quadSymxOyRotOz90SymOz(-t, t, t, -t, size2, t, -t, size2, thickness, -t,
				t, thickness);

		quadSymxOyRotOz90SymOz(-thickness, t, t, -thickness, size2, t, -t,
				size2, t, -t, t, t);

		quadSymxOyRotOz90SymOz(t, size2, t, t, size2 + t - thickness, t, -t,
				size2 + t - thickness, t, -t, size2, t);

		quadSymxOyRotOz90SymOz(t, size2 + t - thickness, t, t,
				size2 + t - thickness, thickness, -t, size2 + t - thickness,
				thickness, -t, size2 + t - thickness, t);

		quadRotOz90SymOz(t, size2, t, t, size2, -t, t, size2 + t - thickness,
				-t, t, size2 + t - thickness, t);

		quadRotOz90SymOz(t, size2 + t - thickness, thickness, t,
				size2 + t - thickness, -thickness, thickness,
				size2 + t - thickness, -thickness, thickness,
				size2 + t - thickness, thickness);

		quadRotOz90SymOz(-t, size2, t, -t, size2 + t - thickness, t, -t,
				size2 + t - thickness, -t, -t, size2, -t);

		quadRotOz90SymOz(-t, size2 + t - thickness, thickness, -thickness,
				size2 + t - thickness, thickness, -thickness,
				size2 + t - thickness, -thickness, -t, size2 + t - thickness,
				-thickness);

		quadSymxOyRotOz90SymOz(t, t, size2, t, t, size2 + t - thickness, t, -t,
				size2 + t - thickness, t, -t, size2);

		quadSymxOyRotOz90SymOz(t, t, size2 + t - thickness, thickness,
				thickness, size2 + t - thickness, thickness, -thickness,
				size2 + t - thickness, t, -t, size2 + t - thickness);

	}

	private void cursorDiamond() {

		float t1 = 0.15f;
		float t2 = 1f - 2 * t1;

		// black parts
		color(0, 0, 0);

		quadSymxOyRotOz90SymOz(1f, 0f, 0f, t2, t1, t1, t1, t1, t2, 0f, 0f, 1f);

		quadSymxOyRotOz90SymOz(0f, 0f, 1f, t1, t1, t2, t1, t2, t1, 0f, 1f, 0f);

		quadSymxOyRotOz90SymOz(0f, 1f, 0f, t1, t2, t1, t2, t1, t1, 1f, 0f, 0f);

		// white parts
		color(1, 1, 1);

		quadSymxOyRotOz90SymOz(t2, t1, t1, t2, t1, t1, t1, t2, t1, t1, t1, t2);

	}

	private void cursorCylinder() {

		int latitude = 8;
		float x1 = 4f;
		float r1 = PlotterBrush.LINE3D_THICKNESS / 3f;
		float r2 = (float) (r1 * Math.sqrt(2));
		float x2 = x1 / 3;

		float da = (float) (Math.PI / latitude);

		float y1;
		float z1;
		float y0, z0;

		// white parts
		color(1, 1, 1);

		// ring
		y1 = 2 * r2 * (float) Math.sin(da);
		z1 = 2 * r2 * (float) Math.cos(da);

		for (int i = 1; i <= latitude; i++) {
			y0 = y1;
			z0 = z1;
			y1 = 2 * r2 * (float) Math.sin((2 * i + 1) * da);
			z1 = 2 * r2 * (float) Math.cos((2 * i + 1) * da);

			quad(-x2, y0, z0, x2, y0, z0, x2, y1, z1, -x2, y1, z1);

		}

		// caps
		y1 = 2 * r1 * (float) Math.sin(da);
		z1 = 2 * r1 * (float) Math.cos(da);

		for (int i = 1; i < latitude / 2; i++) {
			y0 = y1;
			z0 = z1;
			y1 = 2 * r1 * (float) Math.sin((2 * i + 1) * da);
			z1 = 2 * r1 * (float) Math.cos((2 * i + 1) * da);

			quadSymOz(x1, y0, z0, x1, -y0, z0, x1, -y1, z1, x1, y1, z1);

		}

		// black parts
		color(0, 0, 0);

		// ring
		y1 = 2 * (float) Math.sin(da);
		z1 = 2 * (float) Math.cos(da);

		for (int i = 1; i <= latitude; i++) {
			y0 = y1;
			z0 = z1;
			y1 = 2 * (float) Math.sin((2 * i + 1) * da);
			z1 = 2 * (float) Math.cos((2 * i + 1) * da);

			quadSymOz(x2, y0 * r2, z0 * r2, x1, y0 * r1, z0 * r1, x1, y1 * r1,
					z1 * r1, x2, y1 * r2, z1 * r2);

		}

	}

	private void cursorSphere(float gray, float alpha) {

		manager.setDummyTexture();

		color(gray, gray, gray, alpha);

		int latitude = 8;
		float r1 = 1f;

		float d = (float) (0.5 * Math.PI / latitude);

		float rcjp = r1;
		float z3 = 0;

		for (int j = 0; j < latitude; j++) {

			float rcj = rcjp;
			float x2 = rcj;
			float y2 = 0f;
			float z1 = z3;

			rcjp = (float) (r1 * Math.cos((j + 1) * d));
			float x3 = rcjp;
			float y3 = 0f;
			z3 = (float) (r1 * Math.sin((j + 1) * d));

			for (int i = 0; i < 4 * latitude; i++) {

				float x1 = x2;
				float y1 = y2;

				float ci = (float) (Math.cos((i + 1) * d));
				float si = (float) (Math.sin((i + 1) * d));

				x2 = ci * rcj;
				y2 = si * rcj;

				float x4 = x3;
				float y4 = y3;

				x3 = ci * rcjp;
				y3 = si * rcjp;

				quad(x1, y1, z1, x2, y2, z1, x3, y3, z3, x4, y4, z3);

				quad(x1, y1, -z1, x4, y4, -z3, x3, y3, -z3, x2, y2, -z1);

			}
		}

	}

	private void quadSymxOyRotOz90SymOz(float x1, float y1, float z1, float x2,
			float y2, float z2, float x3, float y3, float z3, float x4,
			float y4, float z4) {

		quadRotOz90SymOz(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4);

		quadRotOz90SymOz(x1, y1, -z1, x4, y4, -z4, x3, y3, -z3, x2, y2, -z2);

	}

	private void quadRotOz90SymOz(float x1, float y1, float z1, float x2,
			float y2, float z2, float x3, float y3, float z3, float x4,
			float y4, float z4) {

		quadSymOz(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4);

		quadSymOz(-y1, x1, z1, -y2, x2, z2, -y3, x3, z3, -y4, x4, z4);

	}

	private void quadSymOz(float x1, float y1, float z1, float x2, float y2,
			float z2, float x3, float y3, float z3, float x4, float y4,
			float z4) {

		quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4);

		quad(-x1, -y1, z1, -x2, -y2, z2, -x3, -y3, z3, -x4, -y4, z4);

		/*
		 * vertex(-x1,y1,z1); vertex(-x4,y4,z4); vertex(-x3,y3,z3);
		 * vertex(-x2,y2,z2);
		 */

	}

}
