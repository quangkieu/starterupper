package com.joeylawrance.starterupper.model;

import java.util.Properties;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class GitLabModel extends GenericGitHostRepositoryModel {
	
	public GitLabModel() {
		super("GitLab Cloud", GitLabModel.class.getResource("/gitlab.png"), "GitLab Cloud is an open source git project host.");
		setLoginURL("https://gitlab.com/users/sign_in");
		setLogoutURL("https://gitlab.com/users/sign_out");
		setProfileURL("https://gitlab.com/u/%s");
		setSignupURL("https://gitlab.com/users/sign_up");
		setResetURL("https://gitlab.com/users/password/new");
		setPublicKeyURL("https://gitlab.com/profile/keys/new");
		setRepositoryCreateURL("https://gitlab.com/projects/new");
		setCollaboratorURL("https://gitlab.com/%s/%s/team_members/new");
	}

	public void saveToken(Properties p) throws Exception {
		final String tokenPage = "https://gitlab.com/profile/account";
		client.load(window, tokenPage);
		
		HtmlPage page = client.getPageInWindow(window);
		p.setProperty("gitlab.token", page.getElementById("token").getAttribute("value"));
	}

	public boolean signUp(String username, String password) throws Exception {
		super.signUp();
		
		HtmlPage p = client.getPageInWindow(window);
		HtmlElement accept_terms = (HtmlElement) p.getElementById("user_accept_terms");
		accept_terms.click();
		
		client.submitForm(window,"Sign up");
		
		return !signupURL.equals(client.getPageUrl(window));
	}
}
