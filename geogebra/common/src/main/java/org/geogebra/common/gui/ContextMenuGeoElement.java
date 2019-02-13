package org.geogebra.common.gui;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.CoordStyle;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.SpreadsheetTraceManager;

/**
 * @author gabor
 * 
 *         Superclass for ContextMenuGeoElements in Web and Desktop
 *
 */
public abstract class ContextMenuGeoElement {

	private static final double[] zoomFactors = { 4.0, 2.0, 1.5, 1.25,
			1.0 / 1.25, 1.0 / 1.5, 0.5, 0.25 };
	/** x:y ratios */
	protected static final double[] axesRatios = { 1.0 / 1000.0, 1.0 / 500.0,
			1.0 / 200.0, 1.0 / 100.0, 1.0 / 50.0, 1.0 / 20.0, 1.0 / 10.0,
			1.0 / 5.0, 1.0 / 2.0, 1, 2, 5, 10, 20, 50, 100, 200, 500, 1000 };
	/** selected elements */
	private ArrayList<GeoElement> geos;
	/** current element */
	private String geoLabel;
	/** application */
	public App app;
	/** whether to restrict selection to a single geo */
	protected boolean justOneGeo = false;

	/**
	 * @param app
	 *            application
	 */
	public ContextMenuGeoElement(App app) {
		this.app = app;
	}
	
	/**
	 * 
	 * @param geo
	 *            geo
	 * @param addHTMLtag
	 *            whether to wrap in &lt;html&gt
	 * @return description
	 */
	protected String getDescription(GeoElement geo, boolean addHTMLtag) {
		String title = geo.getLongDescriptionHTML(false, addHTMLtag);
		if (title.length() > 80) {
			title = geo.getNameDescriptionHTML(false, addHTMLtag);
		}
		return title;
	}

	/**
	 * Switch to cartesian coordinates
	 */
	public void cartesianCoordsCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof CoordStyle) {
				CoordStyle point1 = (CoordStyle) geo1;
				point1.setMode(Kernel.COORD_CARTESIAN);
				geo1.updateRepaint();
			}
		}
		app.getKernel().getConstruction().getUndoManager().storeUndoInfo(true);
	}

	/**
	 * Switch to polar coordinates
	 */
	public void polarCoorsCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof CoordStyle) {
				CoordStyle point1 = (CoordStyle) geo1;
				point1.setMode(Kernel.COORD_POLAR);
				geo1.updateRepaint();
			}
		}
		app.getKernel().getConstruction().getUndoManager().storeUndoInfo(true);
	}

	/**
	 * Switch to 3D cartesian coordinates
	 */
	public void cartesianCoords3dCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof CoordStyle) {
				CoordStyle point1 = (CoordStyle) geo1;
				point1.setMode(Kernel.COORD_CARTESIAN_3D);
				geo1.updateRepaint();
			}
		}
		app.getKernel().getConstruction().getUndoManager().storeUndoInfo(true);
	}

	/**
	 * Switch to spherical coordinates
	 */
	public void sphericalCoordsCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof CoordStyle) {
				CoordStyle point1 = (CoordStyle) geo1;
				point1.setMode(Kernel.COORD_SPHERICAL);
				geo1.updateRepaint();
			}
		}
		app.getKernel().getConstruction().getUndoManager().storeUndoInfo(true);
	}

	/**
	 * Change line equation to implicit
	 */
	public void equationImplicitEquationCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof GeoLine && !(geo1 instanceof GeoSegment)) {
				GeoLine line1 = (GeoLine) geo1;
				line1.setMode(GeoLine.EQUATION_IMPLICIT);
				line1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	/**
	 * Change line equation to explicit
	 */
	public void equationExplicitEquationCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof GeoLine && !(geo1 instanceof GeoSegment)) {
				GeoLine line1 = (GeoLine) geo1;
				line1.setMode(GeoLine.EQUATION_EXPLICIT);
				line1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	/**
	 * Change line equation to general
	 */
	public void equationGeneralLineEquationCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof GeoLine && !(geo1 instanceof GeoSegment)) {
				GeoLine line1 = (GeoLine) geo1;
				line1.setMode(GeoLine.EQUATION_GENERAL);
				line1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	/**
	 * Change line equation to parametric
	 */
	public void parametricFormCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof GeoLine && !(geo1 instanceof GeoSegment)) {
				GeoLine line1 = (GeoLine) geo1;
				line1.setMode(GeoLine.PARAMETRIC);
				line1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	/**
	 * Change equation type to implicit / expanded
	 */
	public void implicitConicEquationCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();
		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof EquationValue) {
				EquationValue conic1 = (EquationValue) geo1;
				conic1.setToImplicit();
				geo1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	/**
	 * Change quadric / conic equation type to specific
	 */
	public void equationConicEqnCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof GeoQuadricND) {
				GeoQuadricND conic1 = (GeoQuadricND) geo1;
				conic1.setToSpecific();
				conic1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	/**
	 * Change parabola equation type to vertex
	 */
	public void equationVertexEquationCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1.getClass() == GeoConic.class) {
				GeoConic conic1 = (GeoConic) geo1;
				conic1.setToVertexform();
				conic1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	/**
	 * Change parabola equation type to conic form
	 */
	public void equationConicformEquationCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1.getClass() == GeoConic.class) {
				GeoConic conic1 = (GeoConic) geo1;
				conic1.setToConicform();
				conic1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	/**
	 * Change conic equation type to explicit
	 */
	public void equationExplicitConicEquationCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1.getClass() == GeoConic.class) {
				GeoConic conic1 = (GeoConic) geo1;
				conic1.setToExplicit();
				conic1.updateRepaint();
			}
		}
		app.storeUndoInfo();
	}

	/**
	 * @param removeParents
	 *            whether to also remove parents (eg vertices of a ploygon)
	 */
	public void deleteCmd(boolean removeParents) {

		ArrayList<GeoElement> geos2 = checkOneGeo();

		// geo.remove();
		for (int i = geos2.size() - 1; i >= 0; i--) {
			// maybe we killed siblings -> geos2 list shrinks
			if (i < geos2.size()) {
				GeoElement geo1 = geos2.get(i);
				// clear bounding box if geo if is there any
				if (geo1.isShape()) {
					Drawable d = (Drawable) app.getActiveEuclidianView()
							.getDrawableFor(geo1);
					if (d != null) {
						d.getBoundingBox().resetBoundingBox();
					}
				}
				if (removeParents) {
					if (geo1.getParentAlgorithm() != null) {
						for (GeoElement ge : geo1.getParentAlgorithm().input) {
							ge.removeOrSetUndefinedIfHasFixedDescendent();
						}
					}
				}
				geo1.removeOrSetUndefinedIfHasFixedDescendent();
			}
		}
		app.storeUndoInfo();
	}

	/**
	 * @return single selected geo or all selected geos (see constructor)
	 */
	public ArrayList<GeoElement> checkOneGeo() {
		if (justOneGeo) {
			ArrayList<GeoElement> ret = new ArrayList<>();
			ret.add(getGeo());
			return ret;
		}

		return getGeos();
	}

	/**
	 * Show text dialog
	 */
	public void editCmd() {
		app.getDialogManager().showTextDialog((GeoText) getGeo());
	}

	/**
	 * Show rename dialog
	 */
	public void renameCmd() {
		app.getDialogManager().showRenameDialog(getGeo(), true,
				getGeo().getLabelSimple(), true);
	}

	/**
	 * Fix or unfix slider
	 * 
	 * @param num
	 *            slider
	 */
	public void fixObjectNumericCmd(final GeoNumeric num) {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1.isGeoNumeric()) {
				((GeoNumeric) geo1).setSliderFixed(!num.isSliderFixed());
				geo1.updateRepaint();
			} else {
				geo1.setFixed(!num.isSliderFixed());
			}

		}
		app.storeUndoInfo();
	}

	/**
	 * Lock / unlock object
	 */
	public void fixObjectCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1.isGeoNumeric()) {
				((GeoNumeric) geo1)
						.setSliderFixed(!((GeoNumeric) geo1).isSliderFixed());
				geo1.updateRepaint();
			} else {
				if (geo1.isFixable()) {
					geo1.setFixed(!geo1.isLocked());
					geo1.updateRepaint();
				}
			}

		}
		getGeo().updateVisualStyle(GProperty.COMBINED);
		app.getKernel().notifyRepaint();
		app.storeUndoInfo();
	}

	/**
	 * Fix / unfix checkbox
	 */
	public void fixCheckboxCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoBoolean geo1 = (GeoBoolean) geos2.get(i);
			geo1.setCheckboxFixed(!geo1.isCheckboxFixed());

		}
		app.storeUndoInfo();
	}

	/**
	 * @return whether selected geos are showing label
	 */
	public boolean isLabelShown() {
		return isLabelShown(checkOneGeo());
	}

	/**
	 * @param geos2
	 *            geos
	 * @return whether all geos show label
	 */
	public boolean isLabelShown(ArrayList<GeoElement> geos2) {
		boolean show = true;
		for (int i = geos2.size() - 1; i >= 0; i--) {
			show = show && geos2.get(i).isLabelVisible();
		}
		return show;
	}

	/**
	 * Toggle label visibility (result same for all geos)
	 */
	public void showLabelCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();
		boolean show = isLabelShown(geos2);
		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			geo1.setLabelVisible(!show);
			geo1.updateRepaint();

		}
		app.storeUndoInfo();
	}

	/**
	 * Toggle object visibility
	 */
	public void showObjectCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		boolean newVisibility = !geos2.get(0).isSetEuclidianVisible();
		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			geo1.setEuclidianVisible(newVisibility);
			// do not show bounding box if shape is not shown
			if (!newVisibility && geo1.isShape()) {
				Drawable d = (Drawable) app.getActiveEuclidianView()
						.getDrawableFor(geo1);
				if (d != null) {
					d.getBoundingBox().resetBoundingBox();
				}
			}
			geo1.updateRepaint();
		}
		app.storeUndoInfo();
	}

	/**
	 * Toggle auxiliary flag
	 */
	public void showObjectAuxiliaryCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1.isAlgebraShowable()) {
				geo1.setAuxiliaryObject(!geo1.isAuxiliaryObject());
				geo1.updateRepaint();
			}

		}
		app.storeUndoInfo();
	}

	/**
	 * Open properties dialog, focus objects
	 */
	public void openPropertiesDialogCmd() {
		app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS,
				checkOneGeo());
	}

	/**
	 * Change equation type to user input
	 * 
	 * @param inputElement
	 *            equation
	 */
	public void inputFormCmd(final EquationValue inputElement) {
		inputElement.setToUser();
		((GeoElement) inputElement).updateRepaint();
		app.storeUndoInfo();
	}

	/**
	 * Toggle tracing
	 */
	public void traceCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();
		// if there is at least 1 geo, which has no trace, all geo will have
		// trace, otherwise, if all geo has trace, tracing will be set to false
		if (app.has(Feature.MOW_IMPROVE_CONTEXT_MENU)) {
			boolean istracing = isTracing();
			for (int i = geos2.size() - 1; i >= 0; i--) {
				GeoElement geo1 = geos2.get(i);
				if (geo1.isTraceable()) {
					((Traceable) geo1).setTrace(!istracing);
					geo1.updateRepaint();
				}
			}
		} else { // every geo's behaviour will be changed for the inverse
			for (int i = geos2.size() - 1; i >= 0; i--) {
				GeoElement geo1 = geos2.get(i);
				if (geo1.isTraceable()) {
					((Traceable) geo1).setTrace(!((Traceable) geo1).getTrace());
					geo1.updateRepaint();
				}

			}
		}
		app.storeUndoInfo();
	}

	/**
	 * @return whether all selected geos are tracing
	 */
	public boolean isTracing() {
		ArrayList<GeoElement> geos2 = checkOneGeo();
		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1.isTraceable()) {
				if (!((Traceable) geo1).getTrace()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Toggle animation falg
	 */
	public void animationCmd() {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1.isAnimatable()) {
				geo1.setAnimating(!(geo1.isAnimating()
						&& app.getKernel().getAnimatonManager().isRunning()));
				geo1.updateRepaint();
			}

		}
		app.storeUndoInfo();
		app.getActiveEuclidianView().repaint();

		// automatically start animation when animating was turned on
		if (getGeo().isAnimating()) {
			getGeo().getKernel().getAnimatonManager().startAnimation();
		}
	}

	/**
	 * Pin or unpin to screen
	 * 
	 * @param isSelected
	 *            whether to pin the geos
	 */
	public void pinCmd(boolean isSelected) {
		ArrayList<GeoElement> geos2 = checkOneGeo();

		for (int i = geos2.size() - 1; i >= 0; i--) {
			GeoElement geo1 = geos2.get(i);
			if (geo1 instanceof AbsoluteScreenLocateable && !geo1.isGeoList()) {
				AbsoluteScreenLocateable geoText = (AbsoluteScreenLocateable) geo1;
				boolean flag = !geoText.isAbsoluteScreenLocActive();
				if (flag) {
					// convert real world to screen coords
					int x = app.getActiveEuclidianView()
							.toScreenCoordX(geoText.getRealWorldLocX());
					int y = app.getActiveEuclidianView()
							.toScreenCoordY(geoText.getRealWorldLocY());
					geoText.setAbsoluteScreenLoc(x, y);
					if (geoText.isGeoImage() && geoText.getKernel()
							.getApplication().has(Feature.MOW_PIN_IMAGE)) {
						((GeoImage) geoText).updateScaleAndLocation();
					}
				} else {
					// convert screen coords to real world
					if (geoText.isGeoImage() && geoText.getKernel()
							.getApplication().has(Feature.MOW_PIN_IMAGE)) {
						((GeoImage) geoText).screenToReal();
					} else {
						double x = app.getActiveEuclidianView()
								.toRealWorldCoordX(
										geoText.getAbsoluteScreenLocX());
						double y = app.getActiveEuclidianView()
								.toRealWorldCoordY(
										geoText.getAbsoluteScreenLocY());
						geoText.setRealWorldLoc(x, y);
					}
				}
				geoText.setAbsoluteScreenLocActive(flag);
				geoText.updateRepaint();
			} else if (getGeo().isPinnable()) {
				EuclidianStyleBarStatic.applyFixPosition(geos2, isSelected,
						app.getActiveEuclidianView());
			}
		}

		getGeo().updateVisualStyle(GProperty.COMBINED);
		app.getKernel().notifyRepaint();
		app.storeUndoInfo();
	}

	/**
	 * Show popup to choose geo
	 * 
	 * @param cmdGeo
	 *            first geo
	 * @param sGeos
	 *            selected geos
	 * @param gs
	 *            all geos
	 * @param v
	 *            view
	 * @param l
	 *            location
	 */
	public void geoActionCmd(GeoElement cmdGeo, ArrayList<GeoElement> sGeos,
			ArrayList<GeoElement> gs, EuclidianView v, GPoint l) {
		if (EuclidianConstants.isMoveOrSelectionMode(v.getMode())) { // change selection
															// to geo clicked
			// AbstractApplication.debug(geo.getLabelSimple());
			app.getSelectionManager().clearSelectedGeos(false); // repaint done
																// next step
			app.getSelectionManager().addSelectedGeo(cmdGeo);

			// update the geo lists and show the popup again with the new
			// selection
			sGeos.clear();
			sGeos.add(cmdGeo);
			if (app.getGuiManager() != null) {
				app.getGuiManager().showPopupChooseGeo(sGeos, gs, v, l);
			}

		} else { // use geo clicked to process mode
			Hits hits = new Hits();
			hits.add(cmdGeo);
			v.getEuclidianController().processMode(hits, false);
		}
	}

	/**
	 * Add or remove spreadsheet trace
	 */
	public void recordToSpreadSheetCmd() {
		SpreadsheetTraceManager traceManager = app.getTraceManager();
		if (!traceManager.isTraceGeo(getGeo())) {
			traceManager.addSpreadsheetTraceGeo(getGeo());
		} else {
			traceManager.removeSpreadsheetTraceGeo(getGeo());
		}
	}

	/**
	 * @return first selected geo
	 */
	protected GeoElement getGeo() {
		return app.getKernel().lookupLabel(geoLabel);
	}

	/**
	 * @param geo
	 *            single geo
	 */
	protected void setGeo(GeoElement geo) {
		this.geoLabel = geo.getLabelSimple();
	}

	/**
	 * @param index
	 *            index
	 * @return n-th zoom factor
	 */
	public static double getZoomFactor(int index) {
		return zoomFactors[index];
	}

	/**
	 * @return number of zoom factors
	 */
	public static int getZoomFactorLength() {
		return zoomFactors.length;
	}

	/**
	 * @return selected geos
	 */
	protected ArrayList<GeoElement> getGeos() {
		return geos;
	}

	/**
	 * Update the selection of geos
	 * 
	 * @param geos
	 *            selected geos
	 */
	protected void setGeos(ArrayList<GeoElement> geos) {
		this.geos = geos;
	}

	/**
	 * Cuts selected elements
	 */
	public void cutCmd() {
		app.getCopyPaste().copyToXML(app,
				app.getSelectionManager().getSelectedGeos(), false);
		deleteCmd(true);
	}

	/**
	 * Duplicates selected elements
	 */
	public void duplicateCmd() {
		if (app.getSelectionManager().getSelectedGeos().isEmpty()) {
			app.getSelectionManager().addSelectedGeo(getGeo());
		}
		app.getCopyPaste().copyToXML(app,
				app.getSelectionManager().getSelectedGeos(), false);
		app.getCopyPaste().pasteFromXML(app, false);
	}

	/**
	 * @param geo
	 *            element
	 * @return whether "input form" is acceptable equation form for the geo
	 */
	public boolean needsInputFormItem(GeoElement geo) {
		if (Equation.isAlgebraEquation(geo)) {
			if (geo.isGeoLine()) {
				return ((GeoLine) geo)
						.getToStringMode() != GeoLine.EQUATION_USER;
			}
			if (geo.isGeoPlane()) {
				return ((GeoPlaneND) geo)
						.getToStringMode() != GeoLine.EQUATION_USER;
			}
			if (geo.isGeoConic() || geo.isGeoQuadric()) {
				return ((GeoQuadricND) geo)
						.getToStringMode() != GeoConicND.EQUATION_USER;
			}
			if (geo instanceof GeoImplicit) {
				return !((GeoImplicit) geo).isInputForm();
			}
		}
		return false;
	}
}
