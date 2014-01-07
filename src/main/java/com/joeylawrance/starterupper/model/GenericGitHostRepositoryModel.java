package com.joeylawrance.starterupper.model;

public class GenericGitHostRepositoryModel extends GenericGitHostModel implements
		GitHostRepositoryModel {

	String repositoryCreateURL;

	public GenericGitHostRepositoryModel(String window) {
		super(window);
	}
	
	public void setRepositoryCreateURL(String repositoryCreateURL) {
		this.repositoryCreateURL = repositoryCreateURL;
	}


	@Override
	public void setPrivateRepositoryName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCollaborator(String username) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean createPrivateRepository() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shareRepositoryWithCollaborators() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void fetchToken() throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean canAuthenticateWithToken() {
		// TODO Auto-generated method stub
		return false;
	}

}
