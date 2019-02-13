package org.geogebra.common.kernel.commands;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoExtremumMulti;
import org.geogebra.common.kernel.algos.AlgoExtremumPolynomial;
import org.geogebra.common.kernel.algos.AlgoExtremumPolynomialInterval;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.MyError;

/**
 * Extremum[ &lt;GeoFunction> ]
 */
public class CmdExtremum extends CommandProcessor {

	/**
	 * Create new command processor
	 *
	 * @param kernel
	 *            kernel
	 */
	public CmdExtremum(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			ok[0] = arg[0].isGeoFunctionable();
			if (ok[0]) {
				return extremum(c, (GeoFunctionable) arg[0]);
			}
			throw argErr(c, arg[0]);
		case 3: // Extremum[f,start-x,end-x]
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))
					&& (ok[2] = (arg[2] instanceof GeoNumberValue))

			) {

				AlgoExtremumMulti algo = new AlgoExtremumMulti(cons,
						c.getLabels(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2], true);
				return algo.getExtremumPoints();
			}

			throw argErr(c, getBadArg(ok, arg));
		default:
			throw argNumErr(c);
		}
	}

	/**
	 * @return - all Extrema of function f (for polynomial functions) - all
	 *         Extrema currently visible (for non-polynomial functions)
	 */
	final private GeoPoint[] extremum(Command c, GeoFunctionable gf) {
		GeoFunction f = gf.getGeoFunction();
		// special case for If
		// non-polynomial -> undefined
		ExpressionNode exp = f.getFunctionExpression();
		if (exp.getOperation().isIf()) {

			AlgoExtremumPolynomialInterval algo = new AlgoExtremumPolynomialInterval(
					cons, c.getLabels(), f);
			GeoPoint[] g = algo.getRootPoints();
			return g;

		}

		// check if this is a polynomial at the moment
		// uninitialized CAS algo may return non-polynomial
		// if (!kernelA.getConstruction().isFileLoading() && f.isDefined()
		// && !f.isPolynomialFunction(true))
		// return null;

		if (!f.isPolynomialFunction(true)) {
			EuclidianViewInterfaceCommon view = this.kernel.getApplication()
					.getActiveEuclidianView();
			AlgoExtremumMulti algo = new AlgoExtremumMulti(cons, c.getLabels(),
					f, view);
			return algo.getExtremumPoints();
		}

		AlgoExtremumPolynomial algo = new AlgoExtremumPolynomial(cons,
				c.getLabels(), gf, true);
		GeoPoint[] g = algo.getRootPoints();
		return g;
	}
}
