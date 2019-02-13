package org.geogebra.web.html5.gui.util;

import org.geogebra.web.resources.SVGResource;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.Image;

/**
 * Image that prevents dragging by default
 */
public class NoDragImage extends Image implements HasResource {

	/**
	 * @param uri
	 *            URL
	 * @param width
	 *            width in pixels
	 */
	public NoDragImage(String uri, int width) {
		this(uri);
		this.setWidth(width + "px");
	}

	/**
	 * @param uri
	 *            URL
	 * @param width
	 *            in px
	 * @param height
	 *            in px
	 */
	public NoDragImage(ResourcePrototype uri, int width, int height) {
		this(safeURI(uri));
		this.setWidth(width + "px");
		if (height > 0) {
			this.setHeight(height + "px");
		}
	}

	/**
	 * @param uri
	 *            URL
	 * @param size
	 *            in px
	 */
	public NoDragImage(ResourcePrototype uri, int size) {
		this(uri, size, size);
	}

	/**
	 * @param uri
	 *            URI
	 */
	public NoDragImage(String uri) {
		super(uri);
		this.getElement().setAttribute("draggable", "false");
	}

	/**
	 * 
	 * @param res
	 *            SVG or PNG resource
	 * @return safe URI
	 */
	public static String safeURI(ResourcePrototype res) {
		if (res instanceof ImageResource) {
			return ((ImageResource) res).getSafeUri().asString();
		}
		if (res instanceof SVGResource) {
			return ((SVGResource) res).getSafeUri().asString();
		}
		return "";
	}

	public void setResource(ResourcePrototype res) {
		this.setUrl(NoDragImage.safeURI(res));
		
	}
}
