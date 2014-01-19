package com.joeylawrance.starterupper.model.host;

import java.net.URL;

public class GenericGitHostRepositoryModel extends GenericGitHostModel implements
		GitHostRepositoryModel {

	String repositoryCreateURL;
	String collaboratorURL;
	
	public GenericGitHostRepositoryModel(String window, URL logo, String description) {
		super(window, logo, description);
	}
	
	public void setRepositoryCreateURL(String repositoryCreateURL) {
		this.repositoryCreateURL = repositoryCreateURL;
	}

	public void setCollaboratorURL(String collaboratorURL) {
		this.collaboratorURL = String.format(collaboratorURL, getUsername(), getMap().get("Repository name"));
	}

	@Override
	public void setPrivateRepositoryName(String name) {
		// By this time, hopefully we've set up the user's full name.
		getMap().put("Name", name);
		getMap().put("Repository name", name);
	}

	@Override
	public boolean createPrivateRepository() throws Exception {
		client.load(window,repositoryCreateURL);
		client.fillForm(window, getMap());
		client.submitForm(window,"Create");
		return !client.getPageUrl(window).equals(repositoryCreateURL);
	}

	@Override
	public boolean addCollaboratorToRepository(String username) throws Exception {
		client.load(window, collaboratorURL);
		// This sucks. I need to rethink how I do this.
		getMap().remove("Username");
		getMap().put("user|friend", username);
		client.fillForm(window, getMap());
		client.submitForm(window, "Add");
		return !client.getPageUrl(window).equals(collaboratorURL);
	}

}
