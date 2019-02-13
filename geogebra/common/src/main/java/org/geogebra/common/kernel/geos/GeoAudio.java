package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.media.MediaFormat;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;

/**
 * Class for representing playable audio data.
 * 
 * @author laszlo
 *
 */
public class GeoAudio extends GeoMedia {
	private static final double DEFAULT_STEP = 0.5;
	private static final int DEFAULT_PLAYER_WIDTH = 300;
	private static final int DEFAULT_PLAYER_HEIGHT = 48;

	/**
	 * Constructs a new, empty audio element.
	 * 
	 * @param c
	 *            the construction.
	 */
	public GeoAudio(Construction c) {
		super(c);
		app = getKernel().getApplication();
		setWidth(DEFAULT_PLAYER_WIDTH);
		setHeight(DEFAULT_PLAYER_HEIGHT);
		setAnimationStep(DEFAULT_STEP);
	}

	/**
	 * Constructs a new audio element with given content.
	 * 
	 * @param c
	 *            the construction.
	 * @param url
	 *            the audio URL.
	 */
	public GeoAudio(Construction c, String url) {
		this(c);
		setSrc(url, MediaFormat.AUDIO_HTML5);
		setLabel("audio");
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.AUDIO;
	}

	@Override
	public GeoElement copy() {
		GeoAudio ret = new GeoAudio(cons);
		ret.setSrc(src, getFormat());
		return ret;
	}

	@Override
	public void set(GeoElementND geo) {
		if (!geo.isGeoAudio()) {
			return;
		}
		src = ((GeoAudio) geo).getSrc();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return null;
	}

	@Override
	public boolean showInAlgebraView() {
		return false;
	}

	@Override
	protected void onSourceChanged() {
		if (!hasSoundManager()) {
			return;
		}
		app.getSoundManager().loadGeoAudio(this);
	}

	@Override
	public boolean isGeoAudio() {
		return true;
	}

	@Override
	public void play() {
		if (!hasSoundManager()) {
			return;
		}
		app.getSoundManager().play(this);
	}

	@Override
	public boolean isPlaying() {
		if (!hasSoundManager()) {
			return false;
		}
		return app.getSoundManager().isPlaying(this);
	}

	@Override
	public int getDuration() {
		if (!hasSoundManager()) {
			return -1;
		}
		return app.getSoundManager().getDuration(src);
	}

	@Override
	public int getCurrentTime() {
		if (!hasSoundManager()) {
			return -1;
		}
		return app.getSoundManager().getCurrentTime(src);
	}

	@Override
	public void setCurrentTime(int secs) {
		if (!hasSoundManager()) {
			return;
		}
		app.getSoundManager().setCurrentTime(src, secs);
	}

	@Override
	public void pause() {
		if (!hasSoundManager()) {
			return;
		}
		app.getSoundManager().pause(this);
	}

	@Override
	public void remove() {
		pause();
		super.remove();
	}

	private boolean hasSoundManager() {
		return app.getSoundManager() != null;
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		if (src != null) {
			sb.append("\t<audio src=\"");
			sb.append(StringUtil.encodeXML(src));
			sb.append("\"/>\n");
		}
	}

	@Override
	public MediaFormat getFormat() {
		return MediaFormat.AUDIO_HTML5;
	}

	/**
	 * 
	 * @param src
	 *            the audio source URL to set.
	 */
	public void setSrc(String src) {
		setSrc(src, MediaFormat.AUDIO_HTML5);
	}
}
