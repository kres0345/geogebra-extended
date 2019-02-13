package org.geogebra.common.move.events;

/**
 * @author gabor
 * 
 *         Online events happen, when we are connected to the internet
 * 
 *
 */
public abstract class OnLineEvent extends BaseEvent {

	/**
	 * @param name
	 * 
	 *            Creates a new Online event, if name is null, it will be like
	 *            anonymus function
	 */
	public OnLineEvent(String name) {
		super(name);
	}

}
