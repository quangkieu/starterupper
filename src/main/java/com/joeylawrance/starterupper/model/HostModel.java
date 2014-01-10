package com.joeylawrance.starterupper.model;

import java.net.URL;

/**
 * Defines an interface for authenticating with a host.
 * 
 * @author Joey Lawrance
 *
 */
public interface HostModel extends UserModel {
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
	 * Sign up for the given host.
	 * @return Whether the sign up was successful.
	 * @throws Exception
	 */
	public boolean signUp() throws Exception;
	/**
	 * Log into the given host.
	 * @return Whether the login was successful.
	 * @throws Exception
	 */
	public boolean login() throws Exception;
	/**
	 * Reset the password for the given host.
	 * @throws Exception
	 */
	public void forgotPassword() throws Exception;
}
