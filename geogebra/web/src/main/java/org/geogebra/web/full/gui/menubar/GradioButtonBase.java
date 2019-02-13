package org.geogebra.web.full.gui.menubar;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * @author gabor
 * radiobutton base for the RadioButton menu
 */
public class GradioButtonBase {

	/**
	 * radiobutton for the radiobutton menu
	 */
	RadioButton radio;
	/**
	 * panel to hold the button and the menu's html
	 */
	FlowPanel itemPanel;
	private String command;

	/**
	 * @param html
	 *            Html of the menu
	 * @param cmd
	 *            command associated with this item
	 * @param groupName
	 *            group name
	 */
	public GradioButtonBase(String html, String cmd, String groupName) {
		radio = new RadioButton(groupName);
		command = cmd;
		itemPanel = new FlowPanel();
		itemPanel.add(radio);
		itemPanel.add(new HTML(html));
    }

	/**
	 * @return gets the safe html of the menuitem
	 */
	public SafeHtml getSafeHtml() {
		return new SafeHtml() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String asString() {
				return itemPanel.getElement().getInnerHTML();
			}
		};
	}

	/**
	 * @return command associated with this radio item
	 */
	public String getActionCommand() {
		return command;
	}
}
