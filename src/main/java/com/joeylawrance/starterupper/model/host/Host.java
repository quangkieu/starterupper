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
	URL getLogo();
	/**
	 * Get the host name.
	 * 
	 * @return The name of this host.
	 */
	String getHostName();
	/**
	 * Get a description of the host.
	 * 
	 * @return A description of this host.
	 */
	String getDescription();
	/**
	 * Set the password for this host.
	 * @param password
	 */
	void setPassword(String password);
	/**
	 * Get the password for this host.
	 * @return
	 */
	String getPassword();
	/**
	 * Set the username for this host.
	 * @param password
	 */
	void setUsername(String username);
	/**
	 * Get the username for this host.
	 * @return
	 */
	String getUsername();
	/**
	 * Sign up for the host.
	 * @return Whether the sign up was successful.
	 */
	boolean signUp();
	/**
	 * Log into the host.
	 * @return Whether the login was successful.
	 */
	boolean login();
	/**
	 * Whether we have logged in before.
	 * @return
	 */
	boolean haveLoggedInBefore();
	/**
	 * Whether we are currently logged into the host.
	 * @return
	 */
	boolean loggedIn();
	/**
	 * Log out of the host.
	 */
	void logout();
	/**
	 * Reset the password for the host.
	 */
	void forgotPassword();
}
