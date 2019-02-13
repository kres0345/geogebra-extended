package org.geogebra.common.kernel.statistics;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoPolynomialFromCoordinates;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * Fits a polynomial with given degree to list of points. Adapted from
 * AlgoFitLine and AlgoPolynomialFromCoordinates (Borcherds)
 * 
 * @author Hans-Petter Ulven
 * @version 24.04.08
 * 
 *          27.01.09: Extended FitPoly to more than 4th degree ToDo: Put in a
 *          max degree limit, after some testing...
 */
public class AlgoFitPoly extends AlgoElement {

	private GeoList geolist; // input
	private GeoNumberValue degree; // input
	private GeoFunction geofunction; // output
	private final RegressionMath regMath;

	/**
	 * @param cons
	 *            construction
	 * @param geolist
	 *            points
	 * @param degree
	 *            degree
	 */
	public AlgoFitPoly(Construction cons, GeoList geolist,
			GeoNumberValue degree) {
		super(cons);
		regMath = new RegressionMath();
		this.geolist = geolist;
		this.degree = degree;
		geofunction = new GeoFunction(cons);
		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.FitPoly;
	}

	@Override
	protected void setInputOutput() {
		if (degree == null) {
			input = new GeoElement[] { geolist };
		} else {
			input = new GeoElement[] { geolist, degree.toGeoElement() };
		}
		setOnlyOutput(geofunction);
		setDependencies();
	}

	/**
	 * @return fit polynomial
	 */
	public GeoFunction getFitPoly() {
		return geofunction;
	}

	@Override
	public final void compute() {
		int size = geolist.size();
		int par;
		boolean regok = true;
		double[] cof = null;
		par = degree == null ? size - 1 : (int) Math.round(degree.getDouble());
		if (!geolist.isDefined() || (size < 2) || (par < 0) || (par >= size)) {
			geofunction.setUndefined();
			return;
		}
		if (par == size - 1) {
			AlgoPolynomialFromCoordinates.setFromPoints(geofunction, geolist);
			return;
		}
		// if error in parameters :
		switch (par) {
		case RegressionMath.LINEAR: // moved up linear case from default
			cof = new double[2];
			regok = regMath.doLinear(geolist, cof);
			break;
		case RegressionMath.QUAD:
			cof = new double[3];
			regok = regMath.doQuad(geolist, cof);
			break;
		case RegressionMath.CUBIC:
			cof = new double[4];
			regok = regMath.doCubic(geolist, cof);
			break;
		default:
			if (par < 300) { // ToDo: test speed for max limit!
				cof = new double[par + 1];
				regok = regMath.doPolyN(geolist, par, cof);
				// else: ->
			} else {
				regok = false; // 24.04.08: Only 1<=degree
			} // if
		}
			// System.out.println("Used: "+(System.currentTimeMillis()-ms));
		if (!regok) {
			geofunction.setUndefined();
			return;
		}
		// if error in regression
		geofunction.setFunction(AlgoPolynomialFromCoordinates
				.buildPolyFunctionExpression(cons.getKernel(), cof));
		geofunction.setDefined(true);
	}

}
