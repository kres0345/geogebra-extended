package org.geogebra.web.full.gui.util;

import org.geogebra.web.full.gui.view.spreadsheet.SpreadsheetKeyListenerW;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;

public interface AdvancedFocusPanelI extends IsWidget {

	/**
	 * @param handler
	 *            event handler
	 * @param type
	 *            event type
	 * @return registration
	 */
	public <H extends EventHandler> HandlerRegistration addDomHandler(
			H handler, DomEvent.Type<H> type);

	public int getOffsetHeight();

	public int getOffsetWidth();

	public boolean isVisible();

	public boolean isAttached();

	public void setFocus(boolean b);

	public void setSelectedContent(String cs);

	public void setHeight(String string);

	public void setWidth(String string);
	
	public void addPasteHandler(SpreadsheetKeyListenerW sskl);

}
