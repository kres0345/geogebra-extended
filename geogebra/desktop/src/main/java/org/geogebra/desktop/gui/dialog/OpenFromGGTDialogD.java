package org.geogebra.desktop.gui.dialog;

import java.awt.Dimension;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.move.OpenFromGGTOperation;
import org.w3c.dom.events.Event;

import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.web.WebEvent;

/**
 * A dialog for searching and opening materials from GeoGebraTube This dialog
 * will show a web view that opens a special version of the search dialog from
 * GeoGebraTube
 * 
 * @author stefan
 *
 */
public class OpenFromGGTDialogD extends WebViewDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * @param app
	 *            The app of type AppD
	 */
	public OpenFromGGTDialogD(AppD app) {
		super(app, true);
		app.initOpenFromGGTEventFlow();
		createGUI();
	}

	private void createGUI() {
		setTitle(app.getLocalization().getMenu("OpenFromGeoGebraTube"));
		setResizable(true);
		getContentPane().setPreferredSize(new Dimension(700, 700));

		String url = app.getOpenFromGGTOperation()
				.generateOpenFromGGTURL(OpenFromGGTOperation.APP_TYPE.DESKTOP);

		JFXPanel fxPanel1 = createWebView(url);
		add(fxPanel1);

		pack();
		setLocationRelativeTo(app.getFrame());
	}

	@Override
	protected void onPageLoaded() {
		super.onPageLoaded();

		/**
		 * Add an alert handler that listens to the alert call that passes the
		 * URL of the material to open to this webview
		 */
		getWebEngine().setOnAlert(new EventHandler<WebEvent<String>>() {
			@Override
			public void handle(WebEvent<String> event) {
				String url = event.getData();
				if (url != null && url.startsWith("EXT: ")) {
					app.showURLinBrowser(url.substring(5));
				} else if (url != null && url.startsWith("OPEN: ")) {
					openURL(url.substring(5));
				}
			}
		});

		// Listen to hyperlink clicks to open them in an external browser
		addHyperlinkListener();
	}

	/**
	 * Load the material from the given GGT URL and close the dialog
	 * 
	 * @param url
	 *            The URL of the material to load
	 */
	void openURL(final String url) {
		Log.debug("Opening material from URL: " + url);

		// Close the dialog
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				OpenFromGGTDialogD.this.dispatchEvent(new WindowEvent(
						OpenFromGGTDialogD.this, WindowEvent.WINDOW_CLOSING));

				// Load the URL
				app.getGuiManager().loadURL(url);
			}
		});
	}

	@Override
	void onHyperlinkClicked(String href, String absoluteURL, String domainName,
			Event ev) {
		Log.debug("Link clicked: " + href);

		if (!href.contains("/page/")) {

			ev.preventDefault();

			// Open each hyperlink click in an external browser
			app.showURLinBrowser(absoluteURL);
		}
	}
}
