/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * Area of polygon P[0], ..., P[n]
 *
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.util.DoubleUtil;

/**
 * Computes Mod[a, b]
 * 
 * @author Markus Hohenwarter
 */
public class AlgoMod extends AlgoTwoNumFunction {

	/**
	 * Creates new mod algo
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param a
	 *            dividend
	 * @param b
	 *            divisor
	 */
	public AlgoMod(Construction cons, String label, GeoNumberValue a,
			GeoNumberValue b) {
		super(cons, label, a, b);
	}

	@Override
	public Commands getClassName() {
		return Commands.Mod;
	}

	@Override
	public final void compute() {
		if (input[0].isDefined() && input[1].isDefined()) {

			// removed as causes problems with old files
			// double mod = Math.round(a.getDouble());
			// double bInt = Math.abs(Math.round(b.getDouble()));

			double aVal = DoubleUtil.checkInteger(a.getDouble());
			double bAbs = DoubleUtil.checkInteger(Math.abs(b.getDouble()));

			if (Math.abs(aVal) > MyDouble.LARGEST_INTEGER
					|| bAbs > MyDouble.LARGEST_INTEGER) {
				num.setUndefined();
				return;
			}

			double mod = aVal % bAbs;

			// https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.17.3
			// It follows from this rule that the result of the remainder
			// operation can be negative only if the dividend is negative, and
			// can be positive only if the dividend is positive.
			if (mod < 0) {
				mod += bAbs;
			}

			num.setValue(mod);

		} else {
			num.setUndefined();
		}
	}

}
