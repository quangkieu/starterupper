package com.joeylawrance.starterupper.model;

/**
 * Create a private repository and add collaborators.
 * 
 * @author Joey Lawrance
 *
 */
public interface GitHostRepositoryModel extends GitHostModel {
	public void setPrivateRepositoryName(String name);
	public void addCollaborator(String username);
	public boolean createPrivateRepository() throws Exception;
	public boolean shareRepositoryWithCollaborators() throws Exception;
}
