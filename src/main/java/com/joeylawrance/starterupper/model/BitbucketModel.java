package com.joeylawrance.starterupper.model;

public class BitbucketModel extends GenericGitHostModel {

	public BitbucketModel() {
		super("Bitbucket");
		setLoginURL("https://bitbucket.org/account/signin/");
		setSignupURL("https://bitbucket.org/account/signup/");
		setResetURL("https://bitbucket.org/account/password/reset/");
	}
	
	@Override
	public void setUsername(String username) {
		super.setUsername(username);
		setPublicKeyURL(String.format("https://bitbucket.org/account/user/%s/ssh-keys/", username));
	}
	
	@Override
	public boolean canAuthenticateWithToken() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void fetchToken() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
