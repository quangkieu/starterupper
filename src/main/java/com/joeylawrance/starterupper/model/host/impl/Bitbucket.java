package com.joeylawrance.starterupper.model.host.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.joeylawrance.starterupper.model.host.GenericGitHostRepository;
import com.joeylawrance.starterupper.model.host.HostAction;

public class Bitbucket extends GenericGitHostRepository {
	private final Logger logger = LoggerFactory.getLogger(Bitbucket.class);

	public Bitbucket() {
		super("Bitbucket", Bitbucket.class.getResource("/bitbucket.png"), "Bitbucket hosts unlimited free private git repositories.");
		setHost("bitbucket.org");
		setURL(HostAction.login,"https://bitbucket.org/account/signin/");
		setURL(HostAction.logout,"https://bitbucket.org/account/signout/");
		setURL(HostAction.signup,"https://bitbucket.org/account/signup/");
		setURL(HostAction.reset,"https://bitbucket.org/account/password/reset/");
		setRepositoryCreateURL("https://bitbucket.org/repo/create");
		setCollaboratorURL("https://bitbucket.org/%s/%s/admin/access");
	}
	
	@Override
	public void setUsername(String username) {
		super.setUsername(username);
		setPublicKeyURL(String.format("https://bitbucket.org/account/user/%s/ssh-keys/", username));
	}
	
	@Override
	public boolean signUp() {
		super.signUp();
		
		HtmlPage p = getClient().getPageInWindow(getHostName());
		
		HtmlImage challenge = (HtmlImage) p.getElementById("recaptcha_challenge_image");
		try {
			ImageReader reader = challenge.getImageReader();
			BufferedImage captcha = reader.read(reader.getMinIndex());
			
		} catch (IOException e) {
			logger.error("Unable to load captcha.",e);
		}
		

		
		System.out.println(p.asText());

//		getClient().submitForm(getHostName(),"Sign up");
		
//		return !getURL(HostAction.signup).equals(getClient().getPageUrl(getHostName()));
		return false;
	}
}
