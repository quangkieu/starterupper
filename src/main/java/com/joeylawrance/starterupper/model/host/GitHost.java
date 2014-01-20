package com.joeylawrance.starterupper.model.host;


/**
 * Defines one-time git project hosting setup methods.
 * 
 * @author Joey Lawrance
 * @see GitHostRepository for per-repository setup methods.
 */
public interface GitHost extends Host {
	/**
	 * Share the user's public key with the project host.
	 */
	public void sharePublicKey();
	/**
	 * Did we share the public key yet?
	 * @return Whether we shared the public key yet.
	 */
	public boolean testLogin();
}
