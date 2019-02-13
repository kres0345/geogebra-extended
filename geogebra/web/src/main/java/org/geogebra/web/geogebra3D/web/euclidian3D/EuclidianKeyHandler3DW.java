package org.geogebra.web.geogebra3D.web.euclidian3D;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GlobalKeyDispatcherW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

/**
 * Class to handle tab key properly for {@link EuclidianView3DW}
 *
 * @author laszlo
 *
 */
public class EuclidianKeyHandler3DW implements KeyUpHandler, KeyDownHandler, KeyPressHandler {
	private AccessibilityManagerInterface am;
	private GlobalKeyDispatcherW gkd;

	/**
	 * Constructor.
	 *
	 * @param app
	 *            {@link AppW}
	 */
	public EuclidianKeyHandler3DW(AppW app) {
		am = app.getAccessibilityManager();
		am.setTabOverGeos(true);
		gkd = app.getGlobalKeyDispatcher();
	}

	/**
	 * @param canvas
	 *            to add the key handler to.
	 */
	public void listenTo(Canvas canvas) {
		canvas.addKeyUpHandler(this);
		canvas.addKeyDownHandler(this);
		canvas.addKeyPressHandler(this);
	}

	private boolean isTabOverGui(KeyEvent<?> event) {
		if (am.isTabOverGeos()) {
			return false;
		}

		int key = event.getNativeEvent().getKeyCode();
		return key == KeyCodes.KEY_TAB || key == KeyCodes.KEY_SPACE;
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		if (!isTabOverGui(event)) {
			gkd.onKeyPress(event);
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (!isTabOverGui(event)) {
			gkd.onKeyDown(event);
		}
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		if (!isTabOverGui(event)) {
			gkd.onKeyUp(event);
		}
	}
}
