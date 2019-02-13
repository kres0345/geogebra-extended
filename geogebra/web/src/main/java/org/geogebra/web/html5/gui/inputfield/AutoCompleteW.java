package org.geogebra.web.html5.gui.inputfield;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.App;

import com.google.gwt.user.client.ui.Widget;

public interface AutoCompleteW {
	public boolean getAutoComplete();

	public List<String> resetCompletions();

	public List<String> getCompletions();

	public void setFocus(boolean b, boolean sv);

	public void insertString(String text);

	public ArrayList<String> getHistory();

	public String getText();

	public void setText(String s);

	public int getAbsoluteLeft();

	public int getAbsoluteTop();

	public boolean isSuggesting();

	public void requestFocus();

	public Widget toWidget();

	public void autocomplete(String s);

	public void updatePosition(AbstractSuggestionDisplay sug);

	public boolean isForCAS();

	public String getCommand();

	public App getApplication();
}
