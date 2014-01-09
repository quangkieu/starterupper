package com.joeylawrance.starterupper.model;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.joeylawrance.starterupper.Constants;
import com.timgroup.jgravatar.Gravatar;
import com.timgroup.jgravatar.GravatarDefaultImage;
import com.timgroup.jgravatar.GravatarRating;

public class GravatarConfig {
	private Gravatar gravatar;
	public GravatarConfig(String email) throws Exception {
		gravatar = new Gravatar()
			.setSize(240)
			.setRating(GravatarRating.GENERAL_AUDIENCES)
			.setDefaultImage(GravatarDefaultImage.IDENTICON);
		byte[] jpg = gravatar.download(email);
		ImageIO.write(toBufferedImage(new ImageIcon(jpg).getImage()),
				"jpg",
				Constants.PROFILE_PICTURE);
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
}
