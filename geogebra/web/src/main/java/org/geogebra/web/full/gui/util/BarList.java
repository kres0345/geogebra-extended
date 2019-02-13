package org.geogebra.web.full.gui.util;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

import com.google.gwt.user.client.ui.ListBox;

public class BarList extends ListBox {

	private App app;
	private Localization loc;
	private int barCount;

	/**
	 * @param app
	 *            application
	 */
	public BarList(App app) {
		this.app = app;
		loc = app.getLocalization();
	}

	/**
	 * Update visibility and content of this panel.
	 * 
	 * @param enabled
	 *            whether this should be visible
	 */
	public void update(boolean enabled) {
		setVisible(enabled);
		if (!enabled) {
			return;
		}

		int idx = getSelectedIndex();
		clear();
		addItem(loc.getMenu("AllBars"));
		for (int i = 1; i < getBarCount() + 1; i++) {
			addItem(app.getLocalization().getPlain("BarA", i + ""));
		}
		if (idx != -1) {
			setSelectedIndex(idx);
		}
	}

	public int getBarCount() {
		return barCount;
	}

	/**
	 * @param barCount
	 *            number of bars
	 */
	public void setBarCount(int barCount) {
		this.barCount = barCount;
		update(true);
	}
}
