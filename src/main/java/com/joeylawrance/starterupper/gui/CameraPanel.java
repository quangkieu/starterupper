package com.joeylawrance.starterupper.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

public class CameraPanel extends WebcamPanel {
	private Webcam cam;
	private File image;
	public CameraPanel(File image) {
		this(Webcam.getDefault(), image);
	}
	public CameraPanel(Webcam webcam, File image) {
		super(webcam);
		cam = webcam;
		if (image.exists()) cam.close();
		this.image = image;
	}
	public void take() throws Exception {
		BufferedImage bi = cam.getImage();
		ImageIO.write(bi, "PNG", image);
		cam.close();
	}
	public void discard() throws Exception {
		image.delete();
		cam.open();
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
}
