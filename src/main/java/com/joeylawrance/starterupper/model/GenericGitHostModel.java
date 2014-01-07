package com.joeylawrance.starterupper.model;

import java.net.InetAddress;
import java.util.Properties;

/**
 * Generically share public key with host.
 * 
 * @author Joey Lawrance
 *
 */
public abstract class GenericGitHostModel extends GenericHostModel implements GitHostModel {

	private String publicKeyURL;
	Properties token;

	public GenericGitHostModel(String window) {
		super(window);
	}
	
	protected void setPublicKeyURL(String publicKeyURL) {
		this.publicKeyURL = publicKeyURL;
	}
	
	@Override
	public void sharePublicKey() throws Exception {
		client.load(window, publicKeyURL);
		map.put("Title", String.format("StarterUpper (%s) @ %s", System.getProperty("os.name"), InetAddress.getLocalHost().getHostName()));
		client.fillForm(window, map);
		client.submitForm(window, "Add key");
	}
	
	@Override
	public void setPublicKey(String key) {
		map.put("Key", key);
	}
	
	@Override
	public void setToken(Properties token) {
		this.token = token;
	}
}
