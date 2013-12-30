package com.joeylawrance.starterupper.model;

import java.util.Properties;

public class HostConfig {
	private Properties config = new Properties();
	public HostConfig() throws Exception {
		config.load(HostConfig.class.getResource("/config.properties").openStream());
	}
	public String getDefaultUserName() {
		return System.getProperty("user.name");
	}
}
