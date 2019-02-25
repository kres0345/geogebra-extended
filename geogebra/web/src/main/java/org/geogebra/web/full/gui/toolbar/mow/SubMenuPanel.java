package org.geogebra.web.full.gui.toolbar.mow;

import java.util.Vector;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolbar.ToolbarItem;
import org.geogebra.web.full.gui.toolbar.ToolButton;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class of submenus of MOWToorbar
 * 
 * @author Laszlo Gal, Alicia Hofstaetter
 *
 */
public abstract class SubMenuPanel extends FlowPanel
		implements FastClickHandler, SetLabels {
	/**
	 * panel row
	 */
	protected FlowPanel panelRow;
	/** app **/
	AppW app;
	/**
	 * Here goes the toolbar contents ie the buttons
	 */
	FlowPanel contentPanel;

	/**
	 * group panel
	 */
	protected static class GroupPanel extends FlowPanel {
		private static final int BUTTON_WIDTH = 104;

		/**
		 * constructor
		 */
		public GroupPanel() {
			addStyleName("groupPanel");
		}

		/**
		 * @param columns
		 *            nr of cloumns
		 */
		public void setColumns(int columns) {
			setWidth(((columns) * BUTTON_WIDTH) + "px");
		}
	}

	/**
	 * 
	 * @param app
	 *            GGB application.
	 */
	public SubMenuPanel(AppW app) {
		this.app = app;
		createGUI();
	}

	/**
	 * Makes the content panel and the info panel, if needed.
	 */
	protected void createGUI() {
		addStyleName("mowSubMenu");
		createContentPanel();
	}

	/**
	 * Creates the scrollable panel of contents.
	 */
	protected void createContentPanel() {
		contentPanel = new FlowPanel();
		contentPanel.addStyleName("mowSubMenuContent");
		add(contentPanel);
	}

	/**
	 * @param mowToolsDefString
	 *            mow toolbar definition
	 */
	public void createPanelRow(String mowToolsDefString) {
		panelRow = new FlowPanel();
		panelRow.addStyleName("panelRow");
		addModesToToolbar(panelRow, mowToolsDefString);
		contentPanel.add(panelRow);
	}

	/**
	 * 
	 * @param toolbarString
	 *            GGB toolbar definition string.
	 * @return The vector of modes.
	 */
	protected Vector<ToolbarItem> getToolbarVec(String toolbarString) {
		Vector<ToolbarItem> toolbarVec;
		try {
			toolbarVec = ToolBar.parseToolbarString(toolbarString);
		} catch (Exception e) {
			toolbarVec = ToolBar.parseToolbarString(ToolBar.getAllTools(app));
		}
		return toolbarVec;
	}

	/**
	 * Adds tool buttons to an arbitrary panel depending on a toolbar definition
	 * string.
	 * 
	 * @param panel
	 *            The panel to add mode buttons.
	 * @param toolbarString
	 *            The toolbar definition string
	 */
	protected void addModesToToolbar(FlowPanel panel, String toolbarString) {
		Vector<ToolbarItem> toolbarVec = getToolbarVec(toolbarString);
		for (int i = 0; i < toolbarVec.size(); i++) {
			ToolbarItem ob = toolbarVec.get(i);
			Vector<Integer> menu = ob.getMenu();
			addModeMenu(panel, menu);
		}
	}

	/**
	 * Add buttons to a panel depending a mode vector. add modes to a group so
	 * they get grouped in the toolbox
	 * 
	 * @param panel
	 *            The panel to add the mode buttons.
	 * @param menu
	 *            The vector of modes to add buttons.
	 */
	protected void addModeMenu(FlowPanel panel, Vector<Integer> menu) {
		int col = 0;
		GroupPanel group = new GroupPanel();
		for (Integer mode : menu) {
			if (app.isModeValid(mode)) {
				ToolButton btn = new ToolButton(mode, app, this);
				group.add(btn);
				col++;
			}
		}
		group.setColumns(col / 2 + col % 2);
		panel.add(group);
	}

	/**
	 * Initializes the submenu when it is opened from MOWToolbar.
	 */
	public void onOpen() {
		int mode = app.getMode();
		setMode(mode);
	}

	@Override
	public void onClick(Widget source) {
		int mode = Integer.parseInt(source.getElement().getAttribute("mode"));
		if (mode == EuclidianConstants.MODE_IMAGE) {
			// set css before file picker
			setMode(mode);
		}
		app.setMode(mode);
		closeFloatingMenus();
	}

	/**
	 * Closes burger menu and page control panel
	 */
	public void closeFloatingMenus() {
		if (app.isMenuShowing()) {
			app.toggleMenu();
		}
		((AppWFull) app).getAppletFrame().getPageControlPanel().close();
	}

	/**
	 * Set css to selected for given mode
	 * 
	 * @param mode
	 *            The mode to select
	 */
	public void setMode(int mode) {
		for (int i = 0; i < panelRow.getWidgetCount(); i++) {
			FlowPanel w = (FlowPanel) panelRow.getWidget(i);
			for (int j = 0; j < w.getWidgetCount(); j++) {
				int modeID = Integer.parseInt(
						w.getWidget(j).getElement().getAttribute("mode"));
				if (modeID != mode) {
					w.getWidget(j).getElement().setAttribute("selected",
							"false");
					((ToolButton) w.getWidget(j)).setSelected(false);
				} else {
					w.getWidget(j).getElement().setAttribute("selected",
							"true");
					((ToolButton) w.getWidget(j)).setSelected(true);
				}
			}
		}
	}

	@Override
	public void setLabels() {
		for (int i = 0; i < panelRow.getWidgetCount(); i++) {
			FlowPanel w = (FlowPanel) panelRow.getWidget(i);
			for (int j = 0; j < w.getWidgetCount(); j++) {
				((ToolButton) w.getWidget(j)).setLabel();
			}
		}
	}

	/**
	 * @return first mode; to be selected once this submenu is opened
	 */
	public abstract int getFirstMode();

	/**
	 * @param mode
	 *            id of tool
	 * @return true if mode is valid for the panel
	 */
	public abstract boolean isValidMode(int mode);
}
