package com.joeylawrance.starterupper.model.host;


/**
 * Create a private repository and add collaborators on the project host.
 * 
 * @author Joey Lawrance
 *
 */
public interface GitHostRepository extends GitHost {
	public void setPrivateRepositoryName(String name);
	public boolean addCollaboratorToRepository(String username);
	public boolean createPrivateRepository();
}
