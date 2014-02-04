package com.joeylawrance.starterupper.model;

/**
 * Profile information for git and project hosting services.
 */
public enum GitConfigKey {
	// Only name and email are part of git
	firstname, lastname, name, defaultname, email, organization;
	public static GitConfigKey getByName(String name) {
		for (GitConfigKey key : GitConfigKey.values()) {
			if (key.name().equals(name)) {
				return key;
			}
		}
		return null;
	}
}