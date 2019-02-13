package org.geogebra.web.full.gui.browser;

import org.geogebra.common.main.Feature;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.operations.NetworkOperation;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.web.full.gui.AuxiliaryHeaderPanel;
import org.geogebra.web.full.gui.browser.SearchPanel.SearchListener;
import org.geogebra.web.html5.gui.ResizeListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ProfilePanel;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

public class BrowseHeaderPanel extends AuxiliaryHeaderPanel
		implements ResizeListener, BooleanRenderable, EventRenderable {

	/** contains width of the leftHeader and margin of the searchDiv **/
	private final static int WIDTH_HEADER_FIRST = 105;

	private NetworkOperation op;

	private FlowPanel signInPanel;
	private Button signInButton;

	private ProfilePanel profilePanel;

	private LogInOperation login;
	private AppW app;
	private SearchPanel searchPanel;
	private BrowseGUI bg;

	/**
	 * @param app
	 *            application
	 * @param browseGUI
	 *            browsing UI
	 * @param op
	 *            network online / offline state
	 */
	public BrowseHeaderPanel(final AppW app, final BrowseGUI browseGUI,
			NetworkOperation op) {
		super(app.getLocalization(), browseGUI);
		this.bg = browseGUI;
		this.op = op;
		this.app = app;
		this.login = app.getLoginOperation();
		this.signInPanel = new FlowPanel();

		addSearchPanel();
		if (rightPanelNeeded()) {
			this.add(this.rightPanel);
		}

		createSignIn();
		setLabels();
	}

	private boolean rightPanelNeeded() {
		return !app.has(Feature.MAT_DESIGN_HEADER)
				|| AppW.smallScreen(app.getArticleElement());
	}

	private void addSearchPanel() {
		this.searchPanel = new SearchPanel(app);
		this.searchPanel.addSearchListener(new SearchListener() {
			@Override
			public void onSearch(final String query) {
				BrowseHeaderPanel.this.bg.displaySearchResults(query);
			}
		});
		this.add(this.searchPanel);
	}

	private void createSignIn() {
		op.getView().add(this);

		// this methods should be called only from AppWapplication or AppWapplet

		login.getView().add(this);
		if (login.isLoggedIn()) {
			onLogin(true, login.getModel().getLoggedInUser());
		} else {
			onLogout();
		}

		if (!op.isOnline()) {
			render(false);
		}
	}

	protected void clearSearchPanel() {
		this.searchPanel.onCancel();
	}

	private void onLogout() {
		if (this.signInButton == null) {
			this.signInButton = app.getLAF().getSignInButton(app);

			this.signInPanel.add(this.signInButton);
		}
		this.rightPanel.clear();
		this.rightPanel.add(this.signInPanel);
	}

	private void onLogin(boolean successful, GeoGebraTubeUser user) {
		if (!successful) {
			return;
		}
		if (this.profilePanel == null) {
			this.profilePanel = new ProfilePanel(app);
		}
		this.rightPanel.clear();
		profilePanel.update(user);

		this.rightPanel.add(this.profilePanel);
	}

	@Override
	public void onResize(int appWidth, int appHeight) {
		this.searchPanel.setWidth(getRemainingWidth(appWidth) + "px");
		if (rightPanelNeeded()) {
			this.add(this.rightPanel);
		} else if (app.has(Feature.MAT_DESIGN_HEADER)) {
			this.rightPanel.removeFromParent();
		}
	}

	/**
	 * 
	 * @return the remaining width for the searchPanel.
	 */
	private int getRemainingWidth(int appWidth) {
		if (!rightPanelNeeded()) {
			return appWidth - WIDTH_HEADER_FIRST;
		}
		int rightPanelWidth;
		if (this.rightPanel.getOffsetWidth() == 0) {
			if (this.signInButton != null && this.signInButton.isVisible()) {
				rightPanelWidth = 150;
			} else {
				rightPanelWidth = 60;
			}
		} else {
			rightPanelWidth = this.rightPanel.getOffsetWidth();
		}

		return Math.max(0, appWidth - WIDTH_HEADER_FIRST - rightPanelWidth);
	}

	@Override
	public void render(boolean b) {
		if (this.signInButton != null) {
			this.signInButton.setEnabled(b);
		}
	}

	@Override
	public void setLabels() {
		super.setLabels();
		if (this.signInButton != null) {
			this.signInButton.setText(loc.getMenu("SignIn"));
		}
		if (profilePanel != null) {
			profilePanel.setLabels();
		}
		if (this.searchPanel != null) {
			this.searchPanel.setLabels();
		}
		app.invokeLater(new Runnable() {
			@Override
			public void run() {
				onResize((int) app.getWidth(), (int) app.getHeight());
			}
		});
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if (event instanceof LoginEvent) {
			this.onLogin(((LoginEvent) event).isSuccessful(),
					((LoginEvent) event).getUser());
		}
		if (event instanceof LogOutEvent) {
			this.onLogout();
		}
		onResize((int) app.getWidth(), (int) app.getHeight());
	}
}
