package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.main.AppW;

/**
 * upload an image directly without dialog
 * 
 * @author Alicia
 *
 */
public class UploadImageWithoutDialog {

	private UploadImagePanel uploadImagePanel;
	private AppW app;

	/**
	 * @param app
	 *            application
	 */
	public UploadImageWithoutDialog(AppW app) {
		this.app = app;
		initGUI();
	}

	private void initGUI() {
		uploadImagePanel = new UploadImagePanel(this);
		uploadImagePanel.openFileBrowserDirectly();
	}

	/**
	 * insert image after selection
	 */
	public void insertImage() {
		String data = uploadImagePanel.getImageDataURL();
		String name = uploadImagePanel.getFileName();
		app.imageDropHappened(name, data);
	}

	/**
	 * sets move mode after image mode was cancelled
	 */
	public void setSelectMode() {
		if (!(app.has(Feature.MOW_IMAGE_DIALOG_UNBUNDLED)
				&& app.getMode() == EuclidianConstants.MODE_IMAGE)) {
			return;
		}
		app.setMode(EuclidianConstants.MODE_SELECT_MOW);
	}
}
