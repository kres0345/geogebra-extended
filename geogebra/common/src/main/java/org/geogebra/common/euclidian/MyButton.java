package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.draw.CanvasDrawable;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoButton.Observer;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;

//import java.awt.Color;

/**
 * Replaces Swing button in DrawButton
 */
public class MyButton implements Observer {

	private GeoButton geoButton;
	private EuclidianView view;
	private int x;
	private int y;
	private boolean selected;
	private String text;

	private final static int minSize = 24;

	private GFont font;
	private boolean pressed;
	private boolean draggedOrContext;
	private double textHeight;
	private double textWidth;
	private GBasicStroke borderStroke;
	private boolean firstCall = true;

	private final static int MARGIN_TOP = 6;
	private final static int MARGIN_BOTTOM = 5;
	private final static int MARGIN_LEFT = 10;
	private final static int MARGIN_RIGHT = 10;

	/**
	 * @param button
	 *            geo for this button
	 * @param view
	 *            view
	 */
	public MyButton(GeoButton button, EuclidianView view) {
		this.geoButton = button;
		this.view = view;
		this.x = 20;
		this.y = 20;
		this.borderStroke = EuclidianStatic.getDefaultStroke();
		geoButton.setObserver(this);
	}

	private String getCaption() {
		if (geoButton.getFillImage() == null) {
			return geoButton.getCaption(StringTemplate.defaultTemplate);
		}
		return geoButton.getCaptionDescription(StringTemplate.defaultTemplate);
	}

	/**
	 * Paint this on given graphics
	 * 
	 * @param g
	 *            graphics
	 * @param multiplier
	 *            font size multiplier
	 * @param mayResize
	 *            whether we can resize fonts
	 */
	public void paintComponent(GGraphics2D g, double multiplier,
			boolean mayResize) {

		boolean latex = CanvasDrawable.isLatexString(getCaption());

		g.setAntialiasing();

		font = font.deriveFont(geoButton.getFontStyle(),
				(int) (multiplier * 12));
		g.setFont(font);

		boolean hasText = getCaption().length() > 0;

		int imgHeight = 0;
		int imgWidth = 0;
		int imgGap = 0;
		textHeight = 0;
		textWidth = 0;

		if (geoButton.getFillImage() != null) {
			imgHeight = geoButton.getFillImage().getHeight();
			imgWidth = geoButton.getFillImage().getWidth();
			if (hasText) {
				imgGap = 4;
			}
		}
		GTextLayout t = null;
		// get dimensions
		if (hasText) {
			if (latex) {
				GDimension d = CanvasDrawable.measureLatex(
						view.getApplication(), geoButton, font, getCaption(),
						getSerif());
				textHeight = d.getHeight();
				textWidth = d.getWidth();
			} else {
				t = AwtFactory.getPrototype().newTextLayout(getCaption(), font,
						g.getFontRenderContext());
				textHeight = t.getAscent() + t.getDescent();
				textWidth = t.getAdvance();
			}
		}
		// With fixed size the font are resized if is too big
		if (mayResize && (geoButton.isFixedSize() && ((int) textHeight + imgGap
				+ (MARGIN_TOP + MARGIN_BOTTOM) > getHeight()
				|| (int) textWidth
						+ (MARGIN_LEFT + MARGIN_RIGHT) > getWidth()))) {
			resize(g, imgGap);
			return;
		}

		int currentWidth = Math.max((int) (textWidth
				+ (MARGIN_LEFT + MARGIN_RIGHT)),
				minSize);
		currentWidth = Math.max(currentWidth,
				imgWidth + (MARGIN_LEFT + MARGIN_RIGHT));

		int currentHeight = Math.max((int) (textHeight + imgHeight + imgGap
				+ (MARGIN_TOP + MARGIN_BOTTOM)),
				minSize);

		// Additional offset for image if button has fixed size
		int imgStart = 0;

		// Initial offset for subimage if button has fixed size
		int startX = 0;
		int startY = 0;
		double add = 0;
		if (!geoButton.isFixedSize()) {
			// Some combinations of style, serif / sans and letters
			// overflow from the drawing if the text is extra large
			if (geoButton.getFontStyle() >= 2) {
				add = Math.sin(0.50) * t.getDescent();
				currentWidth += (int) add;
			}
			if (geoButton.isSerifFont()) {
				currentWidth += currentWidth / 10;
			}
			if (geoButton.isSerifFont() && geoButton.getFontStyle() >= 2) {
				add = -add;
				currentWidth += currentWidth / 4;
			}
			geoButton.setWidth(currentWidth);
			geoButton.setHeight(currentHeight);
		} else {
			// With fixed size the image is cut if is too big
			if (imgHeight > getHeight() - textHeight - imgGap
					- (MARGIN_TOP + MARGIN_BOTTOM)) {
				startY = imgHeight - (int) (getHeight() - textHeight - imgGap
						- (MARGIN_TOP + MARGIN_BOTTOM));
				imgHeight = (int) (getHeight() - textHeight - imgGap
						- (MARGIN_TOP + MARGIN_BOTTOM));
				if (imgHeight <= 0) {
					geoButton.setFillImage("");
				} else {
					startY /= 2;
				}
			}
			if (imgWidth > getWidth() - (MARGIN_LEFT + MARGIN_RIGHT)) {
				startX = imgWidth - (getWidth() - (MARGIN_LEFT + MARGIN_RIGHT));
				imgWidth = getWidth() - (MARGIN_LEFT + MARGIN_RIGHT);
				startX /= 2;
			}
			imgStart = (int) (getHeight() - imgHeight
					- (MARGIN_TOP + MARGIN_BOTTOM) - textHeight - imgGap) / 2;
		}

		// prepare colors and paint
		g.setColor(view.getBackgroundCommon());
		GColor bg = geoButton.getBackgroundColor();
		// background not set by user
		if (bg == null) {
			bg = GColor.WHITE;
		}

		GColor paint;

		// change background color on mouse click
		if (pressed) {
			if (bg.equals(GColor.WHITE)) {
				paint = GColor.LIGHTEST_GRAY;
			} else {
				paint = bg.darker();
			}
		} else {
			paint = bg;
		}

		int arcSize = (int) Math.round(Math.min(getWidth(), getHeight())
				* geoButton.getKernel().getApplication().getButtonRouding());

		int shadowSize = 0;

		// fill background

		if (geoButton.getKernel().getApplication().getButtonShadows()) {
			shadowSize = (int) (getHeight() * 0.1);
			g.setPaint(paint.slightlyDarker());
			g.fillRoundRect(x, y, getWidth() + (int) add - 1,
					getHeight() - 1, arcSize, arcSize);
		}

		g.setPaint(paint);
		g.setStroke(borderStroke);
		g.fillRoundRect(x, y, getWidth() + (int) add - 1,
				getHeight() - 1 - shadowSize, arcSize, arcSize);

		// change border on mouseover
		if (isSelected()) {
			// default button design
			if (bg.equals(GColor.WHITE)) {
				// color for inner border
				g.setColor(GColor.GRAY);
				// inner border
				g.drawRoundRect(x + 1, y + 1, getWidth() + (int) add - 3,
						getHeight() - 3, arcSize, arcSize);
				// color for outer border
				g.setColor(GColor.BLACK);

				// user adjusted design
			} else {
				// color for inner border
				g.setColor(bg.darker());
				// inner border
				g.drawRoundRect(x + 1, y + 1, getWidth() + (int) add - 3,
						getHeight() - 3, arcSize, arcSize);
				// color for outer border
				g.setColor(bg.darker().darker());
			}

			// border color
		} else {
			// default button design
			if (bg.equals(GColor.WHITE)) {
				g.setColor(GColor.BLACK);

				// user adjusted design
			} else {
				g.setColor(bg.darker());
			}
		}

		// draw border
		g.drawRoundRect(x, y, getWidth() + (int) add - 1,
				getHeight() - 1 - shadowSize, arcSize, arcSize);

		// prepare to draw text
		g.setColor(geoButton.getObjectColor());

		MyImage im = geoButton.getFillImage();
		// draw image
		if (im != null) {

			if (im.isSVG()) {

				// SVG is scaled to the button size rather than cropped
				double sx = (double) im.getWidth() / (double) getWidth();
				double sy = (double) im.getHeight() / (double) getHeight();

				boolean one2one = MyDouble.exactEqual(sx, 1)
						&& MyDouble.exactEqual(sy, 1);

				if (!one2one) {
					g.saveTransform();

					g.scale(1 / sx, 1 / sy);
					g.translate(x * sx, y * sy);
				}

				g.drawImage(im, 0, 0);

				if (!one2one) {
					g.restoreTransform();
				}

			} else {

				im.drawSubimage(startX, startY, imgWidth, imgHeight, g,
						x + (getWidth() - imgWidth) / 2,
						y + MARGIN_TOP + imgStart);
			}
		}

		// draw the text center-aligned to the button
		if (hasText) {
			if (geoButton.getFillImage() == null) {
				imgStart = (int) (getHeight() - (MARGIN_TOP + MARGIN_BOTTOM)

						- textHeight) / 2;
			}
			drawText(g, t, imgStart + imgGap + imgHeight, latex, add,
					shadowSize);
		}
	}

	private void drawText(GGraphics2D g, GTextLayout t, int imgEnd,
			boolean latex, double add, int shadowSize) {
		int xPos = latex ? (int) (x + (getWidth() - textWidth) / 2)
				: (int) (x + (getWidth() - t.getAdvance() + add) / 2);

		int yPos = latex
				? (int) (y + (getHeight() - textHeight) / 2) + imgEnd
				: (int) (y + MARGIN_TOP + imgEnd + t.getAscent());

		yPos -= shadowSize / 2;

		if (geoButton.getFillImage() != null) {
			yPos = latex ? y + imgEnd
					: (int) (y + MARGIN_TOP + t.getAscent()
							+ imgEnd);
		}

		if (latex) {
			App app = view.getApplication();
			g.setPaint(GColor.BLACK);

			String caption = getCaption();


			app.getDrawEquation().drawEquation(app, geoButton, g, xPos, yPos,
					caption, font, getSerif(), geoButton.getObjectColor(),
					geoButton.getBackgroundColor(), false, false,
					view.getCallBack(geoButton, firstCall));
			firstCall = false;
		} else {
			g.drawString(geoButton.getCaption(StringTemplate.defaultTemplate),
					xPos, yPos);

		}

	}

	private void resize(GGraphics2D g, int imgGap) {
		boolean latex = CanvasDrawable.isLatexString(getCaption());

		// Reduces the font for attempts
		GTextLayout t = null;
		int i = GeoText.getFontSizeIndex(
				((TextProperties) geoButton).getFontSizeMultiplier());
		while (i > 0 && (int) textHeight + imgGap
				+ (MARGIN_TOP + MARGIN_BOTTOM) > getHeight()) {
			i--;
			font = font.deriveFont(font.getStyle(),
					(int) (GeoText.getRelativeFontSize(i) * 12));
			if (latex) {
				GDimension d = CanvasDrawable.measureLatex(
						view.getApplication(), geoButton, font, getCaption(),
						getSerif());
				textHeight = d.getHeight();
				textWidth = d.getWidth();

			} else {
				t = AwtFactory.getPrototype().newTextLayout(getCaption(), font,
						g.getFontRenderContext());
				textHeight = t.getAscent() + t.getDescent();
				textWidth = t.getAdvance();
			}
		}

		while (i > 0 && (int) textWidth
				+ (MARGIN_LEFT + MARGIN_RIGHT) > getWidth()) {
			i--;
			font = font.deriveFont(font.getStyle(),
					(int) (GeoText.getRelativeFontSize(i) * 12));
			if (latex) {
				GDimension d = CanvasDrawable.measureLatex(
						view.getApplication(), geoButton, font, getCaption(),
						getSerif());
				textHeight = d.getHeight();
				textWidth = d.getWidth();

			} else {
				t = AwtFactory.getPrototype().newTextLayout(getCaption(), font,
						g.getFontRenderContext());
				textHeight = t.getAscent() + t.getDescent();
				textWidth = t.getAdvance();
			}
		}
		double ret = GeoText.getRelativeFontSize(i);
		paintComponent(g, ret, false);

	}

	private boolean getSerif() {
		boolean serif = geoButton.isSerifFont();

		if (!serif) {
			serif = StringUtil.startsWithFormattingCommand(getCaption());
		}

		return serif;
	}

	private boolean isSelected() {
		return selected;
	}

	/**
	 * @return width in pixels
	 */
	public int getWidth() {
		return geoButton.getWidth();
	}

	/**
	 * @return height in pixels
	 */
	public int getHeight() {
		return geoButton.getHeight();
	}

	/**
	 * Resizes and moves the button
	 * 
	 * @param labelRectangle
	 *            new bounds
	 */
	public void setBounds(GRectangle labelRectangle) {
		x = (int) labelRectangle.getMinX();
		y = (int) labelRectangle.getMinY();
		geoButton.setWidth((int) labelRectangle.getWidth());
		geoButton.setHeight((int) labelRectangle.getHeight());

	}

	/**
	 * @return bounds of this button
	 */
	public GRectangle getBounds() {
		return AwtFactory.getPrototype().newRectangle(x, y, getWidth(),
				getHeight());
	}

	/**
	 * @param selected
	 *            new selected flag
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;

	}

	/**
	 * @return x-coord
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return y-coord
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param labelDesc
	 *            text for this button
	 */
	public void setText(String labelDesc) {
		text = labelDesc;
	}

	/**
	 * @return text of this button
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param font
	 *            new font
	 */
	public void setFont(GFont font) {
		this.font = font;
		geoButton.getKernel().notifyRepaint();
	}

	/**
	 * @param b
	 *            new pressed flag
	 */
	public void setPressed(boolean b) {
		if (b) {
			draggedOrContext = false;
		}

		pressed = b;
	}

	/**
	 * @param b
	 *            new "dragged or context menu" flag
	 */
	public void setDraggedOrContext(boolean b) {
		draggedOrContext = b;
	}

	/**
	 * @return "dragged or context menu" flag
	 */
	public boolean getDraggedOrContext() {
		return draggedOrContext;
	}

	/**
	 * @return whether the button has fixed size
	 */
	public boolean isFixedSize() {
		return geoButton.isFixedSize();
	}

	/**
	 * @param fixedSize
	 *            change the button to have fixed size
	 */
	public void setFixedSize(boolean fixedSize) {
		geoButton.setFixedSize(fixedSize);
	}

	@Override
	public void notifySizeChanged() {
		geoButton.getKernel().notifyRepaint();
	}

	/**
	 * @return associated GeoButton
	 */
	public GeoElement getButton() {
		return geoButton;
	}

}