package org.geogebra.web.full.euclidian;

import org.geogebra.web.full.main.AppWFull;
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
public class StyleBarTest {

	@Test
	public void updateGraphingStylebar() {
		AppWFull app = MockApp
				.mockApplet(new TestArticleElement("prerelease", "graphing"));
		EuclidianStyleBarW styleBar = new EuclidianStyleBarW(
				app.getActiveEuclidianView(), 1);
		checkUpdate(styleBar);
	}

	private static void checkUpdate(EuclidianStyleBarW styleBar) {
		try {
			styleBar.setOpen(true);
			styleBar.updateStyleBar();
			styleBar.updateButtons();
		} catch (RuntimeException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void updateWhiteboardStylebar() {
		AppWFull app = MockApp
				.mockApplet(new TestArticleElement("prerelease", "notes"));
		EuclidianStyleBarW styleBar = new EuclidianStyleBarW(
				app.getActiveEuclidianView(), 1);
		checkUpdate(styleBar);
	}

	@Before
	public void rootPanel() {
		this.getClass().getClassLoader().setDefaultAssertionStatus(false);
	}

}
