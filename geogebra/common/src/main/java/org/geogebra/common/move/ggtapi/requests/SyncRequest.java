package org.geogebra.common.move.ggtapi.requests;

import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.Request;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.util.debug.Log;

/**
 * Upload request for GeoGebraTube
 *
 */
public class SyncRequest implements Request {

	private static final String API = "1.0.0";
	private static final String GGB = "geogebra";
	private static final String TASK = "sync";
	private long timestamp;

	/**
	 * Used to upload the actual opened application to GeoGebraTube
	 * 
	 * @param timestamp
	 *            since when we want to see the events
	 */
	public SyncRequest(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toJSONString(ClientInfo client) {
		try {
			JSONObject api = new JSONObject();
			api.put("-api", SyncRequest.API);

			// login
			JSONObject login = new JSONObject();
			login.put("-type", SyncRequest.GGB);
			login.put("-token",
					client.getModel().getLoggedInUser().getLoginToken());
			api.put("login", login);

			// task
			JSONObject task = new JSONObject();
			task.put("-type", SyncRequest.TASK);

			// type
			task.put("timestamp", this.timestamp + "");

			api.put("task", task);
			JSONObject request = new JSONObject();
			request.put("request", api);

			return request.toString();
		} catch (Exception e) {
			Log.debug("problem building request: " + e.getMessage());
			return null;
		}

	}
}
