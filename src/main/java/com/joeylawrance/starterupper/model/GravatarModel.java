package com.joeylawrance.starterupper.model;

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

import com.timgroup.jgravatar.Gravatar;
import com.timgroup.jgravatar.GravatarDefaultImage;
import com.timgroup.jgravatar.GravatarRating;

import helma.xmlrpc.secure.SecureXmlRpcClient;

public class GravatarModel extends GenericHostModel {
	private final Logger logger = LoggerFactory.getLogger(GravatarModel.class);
	private Gravatar gravatar;
	private File profilePicture;
	public GravatarModel() {
		super("Gravatar", GravatarModel.class.getResource("/Gravatar.png"), "Gravatar hosts your profile picture across the web.");
		setLoginURL("https://wordpress.com/wp-login.php");
		setSignupURL("https://signup.wordpress.com/signup/?user=1");
		setResetURL("http://wordpress.com/wp-login.php?action=lostpassword");
		profilePicture = new File(System.getProperty("user.home"),"me.jpg");
	}
	public File getProfilePicture() {
		return profilePicture;
	}
	@Override
	public void setEmail(String email) {
		super.setEmail(email);
		gravatar = new Gravatar()
		.setSize(240)
		.setRating(GravatarRating.GENERAL_AUDIENCES)
		.setDefaultImage(GravatarDefaultImage.IDENTICON);
		byte[] jpg = gravatar.download(email);
		try {
			ImageIO.write(toBufferedImage(new ImageIcon(jpg).getImage()),
					"jpg",
					profilePicture);
		} catch (IOException e) {
			logger.error("Unable to save gravatar to file.");
		}
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void uploadPicture() throws Exception {
		// XML-RPC sucks
		// https://en.gravatar.com/site/implement/xmlrpc/
		MessageDigest md = MessageDigest.getInstance("MD5");
		String thedigest = new String(md.digest(this.getEmail().toLowerCase().getBytes()));
		SecureXmlRpcClient rpc = new SecureXmlRpcClient(String.format("https://secure.gravatar.com/xmlrpc?user=%s",thedigest));
		byte[] imageData = FileUtils.readFileToByteArray(profilePicture);
		Vector parameters = new Vector();
		parameters.add(new String(Base64.encodeBase64(imageData)));
		parameters.add(0);
		parameters.add(getPassword());
		Object result = rpc.execute("grav.saveData", parameters);
	}
}
