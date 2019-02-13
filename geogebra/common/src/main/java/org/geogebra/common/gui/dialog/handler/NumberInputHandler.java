package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

public class NumberInputHandler implements InputHandler {
	private AlgebraProcessor algebraProcessor;
	private GeoNumberValue num = null;
	private AsyncOperation<GeoNumberValue> callback;
	private boolean oldVal;
	private App app;

	/**
	 * @param algebraProcessor
	 *            algebra processor
	 */
	public NumberInputHandler(AlgebraProcessor algebraProcessor) {
		super();
		this.algebraProcessor = algebraProcessor;
		this.app = algebraProcessor.getKernel().getApplication();
	}

	/**
	 * @param algebraProcessor
	 *            algebra processor
	 * @param cb
	 *            callback
	 * @param appl
	 *            app
	 * @param oldValue
	 *            old value
	 */
	public NumberInputHandler(AlgebraProcessor algebraProcessor,
			AsyncOperation<GeoNumberValue> cb, App appl, boolean oldValue) {
		this(algebraProcessor);
		callback = cb;
		oldVal = oldValue;
		app = appl;
	}

	@Override
	public void processInput(String inputString, final ErrorHandler handler,
			final AsyncOperation<Boolean> callback0) {
		try {
			handler.resetError();
			algebraProcessor.processAlgebraCommandNoExceptionHandling(
					inputString, false, handler, true,
					new AsyncOperation<GeoElementND[]>() {

						@Override
						public void callback(GeoElementND[] result) {
							boolean success = result != null
									&& result[0] instanceof GeoNumberValue;
							Construction cons = algebraProcessor.getKernel()
									.getConstruction();
							if (success) {
								setNum((GeoNumberValue) result[0]);
								if (callback != null) {
									cons.setSuppressLabelCreation(oldVal);
									callback.callback(num);
								}
							} else {
								handler.showError(app.getLocalization()
										.getError("NumberExpected"));
							}
							if (callback0 != null) {
								callback0.callback(success);
							}
						}
					});
		} catch (Throwable e) {
			if (callback0 != null) {
				callback0.callback(false);
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setNum(GeoNumberValue num) {
		this.num = num;
	}

	public GeoNumberValue getNum() {
		return num;
	}

}
