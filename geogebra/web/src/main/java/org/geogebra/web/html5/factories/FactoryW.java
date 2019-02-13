package org.geogebra.web.html5.factories;

import org.geogebra.common.factories.Factory;
import org.geogebra.common.javax.swing.RelationPane;
import org.geogebra.web.html5.javax.swing.RelationPaneW;
import org.geogebra.web.html5.main.AppW;

public class FactoryW extends Factory {

	private AppW app;

	public FactoryW(AppW appW) {
		this.app = appW;
	}

	@Override
	public RelationPane newRelationPane() {
		return new RelationPaneW(false, app.getPanel(), app);
	}

}
