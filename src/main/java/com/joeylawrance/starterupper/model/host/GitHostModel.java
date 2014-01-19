package com.joeylawrance.starterupper.model.host;


/**
 * Defines one-time git project hosting setup methods.
 * 
 * @author Joey Lawrance
 * @see GitHostRepositoryModel for per-repository setup methods.
 */
public interface GitHostModel extends HostModel {
	/**
	 * Share the user's public key with the project host.
	 * 
	 * Call setPublicKey first.
	 * 
	 * @throws Exception if the key is not set.
	 */
	public void sharePublicKey() throws Exception;
}
