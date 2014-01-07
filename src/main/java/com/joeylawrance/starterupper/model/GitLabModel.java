package com.joeylawrance.starterupper.model;

import java.util.Properties;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class GitLabModel extends GenericGitHostModel {
	
	public GitLabModel() {
		super("gitlab");
		setLoginURL("https://gitlab.com/users/sign_in");
		setSignupURL("https://gitlab.com/users/sign_up");
		setResetURL("https://gitlab.com/users/password/new");
		setPublicKeyURL("https://gitlab.com/profile/keys/new");
	}
	

	public void saveToken(Properties p) throws Exception {
		final String tokenPage = "https://gitlab.com/profile/account";
		client.load(window, tokenPage);
		
		HtmlPage page = client.getPageInWindow(window);
		p.setProperty("gitlab.token", page.getElementById("token").getAttribute("value"));
	}

	public void createPrivateRepository(String name) throws Exception {
		// TODO Auto-generated method stub
		// https://gitlab.com/projects/new
	}

	public void shareRepoWith(String name) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void watchRepo(String repoURL) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public boolean signUp(String username, String password) throws Exception {
		super.signUp();
		
		HtmlPage p = client.getPageInWindow(window);
		HtmlElement accept_terms = (HtmlElement) p.getElementById("user_accept_terms");
		accept_terms.click();
		
		client.submitForm(window,"Sign up");
		
		return !signupURL.equals(client.getPageUrl(window));
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
