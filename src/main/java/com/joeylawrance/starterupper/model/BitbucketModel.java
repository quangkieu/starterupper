package com.joeylawrance.starterupper.model;

public class BitbucketModel extends GenericGitHostRepositoryModel {

	public BitbucketModel() {
		super("Bitbucket");
		setLoginURL("https://bitbucket.org/account/signin/");
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
