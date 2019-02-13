package org.geogebra.web.full.gui.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

@SuppressWarnings("javadoc")
public interface StyleBarResources extends ClientBundle {
	StyleBarResources INSTANCE = GWT.create(StyleBarResources.class);
	
	//EUCLIDIAN STYLEBAR:
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_show_or_hide_the_axes.png")
	ImageResource axes();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_axes_2_arrows.png")
	ImageResource axes_2arrows();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_axes_4_arrows.png")
	ImageResource axes_4arrows();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_empty.png")
	ImageResource stylingbar_empty();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_show_or_hide_the_grid.png")
	ImageResource grid();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_show_or_hide_the_polar_grid.png")
	ImageResource polar_grid();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_show_or_hide_the_isometric_grid.png")
	ImageResource isometric_grid();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_standardview.png")
	ImageResource standard_view();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_view_all_objects.png")
	ImageResource view_all_objects();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_point_capturing.png")
	ImageResource magnet();
	
	//DELETE STYLEBAR
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_delete_small.png")
	ImageResource stylingbar_delete_small();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_delete_medium.png")
	ImageResource stylingbar_delete_medium();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_graphicsview_delete_large.png")
	ImageResource stylingbar_delete_large();
	
	//LINES
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_line-dash-dot.png")
	ImageResource line_dash_dot();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_line-dashed-long.png")
	ImageResource line_dashed_long();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_line-dashed-short.png")
	ImageResource line_dashed_short();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_line-dotted.png")
	ImageResource line_dotted();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_line-solid.png")
	ImageResource line_solid();
	
	//POINTS
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_point-full.png")
	ImageResource point_full();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_point-empty.png")
	ImageResource point_empty();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_point-cross.png")
	ImageResource point_cross();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_point-cross-diag.png")
	ImageResource point_cross_diag();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_point-diamond-full.png")
	ImageResource point_diamond();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_point-diamond-empty.png")
	ImageResource point_diamond_empty();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_point-up.png")
	ImageResource point_up();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_point-down.png")
	ImageResource point_down();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_point-left.png")
	ImageResource point_left();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_point-right.png")
	ImageResource point_right();
	
	//TEXT
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_text_font_size.png")
	ImageResource font_size();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_pin.png")
	ImageResource fixPosition();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_object_fixed.png")
	ImageResource objectFixed();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_object_unfixed.png")
	ImageResource objectUnfixed();
	
	// PATTERNS
	@Source("org/geogebra/common/icons/png/web/stylingbar/pattern_cross_hatching.png")
	ImageResource pattern_cross_hatching();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/pattern_dots.png")
	ImageResource pattern_dots();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/pattern_filled.png")
	ImageResource pattern_filled();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/pattern_hatching.png")
	ImageResource pattern_hatching();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/pattern_honeycomb.png")
	ImageResource pattern_honeycomb();

	//ALGEBRA STYLEBAR
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_algebraview_auxiliary_objects.png")
	ImageResource auxiliary();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_algebraview_sort_objects_by.png")
	ImageResource sortObjects();

	// SPREADSHEET
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_spreadsheet_align_center.png")
	ImageResource stylingbar_spreadsheet_align_center();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_spreadsheet_align_left.png")
	ImageResource stylingbar_spreadsheet_align_left();
	
	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_spreadsheet_align_right.png")
	ImageResource stylingbar_spreadsheet_align_right();

	@Source("org/geogebra/common/icons/png/web/stylingbar/stylingbar_spreadsheetview_show_input_bar.png")
	ImageResource description();
	
}
