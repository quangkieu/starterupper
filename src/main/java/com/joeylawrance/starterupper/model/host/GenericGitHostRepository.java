package com.joeylawrance.starterupper.model.host;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class GenericGitHostRepository extends GenericGitHost implements
		GitHostRepository {
	private final Logger logger = LoggerFactory.getLogger(GenericGitHostRepository.class);

	String repositoryCreateURL;
	String collaboratorURL;
	
	public GenericGitHostRepository(String window, URL logo, String description) {
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
	public boolean createPrivateRepository() {
		try {
			client.load(window,repositoryCreateURL);
			client.fillForm(window, getMap());
			client.submitForm(window,"Create");
		} catch (FailingHttpStatusCodeException | IOException e) {
			logger.error("Unable to create private repository {} on {}", getMap().get("Name"), window);
			return false;
		}
		return !client.getPageUrl(window).equals(repositoryCreateURL);
	}

	@Override
	public boolean addCollaboratorToRepository(String username) {
		try {
			client.load(window, collaboratorURL);
			// This sucks. I need to rethink how I do this.
			// FIXME: Ahem: just use a different map, populated from the existing map. duh
			getMap().remove("Username");
			getMap().put("user|friend", username);
			client.fillForm(window, getMap());
			client.submitForm(window, "Add");
		} catch (FailingHttpStatusCodeException | IOException e) {
			logger.error("Unable to add collaborator {} to private repository {} on {}", username, getMap().get("Name"), window);
			return false;
		}
		return !client.getPageUrl(window).equals(collaboratorURL);
	}

}
