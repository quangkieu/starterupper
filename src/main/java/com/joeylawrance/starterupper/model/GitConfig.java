package com.joeylawrance.starterupper.model;

import java.io.File;
import java.util.Properties;

import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;

public class GitConfig {
	public GitConfig() throws Exception {
		this(new File(new File(System.getProperty("user.home")), ".gitconfig"));
	}
	public GitConfig(File f) throws Exception {
		config = new FileBasedConfig(new File(new File(System.getProperty("user.home")), ".gitconfig"), FS.detect());
		try {
			config.load();
		} catch (Exception e) {
			throw new Exception (String.format("Sorry, your .gitconfig file in %s is unreadble or invalid.", System.getProperty("user.home")));
		}
	}
	private FileBasedConfig config;
	
	public String getUserFullName() {
		return config.getString("user", null, "name");
	}
	public String getUserEmail() {
		return config.getString("user", null, "email");
	}
	public String getUsername() {
		return System.getProperty("user.name");
	}
	public void setUserFullName(String name) throws Exception {
		config.setString("user", null, "name", name);
		save();
	}
	public void setUserEmail(String email) throws Exception {
		config.setString("user", null, "email", email);
		save();
	}
	private void save() throws Exception {
		if (config.isOutdated()) {
			throw new Exception (String.format("Sorry, another program has updated your .gitconfig file in %s.", System.getProperty("user.home")));
		}
		try {
			config.save();
		} catch (Exception e) {
			throw new Exception (String.format("Sorry, another program has locked your .gitconfig file in %s.", System.getProperty("user.home")));
		}
	}
}
