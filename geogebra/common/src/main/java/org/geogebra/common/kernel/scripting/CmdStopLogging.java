package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.SensorLogger;

/**
 * Stops logging
 *
 */
public class CmdStopLogging extends CmdScripting {
	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdStopLogging(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] perform(Command c) {

		SensorLogger logger = app.getSensorLogger();
		if (logger != null) {
			logger.stopLogging();
		} else {
			// no need for error
		}
		return new GeoElement[0];

	}

}
