package com.joeylawrance.starterupper.model;

import com.joeylawrance.starterupper.model.GitConfig.Profile;

public class ConfigChanged {
	public Profile key;
	public String value;
	public ConfigChanged(Profile key, String value) {
		this.key = key;
		this.value = value;
	}
}