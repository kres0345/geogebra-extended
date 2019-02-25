package org.geogebra.common.move.ggtapi.models;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.MaterialRequest.Order;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.move.ggtapi.operations.BackendAPI;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.ggtapi.operations.URLChecker;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.move.ggtapi.requests.SyncCallback;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * API connector for the MARVL restful API
 */
public class MarvlAPI implements BackendAPI {
	/** whether API is available */
	protected boolean available = true;
	/** whether availability check request was sent */
	protected boolean availabilityCheckDone = false;
	private String baseURL;
	private AuthenticationModel model;
	private String basicAuth = null; // for test only
	private URLChecker urlChecker;

	/**
	 * @param baseURL
	 *            URL of the API; endpoints append eg. "/materials" to it
	 */
	public MarvlAPI(String baseURL, URLChecker urlChecker) {
		this.baseURL = baseURL;
		this.urlChecker = urlChecker;
	}

	@Override
	public void getItem(String id, MaterialCallbackI callback) {
		performRequest("GET", "/materials/" + id, null, callback);
	}

	@Override
	public boolean checkAvailable(LogInOperation logInOperation) {
		if (!availabilityCheckDone) {
			performCookieLogin(logInOperation);
		}
		return available;
	}

	@Override
	public String getLoginUrl() {
		return null;
	}

	@Override
	public boolean parseUserDataFromResponse(GeoGebraTubeUser guser, String response) {
		try {
			JSONTokener tokener = new JSONTokener(response);
			JSONObject user = new JSONObject(tokener).getJSONObject("user");
			guser.setRealName(user.getString("displayname"));
			guser.setUserName(user.getString("username"));
			guser.setUserId(user.getInt("id"));
			guser.setIdentifier("");
			guser.setStudent(!"1".equals(user.getString("isTeacher")));
			if (user.has("allGroups")) {
				JSONArray classList = user.getJSONArray("allGroups");
				guser.setGroups(stringList(classList));
			}
			return true;
		} catch (Exception e) {
			Log.warn(e.getMessage());
		}
		return false;
	}

	/**
	 * @param classList
	 *            JSON array
	 * @return Java array
	 * @throws JSONException
	 *             if array contains objects other than strings
	 */
	static ArrayList<String> stringList(JSONArray classList) throws JSONException {
		ArrayList<String> groups = new ArrayList<>();
		for (int i = 0; i < classList.length(); i++) {
			groups.add(classList.getString(i));
		}
		return groups;
	}

	@Override
	public void deleteMaterial(final Material mat, final MaterialCallbackI callback) {

		HttpRequest request = makeRequest();
		request.sendRequestPost("DELETE", baseURL + "/materials/" + mat.getSharingKeyOrId(), null,
				new AjaxCallback() {
					@Override
					public void onSuccess(String responseStr) {
						// we don't parse the response here
						ArrayList<Material> mats = new ArrayList<>();
						mats.add(mat);
						callback.onLoaded(mats, null);
					}

					@Override
					public void onError(String error) {
						callback.onError(new Exception(error));
					}
				});

	}

	@Override
	public final void authorizeUser(final GeoGebraTubeUser user, final LogInOperation op,
			final boolean automatic) {
		HttpRequest request = makeRequest();
		request.sendRequestPost("GET",
				baseURL + "/auth",
				null, new AjaxCallback() {
					@Override
					public void onSuccess(String responseStr) {
						System.out.println(responseStr);
						try {
							MarvlAPI.this.availabilityCheckDone = true;

							MarvlAPI.this.available = true;
							user.setShibbolethAuth(true);
							// Parse the userdata from the response
							if (!parseUserDataFromResponse(user, responseStr)) {
								op.onEvent(new LoginEvent(user, false, automatic, responseStr));
								return;
							}

							op.onEvent(new LoginEvent(user, true, automatic, responseStr));

							// GeoGebraTubeAPID.this.available = false;
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onError(String error) {
						System.out.println(error);
						MarvlAPI.this.availabilityCheckDone = true;
						MarvlAPI.this.available = false;
						user.setShibbolethAuth(true);
						op.onEvent(new LoginEvent(user, false, automatic, null));
					}
				});
	}

	private HttpRequest makeRequest() {
		HttpRequest request = UtilFactory.getPrototype().newHttpRequest();
		if (this.basicAuth != null) {
			request.setAuth(basicAuth);
		}
		return request;
	}

	@Override
	public void setClient(ClientInfo client) {
		this.model = client.getModel();
	}

	@Override
	public void sync(long i, SyncCallback syncCallback) {
		// offline materials not supported
	}

	@Override
	public boolean isCheckDone() {
		return this.availabilityCheckDone;
	}

	@Override
	public void setUserLanguage(String fontStr, String loginToken) {
		// not supported
	}

	@Override
	public void shareMaterial(Material material, String to, String message, MaterialCallbackI cb) {
		// not supported
	}

	@Override
	public void favorite(int id, boolean favorite) {
		// not supported
	}

	@Override
	public String getUrl() {
		return this.baseURL;
	}

	@Override
	public void logout(String token) {
		// open platform dependent popup
	}

	@Override
	public void uploadLocalMaterial(Material mat, MaterialCallbackI cb) {
		// offline materials not supported
	}

	@Override
	public boolean performCookieLogin(final LogInOperation op) {
		op.passiveLogin(new AsyncOperation<Boolean>() {

			@Override
			public void callback(Boolean obj) {
				authorizeUser(new GeoGebraTubeUser(""), op, true);
			}
		});

		return true;
	}

	@Override
	public void performTokenLogin(LogInOperation op, String token) {
		performCookieLogin(op);
	}

	@Override
	public void getUsersMaterials(MaterialCallbackI userMaterialsCB, MaterialRequest.Order order) {
		getUsersOwnMaterials(userMaterialsCB, order);
	}

	/**
	 * @param responseStr
	 *            JSON encoded material or list of materials
	 * @return list of materials
	 * @throws JSONException
	 *             when structure of JSON is invalid
	 */
	protected List<Material> parseMaterials(String responseStr) throws JSONException {
		ArrayList<Material> ret = new ArrayList<>();
		JSONTokener jst = new JSONTokener(responseStr);
		Object parsed = jst.nextValue();
		if (parsed instanceof JSONArray) {
			JSONArray arr = (JSONArray) parsed;
			for (int i = 0; i < arr.length(); i++) {
				Material mat = JSONParserGGT.prototype.toMaterial(arr.getJSONObject(i));
				ret.add(mat);
			}
		} else if (parsed instanceof JSONObject) {
			Material mat = JSONParserGGT.prototype.toMaterial((JSONObject) parsed);
			ret.add(mat);
		}
		return ret;
	}

	@Override
	public void getFeaturedMaterials(MaterialCallbackI userMaterialsCB) {
		// no public materials
		userMaterialsCB.onLoaded(new ArrayList<Material>(), null);
	}

	@Override
	public void getUsersOwnMaterials(final MaterialCallbackI userMaterialsCB,
			MaterialRequest.Order order) {
		if (model == null) {
			userMaterialsCB.onError(new Exception("No user signed in"));
			return;
		}

		performRequest("GET",
				"/users/" + model.getUserId()
						+ "/materials?limit=30&embed=creator&order="
						+ orderStr(order),
				null, userMaterialsCB);
	}

	@Override
	public void getSharedMaterials(final MaterialCallbackI sharedMaterialsCB,
			MaterialRequest.Order order) {
		if (model == null) {
			sharedMaterialsCB.onError(new Exception("No user signed in"));
			return;
		}

		performRequest("GET",
				"/users/" + model.getUserId()
						+ "/materials?type=shared_with&embed=creator&order="
						+ orderStr(order),
				null, sharedMaterialsCB);
	}

	private static String orderStr(Order order) {
		switch (order) {
		case timestamp:
			return "-modified";
		case created:
			return "-" + order.name();
		case title:
		case privacy:
			return order.name();
		default:
			return "title";
		}
	}

	private void performRequest(final String method, String endpoint, String json,
			final MaterialCallbackI userMaterialsCB) {
		HttpRequest request = makeRequest();
		request.setContentTypeJson();
		request.sendRequestPost(method, baseURL + endpoint, json, new AjaxCallback() {
			@Override
			public void onSuccess(String responseStr) {
				try {
					userMaterialsCB.onLoaded(parseMaterials(responseStr), null);
				} catch (Exception e) {
					userMaterialsCB.onError(e);
				}
			}

			@Override
			public void onError(String error) {
				userMaterialsCB.onError(new Exception(error));
			}
		});

	}

	@Override
	public void uploadMaterial(String tubeID, String visibility, String text, String base64,
			MaterialCallbackI materialCallback, MaterialType type) {
		JSONObject request = new JSONObject();
		try {
			request.put("visibility", visibility); // per docs "S" is the only
											// supported visibility
			request.put("title", text);
			request.put("file", base64);
			if (StringUtil.emptyOrZero(tubeID)) {
				request.put("type", type.name());
			}
		} catch (JSONException e) {
			materialCallback.onError(e);
		}
		if (!StringUtil.emptyOrZero(tubeID)) {
			performRequest("PATCH", "/materials/" + tubeID, request.toString(),
					materialCallback);
		} else {
			performRequest("POST", "/materials", request.toString(), materialCallback);
		}
	}

	@Override
	public void uploadRenameMaterial(Material material, MaterialCallbackI materialCallback) {
		JSONObject request = new JSONObject();
		try {
			request.put("title", material.getTitle());
		} catch (JSONException e) {
			materialCallback.onError(e);
		}
		performRequest("PATCH", "/materials/" + material.getSharingKeyOrId(),
				request.toString(), materialCallback);
	}

	@Override
	public void copy(Material material, final String title,
			final MaterialCallbackI materialCallback) {
		performRequest("POST", "/materials/" + material.getSharingKeyOrId(), null,
				new MaterialCallbackI() {

					@Override
					public void onLoaded(List<Material> result, ArrayList<Chapter> meta) {
						if (result.size() == 1) {
							result.get(0).setTitle(title);
							uploadRenameMaterial(result.get(0), materialCallback);
						}
					}

					@Override
					public void onError(Throwable exception) {
						materialCallback.onError(exception);
					}
				});
	}

	/**
	 * Set authentication for HTTP basic auth (used in tests).
	 *
	 * @param base64
	 *            base64 encoded basic auth header
	 */
	public void setBasicAuth(String base64) {
		this.basicAuth = base64;
	}

	/**
	 * @param localization
	 *            localization
	 * @param title
	 *            original title
	 * @return title with "Copy of" prefix or numeric suffix
	 */
	public static String getCopyTitle(Localization localization, String title) {
		if (title.matches(localization.getPlain("CopyOfA", ".*"))) {
			int i = 2;
			String stem = title;
			if (title.endsWith(")")) {
				String numeric = title.substring(title.lastIndexOf('(') + 1, title.length() - 1);
				try {
					i = Integer.parseInt(numeric) + 1;
					stem = title.substring(0, title.lastIndexOf('(') - 1);
				} catch (RuntimeException e) {
					// ignore
				}
			}
			return stem + " (" + i + ")";
		}
		return localization.getPlain("CopyOfA", title);
	}

	@Override
	public void setShared(Material m, String groupID, boolean shared,
			final AsyncOperation<Boolean> callback) {
		HttpRequest request = makeRequest();
		request.sendRequestPost(shared ? "POST" : "DELETE",
				baseURL + "/materials/" + m.getSharingKeyOrId() + "/groups/" + groupID, null,
				new AjaxCallback() {
					@Override
					public void onSuccess(String responseStr) {
						callback.callback(true);
					}

					@Override
					public void onError(String error) {
						callback.callback(false);
					}
				});
	}

	@Override
	public void getGroups(String materialID, final AsyncOperation<List<String>> callback) {
		HttpRequest request = makeRequest();
		request.sendRequestPost("GET",
				baseURL + "/materials/" + materialID + "/groups?type=isShared", null,
				new AjaxCallback() {
					@Override
					public void onSuccess(String responseStr) {
						JSONArray groups;
						try {
							groups = new JSONArray(new JSONTokener(responseStr));
							callback.callback(stringList(groups));
						} catch (JSONException e) {
							callback.callback(null);
						}

					}

					@Override
					public void onError(String error) {
						callback.callback(null);
					}
				});
	}

	@Override
	public boolean owns(Material mat) {
		return mat.getCreator().getId() <= 0
				|| mat.getCreator().getId() == model.getUserId();
	}

	@Override
	public boolean canUserShare(boolean student) {
		return !student;
	}

	@Override
	public boolean anonymousOpen() {
		return false;
	}

	@Override
	public URLChecker getURLChecker() {
		// implement me
		return urlChecker;
	}
}
