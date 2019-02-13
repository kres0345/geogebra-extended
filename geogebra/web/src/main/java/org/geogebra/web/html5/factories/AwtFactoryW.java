package org.geogebra.web.html5.factories;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GAlphaComposite;
import org.geogebra.common.awt.GArc2D;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGradientPaint;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GQuadCurve2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.event.FocusListener;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.main.App;
import org.geogebra.ggbjdk.java.awt.DefaultBasicStroke;
import org.geogebra.ggbjdk.java.awt.geom.AffineTransform;
import org.geogebra.ggbjdk.java.awt.geom.Arc2D;
import org.geogebra.ggbjdk.java.awt.geom.Area;
import org.geogebra.ggbjdk.java.awt.geom.Ellipse2D;
import org.geogebra.ggbjdk.java.awt.geom.GeneralPath;
import org.geogebra.ggbjdk.java.awt.geom.Line2D;
import org.geogebra.ggbjdk.java.awt.geom.Path2D;
import org.geogebra.ggbjdk.java.awt.geom.Point2D;
import org.geogebra.ggbjdk.java.awt.geom.QuadCurve2D;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle2D;
import org.geogebra.web.html5.awt.GAlphaCompositeW;
import org.geogebra.web.html5.awt.GBasicStrokeW;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.awt.GFontRenderContextW;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.awt.GGradientPaintW;
import org.geogebra.web.html5.awt.GTexturePaintW;
import org.geogebra.web.html5.awt.font.GTextLayoutW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.GGraphics2DWI;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.gawt.GBufferedImageW;
import org.geogebra.web.html5.main.MyImageW;
import org.geogebra.web.html5.util.ImageLoadCallback;
import org.geogebra.web.html5.util.ImageWrapper;

import com.google.gwt.core.client.Scheduler;

/**
 * Creates AWT wrappers for web
 *
 */
public class AwtFactoryW extends AwtFactory {
	/** to make code more efficient in the following method */
	boolean repaintDeferred = false;

	/** to avoid infinite loop in the following method */
	int repaintsFromHereInProgress = 0;

	@Override
	public GAffineTransform newAffineTransform() {
		return new AffineTransform();
	}

	@Override
	public GRectangle2D newRectangle2D() {
		return new Rectangle2D.Double();
	}

	@Override
	public GRectangle newRectangle(int x, int y, int w, int h) {
		return new Rectangle(x, y, w, h);
	}

	@Override
	public GBufferedImage newBufferedImage(int pixelWidth, int pixelHeight,
			double pixelRatio) {
		return new GBufferedImageW(pixelWidth, pixelHeight, pixelRatio);
	}

	@Override
	public GBufferedImage createBufferedImage(int width, int height,
			boolean transparency) {
		return new GBufferedImageW(width, height, 1.0f, !transparency);
	}

	@Override
	public GDimension newDimension(int width, int height) {
		return new GDimensionW(width, height);
	}

	@Override
	public GPoint2D newPoint2D() {
		return new Point2D.Double();
	}

	@Override
	public GPoint2D newPoint2D(double x, double y) {
		return new Point2D.Double(x, y);
	}

	@Override
	public GRectangle newRectangle(int x, int y) {
		return new Rectangle(x, y);
	}

	@Override
	public GGeneralPath newGeneralPath() {
		// default winding rule changed for ggb50 (for Polygons) #3983
		return new GeneralPath(Path2D.WIND_EVEN_ODD);
	}

	@Override
	public GBasicStroke newMyBasicStroke(double f) {
		return new GBasicStrokeW(f, DefaultBasicStroke.CAP_ROUND,
				DefaultBasicStroke.JOIN_ROUND);
	}

	@Override
	public GBasicStroke newBasicStroke(double width, int endCap, int lineJoin,
			double miterLimit, double[] dash) {
		return new GBasicStrokeW(width, endCap, lineJoin, miterLimit, dash);
	}

	@Override
	public GLine2D newLine2D() {
		return new Line2D.Double();
	}

	@Override
	public GRectangle newRectangle(GRectangle bb) {
		return new Rectangle(bb);
	}

	@Override
	public GEllipse2DDouble newEllipse2DDouble() {
		return new Ellipse2D.Double();
	}

	@Override
	public GEllipse2DDouble newEllipse2DDouble(double x, double y, double w,
			double h) {
		return new Ellipse2D.Double(x, y, w, h);
	}

	@Override
	public GBasicStroke newBasicStroke(double f) {
		return new GBasicStrokeW(f);
	}

	@Override
	// CAP_BUTT, JOIN_MITER behaves differently on JRE & GWT
	// see #1699
	public GBasicStroke newBasicStrokeJoinMitre(double f) {
		return new GBasicStrokeW(f, GBasicStroke.CAP_SQUARE,
				GBasicStroke.JOIN_MITER);
	}

	@Override
	public GRectangle newRectangle() {
		return new Rectangle();
	}

	@Override
	public GArc2D newArc2D() {
		return new Arc2D.Double();
	}

	@Override
	public GQuadCurve2D newQuadCurve2D() {
		return new QuadCurve2D.Double();
	}

	/*
	 * @Override public Area newArea(GeneralPathClipped hypRight) {
	 * AbstractApplication.debug("implementation needed really"); // TODO
	 * Auto-generated return null; }
	 */

	@Override
	public GArea newArea() {
		return new Area();
	}

	@Override
	public GArea newArea(GShape shape) {
		return new Area(shape);
	}

	@Override
	public GGeneralPath newGeneralPath(int rule) {
		return new GeneralPath(rule);
	}

	@Override
	public GBasicStroke newBasicStroke(double f, int cap, int join) {
		return new GBasicStrokeW(f, cap, join);
	}

	@Override
	public GTextLayout newTextLayout(String string, GFont fontLine,
			GFontRenderContext frc) {
		return new GTextLayoutW(string, fontLine, (GFontRenderContextW) frc);
	}

	@Override
	public GAlphaComposite newAlphaComposite(double alpha) {
		return new GAlphaCompositeW(alpha);
	}

	@Override
	public GGradientPaint newGradientPaint(double x, double y, GColor bg2,
			double x2, double i, GColor bg) {
		return new GGradientPaintW(x, y, bg2, x2, i, bg);
	}

	@Override
	public FocusListener newFocusListener(Object listener) {
		return new FocusListenerW(listener);
	}

	@Override
	public GFont newFont(String name, int style, int size) {
		return new GFontW(name, style, size);
	}

	@Override
	public MyImage newMyImage(int pixelWidth, int pixelHeight,
			int typeIntArgb) {
		return new MyImageW(new GBufferedImageW(pixelWidth, pixelHeight, 1)
				.getImageElement(), false);
	}

	@Override
	public GPaint newTexturePaint(GBufferedImage subimage, GRectangle rect) {
		return new GTexturePaintW((GBufferedImageW) subimage);
	}

	@Override
	public GPaint newTexturePaint(MyImage subimage, GRectangle rect) {
		return new GTexturePaintW(
				new GBufferedImageW(((MyImageW) subimage).getImage()));
	}

	@Override
	public void fillAfterImageLoaded(final GShape shape, final GGraphics2D g3,
			GBufferedImage gi, final App app) {
		{
			if (((GBufferedImageW) gi).isLoaded()) {
				// when the image is already loaded, no new repaint is necessary
				// in theory, the image will be loaded after some repaints so
				// this will not be an infinite loop ...
				g3.fill(shape);
			} else if (repaintsFromHereInProgress == 0) {
				// the if condition makes sure there will be no infinite loop

				// note: AFAIK (?), DOM's addEventListener method can add more
				// listeners
				ImageWrapper.nativeon(((GBufferedImageW) gi).getImageElement(),
						"load", new ImageLoadCallback() {
							@Override
							public void onLoad() {
								if (!repaintDeferred) {
									repaintDeferred = true;
									// otherwise, at the first time, issue a
									// complete repaint
									// but schedule it deferred to avoid
									// conflicts
									// in repaints
									Scheduler.get().scheduleDeferred(
											new Scheduler.ScheduledCommand() {
												@Override
												public void execute() {
													doRepaint(app);
												}

											});
								}
							}
						});
			}
		}
	}

	/**
	 * Helper method for repainting
	 * 
	 * @param app
	 *            application
	 */
	void doRepaint(App app) {
		repaintDeferred = false;
		repaintsFromHereInProgress++;
		((EuclidianViewW) app.getEuclidianView1()).doRepaint();
		if (app.hasEuclidianView2(1)) {
			((EuclidianViewW) app.getEuclidianView2(1)).doRepaint();
		}
		repaintsFromHereInProgress--;
	}

	@Override
	public GBufferedImage newBufferedImage(int pixelWidth, int pixelHeight,
			GGraphics2D g2) {
		return newBufferedImage(pixelWidth, pixelHeight,
				((GGraphics2DWI) g2).getDevicePixelRatio());
	}

}
