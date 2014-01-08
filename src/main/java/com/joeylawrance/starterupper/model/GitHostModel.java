package com.joeylawrance.starterupper.model;

import java.util.Properties;

/**
 * Defines one-time git project hosting setup methods.
 * 
 * @author Joey Lawrance
 * @see GitHostRepositoryModel for per-repository setup methods.
 */
public interface GitHostModel extends HostModel {
	/**
	 * Set the public key.
	 * 
	 * @param key
	 */
	public void setPublicKey(String key);
	/**
	 * Share the user's public key with the project host.
	 * 
	 * Call setPublicKey first.
	 * 
	 * @throws Exception if the key is not set.
	 */
	public void sharePublicKey() throws Exception;
}
