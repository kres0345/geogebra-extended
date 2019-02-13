package org.geogebra.web.geogebra3D.web.euclidian3D;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.web.full.euclidian.PopupMenuButtonWithDefault;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.main.AppW;

/**
 * Popup for axes and coordinate plane
 *
 */
public class AxesAndPlanePopup extends PopupMenuButtonWithDefault {

	private EuclidianView3D ev;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            icons
	 * @param ev
	 *            view
	 */
	public AxesAndPlanePopup(AppW app, ImageOrText[] data, EuclidianView3D ev) {
		super(app, data);
		this.ev = ev;
		this.setIcon(data[getIndexFromEV()]);
	}

	private int getIndexFromEV() {
		int ret = 0;
		if (ev.getShowXaxis()) {
			ret++;
		}
		if (ev.getShowPlane()) {
			ret += 2;
		}
		return ret;
	}

	/**
	 * Select item based on current EV state
	 */
	public void setIndexFromEV() {
		setSelectedIndex(getIndexFromEV());
	}

	/**
	 * set euclidian view from index
	 */
	public void setEVFromIndex() {
		int index = getSelectedIndex();
		ev.getSettings().beginBatch();
		ev.getSettings().setShowAxes(MyDouble.isOdd(index));
		ev.getSettings().setShowPlate(index >= 2);
		ev.getSettings().endBatch();
		((EuclidianView3DW) ev).doRepaint();
	}

	@Override
	public void update(Object[] geos) {	
		this.setVisible(
				geos.length == 0 && !EuclidianView.isPenMode(app.getMode())
						&& app.getMode() != EuclidianConstants.MODE_DELETE);
	}

}
