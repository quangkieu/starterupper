package com.joeylawrance.starterupper.model.host;

import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;

import com.joeylawrance.starterupper.model.KeyConfig;

/**
 * Generically share public key with host.
 * 
 * @author Joey Lawrance
 *
 */
public class GenericGitHostModel extends GenericHostModel implements GitHostModel {
	private String publicKeyURL;
	Properties token;

	public GenericGitHostModel(String window, URL logo, String description) {
		super(window, logo, description);
		getMap().put("Key", KeyConfig.getInstance().getPublicKey());
	}
	
	protected void setPublicKeyURL(String publicKeyURL) {
		this.publicKeyURL = publicKeyURL;
	}
	
	@Override
	public void sharePublicKey() throws Exception {
		client.load(window, publicKeyURL);
		getMap().put("Title", String.format("StarterUpper (%s) @ %s", System.getProperty("os.name"), InetAddress.getLocalHost().getHostName()));
		client.fillForm(window, getMap());
		client.submitForm(window, "Add key");
	}
}
