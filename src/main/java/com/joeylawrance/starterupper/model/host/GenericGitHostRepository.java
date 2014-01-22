package com.joeylawrance.starterupper.model.host;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class GenericGitHostRepository extends GenericGitHost implements
		GitHostRepository {
	private final Logger logger = LoggerFactory.getLogger(GenericGitHostRepository.class);

	private String repositoryCreateURL;
	private String collaboratorURL;
	private String repositoryName;
	
	public GenericGitHostRepository(String window, URL logo, String description) {
		super(window, logo, description);
	}
	
	public void setRepositoryCreateURL(String repositoryCreateURL) {
		this.repositoryCreateURL = repositoryCreateURL;
	}

	public void setCollaboratorURL(String collaboratorURL) {
		this.collaboratorURL = collaboratorURL;
	}

	@Override
	public void setPrivateRepositoryName(String name) {
		repositoryName = name;
	}

	private String getPrivateRepositoryName() {
		return repositoryName;
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
			client.load(window, String.format(collaboratorURL, getUsername(), getPrivateRepositoryName()));
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("user|friend", username);
			client.fillForm(window, map);
			client.submitForm(window, "Add");
		} catch (FailingHttpStatusCodeException | IOException e) {
			logger.error("Unable to add collaborator {} to private repository {} on {}", getUsername(), getPrivateRepositoryName(), window);
			return false;
		}
		return !client.getPageUrl(window).equals(collaboratorURL);
	}

	@Override
	public String getRepositoryURL() {
		return String.format("git@%s:%s/%s",getHost(), getUsername(), getPrivateRepositoryName());
	}

}
