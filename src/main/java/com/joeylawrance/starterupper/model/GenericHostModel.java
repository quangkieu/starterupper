package com.joeylawrance.starterupper.model;

import java.util.HashMap;

public class GenericHostModel implements HostModel {
	WebHelper client;
	final String window;
	String signupURL;
	String loginURL;
	String resetURL;
	
	protected HashMap<String, String> map = new HashMap<String, String>();
	
	public GenericHostModel(String window) {
		this.window = window;
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
		map.put("Username", username);
	}

	@Override
	public void setEmail(String email) {
		map.put("Email", email);
	}

	@Override
	public void setPassword(String password) {
		map.put("Password", password);
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
	}
}
