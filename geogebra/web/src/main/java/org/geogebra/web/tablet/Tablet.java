package org.geogebra.web.tablet;

import java.util.ArrayList;

import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.SilentProfiler;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.full.gui.browser.BrowseGUI;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.WebSimple;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.tablet.main.TabletDevice;
import org.geogebra.web.touch.PhoneGapManager;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.gwtphonegap.client.event.BackButtonPressedEvent;
import com.googlecode.gwtphonegap.client.event.BackButtonPressedHandler;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Tablet implements EntryPoint {

	// zum testen von private zu public
	private static GeoGebraFrameBoth appFrame;

	/**
	 * set true if Google Api Js loaded
	 */

	@Override
	public void onModuleLoad() {
		if (RootPanel.getBodyElement().getAttribute("data-param-laf") != null
		        && !"".equals(RootPanel.getBodyElement().getAttribute(
		                "data-param-laf"))) {
			// loading touch, ignore.
			return;
		}
		Browser.checkFloat64();
		if (useCordova()) {
			PhoneGapManager.initializePhoneGap(new BackButtonPressedHandler() {
	
				@Override
				public void onBackButtonPressed(final BackButtonPressedEvent event) {
					goBack();
				}
			});
		}
		// use GeoGebraProfilerW if you want to profile, SilentProfiler for
		// production
		// GeoGebraProfiler.init(new GeoGebraProfilerW());
		GeoGebraProfiler.init(new SilentProfiler());

		GeoGebraProfiler.getInstance().profile();

		exportGGBElementRenderer();

		loadAppletAsync();

		// phoneGap.initializePhoneGap();
		WebSimple.registerSuperdevExceptionHandler();
	}

	/**
	 * (Android) back button handler
	 */
	public static void goBack() {
		if (appFrame != null && appFrame.getApplication() != null) {
			if (appFrame.isHeaderPanelOpen()) {
				GuiManagerW guiManager = (GuiManagerW) appFrame.getApplication()
				        .getGuiManager();
				appFrame.hideBrowser((BrowseGUI) guiManager.getBrowseView());
			}
		}
	}

	/**
	 * Load in applet mode
	 */
	public static void loadAppletAsync() {
		startGeoGebra(ArticleElement.getGeoGebraMobileTags());
	}

	private native void exportGGBElementRenderer() /*-{
   		$wnd.renderGGBElement = $entry(@org.geogebra.web.tablet.Tablet::renderArticleElement(Lcom/google/gwt/dom/client/Element;Lcom/google/gwt/core/client/JavaScriptObject;))
   		@org.geogebra.web.html5.gui.GeoGebraFrameW::renderGGBElementReady()();
   	}-*/;

	/**
	 * @param el
	 *            article element
	 * @param clb
	 *            rendering finished callback
	 */
	public static void renderArticleElement(final Element el,
	        JavaScriptObject clb) {
		GeoGebraFrameBoth.renderArticleElement(el,
		        (AppletFactory) GWT.create(AppletFactory.class),
		        (GLookAndFeel) GWT.create(TabletLookAndFeel.class), clb);
	}

	/**
	 * @param geoGebraMobileTags
	 *            article elements
	 */
	static void startGeoGebra(final ArrayList<ArticleElement> geoGebraMobileTags) {
		GeoGebraFrameBoth.main(geoGebraMobileTags,
		        (AppletFactory) GWT.create(AppletFactory.class),
				(GLookAndFeel) GWT.create(TabletLookAndFeel.class),
				(GDevice) GWT.create(TabletDevice.class));
	}

	/**
	 * @return whether cordova is needed, based on global war
	 */
	public static native boolean useCordova() /*-{
		if ($wnd.android) {
			if ($wnd.android.noCordova) {
				return false;
			}
		}
		return true;
	}-*/;

}
