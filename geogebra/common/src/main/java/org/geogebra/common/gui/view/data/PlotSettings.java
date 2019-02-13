package org.geogebra.common.gui.view.data;

import org.geogebra.common.plugin.EuclidianStyleConstants;

public class PlotSettings {

	public double xMin = -10;
	public double xMax = 10;
	public double xAxesInterval = 1;
	public boolean xAxesIntervalAuto = true;
	public double yMin = -10;
	public double yMax = 10;
	public double yAxesInterval = 1;
	public boolean yAxesIntervalAuto = true;

	public double[] gridInterval = { 1, 1 };
	public boolean gridIntervalAuto = true;

	public int pointCaptureStyle = EuclidianStyleConstants.POINT_CAPTURING_OFF;

	public boolean showYAxis = false;
	public boolean showXAxis = true;
	public boolean showArrows = false;
	public boolean forceXAxisBuffer = false;
	public boolean[] isEdgeAxis = { false, false };
	public boolean[] isPositiveOnly = { false, false };
	public boolean showGrid = false;

	public boolean logXAxis = false;
	public boolean logYAxis = false;

	/**
	 * Default constructor
	 */
	public PlotSettings() {

	}

	/**
	 * Partial default constructor
	 * 
	 * @param xMinEV
	 * @param xMaxEV
	 * @param yMinEV
	 * @param yMaxEV
	 * @param showYAxis
	 * @param showArrows
	 * @param forceXAxisBuffer
	 * @param isEdgeAxis
	 */
	public PlotSettings(double xMinEV, double xMaxEV, double yMinEV,
			double yMaxEV, boolean showYAxis, boolean showArrows,
			boolean forceXAxisBuffer, boolean[] isEdgeAxis) {
		this.xMin = xMinEV;
		this.xMax = xMaxEV;
		this.yMin = yMinEV;
		this.yMax = yMaxEV;
		this.showYAxis = showYAxis;
		this.showArrows = showArrows;
		this.forceXAxisBuffer = forceXAxisBuffer;
		this.isEdgeAxis = isEdgeAxis;
	}

	// public void debug(){
	//
	// System.out.println("=======================");
	// System.out.println("xMin: " + xMin);
	// System.out.println("xMax: " + xMax);
	// System.out.println("yMin: " + yMin);
	// System.out.println("yMax: " + yMax);
	// System.out.println("=======================");
	// }

}