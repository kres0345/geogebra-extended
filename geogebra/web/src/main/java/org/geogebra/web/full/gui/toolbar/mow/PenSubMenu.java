package org.geogebra.web.full.gui.toolbar.mow;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.toolbar.ToolButton;
import org.geogebra.web.full.gui.util.GeoGebraIconW;
import org.geogebra.web.full.gui.util.PenPreview;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.sliderPanel.SliderPanelW;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Pen/Eraser/Color submenu for MOWToolbar.
 * 
 * @author Laszlo Gal
 *
 */
public class PenSubMenu extends SubMenuPanel {
	private static final int MAX_ERASER_SIZE = 100;
	private static final int ERASER_STEP = 20;
	private ToolButton pen;
	private ToolButton eraser;
	private ToolButton highlighter;
	private ToolButton select;
	private FlowPanel penPanel;
	private FlowPanel colorPanel;
	private FlowPanel sizePanel;
	private Label[] btnColor;
	private GColor[] penColor;
	private SliderPanelW slider;
	private StandardButton btnCustomColor;
	private PenPreview preview;
	/** whether colors are enabled */
	boolean colorsEnabled;
	// preset colors black, green, teal,blue, purple,magenta, red, carrot,
	// yellow
	private final static int[] HEX_COLORS = { 0x000000, 0x2E7D32, 0x00A8A8,
			0x1565C0, 0x6557D2, 0xCC0099, 0xD32F2F, 0xDB6114, 0xFFCC00 };
	private GColor lastSelectedPenColor = GColor.BLACK;
	private GColor lastSelectedHighlighterColor = GColor.MOW_GREEN;
	private int lastPenThickness = EuclidianConstants.DEFAULT_PEN_SIZE;
	private int lastHighlighterThinckness = EuclidianConstants.DEFAULT_HIGHLIGHTER_SIZE;

	/**
	 * 
	 * @param app
	 *            ggb app.
	 */
	public PenSubMenu(AppW app) {
		super(app/* , false */);
		addStyleName("penSubMenu");
		// needed for slider mouse events
		ClickStartHandler.initDefaults(this, false, true);
	}

	private void createPenPanel() {
		penPanel = new FlowPanel();
		penPanel.addStyleName("penPanel");
		pen = new ToolButton(EuclidianConstants.MODE_PEN, app, this);
		eraser = new ToolButton(EuclidianConstants.MODE_ERASER, app, this);
		highlighter = new ToolButton(EuclidianConstants.MODE_HIGHLIGHTER,
				app, this);
		select = new ToolButton(EuclidianConstants.MODE_SELECT_MOW, app,
				this);
		penPanel.add(LayoutUtilW.panelRow(select, pen, eraser, highlighter));
	}

	/**
	 * Create color buttons for selecting pen color
	 * 
	 * @param aColor
	 *            color
	 * @return button
	 */
	private Label createColorButton(GColor aColor, final int colorIndex) {
		ImageOrText color = GeoGebraIconW.createColorSwatchIcon(1, null,
				aColor);
		Label label = new Label();
		color.applyToLabel(label);
		label.addStyleName("mowColorButton");
		ClickStartHandler.init(label, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (!colorsEnabled) {
					return;
				}
				selectColor(colorIndex);
			}
		});
		return label;
	}

	private void createColorPanel() {
		colorPanel = new FlowPanel();
		colorPanel.addStyleName("colorPanel");
		btnColor = new Label[HEX_COLORS.length];
		penColor = new GColor[HEX_COLORS.length];
		for (int i = 0; i < HEX_COLORS.length; i++) {
			penColor[i] = GColor.newColorRGB(HEX_COLORS[i]);
			btnColor[i] = createColorButton(penColor[i], i);
		}
		btnCustomColor = new StandardButton(
				MaterialDesignResources.INSTANCE.add_black(), null, 24, app);
		btnCustomColor.addStyleName("mowColorButton");
		btnCustomColor.addStyleName("mowColorPlusButton");
		btnCustomColor.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				openColorDialog();
			}
		});
		colorPanel.add(LayoutUtilW.panelRow(btnColor[0], btnColor[1],
				btnColor[2], btnColor[3], btnColor[4], btnColor[5], btnColor[6],
				btnColor[7], btnColor[8], btnCustomColor));
	}

	/**
	 * Create panel with slider for pen and eraser size
	 */
	private void createSizePanel() {
		sizePanel = new FlowPanel();
		sizePanel.addStyleName("sizePanel");
		slider = new SliderPanelW(0, 20, app.getKernel(), false);
		slider.addStyleName("mowOptionsSlider");
		setSliderRange(true);
		slider.setWidth(300);
		preview = new PenPreview(app, 50, 30);
		preview.addStyleName("preview");
		slider.add(preview);
		sizePanel.add(slider);
		slider.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				sliderValueChanged(event.getValue());
			}
		});
	}

	private void setSliderRange(boolean isPen) {
		// same min for pen and highlighter
		slider.setMinimum(EuclidianConstants.MIN_PEN_HIGHLIGHTER_SIZE, false);
		slider.setMaximum(isPen ? EuclidianConstants.MAX_PEN_HIGHLIGHTER_SIZE
				: MAX_ERASER_SIZE, false);
		slider.setStep(
				isPen ? EuclidianConstants.DEFAULT_PEN_STEP : ERASER_STEP);
	}

	/**
	 * Sets the size of pen/eraser from the slider value.
	 * 
	 * @param value
	 *            value of slider
	 */
	void sliderValueChanged(double value) {
		if (colorsEnabled) {
			getPenGeo().setLineThickness((int) value);
			if (app.getActiveEuclidianView()
					.getMode() == EuclidianConstants.MODE_PEN) {
				lastPenThickness = (int) value;
			} else if (app.getActiveEuclidianView()
					.getMode() == EuclidianConstants.MODE_HIGHLIGHTER) {
				lastHighlighterThinckness = (int) value;
			}
			updatePreview();
		} else {
			app.getActiveEuclidianView().getSettings().setDeleteToolSize((int) value);
		}
		closeFloatingMenus();
	}

	@Override
	protected void createContentPanel() {
		super.createContentPanel();
		createPenPanel();
		createColorPanel();
		createSizePanel();
		contentPanel.add(
				LayoutUtilW.panelRow(penPanel, colorPanel, sizePanel));
	}

	private void doSelectPen() {
		pen.getElement().setAttribute("selected", "true");
		pen.setSelected(true);
		setColorsEnabled(true);
		selectColor(lastSelectedPenColor);
		setSliderRange(true);
		slider.setValue((double) lastPenThickness);
		getPenGeo().setLineThickness(lastPenThickness);
		getPenGeo().setLineOpacity(255);
		slider.getElement().setAttribute("disabled", "false");
		slider.disableSlider(false);
		preview.setVisible(true);
		updatePreview();
	}

	private void doSelectHighlighter() {
		highlighter.getElement().setAttribute("selected", "true");
		highlighter.setSelected(true);
		setColorsEnabled(true);
		selectColor(lastSelectedHighlighterColor);
		setSliderRange(true);
		slider.setValue((double) lastHighlighterThinckness);
		getPenGeo().setLineThickness(lastHighlighterThinckness);
		getPenGeo()
				.setLineOpacity(EuclidianConstants.DEFAULT_HIGHLIGHTER_OPACITY);
		slider.getElement().setAttribute("disabled", "false");
		slider.disableSlider(false);
 		preview.setVisible(true);
		updatePreview();
	}

	private void doSelectEraser() {
		reset();
		eraser.getElement().setAttribute("selected", "true");
		eraser.setSelected(true);
		setColorsEnabled(false);
		setSliderRange(false);
		int delSize = app.getActiveEuclidianView().getSettings()
				.getDeleteToolSize();
		slider.setValue((double) delSize);
		slider.getElement().setAttribute("disabled", "false");
		slider.disableSlider(false);
		preview.setVisible(false);
	}

	private void doSelectSelect() {
		reset();
		select.getElement().setAttribute("selected", "true");
		select.setSelected(true);
		slider.getElement().setAttribute("disabled", "true");
		slider.disableSlider(true);
	}

	/**
	 * Unselect all buttons and disable colors
	 */
	public void reset() {
		pen.getElement().setAttribute("selected", "false");
		pen.setSelected(false);
		eraser.getElement().setAttribute("selected", "false");
		eraser.setSelected(false);
		select.getElement().setAttribute("selected", "false");
		select.setSelected(false);
		highlighter.getElement().setAttribute("selected", "false");
		highlighter.setSelected(false);
		setColorsEnabled(false);
	}

	/**
	 * @param idx
	 *            index
	 */
	public void selectColor(int idx) {
		for (int i = 0; i < btnColor.length; i++) {
			if (idx == i) {
				getPenGeo().setObjColor(penColor[i]);
				if (colorsEnabled) {
					btnColor[i].addStyleName("mowColorButton-selected");
					if (app.getMode() == EuclidianConstants.MODE_HIGHLIGHTER) {
						getPenGeo().setLineOpacity(
								EuclidianConstants.DEFAULT_HIGHLIGHTER_OPACITY);
						lastSelectedHighlighterColor = penColor[i];
					} else {
						lastSelectedPenColor = penColor[i];
					}
				}
			} else {
				btnColor[i].removeStyleName("mowColorButton-selected");
			}
		}
		updatePreview();
	}

	// remember and set a color that was picked from color chooser
	private void selectColor(GColor color) {
		getPenGeo().setObjColor(color);
		updatePreview();
	}

	private void setColorsEnabled(boolean enable) {
		for (int i = 0; i < btnColor.length; i++) {
			if (enable) {
				btnColor[i].removeStyleName("disabled");
				if (app.getMode() == EuclidianConstants.MODE_HIGHLIGHTER) {
					if (penColor[i] == lastSelectedHighlighterColor) {
						btnColor[i].addStyleName("mowColorButton-selected");
					}
				} else if (app.getMode() == EuclidianConstants.MODE_PEN) {
					if (penColor[i] == lastSelectedPenColor) {
						btnColor[i].addStyleName("mowColorButton-selected");
					}
				}
			} else {
				btnColor[i].addStyleName("disabled");
				btnColor[i].removeStyleName("mowColorButton-selected");
			}
		}
		if (enable) {
			btnCustomColor.removeStyleName("disabled");
		} else {
			btnCustomColor.addStyleName("disabled");
		}
		colorsEnabled = enable;
	}

	private GeoElement getPenGeo() {
		return app.getActiveEuclidianView().getEuclidianController()
				.getPen().defaultPenLine;
	}

	@Override
	public void setMode(int mode) {
		reset();
		if (mode == EuclidianConstants.MODE_SELECT
				|| mode == EuclidianConstants.MODE_SELECT_MOW) {
			doSelectSelect();
		} else if (mode == EuclidianConstants.MODE_ERASER) {
			doSelectEraser();
		} else if (mode == EuclidianConstants.MODE_PEN) {
			doSelectPen();
		} else if (mode == EuclidianConstants.MODE_HIGHLIGHTER) {
			doSelectHighlighter();
		}
	}

	/**
	 * @return last selected pen color
	 */
	public GColor getLastSelectedPenColor() {
		return lastSelectedPenColor;
	}

	/**
	 * @param lastSelectedPenColor
	 *            update last selected pen color
	 */
	public void setLastSelectedPenColor(GColor lastSelectedPenColor) {
		this.lastSelectedPenColor = lastSelectedPenColor;
	}

	/**
	 * @return last selected highlighter color
	 */
	public GColor getLastSelectedHighlighterColor() {
		return lastSelectedHighlighterColor;
	}

	/**
	 * @param lastSelectedHighlighterColor
	 *            update last selected highlighter color
	 */
	public void setLastSelectedColor(GColor lastSelectedHighlighterColor) {
		this.lastSelectedHighlighterColor = lastSelectedHighlighterColor;
	}

	/**
	 * @return get preview of pen
	 */
	public PenPreview getPreview() {
		return preview;
	}

	@Override
	public int getFirstMode() {
		return EuclidianConstants.MODE_PEN;
	}

	private void updatePreview() {
		preview.update();
	}

	/**
	 * Opens the custom color dialog
	 */
	public void openColorDialog() {
		if (colorsEnabled) {
			final GeoElement penGeo = getPenGeo();
			DialogManagerW dm = (DialogManagerW) (app.getDialogManager());
			GColor originalColor = penGeo.getObjectColor();
			dm.showColorChooserDialog(originalColor, new ColorChangeHandler() {
				@Override
				public void onForegroundSelected() {
					// do nothing here
				}

				@Override
				public void onColorChange(GColor color) {
					penGeo.setObjColor(color);
					// setPenIconColor(color.toString());
					setLastSelectedColor(color);
					penGeo.setLineOpacity(
							app.getMode() == EuclidianConstants.MODE_HIGHLIGHTER
									? EuclidianConstants.DEFAULT_HIGHLIGHTER_OPACITY
									: 255);
					selectColor(-1);
					getPreview().update();
				}

				@Override
				public void onClearBackground() {
					// do nothing
				}

				@Override
				public void onBarSelected() {
					// do nothing
				}

				@Override
				public void onBackgroundSelected() {
					// do nothing
				}

				@Override
				public void onAlphaChange() {
					// do nothing
				}
			});
		}
	}

	/**
	 * reset size of pen (for pen and highlighter)
	 */
	public void resetPen() {
		lastPenThickness = EuclidianConstants.DEFAULT_PEN_SIZE;
		lastHighlighterThinckness = EuclidianConstants.DEFAULT_HIGHLIGHTER_SIZE;
		if (app.getMode() == EuclidianConstants.MODE_PEN) {
			slider.setValue((double) lastPenThickness);
		}
	}

	@Override
	public boolean isValidMode(int mode) {
		return mode == EuclidianConstants.MODE_SELECT_MOW
				|| mode == EuclidianConstants.MODE_PEN
				|| mode == EuclidianConstants.MODE_ERASER
				|| mode == EuclidianConstants.MODE_HIGHLIGHTER;
	}

	@Override
	public void setLabels() {
		pen.setLabel();
		select.setLabel();
		eraser.setLabel();
		highlighter.setLabel();
	}
}
