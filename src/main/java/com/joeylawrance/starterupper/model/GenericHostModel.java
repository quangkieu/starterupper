package com.joeylawrance.starterupper.model;

import java.net.URL;
import java.util.HashMap;

/**
 * Generically log in, sign up, or reset the password for a host.
 * 
 * @author Joey Lawrance
 *
 */
public class GenericHostModel implements HostModel {
	WebHelper client;
	final String window;
	final URL logo;
	final String description;
	String signupURL;
	String loginURL;
	String resetURL;
	String username;
	
	protected HashMap<String, String> map = new HashMap<String, String>();
	
	public GenericHostModel(String window, URL logo, String description) {
		this.window = window;
		this.logo = logo;
		this.description = description;
		client = new WebHelper();
		client.newWindow(window);
	}
	
	protected WebHelper getClient() {
		return client;
	}
	
	protected void setSignupURL(String signupURL) {
		this.signupURL = signupURL;
	}
	
	protected void setLoginURL(String loginURL) {
		this.loginURL = loginURL;
	}
	
	protected void setResetURL(String resetURL) {
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
		return !loginURL.equals(client.getPageUrl(window));
	}

	@Override
	public void forgotPassword() throws Exception {
		client.load(window, resetURL);
		client.fillForm(window, map);
		client.submitForm(window, "reset|password|submit");
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
		map.put("Username", username);
	}

	@Override
	public String getUsername() {
		return username;
	}
	
	@Override
	public void setEmail(String email) {
		map.put("Email", email);
	}
	
	@Override
	public String getEmail() {
		return map.get("Email");
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
	public void setFirstname(String firstname) {
		map.put("First name", firstname);
	}

	@Override
	public void setLastname(String lastname) {
		map.put("Last name", lastname);
	}

	@Override
	public void setFullname(String fullname) {
		map.put("Name", fullname);
		map.put("Full name", fullname);
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

}
