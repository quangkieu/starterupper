package com.joeylawrance.starterupper.model.host.impl;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.joeylawrance.starterupper.model.ConfigChanged;
import com.joeylawrance.starterupper.model.Event;
import com.joeylawrance.starterupper.model.GitConfig.Profile;
import com.joeylawrance.starterupper.model.host.GenericHost;
import com.joeylawrance.starterupper.model.host.HostAction;
import com.timgroup.jgravatar.GravatarDefaultImage;
import com.timgroup.jgravatar.GravatarDownloadException;
import com.timgroup.jgravatar.GravatarRating;

import helma.xmlrpc.secure.SecureXmlRpcClient;

public class Gravatar extends GenericHost {
	private final Logger logger = LoggerFactory.getLogger(Gravatar.class);
	private com.timgroup.jgravatar.Gravatar gravatar;
	private File profilePicture;

	/**
	 * Default constructor
	 */
	public Gravatar() {
		super("Gravatar", Gravatar.class.getResource("/Gravatar.png"), "Gravatar (part of Wordpress.com) hosts your profile picture across the web.");
		setURL(HostAction.login,"https://wordpress.com/wp-login.php");
		setURL(HostAction.signup,"https://signup.wordpress.com/signup/?user=1");
		setURL(HostAction.reset,"http://wordpress.com/wp-login.php?action=lostpassword");
		profilePicture = new File(System.getProperty("user.home"),".starterupper-profile-picture.png");
		Event.getBus().register(this);
	}

	/**
	 * Returns the profile picture from gravatar
	 * @return The profile picture from gravatar
	 */
	public File getProfilePicture() {
		return profilePicture;
	}
	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img)
	{
		if (img instanceof BufferedImage)
		{
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	/**
	 * Uploads the current profileImage to gravatar.
	 * @throws Exception In case it failed
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void uploadPicture() throws Exception {
		// XML-RPC sucks
		// https://en.gravatar.com/site/implement/xmlrpc/
		MessageDigest md = MessageDigest.getInstance("MD5");
		String thedigest = new String(md.digest(getMap().get("Email").toLowerCase().getBytes()));
		SecureXmlRpcClient rpc = new SecureXmlRpcClient(String.format("https://secure.gravatar.com/xmlrpc?user=%s",thedigest));
		byte[] imageData = FileUtils.readFileToByteArray(profilePicture);
		Vector parameters = new Vector();
		parameters.add(new String(Base64.encodeBase64(imageData)));
		parameters.add(0);
		parameters.add(getPassword());
		Object result = rpc.execute("grav.saveData", parameters);
	}
	@Subscribe
	public void checkForExistingGravatar(ConfigChanged event) {
		if (event.value == null) return;
		if (event.key == Profile.email) {
			// Now that we know their email, let's see if the user already has a Gravatar
			if (!profilePicture.exists()) {
				gravatar = new com.timgroup.jgravatar.Gravatar()
				.setSize(240)
				.setRating(GravatarRating.GENERAL_AUDIENCES)
				.setDefaultImage(GravatarDefaultImage.IDENTICON);

				// OpenJDK doesn't come with a native jpg encoder, so we'll just use png.
				byte[] bytes;
				try {
					bytes = gravatar.download(event.value);
					ImageIO.write(toBufferedImage(new ImageIcon(bytes).getImage()),
							"png",
							profilePicture);
				} catch (IOException e) {
					logger.error("Couldn't save gravatar to a file");
				} catch (GravatarDownloadException e) {
					logger.error(String.format("No gravatar found for %s.", event.value));
				}
			}
		}
	}
}
