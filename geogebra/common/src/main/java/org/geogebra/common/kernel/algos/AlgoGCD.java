/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.math.BigInteger;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.util.DoubleUtil;

/**
 * Computes GCD[a, b]
 * 
 * @author Michael Borcherds
 */
public class AlgoGCD extends AlgoTwoNumFunction {

	public AlgoGCD(Construction cons, String label, GeoNumberValue a,
			GeoNumberValue b) {
		super(cons, label, a, b);
	}

	@Override
	public Commands getClassName() {
		return Commands.GCD;
	}

	@Override
	public final void compute() {
		if (input[0].isDefined() && input[1].isDefined()) {

			if (a.getDouble() > Long.MAX_VALUE || b.getDouble() > Long.MAX_VALUE
					|| a.getDouble() < -Long.MAX_VALUE
					|| b.getDouble() < -Long.MAX_VALUE) {
				num.setUndefined();
				return;
			}

			if (DoubleUtil.isInteger(a.getDouble())
					&& DoubleUtil.isInteger(b.getDouble())) {
				BigInteger i1 = BigInteger
						.valueOf((long) DoubleUtil.checkInteger(a.getDouble()));
				BigInteger i2 = BigInteger
						.valueOf((long) DoubleUtil.checkInteger(b.getDouble()));

				i1 = i1.gcd(i2);

				double result = Math.abs(i1.doubleValue());

				// can't store integers greater than this in a double accurately
				if (result > 1e15) {
					num.setUndefined();
					return;
				}

				num.setValue(result);
			} else {
				num.setUndefined();
			}

		} else {
			num.setUndefined();
		}
	}

}
