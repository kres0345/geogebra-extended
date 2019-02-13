package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.SensorLogger;

/**
 * Command for logging
 */
public class CmdStartLogging extends CmdScripting {
	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdStartLogging(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] perform(Command c) {

		int n = c.getArgumentNumber();

		// ignore last parameter if odd
		// n = (n / 2) * 2;

		GeoElement[] arg = resArgs(c);

		boolean success = false;

		SensorLogger logger = app.getSensorLogger();
		if (logger != null) {

			logger.stopLogging();

			GeoElement argument = null;
			GeoElement limit = null;
			int offset = 0;

			if (arg[0] instanceof GeoNumberValue) {
				logger.setLimit(((GeoNumberValue) arg[1]).getDouble());
				offset++;
			} else {
				logger.setLimit(SensorLogger.DEFAULT_LIMIT);
			}
			for (int i = offset; i <= n - 2; i += 2) {
				argument = arg[i + 1];
				if (!(arg[i] instanceof GeoText)) {
					throw argErr(c, arg[i]);
				}
				String varName = ((GeoText) arg[i]).getTextString();
				if (argument instanceof GeoNumeric
						|| argument instanceof GeoText) {
					logger.registerGeo(varName, argument);
				} else if (argument instanceof GeoList) {
					// it should be possible to add an optional third parameter
					// to lists - size limit of logging
					if ((i < n - 2)
							&& (limit = arg[i + 2]) instanceof GeoNumeric) {
						logger.registerGeoList(varName, (GeoList) argument,
								((GeoNumeric) limit).getValue());
						i++;
					} else {
						logger.registerGeoList(varName, (GeoList) argument);
					}
				} else if (argument instanceof GeoFunction) {
					// it should be possible to add an optional third parameter
					// to lists - size limit of logging
					if ((i < n - 2)
							&& (limit = arg[i + 2]) instanceof GeoNumeric) {
						logger.registerGeoFunction(varName,
								(GeoFunction) argument,
								((GeoNumeric) limit).getValue());
						i++;
					} else {
						logger.registerGeoFunction(varName,
								(GeoFunction) argument);
					}
				} else {
					throw argErr(c, argument);
				}
				if (!argument.isLabelSet()) {
					argument.setLabel(varName);
				}
			}

			success = logger.startLogging();
		}

		if (!success) {
			throw new MyError(loc, loc.getError("NoLogging"));
		}
		return arg;
	}
}
