package com.joeylawrance.starterupper.model.host;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.joeylawrance.starterupper.model.GitUserMap;
import com.joeylawrance.starterupper.model.WebHelper;
import com.joeylawrance.starterupper.model.GitUserMap.Profile;
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
	public static enum HostAction {
		signup, login, reset, logout, profile;
	}
	private HashMap<HostAction, String> urls = new HashMap<HostAction, String>();
	
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
		setUsername(loadUsername());
	}
	
	public WebHelper getClient() {
		return client;
	}
	public void setURL(HostAction action, String url) {
		urls.put(action, url);
	}
	public String getURL(HostAction action) {
		return urls.get(action);
	}

	@Override
	public boolean signUp() {
		try {
			client.load(window,getURL(HostAction.signup));
		} catch (Exception e) {
			logger.error("Unable to load signup page.");
		}
		client.fillForm(window, map);
		return false;
	}

	@Override
	public boolean login() {
		try {
			client.load(window,getURL(HostAction.login));
		} catch (FailingHttpStatusCodeException | IOException e) {
			logger.error("Unable to load login page.");
			return false;
		}
		client.fillForm(window, map);
		client.submitForm(window,"Sign in|Log in");
		boolean successful = !getURL(HostAction.login).equals(client.getPageUrl(window));
		if (successful) {
			logger.info("Successfully logged into {}", getHostName());
			storeUsername();
		}
		return successful;
	}
	
	private void storeUsername() {
		prefs.node(window).put("login", getUsername());
	}
	
	private String loadUsername() {
		return prefs.node(window).get("login", null);
	}

	@Override
	public void forgotPassword() {
		try {
			client.load(window, getURL(HostAction.reset));
			client.fillForm(window, map);
			client.submitForm(window, "reset|password|submit");
			logger.info("Password reset for {} sent.", getHostName());
		} catch (FailingHttpStatusCodeException | IOException e) {
			logger.error("Unable to load forgot password page");
		}
	}

	@Override
	public void setUsername(String username) {
		logger.info("{} username changed to {}", getHostName(), username);
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
	public void logout() {
		try {
			client.load(window,getURL(HostAction.logout));
		} catch (FailingHttpStatusCodeException | IOException e) {
			logger.error("Unable to logout.");
		}
	}

	@Override
	public boolean nameTaken() {
		try {
			if (loadUsername() != null) return true;
			client.load(window, String.format(getURL(HostAction.profile), getUsername()));
			logger.info("Loaded {}", String.format(getURL(HostAction.profile), getUsername()));
		} catch (FailingHttpStatusCodeException e) {
			logger.info("Load failed. Status code: {}", e.getMessage());
			return false;
		} catch (IOException e) {
			logger.error("Couldn't connect to the network.");
		}
		return true;
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
			// Don't bother changing the name to the default if we've logged in successfully before.
			if (loadUsername() == null)
				setUsername(value);
			break;
		default:
			break;
		}
	}
}
