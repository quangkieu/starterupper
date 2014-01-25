package com.joeylawrance.starterupper.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.joeylawrance.starterupper.model.host.impl.Gravatar;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import java.awt.Color;
import java.io.*;

@SuppressWarnings("serial")
public class PicturePanel extends JPanel {
	final JButton sharePicture;
	final JButton takePicture;
	final JButton discardPicture;
	final JButton uploadPicture;
	CameraPanel camPanel;

	public PicturePanel(Gravatar model) {
		setLayout(new MigLayout("", "[640.00,grow]", "[][480.00][grow][]"));
		setName("Profile picture");
		
		add(new JLabel("Smile! Take a picture and share it to associate names and faces, or upload one."), "cell 0 0,alignx center");

		camPanel = new CameraPanel(model.getProfilePicture());
		add(camPanel, "cell 0 1,alignx center");
		
		JPanel panel = new JPanel();
		add(panel, "cell 0 2,alignx center,growy");
		panel.setLayout(new MigLayout("", "[][][]", "[]"));
		
		takePicture = new JButton("Take");
		takePicture.setToolTipText("Take a picture.");
		takePicture.setEnabled( camPanel.isRunning() );
		takePicture.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					camPanel.take();
					sharePicture.setEnabled(true);
					discardPicture.setEnabled(true);
					takePicture.setEnabled(false);
					uploadPicture.setEnabled(false);
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
					uploadPicture.setEnabled(true);
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

		uploadPicture = new JButton( "Browse..." );
		uploadPicture.setToolTipText( "Browse for a picture on your hard drive" );
		uploadPicture.setEnabled( !model.getProfilePicture().exists() );

		uploadPicture.addActionListener( new ActionListener() 
		{
			@Override
			public void actionPerformed( ActionEvent arg0 )
			{
				try
				{
					JFileChooser fileChooser = new JFileChooser();
					int returnValue = fileChooser.showOpenDialog( new JPanel() );

					if( returnValue == JFileChooser.APPROVE_OPTION )
					{
						File file = fileChooser.getSelectedFile();
						camPanel.setImage( file );

						takePicture.setEnabled( false );
						discardPicture.setEnabled( true );
						sharePicture.setEnabled( true );
					}
				}
				catch (Exception e)
				{
					// set error
				}
			}
		} );

		// Nick: Not sure if this fixes a bug, or is just annoying.
		repaint();
		panel.add( uploadPicture );
	}
}
