package com.joeylawrance.starterupper.model;

public class GitHubModel extends GenericGitHostRepositoryModel {

	public GitHubModel() {
		super("GitHub", GitHubModel.class.getResource("/github.png"));
		setLoginURL("https://github.com/login");
		setSignupURL("https://github.com/join");
		setResetURL("https://github.com/sessions/forgot_password");
		setPublicKeyURL("https://github.com/settings/ssh");
		setRepositoryCreateURL("https://github.com/new");
		setCollaboratorURL("https://github.com/%s/%s/settings/collaboration");
		
		// https://github.com/session
		// Authentication Code
		// Verify
	}
}
