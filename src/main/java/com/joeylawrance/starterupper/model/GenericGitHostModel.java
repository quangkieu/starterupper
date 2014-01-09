package com.joeylawrance.starterupper.model;

import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;

/**
 * Generically share public key with host.
 * 
 * @author Joey Lawrance
 *
 */
public class GenericGitHostModel extends GenericHostModel implements GitHostModel {

	private String publicKeyURL;
	Properties token;

	public GenericGitHostModel(String window, URL logo) {
		super(window, logo);
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
}
