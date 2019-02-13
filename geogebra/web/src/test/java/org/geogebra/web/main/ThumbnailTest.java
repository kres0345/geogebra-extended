package org.geogebra.web.main;

import org.geogebra.common.main.App;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.TestArticleElement;
import org.geogebra.web.test.MockApp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class })
public class ThumbnailTest {

	@Test
	public void thumbnailShouldUseNonemptyView() {
		AppWFull app = MockApp
				.mockApplet(new TestArticleElement("prerelease", "classic"));

		thumbnailShouldUse(App.VIEW_EUCLIDIAN, app);

		app.getGgbApi().setPerspective("GT");
		thumbnailShouldUse(App.VIEW_EUCLIDIAN3D, app);

		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand("SetActiveView(1)", true);
		app.getKernel().getAlgebraProcessor().processAlgebraCommand("\"Text\"",
				true);
		thumbnailShouldUse(App.VIEW_EUCLIDIAN, app);
	}

	private static void thumbnailShouldUse(int viewId, AppW app) {
		Assert.assertEquals(viewId,
				app.getGgbApi().getViewForThumbnail().getViewID());
	}
	
	@Before
	public void rootPanel() {
		this.getClass().getClassLoader().setDefaultAssertionStatus(false);
	}

}
