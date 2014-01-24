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
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("Name", repositoryName);
			client.fillForm(window, map);
			client.submitForm(window,"Create");
			logger.info("Created repository '{}' on {}", repositoryName, window);
		} catch (FailingHttpStatusCodeException e) {
			logger.error("Unable to create private repository '{}' on {}", repositoryName, window);
			return false;
		} catch (IOException e) {
			logger.error("Unable to create private repository '{}' on {}", repositoryName, window);
			return false;
		}
		return true; //!client.getPageUrl(window).equals(repositoryCreateURL);
	}

	@Override
	public boolean addCollaboratorToRepository(String username) {
		try {
			client.load(window, String.format(collaboratorURL, getUsername(), getPrivateRepositoryName()));
			System.out.println(client.getPageUrl(window));
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("user|friend", username);
			client.fillForm(window, map);
			client.submitForm(window, "Add");
			logger.info("Added collaborator {} to {} on {}", username, getPrivateRepositoryName(), window);
		} catch (FailingHttpStatusCodeException e) {
			logger.error("Unable to add collaborator {} to {} on {}", username, getPrivateRepositoryName(), window);
		} catch (IOException e) {
			logger.error("Unable to add collaborator {} to {} on {}", username, getPrivateRepositoryName(), window);
			return false;
		}
		return true; //!client.getPageUrl(window).equals(collaboratorURL);
	}

	@Override
	public String getRepositoryURL() {
		return String.format("git@%s:%s/%s.git",getHost(), getUsername(), getPrivateRepositoryName());
	}

	@Override
	public String getRepositoryWebPage() {
		return String.format("https://%s/%s/%s",getHost(), getUsername(), getPrivateRepositoryName());
	}

}
