package org.geogebra.web.shared.ggtapi.models;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;

/**
 * Callback for Tube API material load.
 */
public abstract class MaterialCallback implements MaterialCallbackI {

	@Override
	public void onError(Throwable exception) {
		Log.error("Tube API error:" + exception.getMessage());
		// TODO
		ToolTipManagerW.sharedInstance().showBottomMessage(
				exception.getMessage(), true, null);
	}

	@Override
	public void onLoaded(List<Material> result, ArrayList<Chapter> meta) {
		// onLoaded(result);
	}

}
