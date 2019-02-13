package org.geogebra.web.full.gui.util;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.HasKeyboardPopup;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.himamis.retex.editor.share.util.GWTKeycodes;

public class ButtonPopupMenu extends GPopupPanel implements HasKeyboardPopup {
	
	FocusPanel container = null;
	VerticalPanel panel = null;
	
	/**
	 * @param root
	 *            root for popup
	 * @param app
	 *            application
	 */
	public ButtonPopupMenu(Panel root, App app) {
		super(root, app);
		container = new FocusPanel();
		panel = new VerticalPanel();
		container.add(panel);
		container.addStyleName("ButtonPopupMenu");
		container.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == GWTKeycodes.KEY_ESCAPE) {
					hide();
				}
			}
		});
		add(container);
	}
	
	public VerticalPanel getPanel() {
		return panel;
	}

	public FocusPanel getFocusPanel() {
	   return container;
    }

}
