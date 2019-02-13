package org.geogebra.web.full.gui.view.data;

import org.geogebra.common.gui.view.data.MultiVarStatTableModel;
import org.geogebra.common.gui.view.data.MultiVarStatTableModel.MultiVarStatTableListener;
import org.geogebra.web.html5.main.AppW;

/**
 * Extension of BasicStatTable that displays summary statistics for multiple
 * data sets.
 * 
 * @author G. Sturr
 * 
 */
public class MultiVarStatPanelW extends BasicStatTableW implements
		MultiVarStatTableListener {

	private boolean minimalTable = false;

	/***************************************************
	 * Constructs a MultiVarStatPanel
	 * 
	 * @param app
	 *            application
	 * @param statDialog
	 *            data analysis view
	 */
	public MultiVarStatPanelW(AppW app, DataAnalysisViewW statDialog) {
		super(app, statDialog, false);
		setModel(new MultiVarStatTableModel(app, this));
		setStyleName("daMultiVarStatistics");
	}

	/**
	 * @param isMinimalTable
	 *            whether this table is minimal (just length, mean, SD)
	 */
	public void setMinimalTable(boolean isMinimalTable) {
		this.minimalTable = isMinimalTable;
		initStatTable();
	}

	@Override
	public String[] getRowNames() {
		return getModel().getRowNames();
	}

	@Override
	public String[] getColumnNames() {
		String[] colNames = getModel().getColumnNames();
		String[] ext = new String[colNames.length + 1];
		ext[0] = "";
		System.arraycopy(colNames, 0, ext, 1, colNames.length);
		return ext;
	}

	@Override
	public int getRowCount() {
		return getModel().getRowCount() - 1;
	}

	@Override
	protected void initStatTable() {
		statTable = new StatTableW();
		statTable.setStatTable(getModel().getRowCount(), getModel().getRowNames(),
				getColumnCount() + 1, getColumnNames());
		clear();
		add(statTable);
	}
	
	@Override
	public int getColumnCount() {
		return getModel().getColumnCount();
	}

	@Override
	public void updatePanel() {
		getModel().updatePanel();
	}

	@Override
	public String[] getDataTitles() {
		return daView.getDataTitles();
	}

	@Override
	public boolean isMinimalTable() {
		return minimalTable;
	}

	@Override
	public void setValueAt(double value, int row, int column) {
		super.setValueAt(value, row, column + 1);
	}

}
