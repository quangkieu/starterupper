package com.joeylawrance.starterupper.model;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitConfig {
	final Logger logger = LoggerFactory.getLogger(GitConfig.class);
	private GitConfig() {
		this(new File(new File(System.getProperty("user.home")), ".gitconfig"));
	}
	private static class SingletonHolder {
		public static final GitConfig INSTANCE = new GitConfig();
	}
	public static GitConfig getInstance() {
		return SingletonHolder.INSTANCE;
	}
	public GitConfig(File f) {
		config = new FileBasedConfig(f, FS.detect());
		try {
			config.load();
		} catch (IOException e) {
			logger.error("Can't read global .gitignore file, even though it exists.");
		} catch (ConfigInvalidException e) {
			logger.error("Global .gitignore file is improperly formatted.");
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
