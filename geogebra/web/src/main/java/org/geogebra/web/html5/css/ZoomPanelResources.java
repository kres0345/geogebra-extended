package org.geogebra.web.html5.css;

import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface ZoomPanelResources extends ClientBundle {
	static ZoomPanelResources INSTANCE = GWT.create(ZoomPanelResources.class);

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/ic_fullscreen_black_18px.svg")
	SVGResource fullscreen_black18();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/ic_fullscreen_exit_black_18px.svg")
	SVGResource fullscreen_exit_black18();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_home_black_24px.svg")
	SVGResource home_zoom_black18();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/ic_zoom_in_black_24px.svg")
	SVGResource zoomin_black24();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/ic_zoom_out_black_24px.svg")
	SVGResource zoomout_black24();

}
