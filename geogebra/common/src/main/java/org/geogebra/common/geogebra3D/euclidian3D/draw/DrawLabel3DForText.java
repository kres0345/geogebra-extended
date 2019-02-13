package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.draw.DrawText;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.TextProperties;

/**
 * class for drawing texts
 * 
 * @author mathieu
 *
 */
public class DrawLabel3DForText extends DrawLabel3D {
	protected GeoText geo;
	private int highLightIndex = -1;
	GRectangle bounds = AwtFactory.getPrototype().newRectangle();

	public DrawLabel3DForText(EuclidianView3D view, Drawable3D drawable) {
		super(view, drawable);
	}

	@Override
	final protected GRectangle getBounds() {

		if (geo.isLaTeX()) {
			EuclidianStatic.drawMultilineLaTeX(view.getApplication(),
					tempGraphics, geo, tempGraphics, font, GColor.BLACK,
					GColor.WHITE, text, 0, 0,
					((TextProperties) geo).isSerifFont(), getCallBack(),
					bounds);
		} else {
			EuclidianStatic.drawIndexedMultilineString(view.getApplication(),
					text, tempGraphics, bounds, font,
					((TextProperties) geo).isSerifFont(), 0, 0);
		}
		return bounds;

	}

	@Override
	final protected GBufferedImage draw() {
		GBufferedImage bimg = createBufferedImage();
		GGraphics2D g2d = createGraphics2D(bimg);

		if (geo.isLaTeX()) {
			EuclidianStatic.drawMultilineLaTeX(view.getApplication(),
					tempGraphics, geo, g2d, font, GColor.BLACK, GColor.WHITE,
					text, 0, 0, ((TextProperties) geo).isSerifFont(),
					getCallBack(), null);
		} else {
			EuclidianStatic.drawIndexedMultilineString(view.getApplication(),
					text, g2d, AwtFactory.getPrototype().newRectangle(),
					g2d.getFont(), ((TextProperties) geo).isSerifFont(), 0, 0);
		}

		return bimg;
	}

	public void setGeo(GeoText geo) {
		this.geo = geo;
	}

	@Override
	protected void drawText(Renderer renderer) {

		// draw text
		super.drawText(renderer);

		if (geo.doHighlighting()) {
			// draw bounds if highlighted
			renderer.disableTextures();
			renderer.disableMultisample();
			renderer.setLineWidth(geo.getLineThickness() / 2.0);
			renderer.setColor(DrawText.HIGHLIGHT_COLOR);
			renderer.getGeometryManager().draw(highLightIndex);
			renderer.enableMultisample();
			renderer.enableTextures();
		}
	}

	@Override
	public void updatePosition(Renderer renderer) {

		super.updatePosition(renderer);

		if (origin == null) {
			renderer.getGeometryManager().remove(highLightIndex);
			highLightIndex = -1;
			return;
		}

		int old = highLightIndex;
		highLightIndex = renderer.getGeometryManager().rectangleBounds(drawX,
				drawY, drawZ, width / getFontScale(), height / getFontScale(),
				highLightIndex);
		renderer.getGeometryManager().remove(old);
		// Log.debug("highLightIndex: "+highLightIndex);
	}

	@Override
	public void setWaitForReset() {
		super.setWaitForReset();

		highLightIndex = -1;
	}

	/**
	 * update draw position
	 */
	@Override
	public void updateDrawPosition() {

		if (geo.isAbsoluteScreenLocActive()) {
			if (origin == null) {
				return;
			}

			drawX = (int) (origin.getX() - drawable.getView3D().getWidth() / 2.0
					+ xOffset2 / getFontScale());
			drawY = (int) (drawable.getView3D().getHeight() / 2.0
					- origin.getY() + yOffset2 / getFontScale());
			drawZ = 0;

		} else {
			super.updateDrawPosition();
		}

	}

	@Override
	public void removeFromGL() {
		super.removeFromGL();
		view.getRenderer().getGeometryManager().remove(highLightIndex);
	}

}
