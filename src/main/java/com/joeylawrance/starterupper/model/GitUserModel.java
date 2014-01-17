package com.joeylawrance.starterupper.model;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joeylawrance.starterupper.model.interfaces.UserModel;

/**
 * A Java Bean for user data, backed by the global git configuration.
 * 
 * @author Joey Lawrance
 *
 */
public class GitUserModel implements UserModel {
	static final Logger logger = LoggerFactory.getLogger(GitUserModel.class);
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
	@Override
	public String getFullname() {
		return getCustomProperty("user", null, "name");
	}
	@Override
	public String getEmail() {
		return getCustomProperty("user", null, "email");
	}
	@Override
	public String getUsername() {
		String storedDefault = getCustomProperty("user", null, "defaultname");
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
		return getConfig().getString(section, subsection, key);
	}
	public void setCustomProperty(String section, String subsection, String key, String value) {
		// Save changes only if necessary.
		if (!getCustomProperty(section, subsection, key).equals(value)) {
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
	@Override
	public void setFirstname(String firstname) {
		setCustomProperty("user", null, "firstname", firstname);
	}
	@Override
	public String getFirstname() {
		return getCustomProperty("user", null, "firstname");
	}
	@Override
	public void setLastname(String lastname) {
		setCustomProperty("user", null, "lastname", lastname);		
	}
	@Override
	public String getLastname() {
		return getCustomProperty("user", null, "lastname");
	}
	@Override
	public String getByName(String name) {
		// Ugly hack
		if (name.equals("Full name")) return getFullname();
		if (name.equals("First name")) return getFirstname();
		if (name.equals("Last name")) return getLastname();
		if (name.equals("Email address")) return getEmail();
		if (name.equals("Username")) return getUsername();
		return null;
	}
	@Override
	public void setByName(String name, String value) {
		// Another ugly hack
		if (name.equals("Full name")) setFullname(value);
		if (name.equals("First name")) setFirstname(value);
		if (name.equals("Last name")) setLastname(value);
		if (name.equals("Email address")) setEmail(value);
		if (name.equals("Username")) setUsername(value);
	}
}
