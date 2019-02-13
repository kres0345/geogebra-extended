package org.geogebra.web.geogebra3D;

import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.geogebra3D.web.main.AppWapplet3D;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.util.ArticleElementInterface;

/**
 * 3D Applets factory class
 *
 */
public class AppletFactory3D implements AppletFactory {

	@Override
	public AppWFull getApplet(ArticleElementInterface ae, GeoGebraFrameBoth fr,
			GLookAndFeelI laf, GDevice device) {
		return new AppWapplet3D(ae, fr, (GLookAndFeel) laf, device);
	}
}
