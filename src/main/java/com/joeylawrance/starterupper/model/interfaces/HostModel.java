package com.joeylawrance.starterupper.model.interfaces;

import java.net.URL;

/**
 * Defines an interface for authenticating with a host.
 * 
 * @author Joey Lawrance
 *
 */
public interface HostModel {
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
	public boolean signUp() throws Exception;
	/**
	 * Log into the host.
	 * @return Whether the login was successful.
	 * @throws Exception
	 */
	public boolean login() throws Exception;
	/**
	 * Log out of the host.
	 * @throws Exception
	 */
	public void logout() throws Exception;
	/**
	 * Reset the password for the host.
	 * @throws Exception
	 */
	public void forgotPassword() throws Exception;
	/**
	 * Determine whether the username is taken
	 * @return
	 */
	public boolean nameTaken();
}
