package org.geogebra.web.full.gui.view.dataCollection;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.DataCollectionSettings;
import org.geogebra.common.plugin.SensorLogger.Types;
import org.geogebra.web.full.gui.view.dataCollection.Settings.SensorSetting;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.ListBox;

/**
 * ListBox with default settings
 */
public class GeoListBox extends ListBox {

	/**
	 * Default entries of the GeoListBox
	 */
	public enum DefaultEntries {
		EMPTY_SELECTION(0, "- - -"), CREATE_NUMBER(1, "CreateNumber"), CREATE_DATA_FUNCTION(
				2, "CreateDataFunction"), CREATE_LIST(3, "CreateDataList");

		private int index;
		private String text;

		DefaultEntries(int index, String text) {
			this.index = index;
			this.text = text;
		}

		/**
		 * @return index of the default setting in the listbox
		 */
		public int getIndex() {
			return this.index;
		}

		/**
		 * @return text to be displayed in the listbox
		 */
		public String getText() {
			return this.text;
		}
	}

	private Types type;
	private ArrayList<GeoElement> items = new ArrayList<>();
	private SensorSetting sensor;
	private final int nextFreeGeoListBoxIndex;
	private AppW app;
	private DataCollectionSettings dataCollectionSettings;

	/**
	 * @param type
	 *            of sensor which is associated with this listbox
	 * @param sensor
	 *            {@link SensorSetting} to which this GeoListBox belongs
	 * @param app
	 *            application
	 * @param dataCollectionSettings
	 *            settings
	 */
	public GeoListBox(Types type, SensorSetting sensor, AppW app,
			DataCollectionSettings dataCollectionSettings) {
		this.sensor = sensor;
		this.type = type;
		this.app = app;
		this.nextFreeGeoListBoxIndex = type == Types.TIMESTAMP ? DefaultEntries
				.values().length - 1 : DefaultEntries.values().length;
		this.dataCollectionSettings = dataCollectionSettings;
	}

	/**
	 * @return the associated {@link Types sensor-type}
	 */
	public Types getType() {
		return this.type;
	}

	/**
	 * Sets the selected GeoElement. if {@code elem} is {@code null}, the
	 * selected index of this listbox is set to
	 * {@link DefaultEntries#EMPTY_SELECTION}.
	 * 
	 * @param elem
	 *            the selected {@link GeoElement}
	 */
	public void setSelection(GeoElement elem) {
		if (elem == null) {
			this.setSelectedIndex(DefaultEntries.EMPTY_SELECTION.getIndex());
			this.dataCollectionSettings.removeMappedGeo(this.type);
		} else {

			this.setSelectedIndex(this.items.indexOf(elem)
					+ nextFreeGeoListBoxIndex);
			this.dataCollectionSettings.mapSensorToGeo(this.type, elem);
		}
	}

	/**
	 * @return the selected {@link GeoElement}
	 */
	public GeoElement getSelection() {
		return this.dataCollectionSettings.getGeoMappedToSensor(this.type, app
				.getKernel().getConstruction());
	}

	/**
	 * 
	 * @return the SensorSetting to which this GeoListBox belongs to
	 */
	public SensorSetting getSensorSetting() {
		return this.sensor;
	}

	/**
	 * @param elem
	 *            {@link GeoElement}
	 */
	public void addItem(GeoElement elem) {
		this.items.add(elem);
		super.addItem(elem.getNameDescription());
	}

	/**
	 * looks up in the list of elements displayed for this listbox for the
	 * geoElement with the given name
	 * 
	 * @param name
	 *            text of the selected item
	 * @return the {@link GeoElement} with the given name if it wasn't found.
	 */
	public GeoElement getGeoElement(String name) {
		for (GeoElement geo : this.items) {
			if (geo.getNameDescription().equals(name)) {
				return geo;
			}
		}
		return null;
	}

	/**
	 * removes all entries and generates the default entries.
	 */
	@Override
	public void clear() {
		this.items = new ArrayList<>();
		this.dataCollectionSettings.removeMappedGeo(this.type);
		super.clear();
		addDefaultEntries();
	}

	private void addDefaultEntries() {
		for (DefaultEntries entry : DefaultEntries.values()) {
			if (this.type != Types.TIMESTAMP
					|| entry != DefaultEntries.CREATE_DATA_FUNCTION) {
				this.addItem(app.getLocalization().getMenu(entry.getText()),
						entry.getText());
			}
		}
	}

	/**
	 * 
	 * @return the first index after the default entries
	 */
	public int getFirstFreeGeoListBoxIndex() {
		return this.nextFreeGeoListBoxIndex;
	}
}
