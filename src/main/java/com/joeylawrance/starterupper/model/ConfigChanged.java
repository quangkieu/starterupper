package com.joeylawrance.starterupper.model;


/**
 * An event for whenever the git configuration changes.
 *
 */
public class ConfigChanged {
	public final GitConfigKey key;
	public final String value;
	public ConfigChanged(GitConfigKey key, String value) {
		this.key = key;
		this.value = value;
	}
}