package com.joeylawrance.starterupper.model;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebWindowNotFoundException;
import com.joeylawrance.starterupper.model.GitUserMap.Profile;
import com.joeylawrance.starterupper.model.interfaces.HostModel;
import com.joeylawrance.starterupper.util.ObservableMap;
import com.joeylawrance.starterupper.util.ObservableMapListener;

/**
 * Generically log in, sign up, or reset the password for a host.
 * 
 * @author Joey Lawrance
 *
 */
public class GenericHostModel implements HostModel, ObservableMapListener<GitUserMap.Profile, String> {
	private final Logger logger = LoggerFactory.getLogger(GenericHostModel.class);
	private final Preferences prefs = Preferences.userNodeForPackage(getClass());
	WebHelper client;
	final String window;
	final URL logo;
	final String description;
	String signupURL;
	String loginURL;
	String resetURL;
	String logoutURL;
	String profileURL;
	
	private HashMap<String, String> map = new HashMap<String, String>();
	
	public Map<String, String> getMap() {
		return map;
	}
	
	public GenericHostModel(String window, URL logo, String description) {
		this.window = window;
		this.logo = logo;
		this.description = description;
		client = new WebHelper();
		client.newWindow(window);
		loadUsername();
	}
	
	public WebHelper getClient() {
		return client;
	}
	
	public void setSignupURL(String signupURL) {
		this.signupURL = signupURL;
	}
	
	public void setLoginURL(String loginURL) {
		this.loginURL = loginURL;
	}
	
	public void setLogoutURL(String logoutURL) {
		this.logoutURL = logoutURL;
	}
	
	public void setResetURL(String resetURL) {
		this.resetURL = resetURL;
	}

	@Override
	public boolean signUp() throws Exception {
		client.load(window,signupURL);
		client.fillForm(window, map);
		return false;
	}

	@Override
	public boolean login() throws Exception {
		client.load(window,loginURL);
		client.fillForm(window, map);
		client.submitForm(window,"Sign in|Log in");
		System.out.println(loginURL);
		System.out.println(client.getPageUrl(window));
		boolean successful = !loginURL.equals(client.getPageUrl(window));
		if (successful) {
			storeUsername();
		}
		return successful;
	}
	
	private void storeUsername() {
		prefs.node(window).put("login", getUsername());
	}
	
	private void loadUsername() {
		setUsername(prefs.node(window).get("login", getUsername()));
	}

	@Override
	public void forgotPassword() throws Exception {
		client.load(window, resetURL);
		client.fillForm(window, map);
		client.submitForm(window, "reset|password|submit");
	}

	@Override
	public void setUsername(String username) {
		map.put("Username", username);
	}

	@Override
	public String getUsername() {
		return map.get("Username");
	}
	
	@Override
	public void setPassword(String password) {
		map.put("Password", password);
	}
	
	@Override
	public String getPassword() {
		return map.get("Password");
	}

	@Override
	public String getHostName() {
		return window;
	}

	@Override
	public URL getLogo() {
		return logo;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void logout() throws Exception {
		client.load(window,logoutURL);
	}

	public void setProfileURL(String profileURL) {
		this.profileURL = profileURL;
	}

	@Override
	public boolean nameTaken() {
		try {
			client.load(window, String.format(profileURL, getUsername()));
			logger.info("Loaded {}", String.format(profileURL, getUsername()));
		} catch (FailingHttpStatusCodeException e) {
			logger.info("Load failed. Status code: {}", e.getMessage());
			return true;
		} catch (WebWindowNotFoundException e) {
			logger.error("Couldn't find window.");
		} catch (IOException e) {
			logger.error("Couldn't connect to the network.");
		}
		return false;
	}

	@Override
	public void mapKeyAdded(ObservableMap<Profile, String> map, Profile key) {
	}
	@Override
	public void mapKeyRemoved(ObservableMap<Profile, String> map, Profile key,
			String value) {
	}
	@Override
	public void mapKeyValueChanged(ObservableMap<Profile, String> map,
			Profile key, String value) {
		switch (key) {
		case email:
			getMap().put("Email", value);
			break;
		case firstname:
			getMap().put("First name", value);
			break;
		case lastname:
			getMap().put("Last name", value);
			break;
		case name:
			getMap().put("Name", value);
			break;
		case defaultname:
			if (getUsername() == null)
				setUsername(value);
			break;
		default:
			break;
		}
	}
}
