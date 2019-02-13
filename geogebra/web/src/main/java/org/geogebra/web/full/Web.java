package org.geogebra.web.full;

import java.util.ArrayList;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.SilentProfiler;
import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.full.gui.laf.BundleLookAndFeel;
import org.geogebra.web.full.gui.laf.ChromeLookAndFeel;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.gui.laf.MebisLookAndFeel;
import org.geogebra.web.full.gui.laf.OfficeLookAndFeel;
import org.geogebra.web.full.gui.laf.SmartLookAndFeel;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.WebSimple;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author apa
 *
 */
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Web implements EntryPoint {

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
		// use GeoGebraProfilerW if you want to profile, SilentProfiler for
		// production
		// GeoGebraProfiler.init(new GeoGebraProfilerW());
		GeoGebraProfiler.init(new SilentProfiler());

		GeoGebraProfiler.getInstance().profile();

		WebSimple.registerSuperdevExceptionHandler();
		exportGGBElementRenderer();

		// setLocaleToQueryParam();

		loadAppletAsync();
		allowRerun();
		// just debug for now
	}

	// TODO: what about global preview events?
	// these are an issue even if we register them elsewhere
	// maybe do not register them again in case of rerun?
	// this could be done easily now with a boolean parameter
	private native void allowRerun() /*-{
		$wnd.ggbRerun = function() {
			@org.geogebra.web.full.Web::loadAppletAsync()();
		}
	}-*/;

	/**
	 * Load UI of all applets.
	 */
	public static void loadAppletAsync() {
		startGeoGebra(ArticleElement.getGeoGebraMobileTags());
	}

	private native void exportGGBElementRenderer() /*-{
		$wnd.renderGGBElement = $entry(@org.geogebra.web.full.Web::renderArticleElement(Lcom/google/gwt/dom/client/Element;Lcom/google/gwt/core/client/JavaScriptObject;))
		@org.geogebra.web.html5.gui.GeoGebraFrameW::renderGGBElementReady()();
		//CRITICAL: "window" below is OK, we need to redirect messages from window to $wnd
		window.addEventListener("message",function(event){$wnd.postMessage(event.data,"*");});
	}-*/;

	/**
	 * @param el
	 *            article element
	 * @param clb
	 *            callback
	 */
	public static void renderArticleElement(Element el, JavaScriptObject clb) {
		GeoGebraFrameBoth.renderArticleElement(el,
				(AppletFactory) GWT.create(AppletFactory.class),
				getLAF(ArticleElement.getGeoGebraMobileTags()), clb);
	}

	/**
	 * @param geoGebraMobileTags
	 *            article elements
	 */
	static void startGeoGebra(ArrayList<ArticleElement> geoGebraMobileTags) {
		GeoGebraFrameBoth.main(geoGebraMobileTags,
				(AppletFactory) GWT.create(AppletFactory.class),
				getLAF(geoGebraMobileTags), null);
	}

	/**
	 * @param geoGebraMobileTags
	 *            article elements
	 * @return look and feel based the first article that has laf parameter
	 */
	public static GLookAndFeel getLAF(
			ArrayList<ArticleElement> geoGebraMobileTags) {
		NodeList<Element> nodes = Dom
				.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		for (int i = 0; i < nodes.getLength(); i++) {
			String laf = ((ArticleElement) nodes.getItem(i))
					.getAttribute("data-param-laf");
			if ("smart".equals(laf)) {
				return new SmartLookAndFeel();
			}

			if ("office".equals(laf)) {
				return new OfficeLookAndFeel();
			}

			if ("bundle".equals(laf)) {
				return new BundleLookAndFeel();
			}
			if ("mebis".equals(laf)) {
				return new MebisLookAndFeel();
			}

			if ("chrome".equals(laf)) {
				return new ChromeLookAndFeel();
			}
		}
		if (!((CASFactory) GWT.create(CASFactory.class)).isEnabled()) {
			return new GLookAndFeel() {
				@Override
				public Versions getVersion(int dim, String appName) {
					return Versions.NO_CAS;
				}
			};
		}

		return new GLookAndFeel();
	}

}
