package org.geogebra.common.geogebra3D.main;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.Textures;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.TexturesShaders;

public class FragmentShader {

	final private static String fragmentHeaderDesktop = "#if __VERSION__ >= 130\n"
			+ "  #define varying in\n"

			+ "  out vec4 mgl_FragColor;\n" + "  #define texture2D texture\n"
			+ "  #define gl_FragColor mgl_FragColor\n"

			+ "#endif \n"

			+ "#ifdef GL_ES \n"

			+ "precision mediump float;\n"

			+ "precision mediump int; \n"

			+ "#endif \n";

	final private static String fragmentHeaderHTML5 = "precision mediump float;\n";

	/**
	 * @param shine
	 *            TODO not used
	 * @param isHTML5
	 *            whether to add HTML5 prefix
	 * @return shiny shader
	 */
	public static String getFragmentShaderShiny(float shine,
			boolean isHTML5) {

		String header = isHTML5 ? fragmentHeaderHTML5 : fragmentHeaderDesktop;

		return header + "uniform int enableShine; \n"
				+ "uniform int textureType;\n"
				+ "uniform int enableClipPlanes;\n"
				+ "uniform vec3 clipPlanesMin;\n"
				+ "uniform vec3 clipPlanesMax;\n"
				+ "uniform float dashValues[4];\n"
				+ "uniform sampler2D Texture0;\n"
				// in (incoming varying data to the fragment shader sent from
				// the vertex shader)
				+ "varying   vec4    varying_Color;  \n"
				+ "varying   vec2  coordTexture;  \n"
				+ "varying   vec3    realWorldCoords;\n"
				+ "varying vec3 viewDirection;\n"
				+ "varying vec3 lightReflect;\n"

				+ "void main (void) \n"

				+ "{ \n"

				+ "float x;\n"
				+ "if (enableClipPlanes == 1  // clip the scene\n"
				+ "&& (realWorldCoords.x < clipPlanesMin.x || realWorldCoords.x > clipPlanesMax.x\n"
				+ "|| realWorldCoords.y < clipPlanesMin.y || realWorldCoords.y > clipPlanesMax.y \n"
				+ "|| realWorldCoords.z < clipPlanesMin.z || realWorldCoords.z > clipPlanesMax.z \n"
				+ "   )){\n"

				+ "discard;\n"

				+ "}\n"

				+ "vec4 color;\n" + "if (enableShine == 1){\n"
				// adding specular
				+ "float specular = dot(lightReflect, viewDirection);\n"
				+ "if (specular > 0.0){\n"
				+ "  float specular2  = specular  * specular;\n"
				+ "  float specular4  = specular2 * specular2;\n"
				+ "  float specular16  = specular4 * specular4;\n"
				+ "  color.rgb = varying_Color.rgb + 0.2 * specular16 * vec3(1.0, 1.0, 1.0);\n"
				+ "  color.a = varying_Color.a;\n"

				+ "}else{\n"

				+ "    color = varying_Color;\n"

				+ "}\n"

				+ "}else{\n"

				+ "color = varying_Color;\n"

				+ "}\n"

				// default

				+ "if (textureType == 0){\n"

				+ "gl_FragColor = color;\n" + "return;\n"

				+ "}\n"
				// fading
				+ "if (textureType == 1){ // TEXTURE_TYPE_FADING = 1\n"
				+ "float factor;\n"

				+ "x = max(coordTexture.x, 0.0);\n"
				+ "float y = max(coordTexture.y, 0.0);\n"
				+ "gl_FragColor.rgb  = color.rgb;\n"
				+ "gl_FragColor.a = color.a * (1.0 - x) * (1.0 - y);\n"
				+ "return;\n"

				+ "}\n"

				// text
				+ "if (textureType == 2){ // TEXTURE_TYPE_TEXT = 2;\n"
				+ "vec4 textureVal = texture2D(Texture0, coordTexture);\n"
				+ "if (textureVal.a < 0.25){\n"

				+ "  discard; // don't write\n" + "  }\n"

				+ "gl_FragColor.rgb = color.rgb;\n"
				+ "gl_FragColor.a = textureVal.a;\n"

				+ "return;\n"

				+ "}\n"
				// dash
				+ "x =  mod(dashValues[0] * coordTexture.x, 1.0);\n"
				+ "if (x > dashValues[1] || (x > dashValues[2] && x <= dashValues[3])){\n"
				+ "discard;\n"

				+ "}\n"

				+ "gl_FragColor = color;\n"

				+ "} ";
	}

	/**
	 * @param shine
	 *            shine level
	 * @param isHTML5
	 *            whether to use html5 header
	 * @return shiny shader
	 */
	public static String getFragmentShaderShinyForPacking(float shine, boolean isHTML5) {

		String header = isHTML5 ? fragmentHeaderHTML5 : fragmentHeaderDesktop;

		return header + "uniform int enableShine; \n"
				+ "uniform int textureType;\n"
				+ "uniform int enableClipPlanes;\n"
				+ "uniform vec3 clipPlanesMin;\n"
				+ "uniform vec3 clipPlanesMax;\n"
				+ "uniform float dashValues[4];\n"
				+ "uniform sampler2D Texture0;\n"
				// in (incoming varying data to the fragment shader sent from
				// the vertex shader)
				+ "varying   vec4    varying_Color;  \n" + "varying   vec2  coordTexture;  \n"
				+ "varying   vec3    realWorldCoords;\n" + "varying vec3 viewDirection;\n"
				+ "varying vec3 lightReflect;\n"

				+ "void main (void) \n"

				+ "{ \n"
				// this occurs when buffer parts have been "removed"
				+ "if (varying_Color.a < 0.0) {\n" + "discard;" + "}\n"

				+ "float x, y;\n"
				+ "if (enableClipPlanes == 1  // clip the scene\n"
				+ "&& (realWorldCoords.x < clipPlanesMin.x || realWorldCoords.x > clipPlanesMax.x\n"
				+ "|| realWorldCoords.y < clipPlanesMin.y || realWorldCoords.y > clipPlanesMax.y \n"
				+ "|| realWorldCoords.z < clipPlanesMin.z || realWorldCoords.z > clipPlanesMax.z \n"
				+ " )){\n"

				+ "discard;\n"

				+ "}\n"

				+ "vec4 color;\n" + "if (enableShine == 1){\n"
				// adding specular
				+ "float specular = dot(lightReflect, viewDirection);\n"
				+ "if (specular > 0.0){\n" + "  float specular2  = specular  * specular;\n"
				+ "  float specular4  = specular2 * specular2;\n"
				+ "  float specular16  = specular4 * specular4;\n"
				+ "  color.rgb = varying_Color.rgb + 0.2 * specular16 * vec3(1.0, 1.0, 1.0);\n"
				+ "  color.a = varying_Color.a;\n"

				+ "}else{\n"

				+ "    color = varying_Color;\n"

				+ "}\n"

				+ "}else{\n"

				+ "color = varying_Color;\n"

				+ "}\n"

				// default

				+ "if (textureType == 0){\n"

				+ "gl_FragColor = color;\n" + "return;\n"

				+ "}\n"
				// fading
				+ "if (textureType == 1){ // TEXTURE_TYPE_FADING = 1\n" + "float factor;\n"

				+ "x = max(coordTexture.x, 0.0);\n"
				+ "float y = max(coordTexture.y, 0.0);\n"
				+ "gl_FragColor.rgb  = color.rgb;\n"
				+ "gl_FragColor.a = color.a * (1.0 - x) * (1.0 - y);\n"
				+ "return;\n"

				+ "}\n"

				// text
				+ "if (textureType == 2){ // TEXTURE_TYPE_TEXT = 2;\n"
				+ "vec4 textureVal = texture2D(Texture0, coordTexture);\n"
				+ "if (textureVal.a < 0.25){\n"

				+ "  discard; // don't write\n" + "  }\n"

				+ "gl_FragColor.rgb = color.rgb;\n" + "gl_FragColor.a = textureVal.a;\n"

				+ "return;\n"

				+ "}\n"

				// dash packed
				+ "if (textureType == 14) { // TEXTURE_TYPE_DASH + DASH_PACKED = 4 + 10;\n"
				+ "y = (coordTexture.y -  float(int((coordTexture.y + 0.5) / "
				+ Textures.DASH_ID_LENGTH + ".0) * " + Textures.DASH_ID_LENGTH
				+ ") + 0.5) / "
				+ TexturesShaders.DESCRIPTIONS_LENGTH + ".0;\n"
				+ "vec4 textureDash = texture2D(Texture0, vec2(coordTexture.x, y));\n"
				+ "if (textureDash.a < 0.5){\n"
				+ "  discard; // don't write\n" + "  }\n"
				+ "gl_FragColor = color;\n"
				+ "return;\n"
				+ "}\n "
				
				// dash packed hidden
				+ "if (textureType == 15) { // TEXTURE_TYPE_DASH + DASH_PACKED_HIDDEN = 4 + 11;\n"
				+ "y = (float(int((coordTexture.y+0.5) / " + Textures.DASH_ID_LENGTH 
				+ ".0)) + " + "0.5) / " + TexturesShaders.DESCRIPTIONS_LENGTH + ".0;\n"
				+ "vec4 textureDash = texture2D(Texture0, vec2(coordTexture.x, y));\n"
				+ "if (textureDash.a < 0.5){\n"
				+ "  discard; // don't write\n" + "  }\n"
				+ "gl_FragColor = color;\n"
				+ "return;\n" 
				+ "}\n "

				// dash
				+ "x =  mod(dashValues[0] * coordTexture.x, 1.0);\n"
				+ "if (x > dashValues[1] || (x > dashValues[2] && x <= dashValues[3])){\n"
				+ "discard;\n"
				+ "}\n"
				+ "gl_FragColor = color;\n"
				+ "} ";

	}
}
