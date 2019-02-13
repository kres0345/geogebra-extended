package org.geogebra.desktop.euclidian.event;

import java.awt.event.FocusEvent;

/**
 * @author judit
 *
 */
public class FocusListenerD
		extends org.geogebra.common.euclidian.event.FocusListener
		implements java.awt.event.FocusListener {

	public FocusListenerD(Object listener) {
		setListenerClass(listener);
	}

	@Override
	public void focusGained(FocusEvent e) {
		GFocusEventD event = GFocusEventD.wrapEvent(e);
		wrapFocusGained(event);
		event.release();
	}

	@Override
	public void focusLost(FocusEvent e) {
		GFocusEventD event = GFocusEventD.wrapEvent(e);
		wrapFocusLost(event);
		event.release();
	}

}
