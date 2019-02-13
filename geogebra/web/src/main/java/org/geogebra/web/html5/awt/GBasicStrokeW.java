package org.geogebra.web.html5.awt;

import org.geogebra.ggbjdk.java.awt.DefaultBasicStroke;
import org.geogebra.ggbjdk.java.awt.geom.Shape;

import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.canvas.dom.client.Context2d.LineJoin;

public class GBasicStrokeW extends DefaultBasicStroke {
	// Constants
	final private static LineJoin[] GWT_JOINS = { LineJoin.MITER,
			LineJoin.ROUND,
	        LineJoin.BEVEL };
	final private static LineCap[] GWT_CAPS = { LineCap.BUTT, LineCap.ROUND,
	        LineCap.SQUARE };

	// Private fields
	private int lineCap = CAP_BUTT;

	public GBasicStrokeW(double width, int cap, int join, double miterLimit,
			double[] dash) {
		super(width, cap, join, miterLimit, dash);
	}

	public GBasicStrokeW(double width, int cap, int join, double miterLimit) {
		super(width, cap, join, miterLimit);
	}

	public GBasicStrokeW(double width, int cap, int join) {
		super(width, cap, join);
	}

	public GBasicStrokeW(double width) {
		super(width);
	}

	public int getLineCap() {
		return lineCap;
	}

	// Methods
	public Shape createStrokedShape(Shape shape) {
		return shape;
	}

	/**
	 * @param join
	 *            native join from context
	 * @return join type
	 */
	public static int getJoin(String join) {
		switch (join.charAt(0)) {
		case 'r':
			return JOIN_ROUND;
		case 'b':
			return JOIN_BEVEL;
		default:
			return JOIN_MITER;
		}
	}

	/**
	 * @param cap
	 *            native join from context
	 * @return cap type
	 */
	public static int getCap(String cap) {
		switch (cap.charAt(0)) {
		case 'r':
			return CAP_ROUND;
		case 's':
			return CAP_SQUARE;
		default:
			return CAP_BUTT;
		}
	}

	/**
	 * @return GWT cap
	 */
	public LineCap getEndCapString() {
		return GWT_CAPS[getEndCap()];
	}

	/**
	 * @return GWT join
	 */
	public LineJoin getLineJoinString() {
		return GWT_JOINS[getLineJoin()];
	}

}
