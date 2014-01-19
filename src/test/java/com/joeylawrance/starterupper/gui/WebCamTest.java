package com.joeylawrance.starterupper.gui;

import javax.swing.JFrame;

import com.joeylawrance.starterupper.model.host.impl.Gravatar;

public class WebCamTest {
	public static void main(String[] args) {
		JFrame window = new JFrame("Test Webcam Panel");
		window.add(new PicturePanel(new Gravatar()));
		window.pack();
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
