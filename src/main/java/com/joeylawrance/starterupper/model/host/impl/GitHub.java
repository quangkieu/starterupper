package com.joeylawrance.starterupper.model.host.impl;

import com.joeylawrance.starterupper.model.host.GenericGitHostRepository;
import com.joeylawrance.starterupper.model.host.HostAction;

public class GitHub extends GenericGitHostRepository {

	public GitHub() {
		super("GitHub", GitHub.class.getResource("/github.png"), "GitHub hosts five free private repositories for students.");
		setHost("github.com");
		setURL(HostAction.login,"https://github.com/login");
		setURL(HostAction.logout,"https://github.com/logout");
		setURL(HostAction.signup,"https://github.com/join");
		setURL(HostAction.reset,"https://github.com/sessions/forgot_password");
		setPublicKeyURL("https://github.com/settings/ssh");
		setRepositoryCreateURL("https://github.com/new");
		setCollaboratorURL("https://github.com/%s/%s/settings/collaboration");
		
		// https://github.com/session
		// Authentication Code
		// Verify
	}
}
