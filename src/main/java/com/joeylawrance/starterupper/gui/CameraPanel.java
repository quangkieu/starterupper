package com.joeylawrance.starterupper.gui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

@SuppressWarnings("serial")
public class CameraPanel extends JPanel {
	private static final Logger logger = LoggerFactory.getLogger(CameraPanel.class);
	private Webcam cam;
	private WebcamPanel panel;
	private final JLabel snapshot;
	JPanel empty;
	private File image;
	private boolean started = false;
	private CardLayout layout;
	public CameraPanel(File image) {
		this(Webcam.getDefault(), image);
	}
	public CameraPanel(Webcam webcam, File image) {
		// There's three states to this panel: the cam, snapshot, or empty (no cam or image).
		layout = new CardLayout(0,0);
		setLayout(layout);

		// Default card in case the user has no cam or gravatar.
		empty = new JPanel();
		empty.setName("empty");
		add(empty);
		layout.show(this, empty.getName());

		// Only create the webcam panel if we've got a webcam.
		if (webcam != null) {
			panel = new WebcamPanel(webcam, new Dimension(320,240), false);
			panel.setName("cam");
			cam = webcam;
			add(panel, panel.getName());
			layout.show(this, panel.getName());
		}

		// Create a snapshot regardless of whether we got one.
		this.image = image;
		snapshot = new JLabel();
		snapshot.setName("snapshot");
		add(snapshot, snapshot.getName());

		// But only show an image if it already exists
		if (image.exists()) {
			snapshot.setIcon(new ImageIcon(image.getAbsolutePath()));
			layout.show(this, snapshot.getName());
		}
		// Don't start the cam until we're actually visible
		this.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent arg0) {
				start();
			}
			@Override
			public void ancestorMoved(AncestorEvent arg0) {
			}
			@Override
			public void ancestorRemoved(AncestorEvent arg0) {
				stop(true);
			}
		});
	}
	/**
	 * Start the camera.
	 */
	public void start() {
		if (!image.exists() && panel != null) {
			layout.show(this, panel.getName());
			if (started) {
				panel.resume();
			} else {
				panel.start();
				started = true;
			} 
		}
	}
	/**
	 * Stop the camera.
	 * @param forReal if true, stop; otherwise, pause.
	 */
	public void stop(boolean forReal) {
		if (panel != null && started) {
			if (!forReal) {
				panel.pause();
			} else {
				started = false;
				panel.stop();
			}
		}
	}
	/**
	 * Can we take a picture?
	 * @return
	 */
	public boolean canTakePicture() {
		return started;
	}
	/**
	 * Take a picture
	 * @throws Exception
	 */
	public void take() {
		if (cam != null) {
			try {
				BufferedImage bi = cam.getImage();
				ImageIO.write(bi, "jpg", image);
				snapshot.setIcon(new ImageIcon(bi));
				layout.show(this, snapshot.getName());
				stop(false);
			} catch (Exception e) {
				logger.error("Unable to save image. {}", e.toString());
				layout.show(this, empty.getName());
			}
		}
	}
	public void discard() {
		try {
			image.delete();
		} catch (Exception e) {
			logger.error("Unable to delete image. {}", e.toString());
		}
		start();
	}
}