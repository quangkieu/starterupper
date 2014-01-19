package com.joeylawrance.starterupper.model.host.impl;

import com.joeylawrance.starterupper.model.host.GenericGitHostRepositoryModel;
import com.joeylawrance.starterupper.model.host.GenericHostModel;

public class GitHub extends GenericGitHostRepositoryModel {

	public GitHub() {
		super("GitHub", GitHub.class.getResource("/github.png"), "GitHub hosts five free private repositories for students.");
		setURL(GenericHostModel.HostAction.login,"https://github.com/login");
		setURL(GenericHostModel.HostAction.logout,"https://github.com/logout");
		setURL(GenericHostModel.HostAction.profile,"https://github.com/%s");
		setURL(GenericHostModel.HostAction.signup,"https://github.com/join");
		setURL(GenericHostModel.HostAction.reset,"https://github.com/sessions/forgot_password");
		setPublicKeyURL("https://github.com/settings/ssh");
		setRepositoryCreateURL("https://github.com/new");
		setCollaboratorURL("https://github.com/%s/%s/settings/collaboration");
		
		// https://github.com/session
		// Authentication Code
		// Verify
	}
}
