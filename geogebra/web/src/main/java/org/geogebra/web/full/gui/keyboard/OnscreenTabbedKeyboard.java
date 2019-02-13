package org.geogebra.web.full.gui.keyboard;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.keyboard.web.KeyBoardButtonBase;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.full.gui.inputbar.InputBarHelpPanelW;
import org.geogebra.web.full.gui.inputbar.InputBarHelpPopup;
import org.geogebra.web.full.gui.util.VirtualKeyboardGUI;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.CSSAnimation;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;

/**
 * Web implementation of onscreen keyboard
 * 
 * @author Zbynek, based on Balazs's cross-platform model
 */
public class OnscreenTabbedKeyboard extends TabbedKeyboard
		implements VirtualKeyboardGUI {

	private InputBarHelpPopup helpPopup = null;

	/**
	 * @param app
	 *            keyboard context
	 * @param scientific
	 *            whether to use scientific layout
	 */
	public OnscreenTabbedKeyboard(HasKeyboard app, boolean scientific) {
		super((AppW) app, app, scientific);
		buildGUI();
		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// just stop propagation

			}
		});
	}
	
	private void createHelpPopup() {
		if (helpPopup != null) {
			return;
		}
		AlgebraViewW av = (AlgebraViewW) ((AppW) hasKeyboard).getAlgebraView();
		helpPopup = new InputBarHelpPopup((AppW) hasKeyboard,
				av != null ? av.getInputTreeItem() : null,
				"helpPopupAV");
		helpPopup.addAutoHidePartner(this.getElement());
		helpPopup.addCloseHandler(new CloseHandler<GPopupPanel>() {

			@Override
			public void onClose(CloseEvent<GPopupPanel> event) {
				// TODO handle closing?
			}

		});
	}
	
	@Override
	public void show() {
		this.keyboardWanted = true;
		checkLanguage();
		setVisible(true);
	}

	@Override
	public void resetKeyboardState() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setStyleName() {
		// TODO Auto-generated method stub
	}

	@Override
	public void afterShown(final Runnable runnable) {
		CSSAnimation.runOnAnimation(runnable, getElement(), "animating");
	}

	@Override
	public void prepareShow(boolean animated) {
		if (animated) {
			addStyleName("animating");
		}
		show();

	}

	@Override
	public void onClick(KeyBoardButtonBase btn, PointerEventType type) {
		ToolTipManagerW.hideAllToolTips();
		super.onClick(btn, type);
	}

	@Override
	public void remove(final Runnable runnable) {
		hasKeyboard.updateCenterPanelAndViews();
		this.addStyleName("animatingOut");
		CSSAnimation.runOnAnimation(new Runnable() {
			@Override
			public void run() {
				removeFromParent();
				runnable.run();
			}
		}, getElement(), "animating");
	}

	@Override
	protected void showHelp(int x, int y) {
		boolean show = helpPopup != null && helpPopup.isShowing();
		if (!show) {
			createHelpPopup();
			GuiManagerInterfaceW gm = ((AppW) hasKeyboard).getGuiManager();
			InputBarHelpPanelW helpPanel = (InputBarHelpPanelW) gm
					.getInputHelpPanel();
			updateHelpPosition(helpPanel, x, y);
			
		} else if (helpPopup != null) {
			helpPopup.hide();
		}
	}
	
	private void updateHelpPosition(final InputBarHelpPanelW helpPanel,
			final int x, final int y) {
		helpPopup.setPopupPositionAndShow(new GPopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				doUpdateHelpPosition(helpPanel, x, y);
			}
		});
	}

	/**
	 * @param helpPanel
	 *            help panel
	 * @param x
	 *            popup x-coord
	 * @param y
	 *            popup y-coord
	 */
	protected void doUpdateHelpPosition(final InputBarHelpPanelW helpPanel,
			final int x, final int y) {
		AppW appw = (AppW) hasKeyboard;
		double scale = appw.getArticleElement().getScaleX();
		double renderScale = appw.getArticleElement().getDataParamApp() ? scale
				: 1;
		double left = x - appw.getAbsLeft()
				- helpPanel.getPreferredWidth(scale);

		helpPopup.getElement().getStyle().setProperty("left",
				left * renderScale + "px");
		int maxOffsetHeight;
		int totalHeight = (int) appw.getHeight();
		int toggleButtonTop = (int) ((y - (int) appw.getAbsTop()) / scale);
		if (toggleButtonTop < totalHeight / 2) {
			int top = (toggleButtonTop);
			maxOffsetHeight = totalHeight - top;
			helpPopup.getElement().getStyle().setProperty("top",
					top * renderScale + "px");
			helpPopup.getElement().getStyle().setProperty("bottom", "auto");
			helpPopup.removeStyleName("helpPopupAVBottom");
			helpPopup.addStyleName("helpPopupAV");
		} else {
			int minBottom = appw.isApplet() ? 0 : 10;
			int bottom = (totalHeight - toggleButtonTop);
			maxOffsetHeight = bottom > 0 ? totalHeight - bottom
					: totalHeight - minBottom;
			helpPopup.getElement().getStyle().setProperty("bottom",
					(bottom > 0 ? bottom : minBottom) * renderScale + "px");
			helpPopup.getElement().getStyle().setProperty("top", "auto");
			helpPopup.removeStyleName("helpPopupAV");
			helpPopup.addStyleName("helpPopupAVBottom");
		}
		helpPanel.updateGUI(maxOffsetHeight, 1);
		helpPopup.show();
	}

	@Override
	public void addAutoHidePartner(GPopupPanel popup) {
		popup.addAutoHidePartner(getElement());
	}
}
