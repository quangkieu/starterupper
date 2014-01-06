package com.joeylawrance.starterupper.model;

import java.util.Properties;

public class BitbucketModel extends GenericGitHostModel implements GitHostPrivateRepositoryModel {

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
	
	public void watchRepo(String repoURL) {
		// TODO Auto-generated method stub

	}

	public void saveToken(Properties p) throws Exception {
		// TODO Auto-generated method stub
		// https://bitbucket.org/account/user/lawrancej/api
	}

	public boolean signUp(String username, String password) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPrivateRepositoryName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCollaborator(String username) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean createPrivateRepository() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shareRepositoryWithCollaborators() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
}
