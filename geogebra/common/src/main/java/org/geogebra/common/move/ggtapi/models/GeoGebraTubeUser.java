package org.geogebra.common.move.ggtapi.models;

import java.util.ArrayList;

import org.geogebra.common.move.models.BaseModel;

/**
 * Represents a user in GeoGebraTube. Each user is identified by a user name.
 * 
 * The login token is used for the authorization of the user via the
 * GeoGebraTube API.
 * 
 * @author stefan
 *
 */
public class GeoGebraTubeUser extends BaseModel {
	private String userName = null;
	private String token = null;
	private int userId = -1;
	private String identifier = null;
	private String profileURL;
	private String realName = null;
	private String cookie;
	private String image;
	private String language;
	private String gender;
	private ArrayList<String> groups;
	private boolean shibbolethAuth;
	private boolean student = false;

	/**
	 * Creates a new user with the specified login token
	 * 
	 * @param token
	 *            The login token of the user
	 */
	public GeoGebraTubeUser(String token) {
		this.token = token;
	}

	/**
	 * @param token
	 *            login token
	 * @param cookie
	 *            login cookie
	 */
	public GeoGebraTubeUser(String token, String cookie) {
		this.token = token;
		this.cookie = cookie;
	}

	/**
	 * @return The Login token of the user
	 */
	public String getLoginToken() {
		return token;
	}

	/**
	 * Token needs to be set on cookie authentication
	 * 
	 * @param token
	 *            new token
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return The user name of the user
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Sets the user name for the user. Usually this is done after the user was
	 * authorized via the GeoGebraTube API and the user name is received as
	 * response.
	 * 
	 * @param userName
	 *            The new user name to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Sets the userid from GeoGeoGebraTube
	 * 
	 * @param userId
	 *            The new userId
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * Sets the real name from GeoGeoGebraTube
	 * 
	 * @param realName
	 *            The new real name of the user
	 */
	public void setRealName(String realName) {
		this.realName = realName;
	}

	/**
	 * @return the userid
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @return the real name
	 */
	public String getRealName() {
		return realName;
	}

	/**
	 * @return URl to profile page
	 */
	public String getProfileURL() {
		return this.profileURL;
	}

	/**
	 * @param URL
	 *            of the profile page
	 */
	public void setProfileURL(String URL) {
		this.profileURL = URL;
	}

	/**
	 * @param gender
	 *            {@code F} for female {@code M} for male {@code O} for not
	 *            specified
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return gender of user
	 * @see #setGender(String)
	 */
	public String getGender() {
		return this.gender;
	}

	/**
	 * @return The login identifier of this user
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier
	 *            The login identifier of this user
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return whether GDrive should be enabled
	 */
	public boolean hasGoogleDrive() {
		return this.identifier.startsWith("google:");
	}

	/**
	 * @return whether onedrive should be enabled
	 */
	public boolean hasOneDrive() {
		return false;
	}

	/**
	 * @return login cookie
	 */
	public String getCookie() {
		return this.cookie;
	}

	/**
	 * @param url
	 *            profile image URL
	 */
	public void setImageURL(String url) {
		this.image = url;
	}

	/**
	 * @return avatar URL
	 */
	public String getImageURL() {
		return this.image;
	}

	/**
	 * @return user preferred language
	 */
	public String getLanguage() {
		return this.language;
	}

	/**
	 * @param language
	 *            user preferred language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @param groups
	 *            group IDs
	 */
	public void setGroups(ArrayList<String> groups) {
		this.groups = groups;
	}

	/**
	 * @return user group IDs (may be empt, not null)
	 */
	public ArrayList<String> getGroups() {
		if (groups == null) {
			return new ArrayList<>();
		}
		return groups;
	}

	/**
	 * @return whether user was logged in with shibboleth
	 */
	public boolean isShibbolethAuth() {
		return shibbolethAuth;
	}

	/**
	 * @param shibbolethAuth
	 *            whether user was logged in with shibboleth
	 */
	public void setShibbolethAuth(boolean shibbolethAuth) {
		this.shibbolethAuth = shibbolethAuth;
	}

	public boolean isStudent() {
		return student;
	}

	public void setStudent(boolean student) {
		this.student = student;
	}
}
