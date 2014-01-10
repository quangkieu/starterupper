package com.joeylawrance.starterupper.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.joeylawrance.starterupper.model.GravatarModel;

import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Color;

public class PicturePanel extends JPanel {
	final JButton sharePicture;
	final JButton takePicture;
	final JButton discardPicture;
	CameraPanel camPanel;
	public PicturePanel(GravatarModel model) {
		setLayout(new MigLayout("", "[320.00,grow]", "[][240.00][grow][]"));
		
		add(new JLabel("Smile! Take a picture and share it to associate names and faces."), "cell 0 0,alignx center");

		camPanel = new CameraPanel(model.getProfilePicture());
		add(camPanel, "cell 0 1,alignx center");
		
		JPanel panel = new JPanel();
		add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[][][]", "[]"));
		
		takePicture = new JButton("Take");
		takePicture.setToolTipText("Take a picture.");
		takePicture.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					camPanel.take();
					sharePicture.setEnabled(true);
					discardPicture.setEnabled(true);
					takePicture.setEnabled(false);
				} catch (Exception e) {
					// set error
				}
			}
			
		});
		
		panel.add(takePicture, "cell 0 0");
		
		discardPicture = new JButton("Discard");
		discardPicture.setToolTipText("Discard picture.");
		discardPicture.setEnabled(model.getProfilePicture().exists());
		panel.add(discardPicture, "cell 1 0");
		discardPicture.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					camPanel.discard();
					sharePicture.setEnabled(false);
					discardPicture.setEnabled(false);
					takePicture.setEnabled(true);
				} catch (Exception e) {
					// set error
				}
			}
			
		});
		
		sharePicture = new JButton("Share");
		sharePicture.setToolTipText("Share picture via Gravatar.");
		sharePicture.setEnabled(model.getProfilePicture().exists());
		panel.add(sharePicture, "cell 2 0");
		
		JLabel error = new JLabel("");
		error.setForeground(Color.RED);
		add(error, "cell 0 3,alignx center");
	}
}
