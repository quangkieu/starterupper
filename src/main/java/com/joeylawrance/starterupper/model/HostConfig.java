package com.joeylawrance.starterupper.model;

import java.util.Properties;

/**
 * Store tokens for hosts. This probably won't make the cut for the first release.
 * 
 * @author Joey Lawrance
 *
 */
public class HostConfig {
	private Properties config = new Properties();
	public HostConfig() throws Exception {
		
		config.load(HostConfig.class.getResourceAsStream("/hosts.properties"));
	}
	public String getDefaultUserName() {
		return System.getProperty("user.name");
	}
}
