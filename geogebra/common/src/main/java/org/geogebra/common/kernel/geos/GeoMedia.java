package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.media.MediaFormat;
import org.geogebra.common.media.MediaURLParser;

/**
 * Class for representing playable media data.
 * 
 * @author laszlo
 *
 */
public abstract class GeoMedia extends GeoWidget implements Translateable {
	/** Source of the media, available for subclasses too */
	protected String src;

	/** Application for subclasses too. */
	protected App app;

	private MediaFormat format;

	/**
	 * Constructs a new, empty media element.
	 * 
	 * @param c
	 *            the construction.
	 */
	public GeoMedia(Construction c) {
		super(c);
		setAbsoluteScreenLocActive(false);
		app = getKernel().getApplication();
	}

	/**
	 * Constructs a new media element with given content.
	 * 
	 * @param c
	 *            the construction.
	 * @param url
	 *            the media URL.
	 * @param format
	 *            {@link MediaFormat}
	 */
	public GeoMedia(Construction c, String url, MediaFormat format) {
		this(c);
		setSrc(url, format);
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return null;
	}

	@Override
	public boolean showInAlgebraView() {
		return false;
	}

	/**
	 * 
	 * @return the source of the media.
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * Sets the source of the media.
	 * 
	 * @param src
	 *            to set.
	 * @param fireChanged
	 *            determines if handler should be called or not.
	 */
	public void setSrc(String src, boolean fireChanged) {
		this.src = src;
		if (fireChanged) {
			onSourceChanged();
		}
	}

	/**
	 * Set the source and call changed handler.
	 * 
	 * @param src
	 *            to set.
	 * @param format
	 *            {@link MediaFormat}
	 */
	public void setSrc(String src, MediaFormat format) {
		this.format = format;
		setSrc(src, true);
	}

	/**
	 * Set the source and call changed handler.
	 * 
	 * @param src
	 *            to set.
	 * @param formatStr
	 *            format string representation.
	 */
	public void setSrc(final String src, String formatStr) {
		MediaFormat fmt = MediaFormat.get(formatStr);
		if (fmt == MediaFormat.NONE) {
			fmt = MediaURLParser.checkVideo(src).getFormat();
		}
		setSrc(src, fmt);
	}

	/**
	 * Called after source has changed.
	 */
	protected abstract void onSourceChanged();

	/**
	 * Plays the media.
	 */
	public abstract void play();

	/**
	 * @return if media is playing.
	 */
	public abstract boolean isPlaying();

	/**
	 * @return the duration in seconds.
	 */
	public abstract int getDuration();

	/**
	 * @return the time where media play is at.
	 */
	public abstract int getCurrentTime();

	/**
	 * Sets the current position to a given time in seconds.
	 * 
	 * @param secs
	 *            to set.
	 */
	public abstract void setCurrentTime(int secs);

	/**
	 * 
	 * @return the media format.
	 */
	public MediaFormat getFormat() {
		return format;
	}

	/**
	 * Stops media play back.
	 */
	public abstract void pause();

	@Override
	public void remove() {
		pause();
		super.remove();
	}

	@Override
	public boolean isPinnable() {
		return true;
	}

	@Override
	public void translate(Coords v) {
		for (int i = 0; i < corner.length; i++) {
			if (corner[i] != null) {
				corner[i].translate(v);
			}
		}
	}

	@Override
	public boolean isTranslateable() {
		return true;
	}
}
