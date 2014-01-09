package com.joeylawrance.starterupper.model;

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
	 * Set the user name for this host.
	 * @param username
	 */
	public void setUsername(String username);
	/**
	 * Get the user name for this host.
	 * @return
	 */
	public String getUsername();
	/**
	 * Set the email address for this host.
	 * @param email
	 */
	public void setEmail(String email);
	/**
	 * Get the email address.
	 * @return
	 */
	public String getEmail();
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
	 * Set the first name for this host.
	 * @param firstname
	 */
	public void setFirstname(String firstname);
	/**
	 * Set the last name for this host.
	 * @param lastname
	 */
	public void setLastname(String lastname);
	/**
	 * Set the full name for this host.
	 * @param fullname
	 */
	public void setFullname(String fullname);
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
	/**
	 * Get the host name.
	 * 
	 * @return The name of this host.
	 */
	public String getHostName();
}
