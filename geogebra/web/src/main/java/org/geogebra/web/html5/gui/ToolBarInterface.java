package org.geogebra.web.html5.gui;

import org.geogebra.common.kernel.ModeSetter;

public interface ToolBarInterface {

	public int setMode(int mode, ModeSetter m);

	public void setVisible(boolean show);

	void closeAllSubmenu();

	public boolean isMobileToolbar();

	public boolean isShown();

}
