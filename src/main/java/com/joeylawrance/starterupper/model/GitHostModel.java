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
	/**
	 * Set the token data structure for authenticating with the project host.
	 * 
	 * @param p Properties from which to read/write to.
	 */
	public void setToken(Properties p);
	/**
	 * Fetch the token from the project host.
	 * 
	 * @throws Exception
	 */
	public void fetchToken() throws Exception;
	/**
	 * Does the token work?
	 * 
	 * @return Whether the token works for authentication.
	 */
	public boolean canAuthenticateWithToken();
}
