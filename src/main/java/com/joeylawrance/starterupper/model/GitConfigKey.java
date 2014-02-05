package com.joeylawrance.starterupper.model;

/**
 * Profile information for git and project hosting services.
 */
public enum GitConfigKey {
	// Only name and email are part of git
	firstname("First name"), lastname("Last name"), name("Name"), defaultname("Username"), email("Email"), organization("School name");
	private final String formElement;
	private GitConfigKey(String formElement) {
		this.formElement = formElement;
	}
	public String formElement() {
		return formElement;
	}
	public static GitConfigKey getByName(String name) {
		for (GitConfigKey key : GitConfigKey.values()) {
			if (key.name().equals(name)) {
				return key;
			}
		}
		return null;
	}
}