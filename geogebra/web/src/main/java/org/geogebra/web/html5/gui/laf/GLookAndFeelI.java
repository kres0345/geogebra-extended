package org.geogebra.web.html5.gui.laf;

import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.euclidian.EuclidianControllerW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public interface GLookAndFeelI {
	public static final int COMMAND_LINE_HEIGHT = 43;
	public static final int TOOLBAR_HEIGHT = 53;

	boolean isSmart();

	boolean supportsGoogleDrive();

	boolean isTablet();

	String getType();

	Button getSignInButton(App app);

	boolean undoRedoSupported();

	MainMenuI getMenuBar(AppW app);

	void addWindowClosingHandler(AppW app);

	void removeWindowClosingHandler();

	boolean copyToClipboardSupported();

	Object getLoginListener();

	boolean registerHandlers(Widget evPanel,
	        EuclidianControllerW euclidiancontroller);

	boolean autosaveSupported();

	boolean exportSupported();

	boolean supportsLocalSave();

	boolean isEmbedded();

	boolean examSupported();

	boolean printSupported();

	public Versions getVersion(int dim, String appName);

	void storeLanguage(String language, AppW app);

	String getFrameStyleName();

	void toggleFullscreen(boolean b);

	boolean isGraphingExamSupported();

	boolean hasHeader();

	/**
	 * @return whether login/logout button should be inside the app
	 */
	boolean hasLoginButton();
}
