package org.geogebra.common.move.views;

import org.geogebra.common.move.events.BaseEvent;

/**
 * Basic view for handling events
 * 
 * @author stefan
 */
public class BaseEventView extends BaseView<EventRenderable> {

	/**
	 * Notifies all view components of an event
	 * 
	 * @param event
	 *            The event that occured.
	 */
	public void onEvent(BaseEvent event) {
		for (EventRenderable view : viewComponents) {
			view.renderEvent(event);
		}
	}
}
