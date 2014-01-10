package com.joeylawrance.starterupper.model;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Java Bean for user data, backed by the global git configuration.
 * 
 * @author Joey Lawrance
 *
 */
public class GitUserModel implements UserModel {
	final Logger logger = LoggerFactory.getLogger(GitUserModel.class);
	private GitUserModel() {
		this(new File(new File(System.getProperty("user.home")), ".gitconfig"));
	}
	private static class SingletonHolder {
		public static final GitUserModel INSTANCE = new GitUserModel();
	}
	public static GitUserModel getInstance() {
		return SingletonHolder.INSTANCE;
	}
	public GitUserModel(File f) {
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
	@Override
	public String getFullname() {
		return config.getString("user", null, "name");
	}
	@Override
	public String getEmail() {
		return config.getString("user", null, "email");
	}
	@Override
	public String getUsername() {
		String storedDefault = config.getString("user", null, "defaultname");
		return (storedDefault == null) ? System.getProperty("user.name") : storedDefault;
	}
	@Override
	public void setFullname(String name) {
		setCustomProperty("user", null, "name", name);
	}
	@Override
	public void setEmail(String email) {
		setCustomProperty("user", null, "email", email);
	}
	@Override
	public void setUsername(String username) {
		setCustomProperty("user", null, "defaultname", username);
	}
	public String getCustomProperty(String section, String subsection, String key) {
		return config.getString(section, subsection, key);
	}
	public void setCustomProperty(String section, String subsection, String key, String value) {
		config.setString(section, subsection, key, value);
		save();
	}
	private void save() {
		if (config.isOutdated()) {
			logger.error(String.format("Sorry, another program has updated your .gitconfig file in %s.", System.getProperty("user.home")));
		}
		try {
			config.save();
		} catch (Exception e) {
			logger.error(String.format("Sorry, another program has locked your .gitconfig file in %s.", System.getProperty("user.home")));
		}
	}
	@Override
	public void setFirstname(String firstname) {
		setCustomProperty("user", null, "firstname", firstname);
	}
	@Override
	public String getFirstname() {
		return config.getString("user", null, "firstname");
	}
	@Override
	public void setLastname(String lastname) {
		setCustomProperty("user", null, "lastname", lastname);		
	}
	@Override
	public String getLastname() {
		return config.getString("user", null, "lastname");
	}
}
