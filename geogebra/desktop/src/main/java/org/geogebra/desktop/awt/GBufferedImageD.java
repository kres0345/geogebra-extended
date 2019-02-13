package org.geogebra.desktop.awt;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.plugin.GgbAPID;

public class GBufferedImageD implements GBufferedImage {
	private BufferedImage impl;

	public GBufferedImageD(int width, int height, int imageType) {
		impl = new BufferedImage(width, height, imageType);
	}

	public GBufferedImageD(BufferedImage image) {
		impl = image;
	}

	@Override
	public int getWidth() {
		return impl.getWidth();
	}

	@Override
	public int getHeight() {
		return impl.getHeight();
	}

	public static BufferedImage getAwtBufferedImage(GBufferedImage im) {
		if (im == null) {
			return null;
		}
		return ((GBufferedImageD) im).impl;
	}

	@Override
	public GGraphics2D createGraphics() {
		return new GGraphics2DD((Graphics2D) impl.getGraphics());
	}

	@Override
	public GBufferedImage getSubimage(int x, int y, int w, int h) {
		return new GBufferedImageD(impl.getSubimage(x, y, w, h));
	}

	/**
	 * 
	 * @return ARGB pixel data
	 */
	public int[] getData() {
		return ((DataBufferInt) impl.getRaster().getDataBuffer()).getData();
	}

	@Override
	public void flush() {
		impl.flush();
	}

	@Override
	public String getBase64() {
		return StringUtil.pngMarker + GgbAPID.base64encode(impl, 72);
	}

}
