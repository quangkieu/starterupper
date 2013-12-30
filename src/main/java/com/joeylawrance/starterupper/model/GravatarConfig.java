package com.joeylawrance.starterupper.model;

import java.awt.Image;

import javax.swing.ImageIcon;

import com.timgroup.jgravatar.Gravatar;
import com.timgroup.jgravatar.GravatarDefaultImage;
import com.timgroup.jgravatar.GravatarRating;

public class GravatarConfig {
	private Gravatar gravatar;
	private Image img;
	private String url;
	public GravatarConfig(String email) throws Exception {
		gravatar = new Gravatar()
			.setSize(100)
			.setRating(GravatarRating.GENERAL_AUDIENCES)
			.setDefaultImage(GravatarDefaultImage.IDENTICON);
		url = gravatar.getUrl(email);
		byte[] jpg = gravatar.download(email);
		img = new ImageIcon(jpg).getImage();
	}
	public Image getImage() {
		return img;
	}
	public String getURL() {
		return url;
	}
}
