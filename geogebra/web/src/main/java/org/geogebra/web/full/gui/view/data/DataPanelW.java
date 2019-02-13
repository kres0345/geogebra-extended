package org.geogebra.web.full.gui.view.data;

import java.util.ArrayList;

import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/** Data panel */
public class DataPanelW extends FlowPanel implements StatPanelInterfaceW,
		RequiresResize {
	private DataAnalysisViewW daView;
	private DataAnalysisControllerW statController;

	private CheckBox cbEnableAll;
	private Boolean[] selectionList;

	private Label lblHeader;

	private LocalizationW loc;

//	private static final GColor TABLE_HEADER_COLOR = GeoGebraColorConstants.TABLE_HEADER_COLOR;
	private StatTableW dataTable;

	private ScrollPanel scrollPane;
	
	private class DataClickHandler implements ClickHandler {
		private int index;

		public DataClickHandler(int index) {
			this.index = index;
		}
		
		@Override
		public void onClick(ClickEvent event) {
	        onDataClick(index);
        }
	}
	
	/*************************************************
	 * Construct a DataPanel
	 * 
	 * @param app
	 *            application
	 * @param statDialog
	 *            data analysis view
	 */
	public DataPanelW(AppW app, DataAnalysisViewW statDialog) {
		this.loc = app.getLocalization();
		this.daView = statDialog;
		this.statController = statDialog.getController();

		buildDataTable();
	
		cbEnableAll = new CheckBox("");
		cbEnableAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				enableAll();
			}
		});
		
		populateDataTable(statController.getDataArray());
		createGUI();
		enableAll();
		setStyleName("daData");
	}

	private void buildDataTable() {
		dataTable = new StatTableW();
	}

	private void createGUI() {
		lblHeader = new Label();
		add(lblHeader);

		scrollPane = new ScrollPanel();
		scrollPane.add(dataTable);
		add(scrollPane);
		setLabels();
	}

	@Override
	public void setLabels() {
		lblHeader.setText(loc.getMenu("Data"));
	}

	@Override
	public void updatePanel() {
//		setRowHeight();
	}
//
//	public void updateFonts(Font font) {
//		// TODO Auto-generated method stub
//
//	}

	private Boolean[] updateSelectionList(ArrayList<GeoElement> dataArray) {

		selectionList = new Boolean[dataArray.size()];
		for (int i = 0; i < selectionList.length; ++i) {
			selectionList[i] = true;
		}

		return selectionList;
	}

	private void populateOneVarDataTable(ArrayList<GeoElement> dataArray) {
		String[] titles = daView.getDataTitles();
		int maxRows = dataArray.size() + 1;
		String[] rowNames = new String[maxRows];
		dataTable.setStatTable(maxRows, rowNames, 2, null);
		for (int row = 0; row < maxRows - 1; row++) {
			CheckBox cb = new CheckBox("" + (row + 1));
			cb.addClickHandler(new DataClickHandler(row));
			cb.setValue(true);
			dataTable.getTable().setWidget(row + 1, 0, cb); 

			dataTable.setValueAt(
					dataArray.get(row).toDefinedValueString(
							StringTemplate.defaultTemplate), row + 1, 1);
		}

		dataTable.getTable().setWidget(0, 0, cbEnableAll);
		dataTable.setValueAt(titles[0], 0, 1);

		updateSelectionList(dataArray);

	}
	
	private void populateRegressionDataTable(ArrayList<GeoElement> dataArray) {
		// a data source may be a list of points with a single title
		// so we must create a title for the y column
		setStyleName("daRegressionData");
		String[] titles = daView.getDataTitles();
		String[] rowNames = new String[dataArray.size()];
		String titleX = titles[0];
		String titleY = titles.length == 1 ? titleX : titles[1];

		int maxRows = dataArray.size() + 1;
		dataTable.setStatTable(maxRows, rowNames, 3, null);

		for (int row = 0; row < maxRows - 1; ++row) {
			CheckBox cb = new CheckBox("" + (row + 1));
			cb.addClickHandler(new DataClickHandler(row));
			cb.setValue(true);
			dataTable.getTable().setWidget(row + 1, 0, cb); 
			GeoPoint pt = (GeoPoint) dataArray.get(row);
			dataTable.setValueAt(pt.getInhomX() + "", row + 1, 1);
			dataTable.setValueAt(pt.getInhomY() + "", row + 1, 2);
		}

		dataTable.getTable().setWidget(0, 0, cbEnableAll);

		// handle x,y titles
		if (daView.getDataSource().isPointData()) {

			dataTable.setValueAt(loc.getMenu("Column.X"), 0, 1);
			dataTable.setValueAt(loc.getMenu("Column.Y"), 0, 2);
		} else {
			dataTable.setValueAt(loc.getMenu("Column.X") + ": " + titleX, 0, 1);
			dataTable.setValueAt(loc.getMenu("Column.Y") + ": " + titleY, 0, 2);
		}

		updateSelectionList(dataArray);
	}
	
	private void populateDataTable(ArrayList<GeoElement> dataArray) {

		if (dataArray == null || dataArray.size() < 1) {
			return;
		}

		int mode = daView.getModel().getMode();
	
		if (mode == DataAnalysisModel.MODE_ONEVAR) {
			populateOneVarDataTable(dataArray);
		}
		else if (mode == DataAnalysisModel.MODE_REGRESSION) {
			populateRegressionDataTable(dataArray);

		}
	}
//
//	/**
//	 * Loads the data table. Called on data set changes.
//	 */
//	public void loadDataTable(ArrayList<GeoElement> dataArray) {
//
//		// load the data model
//		populateDataTable(dataArray);
//
//		// prepare boolean selection list for the checkboxes
//		selectionList = new Boolean[dataArray.size()];
//		for (int i = 0; i < dataArray.size(); ++i) {
//			selectionList[i] = true;
//		}
//
//		// create a new header
//		rowHeader = new MyRowHeader(this, dataTable);
//		scrollPane.setRowHeaderView(rowHeader);
//		updateFonts(getFont());
//
//		// repaint
//		dataTable.repaint();
//		rowHeader.repaint();
//
//	}
//
//	public void ensureTableFill() {
//		Container p = getParent();
//		DefaultTableModel dataModel = (DefaultTableModel) dataTable.getModel();
//		if (dataTable.getHeight() < p.getHeight()) {
//			int newRows = (p.getHeight() - dataTable.getHeight())
//					/ dataTable.getRowHeight();
//			dataModel.setRowCount(dataTable.getRowCount() + newRows);
//			for (int i = 0; i <= dataTable.getRowCount(); ++i) {
//				if (rowHeader.getModel().getElementAt(i) != null)
//					((DefaultListModel) rowHeader.getModel()).add(i, true);
//			}
//		}
//
//	}

//	public void actionPerformed(ActionEvent e) {
//		if (e.getSource() == btnEnableAll) {
//			rowHeader.enableAll();
//			btnEnableAll.setEnabled(false);
//
//		}
//	}
//}
	
	/**
	 * @param index
	 *            clicked item index
	 */
	public void onDataClick(int index) { 
		selectionList[index] = !selectionList[index];
		statController.updateSelectedDataList(index,
				selectionList[index]);
		cbEnableAll.setValue(isAllEnabled());
		cbEnableAll.setEnabled(true);
	}

	/**
	 * Mark all items as enabled
	 */
	public void enableAll() {
		for (int i = 0; i < selectionList.length; ++i) {
			if (!selectionList[i]) {
				statController.updateSelectedDataList(i, true);
				selectionList[i] = true;
				Widget w = dataTable.getTable().getWidget(i + 1, 0);
				if (w instanceof CheckBox) {
					((CheckBox) w).setValue(true);
				}
			}
		}

		cbEnableAll.setValue(true);
		cbEnableAll.setEnabled(false);
	}

	/**
	 * @return whether all items are selected
	 */
	public boolean isAllEnabled() {
		for (int i = 0; i < selectionList.length; ++i) {
			if (!selectionList[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onResize() {
	    scrollPane.setHeight(getOffsetHeight() - lblHeader.getOffsetHeight() + "px");
    }

}