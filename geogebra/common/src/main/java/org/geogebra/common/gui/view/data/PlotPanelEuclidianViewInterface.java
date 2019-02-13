package org.geogebra.common.gui.view.data;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Kernel;

/**
 * @author gabor common interface for plotpaneleuclidianviews
 *
 */
public interface PlotPanelEuclidianViewInterface
		extends EuclidianViewInterfaceCommon {

	/**
	 * @param kernel
	 *            sets the View id
	 */
	public void setViewId(Kernel kernel);

	/**
	 * sets the Evs params
	 */
	public void setEVParams();

	/**
	 * @return get the pixel offset concerning fonts.
	 */
	public double getPixelOffset();

	public void updateSizeKeepDrawables();

}
