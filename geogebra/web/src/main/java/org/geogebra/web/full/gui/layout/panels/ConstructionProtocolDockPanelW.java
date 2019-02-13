package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.util.StyleBarW;
import org.geogebra.web.full.gui.view.consprotocol.ConstructionProtocolViewW;
import org.geogebra.web.full.gui.view.consprotocol.ConstructionProtocolViewW.MyPanel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class ConstructionProtocolDockPanelW extends NavigableDockPanelW {

	private StyleBarW cpStyleBar;

	/**
	 * @param app
	 *            application
	 */
	public ConstructionProtocolDockPanelW(AppW app) {
		super(
			App.VIEW_CONSTRUCTION_PROTOCOL, 	// view id
			"ConstructionProtocol", 					// view title phrase 
			null,	// toolbar string
				false, // style bar?
			7,						// menu order
			'L' // ctrl-shift-L
		);
		this.app = app;
		this.setShowStyleBar(true);
		this.setEmbeddedSize(300);
	}

	@Override
	protected Widget loadStyleBar() {
		if (cpStyleBar == null) {
			cpStyleBar = ((ConstructionProtocolViewW) app.getGuiManager()
					.getConstructionProtocolView()).getStyleBar();
		}
		return cpStyleBar; 
	}
	
	@Override
    public ResourcePrototype getIcon() {
		return getResources().menu_icon_construction_protocol();
	}

	@Override
	protected Panel getViewPanel() {
		return ((ConstructionProtocolViewW) app.getGuiManager()
				.getConstructionProtocolView()).getOuterScrollPanel();
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return getResources().styleBar_ConstructionProtocol();
	}

	@Override
	public void resizeContent(Panel content) {
		super.resizeContent(content);
		if (content instanceof MyPanel) {
			((MyPanel) content).onResize();
		}
	}
}
