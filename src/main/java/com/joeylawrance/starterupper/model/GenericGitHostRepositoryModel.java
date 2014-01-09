package com.joeylawrance.starterupper.model;

import java.net.URL;

public class GenericGitHostRepositoryModel extends GenericGitHostModel implements
		GitHostRepositoryModel {

	String repositoryCreateURL;
	String collaboratorURL;
	
	public GenericGitHostRepositoryModel(String window, URL logo) {
		super(window, logo);
	}
	
	public void setRepositoryCreateURL(String repositoryCreateURL) {
		this.repositoryCreateURL = repositoryCreateURL;
	}

	public void setCollaboratorURL(String collaboratorURL) {
		this.collaboratorURL = String.format(collaboratorURL, getUsername(), map.get("Repository name"));
	}

	@Override
	public void setPrivateRepositoryName(String name) {
		// By this time, hopefully we've set up the user's full name.
		map.put("Name", name);
		map.put("Repository name", name);
	}

	@Override
	public boolean createPrivateRepository() throws Exception {
		client.load(window,repositoryCreateURL);
		client.fillForm(window, map);
		client.submitForm(window,"Create");
		return !client.getPageUrl(window).equals(repositoryCreateURL);
	}

	@Override
	public boolean addCollaboratorToRepository(String username) throws Exception {
		client.load(window, collaboratorURL);
		// This sucks. I need to rethink how I do this.
		map.remove("Username");
		map.put("user|friend", username);
		client.fillForm(window, map);
		client.submitForm(window, "Add");
		return !client.getPageUrl(window).equals(collaboratorURL);
	}

}
