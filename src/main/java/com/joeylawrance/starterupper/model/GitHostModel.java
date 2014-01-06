package com.joeylawrance.starterupper.model;

import java.util.Properties;

public interface GitHostModel extends HostModel {
	public void saveToken(Properties p) throws Exception;
	public void sharePublicKey() throws Exception;
	public void setPublicKey(String key);
}
