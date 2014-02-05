package com.joeylawrance.starterupper.model.host;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.google.common.eventbus.Subscribe;
import com.joeylawrance.starterupper.model.ConfigChanged;
import com.joeylawrance.starterupper.model.Event;
import com.joeylawrance.starterupper.model.WebHelper;

/**
 * Generically log in, sign up, or reset the password for a host.
 * 
 * @author Joey Lawrance
 *
 */
public class GenericHost implements Host {
	private final Logger logger = LoggerFactory.getLogger(GenericHost.class);
	private final Preferences prefs = Preferences.userNodeForPackage(getClass());
	WebHelper client;
	final String window;
	final URL logo;
	final String description;
	private HashMap<HostAction, String> urls = new HashMap<HostAction, String>();
	private boolean loggedIn = false;
	private HashMap<String, String> map = new HashMap<String, String>();
	
	public Map<String, String> getMap() {
		return map;
	}
	
	public GenericHost(String window, URL logo, String description) {
		this.window = window;
		this.logo = logo;
		this.description = description;
		client = new WebHelper();
		client.newWindow(window);
		setUsername(loadUsername());
		Event.getBus().register(this);
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
			Event.getBus().post(new HostPerformedAction(this, HostAction.signup, false));
		}
		client.fillForm(window, map);
		return false;
	}

	@Override
	public boolean login() {
		try {
			client.load(window,getURL(HostAction.login));
		} catch (FailingHttpStatusCodeException e) {
			logger.error("Unable to load login page.");
		} catch (IOException e) {
			logger.error("Unable to load login page.");
			return false;
		}
		client.fillForm(window, map);
		client.submitForm(window,"Sign in|Log in");
		loggedIn = !getURL(HostAction.login).equals(client.getPageUrl(window));
		if (loggedIn) {
			logger.info("Successfully logged into {}", getHostName());
			storeUsername();
			Event.getBus().post(new HostPerformedAction(this, HostAction.login, true));
		} else {
			Event.getBus().post(new HostPerformedAction(this, HostAction.login, false));
		}
		return loggedIn;
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
			Event.getBus().post(new HostPerformedAction(this, HostAction.reset, true));
		} catch (FailingHttpStatusCodeException e) {
			logger.error("Unable to load forgot password page.", e.fillInStackTrace());
			Event.getBus().post(new HostPerformedAction(this, HostAction.reset, false));
		} catch (IOException e) {
			logger.error("Unable to load forgot password page.", e.fillInStackTrace());
			Event.getBus().post(new HostPerformedAction(this, HostAction.reset, false));
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
			loggedIn = false;
			Event.getBus().post(new HostPerformedAction(this, HostAction.logout, true));
		} catch (FailingHttpStatusCodeException e) {
			logger.error("Unable to logout.");
		} catch (IOException e) {
			logger.error("Unable to logout.");
		}
	}

	@Subscribe
	public void updateInternalConfiguration(ConfigChanged event) {
		switch (event.key) {
		case email:
			getMap().put("Email", event.value);
			break;
		case firstname:
			getMap().put("First name", event.value);
			break;
		case lastname:
			getMap().put("Last name", event.value);
			break;
		case name:
			getMap().put("Name", event.value);
			break;
		case defaultname:
			// Don't clobber the name to the default if we've logged in successfully before.
			if (!haveLoggedInBefore()) {
				setUsername(event.value);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean loggedIn() {
		return loggedIn;
	}

	@Override
	public boolean haveLoggedInBefore() {
		return loadUsername() != null;
	}
}
