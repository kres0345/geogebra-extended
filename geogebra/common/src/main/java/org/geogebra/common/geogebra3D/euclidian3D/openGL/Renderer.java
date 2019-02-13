package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3DListsForView;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.AnimationExportSlider;
import org.geogebra.common.main.App;

/**
 *
 * Used for openGL display.
 * <p>
 * It provides:
 * <ul>
 * <li>methods for displaying {@link Drawable3D}, with painting parameters</li>
 * <li>methods for picking object</li>
 * </ul>
 *
 * @author ggb3D
 *
 */
public abstract class Renderer implements RendererInterface {

	/**
	 * renderer type (shader or not)
	 */
	public enum RendererType {
		SHADER, GL2, NOT_SPECIFIED
	}

	private RendererType type;

	public static final int MOUSE_PICK_DEPTH = 10;

	// layers
	/** shift for planes layer to avoid z-fighting */
	public static final int LAYER_PLANE_SHIFT = -1;
	/** shift for angles layer to avoid z-fighting */
	public static final int LAYER_ANGLE_SHIFT = 1;
	/** min value for layers */
	public static final int LAYER_MIN = LAYER_PLANE_SHIFT;
	/** min value for layers (string for shaders) */
	public static final String LAYER_MIN_STRING_WITH_OP = "" + LAYER_MIN;
	/** factor for coding layers (for shaders) */
	public static final int LAYER_FACTOR_FOR_CODING = 2;
	/** default layer */
	public static final int LAYER_DEFAULT = 0;
    /** layer to ensure no z-fighting between text and its background */
    public static final int LAYER_FOR_TEXTS = 5;

	// other
	public Drawable3DListsForView drawable3DLists;

	public EuclidianView3D view3D;

	// for drawing
	protected CoordMatrix4x4 m_drawingMatrix; // matrix for drawing

	// /////////////////
	// primitives
	// private RendererPrimitives primitives;

	// /////////////////
	// geometries
	protected Manager geometryManager;

	// /////////////////
	// textures
	protected Textures textures;

	// /////////////////
	// arrows

	/** no arrows */
	static final public int ARROW_TYPE_NONE = 0;
	/** simple arrows */
	static final public int ARROW_TYPE_SIMPLE = 1;
	public static final float AMBIENT_0 = 0.5f;
	public static final float AMBIENT_1 = 0.4f;

	public boolean enableClipPlanes;
	protected boolean waitForUpdateClipPlanes = false;
	static final private float SQRT2_DIV2 = (float) Math.sqrt(2) / 2;
	public static final float[] LIGHT_POSITION_W = { SQRT2_DIV2, 0f,
			SQRT2_DIV2 };
	static final public float[] LIGHT_POSITION_D = { SQRT2_DIV2, 0f, SQRT2_DIV2,
			0f };
	public boolean needExportImage = false;

	private boolean exportImageForThumbnail = false;

	protected boolean waitForUpdateClearColor = false;

	public int left = 0;
	public int right = 640;
	public int bottom = 0;
	public int top = 480;

	public boolean waitForDisableStencilLines = false;

	protected double[] eyeToScreenDistance = new double[2];

	/** distance camera-near plane */
	private final static double PERSP_NEAR_MIN = 10;
	public double[] perspNear = { PERSP_NEAR_MIN, PERSP_NEAR_MIN };
	public double[] perspLeft = new double[2];
	public double[] perspRight = new double[2];
	public double[] perspBottom = new double[2];
	public double[] perspTop = new double[2];
	public double[] perspFar = new double[2];
	public double[] perspDistratio = new double[2];
	public double[] perspFocus = new double[2];
	public Coords perspEye;

	public double[] glassesEyeX = new double[2];
	public double[] glassesEyeX1 = new double[2];
	public double[] glassesEyeY = new double[2];
	public double[] glassesEyeY1 = new double[2];

	public static final int EYE_LEFT = 0;
	public static final int EYE_RIGHT = 1;
	public int eye = EYE_LEFT;

	public double obliqueX;
	public double obliqueY;
	private Coords obliqueOrthoDirection; // direction "orthogonal" to the
											// screen (i.e. not visible)
	private ExportType exportType = ExportType.NONE;
	private int export_n;
	private double export_val;
	private double export_min;
	private double export_max;
	private double export_step;
	private int export_i;
	private AnimationExportSlider export_num;
	public boolean waitForSetStencilLines = false;
	private Runnable export3DRunnable;

	//ARCore
	private CoordMatrix4x4 arCameraView;
	private CoordMatrix4x4 arModelMatrix;
	private CoordMatrix4x4 arCameraPerspective;
	private float arScaleFactor;

	/**
	 * background type (only for AR)
     * Order matters and corresponds to order in settings
	 */
	public enum BackgroundStyle {
        NONE, TRANSPARENT, OPAQUE
	}

	/**
	 * creates a renderer linked to an {@link EuclidianView3D}
	 *
	 * @param view
	 *            the {@link EuclidianView3D} linked to
	 * @param type
	 *            renderer type
	 */
	public Renderer(EuclidianView3D view, RendererType type) {

		// link to 3D view
		this.view3D = view;

		// type
		this.type = type;
	}

	/**
	 *
	 * @return new Textures object
	 */
	protected Textures newTextures() {
		return new Textures(this);
	}

	/**
	 * Restart ARCore session.
	 */
	public void setARShouldRestart() {
		// Override in RendererWithImplA.
	}

	/**
     * @param ret Hitting Direction from AR. Override in RendererWithImplA
	 */
	public void getHittingDirectionAR(Coords ret) {
		// nothing to do here
	}

	/**
	 * @param ret
     *            Hitting Origin from AR. Override in RendererWithImplA
	 */
	public void getHittingOriginAR(Coords ret) {
		// nothing to do here
	}

	/**
	 * dummy renderer (when no GL available)
	 */
	public Renderer() {

	}

	/**
	 * set the list of {@link Drawable3D} to be drawn
	 *
	 * @param dl
	 *            list of {@link Drawable3D}
	 */
	public void setDrawable3DLists(Drawable3DListsForView dl) {
		drawable3DLists = dl;
	}

	/**
	 *
	 */
	protected void updateViewAndDrawables() {

		view3D.update();
		view3D.getDrawList3D().updateManagerBuffers(this);
		view3D.updateOwnDrawablesNow();

		// update 3D drawables
		view3D.updateDrawables(drawable3DLists);

		// say that 3D view changed has been performed
		view3D.resetViewChanged();
	}

	/**
	 * init rendering values (clear color buffer, etc.)
	 */
	protected void initRenderingValues() {
		// clear color buffer
		if (!view3D.getCompanion().isStereoBuffered()) {
			clearColorBuffer();
		}
		initLighting();
		disableOpaqueSurfaces();
	}

	/**
	 * set runnable to do a 3D export on next frame
	 *
	 * @param runnable
	 *            export handler
	 */
	public void setExport3D(Runnable runnable) {
		export3DRunnable = runnable;
	}

	/**
	 * draw the scene
	 */
	public void drawScene() {

		// update 3D controller
		((EuclidianController3D) view3D.getEuclidianController())
				.updateInput3D();

		useShaderProgram();

		// clip planes
		if (waitForUpdateClipPlanes) {
			// Application.debug(enableClipPlanes);
			if (enableClipPlanes) {
				enableClipPlanes();
			} else {
				disableClipPlanes();
			}
			waitForUpdateClipPlanes = false;
		}

		// update 3D controller
		((EuclidianController3D) view3D.getEuclidianController()).update();

		// long time = System.currentTimeMillis();
		// update 3D view and drawables
		updateViewAndDrawables();
		// Log.debug("======= UPDATE : "+(System.currentTimeMillis() - time));

		if (needExportImage) {
			selectFBO();
		}

		if (waitForSetStencilLines) {
			setStencilLines();
		}

		if (waitForDisableStencilLines) {
			disableStencilLines();
		}

		if (waitForUpdateClearColor) {
			updateClearColor();
			waitForUpdateClearColor = false;
		}

		// init rendering values
		initRenderingValues();

		// time = System.currentTimeMillis();

		if (view3D.getProjection() == EuclidianView3D.PROJECTION_GLASSES) {

			// left eye
			setDrawLeft();
			clearDepthBuffer();
			setView();
			draw();

			// right eye
			setDrawRight();
			clearDepthBufferForSecondAnaglyphFilter();
			setView();
			draw();

		} else {
			if (view3D.getCompanion().isStereoBuffered()) {
				// we draw the same image on both left/right buffers
				setBufferLeft();
				clearColorBuffer();
				clearDepthBuffer();
				setView();
				draw();

				setBufferRight();
				clearColorBuffer();
				clearDepthBuffer();
				setView();
				draw();
			} else {
				clearDepthBuffer();
				setView();
				draw();
			}
		}

		// Log.debug("======= DRAW : "+(System.currentTimeMillis() - time));

		// prepare correct color mask for next clear
		setColorMask(ColorMask.ALL);

        endOfDrawScene();
	}

	protected void endOfDrawScene() {
        boolean nei = needExportImage;

        exportImage();

        if (nei) {
            unselectFBO();
        }

        if (export3DRunnable != null) {
            export3DRunnable.run();
            export3DRunnable = null;
        }
    }

	/**
	 * says that an export image is needed, and call immediate display
	 */
	public void needExportImage() {

		setExportImageForThumbnail(true);
		double scale = Math.min(MyXMLio.THUMBNAIL_PIXELS_X / getWidth(),
				MyXMLio.THUMBNAIL_PIXELS_Y / getHeight());

		needExportImage(scale, (int) (getWidth() * scale),
				(int) (getHeight() * scale));

	}

	/**
	 *
	 * @param scale
	 *            scale
	 * @return export image immediately
	 */
	public GBufferedImage getExportImage(double scale) {

		setExportImageForThumbnail(true);

		needExportImage(scale, (int) (getWidth() * scale),
				(int) (getHeight() * scale));

		return getExportImage();
	}

	private void setExportImageForThumbnail(boolean flag) {
		exportImageForThumbnail = flag;
	}

	/**
	 * @return whether exported image is for thumbnail
	 */
	protected boolean getExportImageForThumbnail() {
		return exportImageForThumbnail;
	}

	/**
	 * says that an export image is needed, and call immediate display
	 *
	 * @param scale
	 *            scale for export image
	 * @param forThumbnail
	 *            whether output is thumbnail
	 */
	public void needExportImage(double scale, boolean forThumbnail) {

		setExportImageForThumbnail(forThumbnail);
		needExportImage(scale, (int) (getWidth() * scale),
				(int) (getHeight() * scale));
	}

	/**
	 * @return an image containing last export image created
	 */
	public GBufferedImage getExportImage() {
		return null;
	}

	/**
	 * start animation for gif export
	 *
	 * @param gifEncoder
	 *            gif encoder
	 * @param num
	 *            slider to anime
	 * @param n
	 *            number of images
	 * @param val
	 *            start value
	 * @param min
	 *            slider min value
	 * @param max
	 *            slider max value
	 * @param step
	 *            slider step
	 */
	public void startAnimatedGIFExport(Object gifEncoder,
			AnimationExportSlider num, int n, double val, double min,
			double max, double step) {
		exportType = ExportType.ANIMATEDGIF;

		num.setValue(val);
		num.updateRepaint();
		export_i = 0;

		this.export_n = n;
		this.export_num = num;
		this.export_val = val;
		this.export_min = min;
		this.export_max = max;
		this.export_step = step;
		setGIFEncoder(gifEncoder);

		needExportImage(1, false);

	}

	/**
	 * set gif encoder
	 *
	 * @param gifEncoder
	 *            gif encoder
	 */
	protected void setGIFEncoder(Object gifEncoder) {
		// TODO make it abstract
	}

	/**
	 * says that we need an export image with scale, width and height
	 *
	 * @param scale
	 *            scale factor
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	abstract protected void needExportImage(double scale, int w, int h);

	/**
	 * set export image width and height
	 *
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	abstract protected void setExportImageDimension(int w, int h);

	protected void selectFBO() {
		// to be overridden
	}

	protected void unselectFBO() {
		// to be overridden
	}

	/**
	 * set drawing for left eye
	 */
	final protected void setDrawLeft() {
		if (view3D.getCompanion().isPolarized()) {
			// draw where stencil's value is 0
			setStencilFunc(0);
		} else if (view3D.getCompanion().isStereoBuffered()) {
			setBufferLeft();
			clearColorBuffer();
		}

		eye = EYE_LEFT;
		setColorMask();
	}

	/**
	 * set drawing for right eye
	 */
	final protected void setDrawRight() {
		if (view3D.getCompanion().isPolarized()) {
			// draw where stencil's value is 1
			setStencilFunc(1);
		} else if (view3D.getCompanion().isStereoBuffered()) {
			setBufferRight();
			clearColorBuffer();
		}

		if (view3D.getCompanion().isStereoBuffered()
				&& !view3D.getCompanion().wantsStereo()) {
			// draw again left eye if no stereo glasses detected
			eye = EYE_LEFT;
		} else {
			eye = EYE_RIGHT;
		}

		setColorMask();
	}

	/**
	 * set drawing to left buffer (when stereo buffered)
	 */
	abstract protected void setBufferLeft();

	/**
	 * set drawing to right buffer (when stereo buffered)
	 */
	abstract protected void setBufferRight();

	/**
	 * clear color buffer
	 */
	abstract protected void clearColorBuffer();

	/**
	 * clear depth buffer
	 */
	abstract protected void clearDepthBuffer();

	/**
	 * clear depth buffer for anaglyph glasses, between first and second eye
	 */
	abstract protected void clearDepthBufferForSecondAnaglyphFilter();

	/**
	 * set value for the stencil function (equal to value)
	 *
	 * @param value
	 *            stencil value
	 */
	abstract protected void setStencilFunc(int value);

	/**
	 * do export image if needed
	 */
	abstract protected void exportImage();

	protected void drawTranspNotCurved() {
		disableCulling();
		drawable3DLists.drawTransp(this);
		drawable3DLists.drawTranspClosedNotCurved(this);
		enableCulling();
	}

	private void drawTransp() {

		setLight(1);

		drawTranspNotCurved();

		setCullFaceFront();
		drawable3DLists.drawTranspClosedCurved(this); // draws inside parts
		if (drawable3DLists.containsClippedSurfacesInclLists()) {
			enableClipPlanesIfNeeded();
			drawable3DLists.drawTranspClipped(this); // clipped surfaces
														// back-faces
			disableClipPlanesIfNeeded();
		}
		setCullFaceBack();
		drawable3DLists.drawTranspClosedCurved(this); // draws outside parts
		if (drawable3DLists.containsClippedSurfacesInclLists()) {
			enableClipPlanesIfNeeded();
			drawable3DLists.drawTranspClipped(this); // clipped surfaces
														// back-faces
			disableClipPlanesIfNeeded();
		}

		setLight(0);

	}

	/**
	 * switch GL_LIGHT0 / GL_LIGHT1
	 *
	 * @param light
	 *            GL_LIGHT0 or GL_LIGHT1
	 */
	abstract protected void setLight(int light);

	private void drawNotTransp() {

		setLight(1);

		enableBlending();

		// TODO improve this !
		enableCulling();
		setCullFaceFront();
		drawable3DLists.drawNotTransparentSurfaces(this);
		drawable3DLists.drawNotTransparentSurfacesClosed(this); // draws inside
																// parts
		if (drawable3DLists.containsClippedSurfacesInclLists()) {
			enableClipPlanesIfNeeded();
			drawable3DLists.drawNotTransparentSurfacesClipped(this); // clipped
																		// surfaces
																		// back-faces
			disableClipPlanesIfNeeded();
		}
		setCullFaceBack();
		drawable3DLists.drawNotTransparentSurfaces(this);
		drawable3DLists.drawNotTransparentSurfacesClosed(this); // draws outside
																// parts
		if (drawable3DLists.containsClippedSurfacesInclLists()) {
			enableClipPlanesIfNeeded();
			drawable3DLists.drawNotTransparentSurfacesClipped(this); // clipped
																		// surfaces
																		// back-faces
			disableClipPlanesIfNeeded();
		}

		setLight(0);
	}

	/**
	 * disable shine (specular)
	 */
	public void disableShine() {
		// only implemented with shaders
	}

	/**
	 * enable shine (specular)
	 */
	public void enableShine() {
		// only implemented with shaders
	}

	/**
	 * disable opaque surfaces
	 */
	public void disableOpaqueSurfaces() {
		// only implemented with shaders
	}

	/**
	 * enable opaque surfaces
	 */
	public void enableOpaqueSurfaces() {
		// only implemented with shaders
	}

	/**
	 * draw face-to screen parts (labels, ...)
	 */
	protected void drawFaceToScreen() {

		// drawing labels and texts

		enableAlphaTest();
		disableLighting();
		enableBlending();

		enableTexturesForText();
		drawable3DLists.drawLabel(this);
		drawable3DLists.drawForAbsoluteText(this, false);

		disableTextures();

		if (enableClipPlanes) {
			disableClipPlanes();
		}

		view3D.drawMouseCursor(this);

		if (enableClipPlanes) {
			enableClipPlanes();
		}

	}

	/**
	 * draw face-to screen parts at end (absolute texts, ...)
	 */
	protected void drawFaceToScreenEnd() {

		// drawing texts

		enableAlphaTest();
		disableLighting();
		enableBlending();

		enableTexturesForText();

		drawable3DLists.drawForAbsoluteText(this, true);

		disableTextures();

	}

	/**
	 * sets if clip planes have to be enabled
	 *
	 * @param flag
	 *            flag
	 */
	public void setEnableClipPlanes(boolean flag) {
		waitForUpdateClipPlanes = true;
		enableClipPlanes = flag;
	}

	/**
	 * enables clip planes
	 */
	abstract protected void enableClipPlanes();

	/**
	 * disables clip planes
	 */
	abstract protected void disableClipPlanes();

	/**
	 * enable clipping if needed
	 */
	public void enableClipPlanesIfNeeded() {
		if (!enableClipPlanes) {
			enableClipPlanes();
		}
	}

	/**
	 * disable clipping if needed
	 */
	public void disableClipPlanesIfNeeded() {
		if (!enableClipPlanes) {
			disableClipPlanes();
		}
	}

	/**
	 * init drawing matrix to view3D toScreen matrix
	 */
	abstract protected void setMatrixView();

	abstract protected void setProjectionMatrixViewForAR(CoordMatrix4x4 cameraView,
                                                         CoordMatrix4x4 cameraPerspective,
                                                         CoordMatrix4x4 modelMatrix,
                                                         float scaleFactor);

	public final void fromARCoreCoordsToGGBCoords(Coords coords, Coords ret) {
		fromARCoreCoordsToGGBCoords(coords, arModelMatrix, arScaleFactor, ret);
	}

	abstract protected void fromARCoreCoordsToGGBCoords(Coords coords, CoordMatrix4x4 modelMatrix,
                                                        float scaleFactor, Coords ret);

	/**
	 * reset to projection matrix only
	 */
	abstract protected void unsetMatrixView();

	protected void draw() {

		// labels
		if (enableClipPlanes) {
			enableClipPlanes();
		}
		drawFaceToScreen();

		// init drawing matrix to view3D toScreen matrix
		setMatrixView();

		setLightPosition();
		setLight(0);

		// drawing the cursor
		enableLighting();
		disableAlphaTest();
		enableCulling();
		if (needExportImage) {
			// we don't want mouse cursor on export image
			setCullFaceBack(); // needed for further calculations
		} else {
			drawCursor();
		}

		// drawing hidden part
		enableAlphaTest();
		disableTextures();
		drawable3DLists.drawHiddenNotTextured(this);
		enableDashHidden();
		drawable3DLists.drawHiddenTextured(this);

		// ////////////////////////////
		// draw surfaces
		enableShine();

		// draw hidden surfaces
		enableFading(); // from RendererShaders -- check when enable textures if
						// already done
		drawNotTransp();

		// draw opaque surfaces for packed buffers
		if (geometryManager.packBuffers()) {
			setLight(1);
			enableOpaqueSurfaces();
			disableCulling();
			((ManagerShadersElementsGlobalBufferPacking) geometryManager)
					.drawSurfaces(this);
			((ManagerShadersElementsGlobalBufferPacking) geometryManager)
					.drawSurfacesClosed(this);
			enableClipPlanesIfNeeded();
			((ManagerShadersElementsGlobalBufferPacking) geometryManager)
					.drawSurfacesClipped(this);
			disableClipPlanesIfNeeded();
			enableCulling();
			disableOpaqueSurfaces();
			setLight(0);
		}
		disableTextures();
		disableAlphaTest();

		// drawing transparents parts
		disableDepthMask();
		enableFading();
		drawTransp();
		enableDepthMask();

		disableTextures();
		enableCulling();
		disableBlending();

		// drawing hiding parts
		setColorMask(ColorMask.NONE); // no writing in color buffer
		setCullFaceFront(); // draws inside parts
		drawable3DLists.drawClosedSurfacesForHiding(this); // closed surfaces
															// back-faces
		if (drawable3DLists.containsClippedSurfacesInclLists()) {
			enableClipPlanesIfNeeded();
			drawable3DLists.drawClippedSurfacesForHiding(this); // clipped
																// surfaces
																// back-faces
			disableClipPlanesIfNeeded();
		}
		disableCulling();
		drawable3DLists.drawSurfacesForHiding(this); // non closed surfaces
		// getGL().glColorMask(true,true,true,true);
		setColorMask();

		// re-drawing transparents parts for better transparent effect
		// TODO improve it !
		enableFading();
		disableDepthMask();
		enableBlending();
		drawTransp();
		enableDepthMask();
		disableTextures();

		// drawing hiding parts
		setColorMask(ColorMask.NONE); // no writing in color buffer
		disableBlending();
		enableCulling();
		setCullFaceBack(); // draws inside parts
		drawable3DLists.drawClosedSurfacesForHiding(this); // closed surfaces
															// front-faces
		if (drawable3DLists.containsClippedSurfacesInclLists()) {
			enableClipPlanesIfNeeded();
			drawable3DLists.drawClippedSurfacesForHiding(this); // clipped
																// surfaces
																// back-faces
			disableClipPlanesIfNeeded();
		}
		setColorMask();

		// re-drawing transparents parts for better transparent effect
		// TODO improve it !
		enableFading();
		disableDepthMask();
		enableBlending();
		drawTransp();
		enableDepthMask();

		// ////////////////////////
		// end of surfaces
		disableShine();

		// drawing not hidden parts
		enableDash();
		enableCulling();
		setCullFaceBack();
		drawable3DLists.draw(this);

		// draw cursor at end
		if (enableClipPlanes) {
			disableClipPlanes();
		}
		if (!needExportImage) {
			view3D.drawCursorAtEnd(this);
		}

		disableLighting();
		disableDepthTest();
		unsetMatrixView();

		// absolute texts
		enableTexturesForText();
		drawFaceToScreenEnd();

		enableDepthTest();
		enableLighting();
	}

	/**
	 * draw view cursor
	 */
	private void drawCursor() {
		if (enableClipPlanes) {
			disableClipPlanes();
		}
		setCullFaceBack();
		view3D.drawCursor(this);
		if (enableClipPlanes) {
			enableClipPlanes();
		}
	}

	/**
	 * draw outline for surfaces
	 */
	abstract protected void drawSurfacesOutline();

	/*
	 * private void drawWireFrame() {
	 *
	 * getGL().glPushAttrib(GLlocal.GL_ALL_ATTRIB_BITS);
	 *
	 * getGL().glDepthMask(false); getGL().glPolygonMode(GLlocal.GL_FRONT,
	 * GLlocal.GL_LINE);getGL().glPolygonMode(GLlocal.GL_BACK, GLlocal.GL_LINE);
	 *
	 * getGL().glLineWidth(5f);
	 *
	 * getGL().glEnable(GLlocal.GL_LIGHTING);
	 * getGL().glDisable(GLlocal.GL_LIGHT0);
	 * getGL().glDisable(GLlocal.GL_CULL_FACE);
	 * getGL().glDisable(GLlocal.GL_BLEND);
	 * getGL().glEnable(GLlocal.GL_ALPHA_TEST);
	 *
	 * drawable3DLists.drawTransp(this);
	 * drawable3DLists.drawTranspClosedNotCurved(this);
	 * drawable3DLists.drawTranspClosedCurved(this); if
	 * (drawable3DLists.containsClippedSurfaces()){ enableClipPlanesIfNeeded();
	 * drawable3DLists.drawTranspClipped(this); disableClipPlanesIfNeeded(); }
	 *
	 * getGL().glPopAttrib(); }
	 */

	// /////////////////////////////////////////////////
	//
	// pencil methods
	//
	// ///////////////////////////////////////////////////

	/**
	 * sets the color
	 *
	 * @param color
	 *            (r,g,b,a) vector
	 *
	 */
	final public void setColor(Coords color) {
		setColor((float) color.getX(), (float) color.getY(),
				(float) color.getZ(), (float) color.getW());

	}

	/**
	 * sets the color
	 *
	 * @param color
	 *            (r,g,b,a)
	 */
	final public void setColor(GColor color) {
		setColor(color.getRed() / 255f, color.getGreen() / 255f,
				color.getBlue() / 255f, color.getAlpha() / 255f);
	}

	/**
	 * sets the color
	 *
	 * @param r
	 *            red
	 * @param g
	 *            green
	 * @param b
	 *            blue
	 * @param a
	 *            alpha
	 *
	 */
	abstract protected void setColor(float r, float g, float b, float a);

	// drawing matrix

	/**
	 * sets the matrix in which coord sys the pencil draws.
	 *
	 * @param a_matrix
	 *            the matrix
	 */
	public void setMatrix(CoordMatrix4x4 a_matrix) {
		m_drawingMatrix = a_matrix;
	}

	/**
	 * gets the matrix describing the coord sys used by the pencil.
	 *
	 * @return the matrix
	 */
	public CoordMatrix4x4 getMatrix() {
		return m_drawingMatrix;
	}

	// /////////////////////////////////////////////////////////
	// drawing geometries

	final public Manager getGeometryManager() {
		return geometryManager;
	}

	// /////////////////////////////////////////////////////////
	// textures

	public Textures getTextures() {
		return textures;
	}

	/**
	 * draws a 3D cross cursor
	 *
	 * @param cursorType
	 *            cursor type
	 */
	final public void drawCursor(int cursorType) {

		if (!PlotterCursor.isTypeAlready(cursorType)) {
			disableLighting();
		}

		initMatrix();
		geometryManager.draw(geometryManager.cursor.getIndex(cursorType));
		resetMatrix();

		if (!PlotterCursor.isTypeAlready(cursorType)) {
			enableLighting();
		}
	}

	/**
	 * draws a 3D cross cursor; doesn't modify the lighting
	 * 
	 * @param dotMatrix
	 *            matrix for target dot
	 * @param circleMatrix
	 *            matrix for target circle
	 *
	 */
	final public void drawTarget(CoordMatrix4x4 dotMatrix,
			CoordMatrix4x4 circleMatrix) {
		disableLighting();
		disableDepthMask();
		enableBlending();
		setMatrix(dotMatrix);
		initMatrix();
		geometryManager.draw(
				geometryManager.cursor.getIndex(PlotterCursor.TYPE_SPHERE));
		setMatrix(circleMatrix);
		initMatrix();
		geometryManager.draw(geometryManager.cursor
				.getIndex(PlotterCursor.TYPE_TARGET_CIRCLE));
		resetMatrix();
		disableBlending();
		enableDepthMask();
		enableLighting();
	}

	/**
	 * Draw completing cursor for 3D input.
	 *
	 * @param value
	 *            value
	 * @param out
	 *            out
	 */
	final public void drawCompletingCursor(double value, boolean out) {

		initMatrix();
		setLineWidth(PlotterCompletingCursor.WIDTH);
		enableBlending();
		geometryManager.getCompletingCursor().drawCircle(out);
		geometryManager.getCompletingCursor().drawCompleting(value, out);
		disableBlending();
		resetMatrix();

	}

	/**
	 * draws a view button
	 */
	public void drawViewInFrontOf() {
		// Application.debug("ici");
		initMatrix();
		disableBlending();
		geometryManager.draw(geometryManager.getViewInFrontOf().getIndex());
		enableBlending();
		resetMatrix();
	}

	/**
	 * draws mouse cursor
	 */
	final public void drawMouseCursor() {

		initMatrixForFaceToScreen();
		disableBlending();
		disableCulling();
		geometryManager.draw(geometryManager.getMouseCursor().getIndex());
		enableCulling();
		enableBlending();
		resetMatrix();
	}

	/*
	 * draws the text s
	 *
	 * @param x x-coord
	 *
	 * @param y y-coord
	 *
	 * @param s text
	 *
	 * @param colored says if the text has to be colored
	 *
	 * public void drawText(float x, float y, String s, boolean colored){
	 *
	 *
	 * //if (true) return;
	 *
	 * getGL().glMatrixMode(GLlocal.GL_TEXTURE); getGL().glLoadIdentity();
	 *
	 * getGL().glMatrixMode(GLlocal.GL_MODELVIEW);
	 *
	 *
	 * initMatrix(); initMatrix(view3D.getUndoRotationMatrix());
	 *
	 *
	 * textRenderer.begin3DRendering();
	 *
	 * if (colored) textRenderer.setColor(textColor);
	 *
	 *
	 * float textScaleFactor = DEFAULT_TEXT_SCALE_FACTOR/((float)
	 * view3D.getScale());
	 *
	 *
	 * if (x<0) x=x-(s.length()-0.5f)*8; //TODO adapt to police size
	 *
	 * textRenderer.draw3D(s, x*textScaleFactor,//w / -2.0f * textScaleFactor,
	 * y*textScaleFactor,//h / -2.0f * textScaleFactor, 0, textScaleFactor);
	 *
	 * textRenderer.end3DRendering();
	 *
	 *
	 *
	 * resetMatrix(); //initMatrix(m_view3D.getUndoRotationMatrix());
	 * resetMatrix(); //initMatrix();
	 *
	 * }
	 */

	// ///////////////////////
	// FPS

	/*
	 * double displayTime = 0; int nbFrame = 0; double fps = 0;
	 *
	 * private void drawFPS(){
	 *
	 * if (displayTime==0) displayTime = System.currentTimeMillis();
	 *
	 * nbFrame++;
	 *
	 * double newDisplayTime = System.currentTimeMillis();
	 *
	 *
	 * //displayTime = System.currentTimeMillis(); if (newDisplayTime >
	 * displayTime+1000){
	 *
	 *
	 *
	 * fps = 1000*nbFrame/(newDisplayTime - displayTime); displayTime =
	 * newDisplayTime; nbFrame = 0; }
	 *
	 *
	 *
	 *
	 * getGL().glMatrixMode(GLlocal.GL_TEXTURE); getGL().glLoadIdentity();
	 *
	 * getGL().glMatrixMode(GLlocal.GL_MODELVIEW);
	 *
	 * getGL().glPushMatrix(); getGL().glLoadIdentity();
	 *
	 *
	 * textRenderer.begin3DRendering();
	 *
	 *
	 * textRenderer.setColor(Color.BLACK);
	 *
	 *
	 * textRenderer.draw3D("FPS="+ ((int) fps),left,bottom,0,1);
	 *
	 * textRenderer.end3DRendering();
	 *
	 * getGL().glPopMatrix(); }
	 */

	abstract protected void pushSceneMatrix();

	public enum PickingType {
		POINT_OR_CURVE, SURFACE, LABEL
	}

	// ////////////////////////////////
	// LIGHTS
	// ////////////////////////////////

	protected void setLightPosition() {
		setLightPosition(getLightPosition());
	}

	/**
	 *
	 * @return light position
	 */
	abstract protected float[] getLightPosition();

	/**
	 * set light position
	 *
	 * @param values
	 *            attribute values
	 */
	abstract protected void setLightPosition(float[] values);

	/**
	 * set light ambiant and diffuse values (white lights)
	 *
	 */
	abstract protected void setLightAmbiantDiffuse(float ambiant0,
			float diffuse0, float ambiant1, float diffuse1);

	// ////////////////////////////////
	// clear color
	public void setWaitForUpdateClearColor() {
		waitForUpdateClearColor = true;
	}

	final protected void updateClearColor() {

		GColor c = view3D.getApplyedBackground();
		float r, g, b;
		if (view3D.getProjection() == EuclidianView3D.PROJECTION_GLASSES
				&& !view3D.getCompanion().isPolarized()
				&& !view3D.getCompanion().isStereoBuffered()) { // grayscale for
																// anaglyph
																// glasses
			r = (float) (c.getGrayScale() / 255);
			g = r;
			b = r;
		} else {
			r = (float) c.getRed() / 255;
			g = view3D.isShutDownGreen() ? 0 : (float) c.getGreen() / 255;
			b = (float) c.getBlue() / 255;
		}

		setClearColor(r, g, b, 1.0f);
	}

	// ////////////////////////////////
	// initializations

	/**
	 * init shaders (when used)
	 */
	protected void initShaders() {
		// no shader here
	}

	/**
	 * Use the shaderProgram that got linked during the init part.
	 */
	protected void useShaderProgram() {
		// no shader here
	}

	/**
	 *
	 * @return new geometry manager
	 */
	abstract protected Manager createManager();

	abstract protected void setColorMaterial();

	abstract protected void setLightModel();

	abstract protected void setAlphaFunc();

	// projection mode

	public int getLeft() {
		return left;
	}

	public int getRight() {
		return right;
	}

	public int getWidth() {
		return right - left;
	}

	/**
	 * Used for dip density devices
	 *
	 * @return height in pixels
	 */
	public int getWidthInPixels() {
		return getWidth();
	}

	public int getBottom() {
		return bottom;
	}

	public int getTop() {
		return top;
	}

	public int getHeight() {
		return top - bottom;
	}

	/**
	 * Used for dip density devices
	 *
	 * @return height in pixels
	 */
	public int getHeightInPixels() {
		return getHeight();
	}

	final public double getVisibleDepth() {
		return getWidth() * 2;
	} // keep visible objects at twice center-to-right distance

	public int getNear() {
		return -getWidth();
	}

	public int getFar() {
		return getWidth();
	}

	/**
	 * for a line described by (o,v), return the min and max parameters to draw
	 * the line
	 *
	 * @param minmax
	 *            initial interval
	 * @param o
	 *            origin of the line
	 * @param v
	 *            direction of the line
	 * @param extendedDepth
	 *            says if it looks to real depth bounds, or working depth bounds
	 * @return interval to draw the line
	 */
	public double[] getIntervalInFrustum(double[] minmax, Coords o, Coords v,
			boolean extendedDepth) {

		double left1 = (getLeft() - o.get(1)) / v.get(1);
		double right1 = (getRight() - o.get(1)) / v.get(1);
		updateIntervalInFrustum(minmax, left1, right1);

		double top1 = (getTop() - o.get(2)) / v.get(2);
		double bottom1 = (getBottom() - o.get(2)) / v.get(2);
		updateIntervalInFrustum(minmax, top1, bottom1);

		double halfDepth = getVisibleDepth() / 2.0;
		double front = (-halfDepth - o.get(3)) / v.get(3);
		double back = (halfDepth - o.get(3)) / v.get(3);
		updateIntervalInFrustum(minmax, front, back);

		return minmax;
	}

	/**
	 * return the intersection of intervals [minmax] and [v1,v2]
	 *
	 * @param minmax
	 *            initial interval
	 * @param v1
	 *            first value
	 * @param v2
	 *            second value
	 * @return intersection interval
	 */
	private static double[] updateIntervalInFrustum(double[] minmax, double v1,
			double v2) {
		double vMin = v1;
		double vMax = v2;

		if (vMin > vMax) {
			vMin = v2;
			vMax = v1;
		}

		if (vMin > minmax[0]) {
			minmax[0] = vMin;
		}

		if (vMax < minmax[1]) {
			minmax[1] = vMax;
		}

		return minmax;
	}

	/**
	 * set up the view
	 */
	abstract protected void setView();

	public void setWaitForDisableStencilLines() {
		waitForDisableStencilLines = true;
	}

	abstract protected void disableStencilLines();

	public void setWaitForSetStencilLines() {
		waitForSetStencilLines = true;
	}

	abstract protected void setStencilLines();

	protected void setProjectionMatrixForPicking() {

		switch (view3D.getProjection()) {
		default:
		case EuclidianView3D.PROJECTION_ORTHOGRAPHIC:
			viewOrtho();
			break;
		case EuclidianView3D.PROJECTION_GLASSES:
			viewGlasses();
			break;
		case EuclidianView3D.PROJECTION_PERSPECTIVE:
			viewPersp();
			break;
		case EuclidianView3D.PROJECTION_OBLIQUE:
			viewOblique();
			break;
		}

	}

	/**
	 * Update projection matrix for view's projection.
	 */
	public final void setProjectionMatrix() {
		if (view3D.isARDrawing()) {
			setProjectionMatrixViewForAR(arCameraView, arCameraPerspective, arModelMatrix,
					arScaleFactor);
		} else {
			switch (view3D.getProjection()) {
				default:
				case EuclidianView3D.PROJECTION_ORTHOGRAPHIC:
					viewOrtho();
					break;
				case EuclidianView3D.PROJECTION_PERSPECTIVE:
					viewPersp();
					break;
				case EuclidianView3D.PROJECTION_GLASSES:
					viewGlasses();
					break;
				case EuclidianView3D.PROJECTION_OBLIQUE:
					viewOblique();
					break;
			}
		}
	}

	/**
	 * Set Up An Ortho View regarding left, right, bottom, front values
	 *
	 */
	abstract protected void viewOrtho();

	/**
	 * Update perspective for eye position.
	 *
	 * @param left
	 *            left eye distance
	 * @param right
	 *            right eye distance
	 */
	final public void setNear(double left, double right) {
		eyeToScreenDistance[EYE_LEFT] = left;
		eyeToScreenDistance[EYE_RIGHT] = right;
		updatePerspValues();
		updatePerspEye();
	}

	protected void updatePerspValues() {

		for (int i = 0; i < 2; i++) {
			perspNear[i] = eyeToScreenDistance[i] - getVisibleDepth() / 2.0;
			if (perspNear[i] < PERSP_NEAR_MIN) {
				perspNear[i] = PERSP_NEAR_MIN;
			}

			perspFocus[i] = -eyeToScreenDistance[i] + view3D.getScreenZOffset();

			// ratio so that distance on screen plane are not changed
			perspDistratio[i] = perspNear[i] / eyeToScreenDistance[i];

			// frustum
			perspLeft[i] = getLeft() * perspDistratio[i];
			perspRight[i] = getRight() * perspDistratio[i];
			perspBottom[i] = getBottom() * perspDistratio[i];
			perspTop[i] = getTop() * perspDistratio[i];

			// distance camera-far plane
			perspFar[i] = perspNear[i] + getVisibleDepth();
		}
	}

	private void updatePerspEye() {
		perspEye = new Coords(glassesEyeX[1], glassesEyeY[1],
				-perspFocus[EYE_LEFT], 1); // perspFocus is negative
	}

	/**
	 *
	 * @return coords of the eye (in real coords) when perspective projection
	 */
	public Coords getPerspEye() {
		return perspEye;
	}

	/**
	 *
	 * @return eyes separation (half of, in real coords)
	 */
	public double getEyeSep() {
		return (glassesEyeX[0] - glassesEyeX[1]) / 2;
	}

	abstract protected void viewPersp();

	/**
	 * Update glasses coordinates.
	 */
	public void updateGlassesValues() {
		for (int i = 0; i < 2; i++) {
			// eye values
			glassesEyeX[i] = view3D.getEyeX(i);
			glassesEyeY[i] = view3D.getEyeY(i);
			// eye values for frustum
			glassesEyeX1[i] = glassesEyeX[i] * perspDistratio[i];
			glassesEyeY1[i] = glassesEyeY[i] * perspDistratio[i];
		}
	}

	abstract protected void viewGlasses();

	protected void setColorMask() {

		if (view3D.getProjection() == EuclidianView3D.PROJECTION_GLASSES
				&& !view3D.getCompanion().isPolarized()
				&& !view3D.getCompanion().isStereoBuffered()) {
			if (eye == EYE_LEFT) {
				setColorMask(ColorMask.RED); // cyan
			} else {
				setColorMask(view3D.isGlassesShutDownGreen() ? ColorMask.BLUE
						: ColorMask.BLUE_AND_GREEN); // red
			}
		} else {
			setColorMask(ColorMask.ALL);
		}

	}

	abstract public void setColorMask(final int type);

	public enum ExportType {
		NONE, ANIMATEDGIF, THUMBNAIL_IN_GGBFILE, PNG, CLIPBOARD, UPLOAD_TO_GEOGEBRATUBE
	}

	/**
	 * Update oblique projection attributes.
	 */
	public void updateProjectionObliqueValues() {
		double angle = Math.toRadians(view3D.getProjectionObliqueAngle());
		obliqueX = -view3D.getProjectionObliqueFactor() * Math.cos(angle);
		obliqueY = -view3D.getProjectionObliqueFactor() * Math.sin(angle);
		obliqueOrthoDirection = new Coords(obliqueX, obliqueY, -1, 0);
	}

	abstract protected void viewOblique();

	/**
	 *
	 * @return x oblique factor
	 */
	public double getObliqueX() {
		return obliqueX;
	}

	/**
	 *
	 * @return y oblique factor
	 */
	public double getObliqueY() {
		return obliqueY;
	}

	public Coords getObliqueOrthoDirection() {
		return obliqueOrthoDirection;
	}

	/**
	 * Set Up An Ortho View after setting left, right, bottom, front values
	 *
	 * @param x
	 *            left
	 * @param y
	 *            bottom
	 * @param w
	 *            width
	 * @param h
	 *            height
	 *
	 */
	public void setView(int x, int y, int w, int h) {
		left = x - w / 2;
		bottom = y - h / 2;
		right = left + w;
		top = bottom + h;

		if (needExportImage) {
			return;
		}

		switch (view3D.getProjection()) {
		default:
		case EuclidianView3D.PROJECTION_ORTHOGRAPHIC:
			updateOrthoValues();
			break;
		case EuclidianView3D.PROJECTION_PERSPECTIVE:
			updatePerspValues();
			updatePerspEye();
			break;
		case EuclidianView3D.PROJECTION_GLASSES:
			updatePerspValues();
			updateGlassesValues();
			updatePerspEye();
			if (view3D.getCompanion().isPolarized()) {
				setWaitForSetStencilLines();
			}
			break;
		case EuclidianView3D.PROJECTION_OBLIQUE:
			updateProjectionObliqueValues();
			break;
		}

		setView();

		view3D.setViewChanged();
		view3D.setWaitForUpdate();
	}

	/**
	 * Export image to clipboardd (async)
	 */
	public void exportToClipboard() {
		exportType = ExportType.CLIPBOARD;
		needExportImage(App.getMaxScaleForClipBoard(view3D), true);

	}

	/**
	 * Export image (async), start Tube upload after that.
	 */
	public void uploadToGeoGebraTube() {
		exportType = ExportType.UPLOAD_TO_GEOGEBRATUBE;
		needExportImage();
	}

	/**
	 * Double.POSITIVE_INFINITY for parallel projections
	 *
	 * @return eye to screen distance
	 */
	public double getEyeToScreenDistance() {
		if (view3D.getProjection() == EuclidianView3D.PROJECTION_PERSPECTIVE
				|| view3D
						.getProjection() == EuclidianView3D.PROJECTION_GLASSES) {
			return eyeToScreenDistance[EYE_LEFT];
		}

		return Double.POSITIVE_INFINITY;
	}

	/**
	 *
	 * @param val
	 *            value
	 * @return first power of 2 greater than val
	 */
	public static final int firstPowerOfTwoGreaterThan(int val) {

		int ret = 1;
		while (ret < val) {
			ret *= 2;
		}
		return ret;
	}

	/**
	 * init the renderer
	 */
	public void init() {

		initShaders();

		textures = newTextures();
		geometryManager = createManager();

		// GL_LIGHT0 & GL_LIGHT1
		float diffuse0 = 1f - AMBIENT_0;
		float diffuse1 = 1f - AMBIENT_1;

		setLightAmbiantDiffuse(AMBIENT_0, diffuse0, AMBIENT_1, diffuse1);

		// material and light
		setColorMaterial();

		// setLight(GLlocal.GL_LIGHT0);
		setLightModel();
		enableLightingOnInit();

		// common enabling
		enableDepthTest();
		setDepthFunc();
		enablePolygonOffsetFill();
		initCulling();

		// blending
		setBlendFunc();
		enableBlending();
		updateClearColor();

		setAlphaFunc();

		// normal anti-scaling
		enableNormalNormalized();

		// textures
		initTextures();

		// reset euclidian view
		view3D.reset();

		// ensure that animation is on (needed when undocking/docking 3D view)
		resumeAnimator();

	}

	protected void initTextures() {
		textures.init();
	}

	protected void enableLightingOnInit() {
		enableLighting();
	}

	protected void initCulling() {
		enableCulling();
		setCullFaceBack();
	}

	/**
	 * set the depth function
	 */
	abstract protected void setDepthFunc();

	/**
	 * enable polygon offset fill
	 */
	abstract protected void enablePolygonOffsetFill();

	/**
	 * set the blend function
	 */
	abstract protected void setBlendFunc();

	/**
	 * enables normalization for normals
	 */
	abstract protected void enableNormalNormalized();

	/**
	 * enable text textures
	 */
	public void enableTexturesForText() {
		enableTextures();
	}

	/**
	 *
	 * @return hitting
	 */
	public Hitting getHitting() {
		return null;
	}

	/**
	 * reset the point center
	 */
	public void resetCenter() {
		// only used with shaders
	}

	/**
	 *
	 * @return the 3D view attached
	 */
	final public EuclidianView3D getView() {
		return view3D;
	}

	public CoordMatrix4x4 getToScreenMatrix() {
		return view3D.getToScreenMatrixForGL();
	}

	/**
	 * @param flag
	 *            image export flag
	 */
	final public void setNeedExportImage(boolean flag) {
		// Log.printStacktrace("" + flag);
		needExportImage = flag;
	}

	protected ExportType getExportType() {
		return exportType;
	}

	protected AnimationExportSlider getExportNum() {
		return export_num;
	}

	protected double getExportVal() {
		return export_val;
	}

	protected double getExportMax() {
		return export_max;
	}

	protected double getExportMin() {
		return export_min;
	}

	protected int getExportI() {
		return export_i;
	}

	protected double getExportN() {
		return export_n;
	}

	protected double getExportStep() {
		return export_step;
	}

	protected RendererType getType() {
		return type;
	}

	protected void setType(RendererType t) {
		type = t;
	}

	protected void setExportVal(ExportType t) {
		exportType = t;
	}

	protected void setExportStep(double step) {
		export_step = step;
	}

	protected void setExportVal(double val) {
		export_val = val;
	}

	protected void setExportI(int i) {
		export_i = i;
	}

	protected void setExportType(ExportType type) {
		exportType = type;
	}

	/**
	 *
	 * @return true (default) if reduce "window" for clipping box
	 */
	public boolean reduceForClipping() {
		return true;
	}

	/**
	 * @param cameraView
	 *            camera view flattened matrix
	 * @param cameraPerspective
	 *            camera perspective flattened matrix
	 * @param modelMatrix
	 *            model flattened matrix
	 * @param scaleFactor
	 *            scale factor
	 */
	public void setARMatrix(CoordMatrix4x4 cameraView, CoordMatrix4x4 cameraPerspective,
                            CoordMatrix4x4 modelMatrix, float scaleFactor) {
		arCameraView = cameraView;
		arCameraPerspective = cameraPerspective;
		arModelMatrix = modelMatrix;
		arScaleFactor = scaleFactor;
	}

	/**
	 * Set scale for AR
	 */
	public void setScaleFactor() {
		// only for AR
	}

	public void setBackgroundColor() {
	    // only for AR
    }

	public void setBackgroundStyle(BackgroundStyle backgroundStyle) {
        // only for AR
	}

	/**
	 * @return background for AR, null otherwise
	 */
	public BackgroundStyle getBackgroundStyle() {
		return null;
	}
}
