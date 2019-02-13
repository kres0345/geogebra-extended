package org.geogebra.web.full.gui.util;

import java.util.HashMap;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.IComboListener;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.main.AppW;

public class PointStylePopup extends PopupMenuButtonW
		implements IComboListener, SetLabels {

	private static final int DEFAULT_SIZE = 4;
	static HashMap<Integer, Integer> pointStyleMap;
	int mode;
	private PointStyleModel model;
	private boolean euclidian3D;

	/**
	 * @param app
	 *            application
	 * @param mode
	 *            mode
	 * @param hasSlider
	 *            whether to include size slider
	 * @param model
	 *            model
	 * @param isTealBorder
	 *            whether to use MOW teal
	 * @return point stylle popup
	 */
	public static PointStylePopup create(AppW app, int mode, boolean hasSlider,
			PointStyleModel model, boolean isTealBorder) {
		
		pointStyleMap = new HashMap<>();
		for (int i = 0; i < EuclidianView.getPointStyleLength(); i++) {
			pointStyleMap.put(EuclidianView.getPointStyle(i), i);
		}

		ImageOrText[] pointStyleIcons = new ImageOrText[EuclidianView
				.getPointStyleLength()];
		for (int i = 0; i < EuclidianView.getPointStyleLength(); i++) {
			pointStyleIcons[i] = GeoGebraIconW
					.createPointStyleIcon(EuclidianView.getPointStyle(i));
		}

		PointStylePopup popup = new PointStylePopup(app, pointStyleIcons, 2,
				SelectionTable.MODE_ICON, true,
				hasSlider, model, isTealBorder);
		popup.mode = mode;
		return popup;
	}

	/**
	 * @param app
	 *            application
	 * @param mode
	 *            mode
	 * @param model
	 *            model
	 * @param isTealBorder
	 *            whether to use MOW teal
	 * @return point stylle popup
	 */
	public static PointStylePopup create(AppW app, int mode,
			PointStyleModel model, boolean isTealBorder) {
		return new PointStylePopup(app, null, 1, 
				SelectionTable.MODE_ICON, false, true, model, isTealBorder);
	}

	/**
	 * @param app
	 *            application
	 * @param data
	 *            point style icons
	 * @param rows
	 *            number of rows
	 * @param tableMode
	 *            selection mode
	 * @param hasTable
	 *            whether table is used
	 * @param hasSlider
	 *            whether size slider is used
	 * @param model
	 *            options model
	 * @param isTealBorder
	 *            whether to use MOW teal
	 */
	public PointStylePopup(AppW app, ImageOrText[] data, Integer rows,
			SelectionTable tableMode, boolean hasTable, boolean hasSlider,
			PointStyleModel model,
			boolean isTealBorder) {
		super(app, data, rows, -1, tableMode, hasTable, hasSlider, null,
				isTealBorder);
		getMyPopup().addStyleName("pointSizeSlider");
		this.model = model;
		euclidian3D = false;
	}

	public void setModel(PointStyleModel model) {
		this.model = model;
	}

	@Override
	public void update(Object[] geos) {
		updatePanel(geos);
	}

	@Override
	public Object updatePanel(Object[] geos) {
		model.setGeos(geos);
		
		if (!model.hasGeos()) {
			this.setVisible(false);
			return null;
		}
		
		boolean geosOK = model.checkGeos(); 
		
		this.setVisible(geosOK);

		if (geosOK) {
			getMyTable().setVisible(!euclidian3D);

			model.updateProperties();

			PointProperties geo0 = (PointProperties) model.getGeoAt(0);
			if (hasSlider()) {
				setSliderValue(geo0.getPointSize());
			}

			setSelectedIndex(pointStyleMap.get(euclidian3D ? 0 : geo0
			        .getPointStyle()));

			this.setKeepVisible(EuclidianConstants.isMoveOrSelectionMode(mode));
		}
		return this;
	}

	@Override
	public void handlePopupActionEvent() {
		super.handlePopupActionEvent();
		model.applyChanges(getSelectedIndex());
	}

	@Override
	public ImageOrText getButtonIcon() {
		if (getSelectedIndex() > -1) {
			return GeoGebraIconW
					.createPointStyleIcon(EuclidianView
							.getPointStyle(this.getSelectedIndex()));
		}
		return new ImageOrText();
	}
	
	@Override 
	public int getSliderValue() {
		int val = super.getSliderValue();
		return val == -1 ? DEFAULT_SIZE : val;
	}

	@Override
	public void setSelectedIndex(int index) {
		super.setSelectedIndex(index);
	}

	@Override
	public void addItem(String item) {
	    // TODO Auto-generated method stub
	}

	public boolean isEuclidian3D() {
		return euclidian3D;
	}

	public void setEuclidian3D(boolean euclidian3d) {
		euclidian3D = euclidian3d;
	}

	@Override
	public void clearItems() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setLabels() {
		// Overridden in MowPointStyle
	}

}
