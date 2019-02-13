package org.geogebra.web.html5.event;

import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;

/**
 * Union of GWT key-handling interfaces
 * 
 * @author Zbynek Konecny
 *
 */
public interface KeyEventsHandler extends KeyDownHandler, KeyPressHandler,
        KeyUpHandler {
	// methods declared in parent interfaces
}
