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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import java.io.File;

@SuppressWarnings("serial")
public class PicturePanel extends JPanel {
	final JButton sharePicture;
	final JButton takePicture;
	final JButton browsePicture;
	
	final Gravatar model;
	CameraPanel camPanel;
	boolean take;

	public PicturePanel(final Gravatar model) {
		setLayout(new MigLayout("", "[640.00,grow]", "[][480.00][grow][]"));
		this.model = model;
		setName("Profile picture");

		add(new JLabel("Smile! Take a picture or browse for one and share it."), "cell 0 0,alignx center");

		camPanel = new CameraPanel(model.getProfilePicture());
		add(camPanel, "cell 0 1,alignx center");

		JPanel panel = new JPanel();
		add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[][][]", "[]"));

		// takePicture is stateful: it switches between Take and Clear in the UI
		takePicture = new JButton();
		take = model.getProfilePicture().exists();
		takePicture.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (take) {
					takePicture.setEnabled(false);
					camPanel.take();
					takePicture.setEnabled(true);
				} else {
					takePicture.setEnabled(false);
					camPanel.discard();
					takePicture.setEnabled(camPanel.canTakePicture());
				}
				updateState();
			}
		});

		panel.add(takePicture, "cell 0 0");

		sharePicture = new JButton("Share");
		sharePicture.setToolTipText("Share picture via Gravatar");
		sharePicture.setEnabled(false);
		panel.add(sharePicture, "cell 2 0");

		browsePicture = new JButton( "Browse..." );
		browsePicture.setToolTipText( "Browse for a picture on your computer" );
		browsePicture.setEnabled( true );

		browsePicture.addActionListener( new ActionListener() 
		{
			@Override
			public void actionPerformed( ActionEvent arg0 )
			{
				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("All Image Files (*.jpg, *.png, *.bmp, *.gif)", "jpg", "png", "bmp", "gif");
				fileChooser.setFileFilter(filter);
				int returnValue = fileChooser.showOpenDialog( new JPanel() );

				if( returnValue == JFileChooser.APPROVE_OPTION )
				{
					File file = fileChooser.getSelectedFile();
					camPanel.setImage( file );
					take = true;
					takePicture.setEnabled(true);
					updateState();
				}
			}
		} );
		panel.add( browsePicture, "cell 1 0" );
		
		JLabel error = new JLabel("");
		error.setForeground(Color.RED);
		add(error, "cell 0 3,alignx center");
		
		updateState();

		Event.getBus().register(this);
	}
	/**
	 * Enable the share button when we log into gravatar.
	 * @param event
	 */
	@Subscribe
	public void gravatarLoggedIn(HostPerformedAction event) {
		if (event.host.equals(model) && event.action == HostAction.login) {
			sharePicture.setEnabled(model.getProfilePicture().exists());
		}
	}
	private void updateState() {
		// If we just took a picture (or a picture is now available)...
		if (take) {
			takePicture.setText("Clear");
			takePicture.setToolTipText("Discard profile picture on this computer");
			sharePicture.setEnabled(model.getProfilePicture().exists() && model.loggedIn());
			take = false;
		// If we just discarded the picture (or none was available)...
		} else {
			take = camPanel.canTakePicture();
			if (take) {
				takePicture.setText("Take");
				takePicture.setToolTipText("Take a picture using your computer's webcam");
				sharePicture.setEnabled(false);
			}
		}
	}
}
