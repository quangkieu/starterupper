package com.joeylawrance.starterupper.model.host;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.joeylawrance.starterupper.model.KeyConfig;

/**
 * Generically share public key with host.
 * 
 * @author Joey Lawrance
 *
 */
public class GenericGitHost extends GenericHost implements GitHost {
	private final Logger logger = LoggerFactory.getLogger(GitHost.class);
	private String publicKeyURL;
	Properties token;
	private String host;

	public GenericGitHost(String window, URL logo, String description) {
		super(window, logo, description);
		getMap().put("Key", KeyConfig.getInstance().getPublicKey());
	}
	
	protected void setPublicKeyURL(String publicKeyURL) {
		this.publicKeyURL = publicKeyURL;
	}
	
	protected void setHost(String host) {
		this.host = host;
	}
	protected String getHost() {
		return host;
	}
	
	@Override
	public void sharePublicKey() {
		try {
			client.load(window, publicKeyURL);
			getMap().put("Title", String.format("StarterUpper (%s) @ %s", System.getProperty("os.name"), InetAddress.getLocalHost().getHostName()));
			client.fillForm(window, getMap());
			client.submitForm(window, "Add key");
			logger.info("Shared public key with {}", window);
		} catch (FailingHttpStatusCodeException e) {
			logger.error("Unable to share public key with {}.", window, e);
		} catch (IOException e) {
			logger.error("Unable to share public key with {}.", window, e);
		}
	}

	@Override
	public boolean testLogin() {
		logger.info("Testing SSH credentials for {}.", window);
		return KeyConfig.getInstance().testLogin(host);
	}
}
