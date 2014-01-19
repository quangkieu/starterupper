package com.joeylawrance.starterupper.model.host.impl;

import com.joeylawrance.starterupper.model.host.GenericGitHostRepositoryModel;
import com.joeylawrance.starterupper.model.host.GenericHostModel;

public class Bitbucket extends GenericGitHostRepositoryModel {

	public Bitbucket() {
		super("Bitbucket", Bitbucket.class.getResource("/bitbucket.png"), "Bitbucket hosts unlimited free private git repositories.");
		setURL(GenericHostModel.HostAction.login,"https://bitbucket.org/account/signin/");
		setURL(GenericHostModel.HostAction.profile,"https://bitbucket.org/%s");
		setURL(GenericHostModel.HostAction.logout,"https://bitbucket.org/account/signout/");
		setURL(GenericHostModel.HostAction.signup,"https://bitbucket.org/account/signup/");
		setURL(GenericHostModel.HostAction.reset,"https://bitbucket.org/account/password/reset/");
		setRepositoryCreateURL("https://bitbucket.org/repo/create");
		setCollaboratorURL("https://bitbucket.org/%s/%s/admin/access");
	}
	
	@Override
	public void setUsername(String username) {
		super.setUsername(username);
		setPublicKeyURL(String.format("https://bitbucket.org/account/user/%s/ssh-keys/", username));
	}
}
