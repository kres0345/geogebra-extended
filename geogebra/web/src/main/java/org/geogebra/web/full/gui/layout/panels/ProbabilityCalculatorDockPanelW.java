package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.view.probcalculator.ProbabilityCalculatorViewW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author gabor
 * 
 *         ProabilityCalculator dockpanel for Web
 *
 */
public class ProbabilityCalculatorDockPanelW extends DockPanelW {

	/**
	 * default width of this panel
	 */
	public static final int DEFAULT_WIDTH = 480;

	/**
	 * @param app
	 *            App Creates panel
	 */
	public ProbabilityCalculatorDockPanelW(AppW app) {
		super(App.VIEW_PROBABILITY_CALCULATOR, // view id
				"ProbabilityCalculator", // view title phrase
				"0", // toolbar string - move tool only, force!
				true, // style bar?
				-1, // menu order
				'P' // menu shortcut
		);

		this.app = app;
		// this.setOpenInFrame(true);
		this.setEmbeddedSize(DEFAULT_WIDTH);

		// this.app = app;
	}

	@Override
	public void onResize() {
		super.onResize();
		if (app.getGuiManager().hasProbabilityCalculator()) {
			((ProbabilityCalculatorViewW) app.getGuiManager()
					.getProbabilityCalculator()).onResize();
		}
	}

	@Override
	protected Widget loadComponent() {
		return ((ProbabilityCalculatorViewW) app.getGuiManager()
				.getProbabilityCalculator()).getWrapperPanel();
	}

	@Override
	public void setAlone(boolean isAlone) {
		setCloseButtonVisible(!isAlone);
		super.setAlone(isAlone);
	}

	@Override
	protected Widget loadStyleBar() {
		return ((ProbabilityCalculatorViewW) app.getGuiManager()
				.getProbabilityCalculator()).getStyleBar().getWrappedToolBar();
	}

	@Override
	public boolean isStyleBarEmpty() {
		return true;
	}

	@Override
	public ResourcePrototype getIcon() {
		return getResources().menu_icon_probability();
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return null;
	}

}
