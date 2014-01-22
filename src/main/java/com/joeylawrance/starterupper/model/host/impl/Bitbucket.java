package com.joeylawrance.starterupper.model.host.impl;

import com.joeylawrance.starterupper.model.host.GenericGitHostRepository;
import com.joeylawrance.starterupper.model.host.HostAction;

public class Bitbucket extends GenericGitHostRepository {

	public Bitbucket() {
		super("Bitbucket", Bitbucket.class.getResource("/bitbucket.png"), "Bitbucket hosts unlimited free private git repositories.");
		setHost("bitbucket.org");
		setURL(HostAction.login,"https://bitbucket.org/account/signin/");
		setURL(HostAction.logout,"https://bitbucket.org/account/signout/");
		setURL(HostAction.signup,"https://bitbucket.org/account/signup/");
		setURL(HostAction.reset,"https://bitbucket.org/account/password/reset/");
		setRepositoryCreateURL("https://bitbucket.org/repo/create");
		setCollaboratorURL("https://bitbucket.org/%s/%s/admin/access");
	}
	
	@Override
	public void setUsername(String username) {
		super.setUsername(username);
		setPublicKeyURL(String.format("https://bitbucket.org/account/user/%s/ssh-keys/", username));
	}
}
