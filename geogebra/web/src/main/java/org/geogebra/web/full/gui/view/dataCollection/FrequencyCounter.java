package org.geogebra.web.full.gui.view.dataCollection;

import java.util.ArrayList;

import org.geogebra.common.plugin.SensorLogger.Types;

/**
 * 
 */
public class FrequencyCounter {
	
	/** time in ms to keep the last received data */
	private final static int TIME_TO_KEEP = 1000;
	
	private ArrayList<Double> timestamps = new ArrayList<>();
	private Types type;
	private int frequency;
	private DataCollection data;

	private int lastDataCount = 0;
	
	/**
	 * 
	 * @param type
	 *            {@link Types sensorType}
	 * @param data
	 *            {@link DataCollection}
	 */
	public FrequencyCounter(Types type, DataCollection data) {
		this.type = type;
		this.data = data;
	}

	/**
	 * @param time
	 *            timestamp
	 * @param dataCount
	 *            count
	 */
	public synchronized void addTimestamp(double time, int dataCount) {
		if (dataCount < this.lastDataCount) {
			clearTimestamps();
		}
		this.lastDataCount = dataCount;
		this.timestamps.add(time);
		
		removeOldValues();
		calcualteFreq();
		updateUI();
	}

	private void removeOldValues() {
		ArrayList<Double> x = new ArrayList<>();
		for (int i = timestamps.size() - 1; i >= 0; i--) {
			if (timestamps.get(i) > timestamps.get(timestamps.size() - 1)
					- TIME_TO_KEEP) {
				x.add(timestamps.get(i));
			}
		}
		this.timestamps = x;
	}
	
	public void clearTimestamps() {
		this.timestamps.clear();
	}

	private void updateUI() {
		data.updateRealFrequency(this.type, this.frequency);
	}
	
	private void calcualteFreq() {
		this.frequency = this.timestamps.size();
	}

	public Types getType() {
		return this.type;
	}
}