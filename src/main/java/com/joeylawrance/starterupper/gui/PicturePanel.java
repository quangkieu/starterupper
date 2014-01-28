package com.joeylawrance.starterupper.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;
import com.joeylawrance.starterupper.model.Event;
import com.joeylawrance.starterupper.model.host.HostAction;
import com.joeylawrance.starterupper.model.host.HostPerformedAction;
import com.joeylawrance.starterupper.model.host.impl.Gravatar;

import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Color;

@SuppressWarnings("serial")
public class PicturePanel extends JPanel {
	final JButton sharePicture;
	final JButton takePicture;
	final JButton discardPicture;
	final Gravatar model;
	CameraPanel camPanel;
	public PicturePanel(Gravatar model) {
		this.model = model;
		setLayout(new MigLayout("", "[320.00,grow]", "[][240.00][grow][]"));
		setName("Profile picture");

		add(new JLabel("Smile! Take a picture and share it to associate names and faces."), "cell 0 0,alignx center");

		camPanel = new CameraPanel(model.getProfilePicture());
		add(camPanel, "cell 0 1,alignx center");

		JPanel panel = new JPanel();
		add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[][][]", "[]"));

		takePicture = new JButton("Take");
		takePicture.setToolTipText("Take a picture");
		takePicture.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				camPanel.take();
				sharePicture.setEnabled(true);
				discardPicture.setEnabled(true);
				takePicture.setEnabled(false);
			}

		});

		panel.add(takePicture, "cell 0 0");

		discardPicture = new JButton("Discard");
		discardPicture.setToolTipText("Discard picture");
		discardPicture.setEnabled(model.getProfilePicture().exists());
		panel.add(discardPicture, "cell 1 0");
		discardPicture.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				camPanel.discard();
				sharePicture.setEnabled(false);
				discardPicture.setEnabled(false);
				takePicture.setEnabled(camPanel.canTakePicture());
			}
		});

		sharePicture = new JButton("Share");
		sharePicture.setToolTipText("Share picture via Gravatar");
		sharePicture.setEnabled(false);
		panel.add(sharePicture, "cell 2 0");

		JLabel error = new JLabel("");
		error.setForeground(Color.RED);
		add(error, "cell 0 3,alignx center");
		Event.getBus().register(this);
	}
	@Subscribe
	public void gravatarLoggedIn(HostPerformedAction event) {
		if (event.host.equals(model) && event.action == HostAction.login) {
			sharePicture.setEnabled(model.getProfilePicture().exists());
		}
	}
}
