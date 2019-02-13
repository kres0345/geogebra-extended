package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;

public interface RendererImplInterface {
	/**
	 * enable flag
	 * 
	 * @param flag
	 *            GL flag
	 */
	public abstract void glEnable(int flag);

	/**
	 * disable flag
	 * 
	 * @param flag
	 *            GL flag
	 */
	public abstract void glDisable(int flag);

	/**
	 * clear buffers corresponding to flag
	 * 
	 * @param flag
	 *            buffers flag
	 */
	abstract public void glClear(int flag);

	/**
	 * clear depth buffer
	 */
	abstract public void clearDepthBuffer();

	// GL flags getters
	abstract public int getGL_BLEND();

	abstract public int getGL_CULL_FACE();

	abstract public int getGL_COLOR_BUFFER_BIT();

	abstract public int getGL_DEPTH_BUFFER_BIT();

	abstract public int getGL_DEPTH_TEST();

	abstract public void disableCulling();

	public abstract void setClipPlanes(double[][] minMax);

	public abstract void setColor(float r, float g, float b, float a);

	public abstract void initMatrix();

	public abstract void initMatrixForFaceToScreen();

	public abstract void resetMatrix();

	public abstract void updateOrthoValues();

	public abstract void enableTextures();

	public abstract void disableTextures();

	public abstract void enableFading();

	public abstract void enableDash();

	public abstract void enableDashHidden();

	public abstract void setLight(int light);

	public abstract void setColorMaterial();

	public abstract void setLightModel();

	public abstract void setAlphaFunc();

	public abstract void setView();

    void glViewPort();

	public abstract void setStencilLines();

	public abstract void viewOrtho();

	public abstract void viewPersp();

	public abstract void viewGlasses();

	public abstract void viewOblique();

	public abstract Manager createManager();

	public abstract float[] getLightPosition();

	public abstract void drawSurfacesOutline();

	public abstract void enableClipPlanes();

	public abstract void disableClipPlanes();

	public abstract void useShaderProgram();

	public abstract void draw();

	public abstract void dispose();

	public abstract void updatePerspValues();

	public abstract void updateGlassesValues();

	public abstract void updateProjectionObliqueValues();

	public abstract void enableTexturesForText();

	public abstract void initRenderingValues();

	public abstract void drawFaceToScreenAbove();

	public abstract void drawFaceToScreenBelow();

	public abstract void enableLightingOnInit();

	public abstract void initCulling();

	public abstract void drawTranspNotCurved();

	public abstract void setCullFaceFront();

	public abstract void setCullFaceBack();

	abstract public void initShaders();

	public abstract void disableShine();

	public abstract void enableShine();

	public abstract void disableOpaqueSurfaces();

	public abstract void enableOpaqueSurfaces();

	abstract public void setBufferLeft();

	abstract public void setBufferRight();

	abstract public void setStencilFunc(int value);

	abstract public void enableDepthMask();

	abstract public void disableDepthMask();

	abstract public void setClearColor(float r, float g, float b, float a);

	abstract public void setPolygonOffset(float factor, float units);

	abstract public void setLayer(int layer);

	abstract public void genTextures2D(int number, int[] index);

	abstract public void bindTexture(int index);

	abstract public void enableAlphaTest();

	abstract public void disableAlphaTest();

	abstract public void enableMultisample();

	abstract public void disableMultisample();

	public abstract void setLabelOrigin(float[] origin);

	public abstract void enableLighting();

	public abstract void disableLighting();

	public abstract void initLighting();

	public abstract boolean useShaders();

	public abstract void setMatrixView();

	public abstract void setProjectionMatrixViewForAR(CoordMatrix4x4 cameraView,
			CoordMatrix4x4 cameraPerspective, CoordMatrix4x4 modelMatrix,
			float scaleFactor);

	public abstract void fromARCoreCoordsToGGBCoords(Coords coords,
			CoordMatrix4x4 modelMatrix, float scaleFactor, Coords ret);

	public abstract void unsetMatrixView();

	public abstract void pushSceneMatrix();

	public abstract void setLightPosition(float[] values);

	public abstract void setLightAmbiantDiffuse(float ambiant0, float diffuse0, float ambiant1,
			float diffuse1);

	/**
	 * create a dummy texture to please the GL shader language, that needs
	 * something correct to be bound on texture 0
	 */
	abstract public void createDummyTexture();

	abstract public void attribPointers();
}
