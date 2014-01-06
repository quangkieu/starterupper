package com.joeylawrance.starterupper.model;

public class GravatarModel extends GenericHostModel {
	final String window = "gravatar";

	public GravatarModel() {
		super("Bitbucket");
		setLoginURL("https://wordpress.com/wp-login.php");
		setSignupURL("https://signup.wordpress.com/signup/?user=1");
		setResetURL("http://wordpress.com/wp-login.php?action=lostpassword");
	}
}
