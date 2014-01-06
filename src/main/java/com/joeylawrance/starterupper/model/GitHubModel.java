package com.joeylawrance.starterupper.model;

import java.util.Properties;

public class GitHubModel extends GenericGitHostModel implements GitHostPrivateRepositoryModel {

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

	@Override
	public void saveToken(Properties p) throws Exception {
		// TODO Auto-generated method stub
		// https://github.com/settings/applications
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
