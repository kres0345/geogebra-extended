package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.draw.DrawText;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Handling text editor in Euclidian View.
 * 
 * @author laszlo
 *
 */
public interface TextController {

	/**
	 * Creates in-place editable GeoText
	 * 
	 * @param loc
	 *            Text location.
	 * @return the created GeoText object.
	 */
	GeoText createText(GeoPointND loc);

	/**
	 * Edit text
	 * 
	 * @param geo
	 *            to edit
	 */
	void edit(GeoText geo);

	/**
	 * 
	 * @return the bounding rectangle of the current editor.
	 */
	public GRectangle getEditorBounds();

	/**
	 * Handles pointer press on text object.
	 */
	void handleTextPressed();

	/**
	 * Handles pointer release on text object.
	 * 
	 * @param drag
	 *            true if release is happened after drag.
	 * 
	 * @return true if release is handled.
	 */
	boolean handleTextReleased(boolean drag);

	/**
	 *
	 * @return GeoText that was hit by pointer.
	 */
	GeoText getHit();

	/**
	 * Wraps the text.
	 * 
	 * @param editText
	 *            text to wrap.
	 * @param d
	 *            drawable
	 * @return wrapped text
	 */
	String wrapText(String editText, DrawText d);

	/**
	 * @return if text editor is active.
	 */
	boolean isEditing();

	/**
	 * Stops the current editor.
	 */
	void stopEditing();

	/**
	 * update editor size
	 * 
	 * @param width
	 *            to set
	 * @param height
	 *            to set
	 */
	void resizeEditor(int width, int height);

	/**
	 * Resets controller for new construction.
	 */
	void reset();
}

