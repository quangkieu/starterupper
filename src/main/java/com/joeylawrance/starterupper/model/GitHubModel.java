package com.joeylawrance.starterupper.model;

import java.util.Properties;

public class GitHubModel extends GenericGitHostModel {

	public GitHubModel() {
		super("GitHub");
		setLoginURL("https://github.com/login");
		setSignupURL("https://github.com/join");
		setResetURL("https://github.com/sessions/forgot_password");
		setPublicKeyURL("https://github.com/settings/ssh");
	}


	public void watchRepo(String repoURL) throws Exception {
		// TODO Auto-generated method stub

	}

	public void saveToken(Properties p) throws Exception {
		// TODO Auto-generated method stub
		// https://github.com/settings/applications
	}

	@Override
	public boolean canAuthenticateWithToken() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void setToken(Properties p) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void fetchToken() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
