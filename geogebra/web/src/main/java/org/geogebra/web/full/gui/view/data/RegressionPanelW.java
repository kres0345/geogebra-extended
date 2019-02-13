package org.geogebra.web.full.gui.view.data;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.DataAnalysisModel.Regression;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.html5.main.LocalizationW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Panel to select and display the DataAnalysisView regression model.
 * 
 * @author G. Sturr, Laszlo Gal
 */
public class RegressionPanelW extends FlowPanel implements StatPanelInterfaceW {

	private AppW app;
	private final LocalizationW loc;
	private DataAnalysisViewW statDialog;

	// regression panel objects
	private Label lblEqn;

	private ListBox lbRegression;
	private ListBox lbPolyOrder;
	private Label lblEvaluate;
	private AutoCompleteTextFieldW fldInputX;

	private String[] regressionLabels;
	private Label fldOutputY;
	private boolean isIniting = true;
	private FlowPanel predictionPanel;
	private Canvas latexCanvas;
	private GeoNumeric sample;

	private DataAnalysisModel daModel;

	private FlowPanel regressionPanel;
	private Label regressionTitle;

	/**
	 * Construct a regression panel
	 * 
	 * @param app
	 *            application
	 * @param statDialog
	 *            invoking instance of DataAnalysisView
	 */
	public RegressionPanelW(AppW app, DataAnalysisViewW statDialog) {

		this.app = app;
		this.loc = app.getLocalization();
		this.statDialog = statDialog;
		this.daModel = statDialog.getModel();
		setStyleName("daRegressionPanel");
		sample = new GeoNumeric(app.getKernel().getConstruction());
		sample.setObjColor(GColor.RED);
		createRegressionPanel();
		setLabels();
		updateRegressionPanel();
		updateGUI();
		isIniting = false;
	}

	private void createRegressionPanel() {
		// components
		String[] orders = { "2", "3", "4", "5", "6", "7", "8", "9" };
		lbPolyOrder = new ListBox();
		for (String item: orders) {
			lbPolyOrder.addItem(item);
		}
		
		lbPolyOrder.setSelectedIndex(0);
		lbPolyOrder.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				actionPerformed(lbPolyOrder);
			}
		});

		regressionLabels = new String[Regression.values().length];
		setRegressionLabels(app.getLocalization());
		lbRegression = new ListBox();
		for (String item: regressionLabels) {
			lbRegression.addItem(item);
		}
		
		lbRegression.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				actionPerformed(lbRegression);
			}
		});

		lblEqn = new Label();

		// regression combo panel
		FlowPanel lbPanel = new FlowPanel();
		lbPanel.add(lbRegression);
		lbPanel.add(lbPolyOrder);

		// regression label panel
		FlowPanel eqnPanel = new FlowPanel();

		latexCanvas = Canvas.createIfSupported();
		latexCanvas.setStyleName("daRegEquation");
		eqnPanel.add(latexCanvas);
		ScrollPanel scroller = new ScrollPanel();
		scroller.addStyleName("daEquationScrollPane");
		scroller.add(eqnPanel);

		// prediction panel
		createPredictionPanel();

		// model panel: equation + prediction
		FlowPanel modelPanel = new FlowPanel();
		modelPanel.add(scroller);
		modelPanel.add(predictionPanel);

		regressionTitle = new Label(loc.getMenu("RegressionModel"));
		regressionTitle.setStyleName("panelTitle");
		// put it all together
		regressionPanel = new FlowPanel();
		regressionPanel.add(regressionTitle);
		regressionPanel.add(LayoutUtilW.panelRow(lbPanel, modelPanel));
		
		add(regressionPanel);
	}

	/**
	 * Creates a panel to evaluate the regression model for a given x value
	 */
	private void createPredictionPanel() {

		lblEvaluate = new Label();
		fldInputX = new AutoCompleteTextFieldW(6, app);
		
		fldInputX.addKeyHandler(new KeyHandler() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					doTextFieldActionPerformed(fldInputX);
				}
			}
		});
		
		fldInputX.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				doTextFieldActionPerformed(fldInputX);
			}
		});

		Label lblOutputY = new Label();
		fldOutputY = new Label();

		predictionPanel = new FlowPanel();
		
		predictionPanel.add(LayoutUtilW.panelRow(lblEvaluate, new Label("x = "), fldInputX, 
				new Label("y = "), lblOutputY, fldOutputY));
	}

	/**
	 * Updates the regression equation label and the prediction panel
	 */
	public void updateRegressionPanel() {

		if (statDialog.getController().isValidData()) {
			setRegressionEquationLabel();
			doTextFieldActionPerformed(fldInputX);
		} else {
			setRegressionEquationLabelEmpty();
		}
		updateGUI();

	}

	/**
	 * Clears the X and Y fields of the prediction panel
	 */
	public void clearPredictionPanel() {
		fldInputX.setText("");
		fldOutputY.setText("");
	}

	private void setRegressionLabels(Localization loc) {

		for (Regression r : Regression.values()) {

			regressionLabels[r.ordinal()] = loc.getMenu(r.getLabel());
		}

	}

	/**
	 * Sets the labels according to current locale
	 */
	@Override
	public void setLabels() {
		regressionLabels = new String[Regression.values().length];
		setRegressionLabels(loc);

		// we need to remove old labels from combobox and we don't want the
		// listener to
		// be operational since it will call unnecessary Construction updates
		int j = lbRegression.getSelectedIndex();
		lbRegression.clear();

		for (int i = 0; i < regressionLabels.length; i++) {
			lbRegression.addItem(regressionLabels[i]);
		}

		lbRegression.setSelectedIndex(j);
		regressionTitle.setText(loc
				.getMenu("RegressionModel"));
		lblEqn.setText(loc.getMenu("Equation") + ":");

		lblEvaluate.setText(loc.getMenu("Evaluate") + ": ");

	}

	/**
	 * Draws the regression equation into the regression equation JLabel icon
	 */
	public void setRegressionEquationLabel() {

		// get the LaTeX string for the regression equation

		String eqn;
		// GeoElement geoRegression = statDialog.getRegressionModel();

		try {
			// prepare number format
			StringTemplate highPrecision;
			if (daModel.getPrintDecimals() >= 0) {
				highPrecision = StringTemplate.printDecimals(StringType.LATEX,
						daModel.getPrintDecimals(), false);
			} else {
				highPrecision = StringTemplate.printFigures(StringType.LATEX,
						daModel.getPrintFigures(), false);
			}

			// no regression
			if (daModel.getRegressionMode().equals(Regression.NONE)
					|| statDialog.getRegressionModel() == null) {
				eqn = "";
			}

			else {
				eqn = "y = "
						+ statDialog.getRegressionModel().getFormulaString(
								highPrecision, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			eqn = "\\text{" + loc.getMenu("NotAvailable") + "}";
		}

		DrawEquationW.paintOnCanvas(sample, eqn, latexCanvas,
				app.getFontSizeWeb());
		
		updateGUI();
	}

	/**
	 * Set the regression equation label to an empty string
	 */
	private void setRegressionEquationLabelEmpty() {
		updateGUI();
	}

	private void updateGUI() {
		lbPolyOrder.setVisible(daModel.getRegressionMode().equals(
				Regression.POLY));
		predictionPanel.setVisible(!(daModel.getRegressionMode()
				.equals(Regression.NONE)));

	}

	/**
	 * Handle input event
	 * 
	 * @param source
	 *            input field
	 */
	public void actionPerformed(Object source) {
		if (source instanceof AutoCompleteTextFieldW) {
			doTextFieldActionPerformed((AutoCompleteTextFieldW) source);
		}

		else if (source == lbRegression) {
			daModel.setRegressionMode(lbRegression.getSelectedIndex());
			updateRegressionPanel();
		}

		else if (source == lbPolyOrder) {
			daModel.setRegressionOrder(lbPolyOrder.getSelectedIndex() + 2);
			statDialog.getController().setRegressionGeo();
			statDialog.getController().updateRegressionPanel();
			setRegressionEquationLabel();

			// force update
			daModel.setRegressionMode(Regression.POLY.ordinal());
		}

	}

	private void doTextFieldActionPerformed(AutoCompleteTextFieldW source) {
		if (isIniting) {
			return;
		}

		if (source == fldInputX) {
			try {
				String inputText = source.getText().trim();
				if (inputText == null || inputText.length() == 0) {
					return;
				}

				NumberValue nv;
				nv = app.getKernel().getAlgebraProcessor()
						.evaluateToNumeric(inputText, ErrorHelper.silent());
				double value = nv.getDouble();
				double output = ((GeoFunctionable) statDialog
						.getRegressionModel()).getGeoFunction().value(value);

				fldOutputY.setText(statDialog.format(output));

			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void updatePanel() {
		// TODO Auto-generated method stub

	}

	public int getRegressionIdx() {
	    return lbRegression.getSelectedIndex();
    }

	public void setRegressionIdx(int idx) {
	    lbRegression.setSelectedIndex(idx);
    }

}
