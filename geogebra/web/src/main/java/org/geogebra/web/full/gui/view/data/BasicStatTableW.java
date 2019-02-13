package org.geogebra.web.full.gui.view.data;

import java.util.ArrayList;

import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.DataAnalysisModel.Regression;
import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.gui.view.data.StatTableModel;
import org.geogebra.common.gui.view.data.StatTableModel.Stat;
import org.geogebra.common.gui.view.data.StatTableModel.StatTableListener;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Displays statistics for DataAnalysisView when in one variable or regression
 * mode.
 * 
 * @author G. Sturr
 * 
 */
public class BasicStatTableW extends FlowPanel implements StatPanelInterfaceW,
		StatTableListener {
	private StatTableModel model;

	protected DataAnalysisViewW daView;
	protected StatTableW statTable;

	public BasicStatTableW(AppW app, DataAnalysisViewW statDialog) {
		this(app, statDialog, true);
	} // END constructor

	/**
	 * @param app
	 *            application
	 * @param statDialog
	 *            stats dialog
	 * @param defaultModel
	 *            model
	 */
	public BasicStatTableW(AppW app, DataAnalysisViewW statDialog,
			boolean defaultModel) {
		this.daView = statDialog;
		setStyleName("daStatistics");
		
		if (defaultModel) {
			setModel(new StatTableModel(app, this));
		}
	}

	/**
	 * @param model
	 *            stat model
	 */
	public void setModel(StatTableModel model) {
		this.model = model;
		initStatTable();
	}

	protected void initStatTable() {

		statTable = new StatTableW();
		statTable.setStatTable(getModel().getRowCount(), getModel().getRowNames(),
				getColumnCount(), getModel().getColumnNames());
		clear();
		add(statTable);
	}

	public String[] getRowNames() {
		return getModel().getRowNames();
	}

	public String[] getColumnNames() {
		return getModel().getColumnNames();
	}

	public int getRowCount() {
		return getModel().getRowCount() - 1;
	}

	public int getColumnCount() {
		return 2;
	}

	// =======================================================

	/**
	 * Evaluates all statistics for the selected data list. If data source is
	 * not valid, the result cells are set blank.
	 * 
	 */
	@Override
	public void updatePanel() {
		// App.printStacktrace("update stat panel");
		GeoList dataList = getDataSelected();

		GeoElement geoRegression = getRegressionModel();
		// when the regression mode is NONE geoRegression is a dummy linear
		// model, so reset it to null
		if (getRegressionMode().equals(Regression.NONE)) {
			geoRegression = null;
		}

		double value;
		statTable.setStatTable(getModel().getRowCount(), getModel()
				.getRowNames(), getColumnCount(), getModel()
				.getColumnNames());
		ArrayList<Stat> list = getModel().getStatList();

		for (int row = 0; row < list.size(); row++) {
			for (int column = 1; column < getColumnCount(); column++) {
				Stat stat = list.get(row);
				if (isValidData() && stat != Stat.NULL) {
					AlgoElement algo = getAlgo(stat, dataList, geoRegression);
					if (algo != null) {
						getModel().getConstruction().removeFromConstructionList(algo);
						value = ((GeoNumeric) algo.getGeoElements()[0])
								.getDouble();
						setValueAt(value, row,	1);
					}
				}
			}
		}
	}

	protected AlgoElement getAlgo(Stat algoName, GeoList dataList,
			GeoElement geoRegression) {
		return getModel().getAlgo(algoName, dataList, geoRegression);
	}

	protected AlgoElement getAlgoRawData(Stat stat, GeoList dataList,
			GeoElement geoRegression) {
		return getModel().getAlgoRawData(stat, dataList, geoRegression);

	}

	protected AlgoElement getAlgoFrequency(Stat stat, GeoList frequencyData) {
		return getModel().getAlgoFrequency(stat, frequencyData);
	}

	protected AlgoElement getAlgoClass(StatTableModel.Stat stat,
			GeoList frequencyData, GeoElement geoRegression) {
		return getModel().getAlgoClass(stat, frequencyData, geoRegression);
	}

	@Override
	public void setLabels() {
		statTable.setLabels(getModel().getRowNames(), getModel().getColumnNames(), false);
	}

	@Override
	public GeoList getDataSelected() {
		return daView.getController().getDataSelected();
	}

	@Override
	public GeoElement getRegressionModel() {
		return daView.getRegressionModel();
	}

	@Override
	public DataAnalysisModel.Regression getRegressionMode() {
		return daView.getModel().getRegressionMode();
	}

	@Override
	public boolean isValidData() {
		return daView.getController().isValidData();
	}

	@Override
	public void setValueAt(double value, int row, int col) {
		statTable.setValueAt(daView.getModel().format(value), row,
				col);
	}

	@Override
	public boolean isViewValid() {
		return daView == null || daView.getDataSource() == null;
	}

	@Override
	public int getMode() {
		return daView.getModel().getMode();
	}

	@Override
	public GroupType groupType() {
		return daView.groupType();
	}

	@Override
	public boolean isNumericData() {
		return daView.getDataSource().isNumericData();
	}

	public StatTableModel getModel() {
	    return model;
    }

}
