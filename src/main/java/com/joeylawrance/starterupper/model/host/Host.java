package com.joeylawrance.starterupper.model.host;

import java.net.URL;

/**
 * Defines an interface for authenticating with a host.
 * 
 * @author Joey Lawrance
 *
 */
public interface Host {
	/**
	 * Get the logo for this host.
	 */
	public URL getLogo();
	/**
	 * Get the host name.
	 * 
	 * @return The name of this host.
	 */
	public String getHostName();
	/**
	 * Get a description of the host.
	 * 
	 * @return A description of this host.
	 */
	public String getDescription();
	/**
	 * Set the password for this host.
	 * @param password
	 */
	public void setPassword(String password);
	/**
	 * Get the password for this host.
	 * @return
	 */
	public String getPassword();
	/**
	 * Set the username for this host.
	 * @param password
	 */
	public void setUsername(String username);
	/**
	 * Get the username for this host.
	 * @return
	 */
	public String getUsername();
	/**
	 * Sign up for the host.
	 * @return Whether the sign up was successful.
	 * @throws Exception
	 */
	public boolean signUp();
	/**
	 * Log into the host.
	 * @return Whether the login was successful.
	 * @throws Exception
	 */
	public boolean login();
	/**
	 * Whether we have logged in before.
	 * @return
	 */
	public boolean haveLoggedInBefore();
	/**
	 * Whether we are currently logged into the host.
	 * @return
	 */
	public boolean loggedIn();
	/**
	 * Log out of the host.
	 * @throws Exception
	 */
	public void logout();
	/**
	 * Reset the password for the host.
	 * @throws Exception
	 */
	public void forgotPassword();
}
