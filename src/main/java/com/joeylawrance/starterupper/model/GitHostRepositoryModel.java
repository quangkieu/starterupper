package com.joeylawrance.starterupper.model;

/**
 * Create a private repository and add collaborators on the project host.
 * 
 * @author Joey Lawrance
 *
 */
public interface GitHostRepositoryModel extends GitHostModel {
	public void setPrivateRepositoryName(String name);
	public boolean addCollaboratorToRepository(String username) throws Exception;
	public boolean createPrivateRepository() throws Exception;
}
