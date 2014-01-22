package com.joeylawrance.starterupper.model.host;


/**
 * Create a private repository and add collaborators on the project host.
 * 
 * @author Joey Lawrance
 *
 */
public interface GitHostRepository extends GitHost {
	/**
	 * Get the SSH url for the repository.
	 * @return
	 */
	String getRepositoryURL(); 
	/**
	 * Set the name of the private repository.
	 * @param name
	 */
	void setPrivateRepositoryName(String name);
	/**
	 * Add a collaborator to this repository.
	 * @param username
	 * @return
	 */
	boolean addCollaboratorToRepository(String username);
	/**
	 * Create the private repository.
	 * @return
	 */
	boolean createPrivateRepository();
}
