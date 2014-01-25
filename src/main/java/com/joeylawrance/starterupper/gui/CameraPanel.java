package com.joeylawrance.starterupper.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

@SuppressWarnings("serial")
public class CameraPanel extends WebcamPanel {
	private Webcam cam;
	private File image;

	/**
	 * Initializes the camera panel with the predefined
	 * image.
	 * @param image The image already able to be placed
	 */
	public CameraPanel(File image) {
		this(Webcam.getDefault(), image);
	}

	/**
	 * Called when you already have a handle to the webcam
	 * and an image.
	 * @param webcam The webcam to modify the image
	 * @param image A buffer for the webcam
	 */
	public CameraPanel(Webcam webcam, File image) {
		super(webcam, new Dimension(640,480), false);
		cam = webcam;
		this.image = image;

		// Is this ancestry stuff necessary?
		this.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent arg0) {
				if (!CameraPanel.this.image.exists()) {
					CameraPanel.this.start();
				}
			}
			@Override
			public void ancestorMoved(AncestorEvent arg0) {
			}
			@Override
			public void ancestorRemoved(AncestorEvent arg0) {
				CameraPanel.this.stop();
			}
		});

		// Check if the gravatar image failed to download
		if( this.image.length() == 0 )
		{
			image.delete();
			this.start();
		}
	}

	/**
	 * Takes a picture.
	 * @throws Exception Throws an exception if the camera isn't running
	 */
	public void take() throws Exception {
		BufferedImage bi = cam.getImage();
		ImageIO.write(bi, "jpg", image);
		this.stop();
	}

	/**
	 * Discards the current image, and starts the camera back up.
	 * @throws Exception Just in case it fails to delete or set something
	 */
	public void discard() throws Exception {
		image.delete();
		this.start();
	}

	public void paintComponent(Graphics g) {
		if (image.exists()) {
			try {
				g.drawImage(ImageIO.read(image), 0, 0, null);
			} catch (IOException e) {
			}
		} else {
			super.paintComponent(g);
		}
	}

	/**
	 * Implicitly change the image, used when uploading an image.
	 * @param image Takes a file input
	 */
	public void setImage( File image ) {
		CameraPanel.this.stop();
		this.cam.close();
		this.image = image;
	}

	/**
	 * Checks if the webcam is currently running
	 * @return If the webcam is running
	 */
	public boolean isRunning() {
		return this.cam.isOpen();
	}
}
