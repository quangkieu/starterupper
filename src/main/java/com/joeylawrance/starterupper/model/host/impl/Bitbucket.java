package com.joeylawrance.starterupper.model.host.impl;

import com.joeylawrance.starterupper.model.host.GenericGitHostRepositoryModel;

public class Bitbucket extends GenericGitHostRepositoryModel {

	public Bitbucket() {
		super("Bitbucket", Bitbucket.class.getResource("/bitbucket.png"), "Bitbucket hosts unlimited free private git repositories.");
		setLoginURL("https://bitbucket.org/account/signin/");
		setProfileURL("https://bitbucket.org/%s");
		setLogoutURL("https://bitbucket.org/account/signout/");
		setSignupURL("https://bitbucket.org/account/signup/");
		setResetURL("https://bitbucket.org/account/password/reset/");
		setRepositoryCreateURL("https://bitbucket.org/repo/create");
		setCollaboratorURL("https://bitbucket.org/%s/%s/admin/access");
	}
	
	@Override
	public void setUsername(String username) {
		super.setUsername(username);
		setPublicKeyURL(String.format("https://bitbucket.org/account/user/%s/ssh-keys/", username));
	}
}
