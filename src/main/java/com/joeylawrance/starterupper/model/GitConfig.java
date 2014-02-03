package com.joeylawrance.starterupper.model;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper around ~/.gitconfig
 * 
 * @author Joey Lawrance
 *
 */
public class GitConfig {
	private static final Logger logger = LoggerFactory.getLogger(GitConfig.class);
	/**
	 * Profile information for git and project hosting services.
	 */
	public static enum Profile {
		// Only name and email are part of git
		firstname, lastname, name, defaultname, email, organization;
	}
	private static class SingletonHolder {
		public static final FileBasedConfig INSTANCE;
		static {
			INSTANCE = new FileBasedConfig(new File(new File(System.getProperty("user.home")), ".gitconfig"), FS.detect());
			try {
				INSTANCE.load();
				logger.info("Loaded global .gitconfig file.");
			} catch (IOException e) {
				logger.error("Can't read global .gitconfig file, even though it exists.");
			} catch (ConfigInvalidException e) {
				logger.error("Global .gitignore file is improperly formatted.");
			}
		}
	}
	private static FileBasedConfig getConfig() {
		return SingletonHolder.INSTANCE;
	}
	public GitConfig() {
		if (this.get(Profile.defaultname) == null) {
			this.put(Profile.defaultname, System.getProperty("user.name"));
		}
	}
	private String getCustomProperty(String section, String subsection, String key) {
		return getConfig().getString(section, subsection, key);
	}
	private void setCustomProperty(String section, String subsection, String key, String value) {
		// Don't save anything outside the purview of the git user model.
		// Save changes only if necessary.
		if (!value.equals(getCustomProperty(section, subsection, key))) {
			logger.info("git config --global user.{} {}", key, value);
			getConfig().setString(section, subsection, key, value);
			save();
		}
	}
	private void save() {
		try {
			getConfig().save();
			logger.info("Saved changes to .gitconfig.");
		} catch (Exception e) {
			logger.error("Sorry, another program has locked your .gitconfig file in {}.", System.getProperty("user.home"));
		}
	}
	/**
	 * Get the current value of user.key in ~/.gitconfig.
	 * @param key
	 * @return the value associated with the key in ~/.gitconfig
	 */
	public String get(Profile key) {
		return getCustomProperty("user", null, key.toString());
	}
	/**
	 * Put a user.key/value pair into the ~/.gitconfig
	 * @param key
	 * @param value
	 * @return the new value stored in ~/.gitconfig
	 */
	public String put(Profile key, String value) {
		if (value == null) return value;
		Event.getBus().post(new ConfigChanged(key, value));
		setCustomProperty("user", null, key.name(), value);
		return getCustomProperty("user", null, key.toString());
	}
	/**
	 * Post the saved configuration to the event bus.
	 */
	public void postConfiguration() {
		for (Profile key : Profile.values()) {
			this.put(key, this.get(key));
		}
	}
}
