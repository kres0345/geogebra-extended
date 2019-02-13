package org.geogebra.web.full.gui.color;

import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GeoElement.FillType;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.web.full.gui.util.ButtonPopupMenu;
import org.geogebra.web.full.gui.util.GeoGebraIconW;
import org.geogebra.web.full.gui.util.SelectionTableW;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Color chooser button for MOW.
 * 
 * @author Laszlo Gal
 *
 */
public class MOWColorButton extends ColorPopupMenuButton {
	private static final int FILL_TYPES_COUNT = 5;
	private SelectionTableW fillTable;
	private FillType[] fillTypes = { FillType.STANDARD, FillType.HATCH,
			FillType.DOTTED, FillType.CROSSHATCHED, FillType.HONEYCOMB };

	/**
	 * @param app
	 *            GGB application.
	 */
	public MOWColorButton(AppW app) {
		super(app, 0, true);
		ButtonPopupMenu pp = (ButtonPopupMenu) getMyPopup();
		pp.addStyleName("mowColorPopup");
		if (!app.has(Feature.MOW_COLOR_FILLING_LINE)) {
			createFillTable();
			pp.getPanel().add(fillTable);
		}
	}

	private void createFillTable() {
		ImageOrText[] icons = new ImageOrText[FILL_TYPES_COUNT];
		for (int i = 0; i < FILL_TYPES_COUNT; i++) {
			icons[i] = GeoGebraIconW.createFillStyleIcon(i);
		}
		fillTable = new SelectionTableW(icons, 1, 5,
				SelectionTable.MODE_ICON, false);
		fillTable.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				handlePopupActionEvent();
			}

		});
	}

	@Override
	protected void setColors() {
		setColorSet(GeoGebraColorConstants.getMOWPopupArray());
	}

	@Override
	protected String getSliderPostfix() {
		return " %";
	}

	/**
	 * Shows/hides fill table.
	 * 
	 * @param b
	 *            true if filling is enabled.
	 */
	public void setFillEnabled(boolean b) {
		fillTable.setVisible(b);
		if (b && !app.has(Feature.MOW_COLORPOPUP_IMPROVEMENTS)) {
			getMyPopup().setHeight("125px");
		}
	}

	/**
	 * @return fill type id
	 */
	public FillType getSelectedFillType() {
		int idx = fillTable.getSelectedIndex();
		return idx != -1 ? fillTypes[idx] : FillType.STANDARD;
	}

	/**
	 * Sets the original fill type of the geo.
	 * 
	 * @param fillType
	 *            the type to select initially.
	 */
	public void setFillType(FillType fillType) {
		for (int i = 0; i < fillTypes.length; i++) {
			if (fillTypes[i] == fillType) {
				fillTable.setSelectedIndex(i);
			}
		}
	}
	//
	// @Override
	// public void onClick(ClickEvent event) {
	// if (event.getSource() == fillTable) {
	// fireActionPerformed();
	// return;
	// }
	// super.onClick(event);
	// }
}
