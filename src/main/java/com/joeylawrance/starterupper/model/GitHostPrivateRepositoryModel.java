package com.joeylawrance.starterupper.model;

public interface GitHostPrivateRepositoryModel extends GitHostModel {
	public void setPrivateRepositoryName(String name);
	public void addCollaborator(String username);
	public boolean createPrivateRepository() throws Exception;
	public boolean shareRepositoryWithCollaborators() throws Exception;
}
