package org.geogebra.web.geogebra3D.web.gui.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

@SuppressWarnings("javadoc")
public interface StyleBar3DResources extends ClientBundle {
	StyleBar3DResources INSTANCE = GWT.create(StyleBar3DResources.class);

	// EUCLIDIAN 3D STYLEBAR:

	@Source("org/geogebra/common/icons/png/stylebar3d/stylingbar_graphics3D_plane.gif")
	ImageResource plane();

	@Source("org/geogebra/common/icons/png/stylebar3d/stylingbar_graphics3D_axes_plane.gif")
	ImageResource axes_plane();

	@Source("org/geogebra/common/icons/png/stylebar3d/stylingbar_graphics3D_rotateview_play.gif")
	ImageResource rotateViewPlay();

	@Source("org/geogebra/common/icons/png/stylebar3d/stylingbar_graphics3D_rotateview_pause.gif")
	ImageResource rotateViewPause();

	@Source("org/geogebra/common/icons/png/stylebar3d/stylingbar_graphics3D_clipping_big.gif")
	ImageResource clippingBig();

	@Source("org/geogebra/common/icons/png/stylebar3d/stylingbar_graphics3D_clipping_medium.gif")
	ImageResource clippingMedium();

	@Source("org/geogebra/common/icons/png/stylebar3d/stylingbar_graphics3D_clipping_small.gif")
	ImageResource clippingSmall();

	@Source("org/geogebra/common/icons/png/stylebar3d/stylingbar_graphics3D_standardview_rotate.gif")
	ImageResource standardViewRotate();

	@Source("org/geogebra/common/icons/png/stylebar3d/stylingbar_graphics3D_view_xy.gif")
	ImageResource viewXY();

	@Source("org/geogebra/common/icons/png/stylebar3d/stylingbar_graphics3D_view_xz.gif")
	ImageResource viewXZ();

	@Source("org/geogebra/common/icons/png/stylebar3d/stylingbar_graphics3D_view_yz.gif")
	ImageResource viewYZ();

	@Source("org/geogebra/common/icons/png/stylebar3d/stylingbar_graphics3D_view_orthographic.gif")
	ImageResource viewOrthographic();

	@Source("org/geogebra/common/icons/png/stylebar3d/stylingbar_graphics3D_view_perspective.gif")
	ImageResource viewPerspective();

	@Source("org/geogebra/common/icons/png/stylebar3d/stylingbar_graphics3D_view_glasses.gif")
	ImageResource viewGlasses();

	@Source("org/geogebra/common/icons/png/stylebar3d/stylingbar_graphics3D_view_oblique.gif")
	ImageResource viewOblique();

}
