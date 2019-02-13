package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * ANOVA test
 */
public class CmdANOVA extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdANOVA(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 0:
			throw argNumErr(c);
		case 1: // list of lists, result of XML conversion
			ok[0] = (arg[0].isGeoList());
			if (ok[0]) {
				GeoList list = (GeoList) arg[0];

				if (list.size() == 0) {
					throw argErr(c, arg[0]);
				}

				if (list.get(0).isGeoList()) {
					GeoElement[] ret = {
							anovaTest(c.getLabel(), (GeoList) arg[0]) };
					return ret;

				}
				throw argErr(c, arg[0]);
			}

		default:
			GeoList list = wrapInList(kernel, arg, arg.length, GeoClass.LIST);
			if (list != null) {
				GeoElement[] ret = { anovaTest(c.getLabel(), list) };
				return ret;
			}
			// null ret should mean that an arg is not a GeoList
			// so find the bad one
			for (int i = 0; i <= n; i++) {
				if (!arg[i].isGeoList()) {
					throw argErr(c, arg[i]);
				}
			}
			// throw error for any other reason ...
			throw argErr(c, arg[0]);
		}
	}

	final private GeoList anovaTest(String label, GeoList dataArrayList) {
		AlgoANOVA algo = new AlgoANOVA(cons, label, dataArrayList);
		GeoList result = algo.getResult();
		return result;
	}

}
