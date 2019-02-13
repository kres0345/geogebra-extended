package org.geogebra.web.geogebra3D.web.euclidian3D.openGL.shaders;

import org.geogebra.common.geogebra3D.main.FragmentShader;
import org.geogebra.common.geogebra3D.main.VertexShader;

/**
 * This class provides different shaders based on gpu/renderer information.
 */
public class ShaderProvider {

	/**
	 * @param packed
	 *            says if we use packed buffers
	 * @return Fragment shader
	 */
	public static String getFragmentShader(boolean packed) {
			if (packed) {
				return FragmentShader.getFragmentShaderShinyForPacking(0.2f, true);
			}
			return FragmentShader.getFragmentShaderShiny(0.2f, true);
	}

	/**
	 * @param shiny
	 *            says if we use specular light to get it shiny
	 * @return Vertex shader
	 */
	public static String getVertexShader(boolean shiny) {
		if (shiny) {
			return VertexShader.getVertexShaderShiny(true);
		}
		return VertexShader.getVertexShader(true);
	}
}
