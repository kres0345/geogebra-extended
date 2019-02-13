package org.geogebra.web.full.gui.pagecontrolpanel;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Page Preview Card showing preview of EuclidianView
 * 
 * @author Alicia Hofstaetter
 *
 */
public class PagePreviewCard extends FlowPanel
		implements SetLabels {

	/** Margin of the cards. */
	static final int MARGIN = 16;

	/** Height of a card without margins */
	static final int CARD_HEIGHT = 172;

	/** Space needed for the card to drag. */
	static final int SPACE_HEIGHT = CARD_HEIGHT + 2 * MARGIN;
	private AppW app;
	private Localization loc;
	private int pageIndex;
	private FlowPanel imagePanel;
	private String image;
	private FlowPanel titlePanel;
	private Label titleLabel;
	// private static final int LABELFONT_SIZE = 16;
	// private AutoCompleteTextFieldW textField;
	// private boolean isTitleSet = false;
	private ContextMenuButtonPreviewCard contextMenu;
	private int grabY; // where the user grabbed the card when dragging.
	/**
	 * ggb file
	 */
	protected GgbFile file;
	
	/**
	 * @param app
	 *            parent application
	 * @param pageIndex
	 *            current page index
	 * @param file
	 *            see {@link GgbFile}
	 */
	public PagePreviewCard(AppW app, int pageIndex, GgbFile file) {
		this.app = app;
		this.pageIndex = pageIndex;
		this.file = file;
		this.loc = app.getLocalization();
		this.image = file.get("geogebra_thumbnail.png");
		initGUI();
	}

	/**
	 * Duplicates card with pageIndex incremented by 1.
	 * 
	 * @param source
	 *            to duplicate.
	 * @param targetID
	 *            ID of the new slide
	 * @return The duplicated card.
	 */
	public static PagePreviewCard duplicate(PagePreviewCard source,
			String targetID) {
		return new PagePreviewCard(source.app, source.getPageIndex() + 1,
				source.getFile().duplicate(targetID));
	}
	
	private void initGUI() {
		addStyleName("mowPagePreviewCard");
		if (!(Browser.isMobile())) {
			addStyleName("desktop");
		}

		imagePanel = new FlowPanel();
		imagePanel.addStyleName("mowImagePanel");

		titlePanel = new FlowPanel();
		titlePanel.addStyleName("mowTitlePanel");
		titleLabel = new Label("");
		titlePanel.add(titleLabel);

		contextMenu = new ContextMenuButtonPreviewCard(app, this);
		titlePanel.add(contextMenu);

		add(imagePanel);
		add(titlePanel);
		if (StringUtil.empty(image)) {
			updatePreviewImage();
		} else {
			setPreviewImage(image);
		}
		// addTextField();
		updateLabel();
	}

	/*
	 * private void addTextField() { textField =
	 * InputPanelW.newTextComponent(app); textField.setAutoComplete(false);
	 * textField.addFocusListener(new FocusListenerW(this) {
	 * 
	 * @Override protected void wrapFocusLost() { rename(); } });
	 * textField.addKeyHandler(new KeyHandler() {
	 * 
	 * @Override public void keyReleased(KeyEvent e) { if (e.isEnterKey()) {
	 * rename(); } } }); titlePanel.add(textField); }
	 */

	/**
	 * remember if title was renamed
	 */
	/*
	 * protected void rename() { if
	 * (textField.getText().equals(getDefaultLabel())) { isTitleSet = false; }
	 * else { isTitleSet = true; } setTextFieldWidth();
	 * textField.setFocus(false); }
	 */

	/**
	 * using an approximate calculation for text field width.
	 * 
	 */
	/*
	 * private void setTextFieldWidth() { int length = LABELFONT_SIZE *
	 * (textField.getText().length() + 2);
	 * textField.setWidth(Math.max(Math.min(length, 178), 10)); }
	 */

	/**
	 * @return ggb file associated to this card
	 */
	public GgbFile getFile() {
		return file;
	}

	/**
	 * @param file
	 *            see {@link GgbFile}
	 */
	public void setFile(GgbFile file) {
		this.file = file;
	}

	private void setPreviewImage(String img) {
		image = img;
		if (image != null && image.length() > 0) {
			imagePanel.getElement().getStyle().setBackgroundImage(
					"url(" + Browser.normalizeURL(image) + ")");
		}
	}

	/**
	 * Updates the preview image
	 */
	public void updatePreviewImage() {
		imagePanel.clear();
		setPreviewImage(((EuclidianViewWInterface) app.getActiveEuclidianView())
				.getExportImageDataUrl(0.5, false));
	}

	/*
	 * private String getDefaultLabel() { return loc.getMenu("page") + " " +
	 * (pageIndex + 1); }
	 */

	private void updateLabel() {
		titleLabel.setText(loc.getMenu("page") + " " + (pageIndex + 1));
		/*
		 * if (!isTitleSet) { textField.setText(getDefaultLabel());
		 * setTextFieldWidth(); }
		 */
	}

	/**
	 * get the index of the page
	 * 
	 * @return page index
	 */
	public int getPageIndex() {
		return pageIndex;
	}

	/**
	 * set index of page
	 * 
	 * note: this will also update the title of the page
	 * 
	 * @param index
	 *            new index
	 */
	public void setPageIndex(int index) {
		pageIndex = index;
		updateLabel();
	}

	@Override
	public void setLabels() {
		contextMenu.setLabels();
		updateLabel();
	}

	/**
	 * @param x
	 *            is unused for now.
	 * @param y
	 *            coordinate.
	 */
	public void setDragPosition(int x, int y) {
		int top = getTopFromDrag(y);
		getElement().getStyle().setTop(top, Unit.PX);
	}

	/**
	 * 
	 * @param top
	 *            to set.
	 */
	public void setTop(int top) {
		getElement().getStyle().setTop(top, Unit.PX);
	}

	/**
	 * @return the top of card without margin.
	 */
	public int getTop() {
		return getElement().getOffsetTop() - MARGIN;
	}
	
	/**
	 * Change the top by a given value.
	 * 
	 * @param value
	 *            to change by.
	 */
	public void setTopBy(int value) {
		int top = getTop() + value;
		getElement().getStyle().setTop(top, Unit.PX);
	}

	/**
	 * 
	 * @param y
	 *            the top position of the drag.
	 * @return top of the card after drag
	 */
	private int getTopFromDrag(int y) {
		return y - getParent().getAbsoluteTop() - grabY + MARGIN;
	}

	/**
	 * 
	 * @return the bottom of the card.
	 */
	public int getAbsoluteBottom() {
		return getAbsoluteTop() + getOffsetHeight();
	}

	/**
	 * Checks if (x, y) is within the card.
	 * 
	 * @param x
	 *            coordinate to check.
	 * @param y
	 *            coordinate to check.
	 * @return if card was hit at (x, y).
	 */
	public boolean isHit(int x, int y) {
		int left = getAbsoluteLeft();
		int top = getComputedTop();
		int right = left + getOffsetWidth();
		int bottom = getComputedBottom();

		return x > left && x < right && y > top && y < bottom;
	}

	/**
	 * Make card grabbed (by pointer) at y.
	 * 
	 * @param y
	 *            coordinate where user has grabbed the card.
	 */
	public void grabCard(int y) {
		grabY = y - getAbsoluteTop() + 2 * MARGIN;
	}

	/**
	 * 
	 * @return the middle line of the card horizontally.
	 */
	public int getMiddleX() {
		return getAbsoluteLeft() + getOffsetWidth() / 2;
	}
	
	/**
	 * 
	 * @return the middle line of the card vertically
	 */
	public int getMiddleY() {
		return getAbsoluteTop() + getOffsetHeight() / 2;
	}

	/**
	 * Adds space before the card for animation.
	 */
	public void addSpaceTop() {
		getElement().getStyle().setMarginTop(SPACE_HEIGHT, Unit.PX);
		getElement().getStyle().setMarginBottom(MARGIN, Unit.PX);
	}

	/**
	 * Adds space after the card for animation.
	 */
	public void addSpaceBottom() {
		getElement().getStyle().setMarginTop(MARGIN, Unit.PX);
		getElement().getStyle().setMarginBottom(SPACE_HEIGHT, Unit.PX);
	}

	/**
	 * Removes space before the card for animation.
	 */
	public void removeSpace() {
		getElement().getStyle().setMarginTop(MARGIN, Unit.PX);
		getElement().getStyle().setMarginBottom(MARGIN, Unit.PX);
	}

	/**
	 * Removes space before the card for animation.
	 */
	public void clearSpace() {
		getElement().getStyle().setMarginTop(0, Unit.PX);
		getElement().getStyle().setMarginBottom(0, Unit.PX);
	}

	/**
	 * Sets margins for drag animation.
	 * 
	 * @param value
	 *            to set.
	 * @param down
	 *            the direction of the drag animation
	 */
	public void setSpaceValue(int value, boolean down) {
		int opposite = SPACE_HEIGHT  - value + MARGIN;
		getElement().getStyle().setMarginTop(down ? opposite : value, Unit.PX);
		getElement().getStyle().setMarginBottom(down ? value : opposite,
				Unit.PX);
	}

	/**
	 * 
	 * @return the scroll independent top based on the card index.
	 */
	public int getComputedTop() {
		return MARGIN + (pageIndex * (CARD_HEIGHT + MARGIN));
	}
	
	/**
	 * 
	 * @return the scroll independent bottom based on the card index.
	 */
	public int getComputedBottom() {
		return getComputedTop() + CARD_HEIGHT;
	}

	/**
	 * @param add
	 *            true to apply drag style
	 */
	public void addDragStartStyle(boolean add) {
		if (add) {
			addStyleName("dragCanStart");
		} else {
			removeStyleName("dragCanStart");
		}
	}
}
