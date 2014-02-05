package com.joeylawrance.starterupper.model;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
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
	private static class SingletonHolder {
		public static final FileBasedConfig INSTANCE;
		static {
			INSTANCE = new FileBasedConfig(new File(new File(System.getProperty("user.home")), ".gitconfig"), FS.detect());
			try {
				INSTANCE.load();
				logger.info("Loaded global .gitconfig file.");
			} catch (IOException e) {
				logger.error("Can't read global .gitconfig file, even though it exists.", e);
			} catch (ConfigInvalidException e) {
				logger.error("Global .gitignore file is improperly formatted.", e);
			}
		}
	}
	private static FileBasedConfig getConfig() {
		return SingletonHolder.INSTANCE;
	}
	public GitConfig() {
		if (this.get(GitConfigKey.defaultname) == null) {
			this.put(GitConfigKey.defaultname, System.getProperty("user.name"));
		}
	}
	private String getCustomProperty(String section, String subsection, String key) {
		return getConfig().getString(section, subsection, key);
	}
	private void setCustomProperty(String section, String subsection, GitConfigKey key, String value) {
		// Don't save anything outside the purview of the git user model.
		// Save changes only if necessary.
		Event.getBus().post(new ConfigChanged(key, value));
		if (!value.equals(getCustomProperty(section, subsection, key.name()))) {
			logger.info("git config --global user.{} \"{}\"", key, value);
			getConfig().setString(section, subsection, key.name(), value);
			save();
		}
	}
	private void save() {
		try {
			getConfig().save();
			logger.info("Saved changes to .gitconfig.");
		} catch (Exception e) {
			logger.error("Sorry, another program has locked your .gitconfig file in {}.", System.getProperty("user.home"), e);
		}
	}
	/**
	 * Get the current value of user.key in ~/.gitconfig.
	 * @param key
	 * @return the value associated with the key in ~/.gitconfig
	 */
	public final String get(GitConfigKey key) {
		return getCustomProperty("user", null, key.toString());
	}
	/**
	 * Put a user.key/value pair into the ~/.gitconfig
	 * @param key
	 * @param value
	 * @return the new value stored in ~/.gitconfig
	 */
	public final String put(GitConfigKey key, String value) {
		if (value == null) {
			return value;
		}
		setCustomProperty("user", null, key, value);
		// Set the user.name from "firstname lastname"
		if (key.equals(GitConfigKey.firstname) || key.equals(GitConfigKey.lastname)) {
			setCustomProperty("user", null, GitConfigKey.name, StringUtils.join(get(GitConfigKey.firstname), " ", get(GitConfigKey.lastname)));
		}
		return get(key);
	}
	/**
	 * Post the saved configuration to the event bus.
	 */
	public void postConfiguration() {
		for (GitConfigKey key : GitConfigKey.values()) {
			this.put(key, this.get(key));
		}
	}
	public String get(String name) {
		return getCustomProperty("user", null, name);
	}
	public void put(String name, String value) {
		put(GitConfigKey.getByName(name),value);
	}
}
