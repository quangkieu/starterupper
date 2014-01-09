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
	CameraPanel camPanel;
	public PicturePanel(GravatarModel model) {
		setLayout(new MigLayout("", "[320.00,grow]", "[][240.00][grow][]"));
		
		JLabel lblNewLabel = new JLabel("Smile! Take a picture and share it to associate names and faces.");
		add(lblNewLabel, "cell 0 0,alignx center");

		camPanel = new CameraPanel(model.getProfilePicture());
		add(camPanel, "cell 0 1,alignx center");
		
		JPanel panel = new JPanel();
		add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[][][]", "[]"));
		
		JButton takePicture = new JButton("Take");
		takePicture.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					camPanel.take();
				} catch (Exception e) {
					// set error
				}
			}
			
		});
		
		panel.add(takePicture, "cell 0 0");
		
		JButton discardPicture = new JButton("Discard");
		panel.add(discardPicture, "cell 1 0");
		discardPicture.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					camPanel.discard();
				} catch (Exception e) {
					// set error
				}
			}
			
		});
		
		JButton sharePicture = new JButton("Share");
		panel.add(sharePicture, "cell 2 0");
		
		JLabel error = new JLabel(" ");
		error.setForeground(Color.RED);
		add(error, "cell 0 3,alignx center");
	}
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		camPanel.setVisible(visible);
	}
}
