package org.geogebra.web.html5.gui.util;

import com.google.gwt.user.client.ui.UIObject;

public interface MathKeyboardListener {
	public void setFocus(boolean focus, boolean scheduled);

	public void ensureEditing();

	public UIObject asWidget();

	public String getText();

	public void onEnter(boolean b);

	public boolean needsAutofocus();

}
