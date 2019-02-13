package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * Specific manager for browsers with no TRIANGLE_FAN geometry (e.g. IE)
 * 
 * @author mathieu
 *
 */
abstract public class ManagerShadersNoTriangleFan extends ManagerShadersWithTemplates {
	private Coords triangleFanApex;

	/**
	 * constructor
	 * 
	 * @param renderer
	 *            GL renderer
	 * @param view3d
	 *            3D view
	 */
	public ManagerShadersNoTriangleFan(Renderer renderer,
			EuclidianView3D view3d) {
		super(renderer, view3d);
	}

	@Override
	protected void triangleFanApex(Coords v) {
		triangleFanApex = v.copyVector();
	}

	@Override
	protected void triangleFanVertex(Coords v) {
		vertexToScale(triangleFanApex);
		vertexToScale(v);
	}

	@Override
	public int getLongitudeMax() {
		return 64;
	}

	@Override
	public int getLongitudeDefault() {
		return getLongitudeMax();
	}

}
