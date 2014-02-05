package com.joeylawrance.starterupper.controller;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import com.google.common.eventbus.Subscribe;
import com.joeylawrance.starterupper.gui.HostPanel;
import com.joeylawrance.starterupper.gui.View;
import com.joeylawrance.starterupper.model.Event;
import com.joeylawrance.starterupper.model.host.Host;
import com.joeylawrance.starterupper.model.host.HostPerformedAction;

public class HostController {
	private final Host model;
	private JTextField username;
	private JPasswordField password;
	private JButton signUp;
	private JButton logIn;
	private JButton forgotPassword;
	private JLabel status;
	private View view;

	private void enableFields(boolean enable) {
		username.setEnabled(enable && !model.haveLoggedInBefore());
		password.setEnabled(enable);
		signUp.setEnabled(enable && !model.haveLoggedInBefore());
		logIn.setEnabled(enable);
		forgotPassword.setEnabled(enable);
	}

	public HostController(final View view, final Host model) {
		Event.getBus().register(this);
		this.model = model;
		this.view = view;
		view.getComponent(null, JPanel.class).setName(model.getHostName());
		view.getComponent(HostPanel.Controls.logo, JLabel.class).setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(model.getLogo())));
		view.getComponent(HostPanel.Controls.description, JLabel.class).setText(model.getDescription());

		username = view.getComponent(HostPanel.Controls.username, JTextField.class);
		password = view.getComponent(HostPanel.Controls.password, JPasswordField.class);
		signUp = view.getComponent(HostPanel.Controls.signup, JButton.class);
		logIn = view.getComponent(HostPanel.Controls.login, JButton.class);
		forgotPassword = view.getComponent(HostPanel.Controls.forgot, JButton.class);
		status = view.getComponent(HostPanel.Controls.status, JLabel.class);
		username.setText(model.getUsername());
		view.getComponent(null, JPanel.class).addAncestorListener(new AncestorListener() {
			// We need to update the view to reflect model changes (if any)
			@Override
			public void ancestorAdded(AncestorEvent arg0) {
				view.getComponent(HostPanel.Controls.username, JTextField.class).setText(model.getUsername());
			}
			@Override
			public void ancestorMoved(AncestorEvent arg0) {
			}
			@Override
			public void ancestorRemoved(AncestorEvent arg0) {
			}
		});

		username.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				model.setUsername(username.getText().trim());
			}
			@Override
			public void focusLost(FocusEvent arg0) {
				model.setUsername(username.getText().trim());
			}
		});
		password.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				model.setPassword(new String(password.getPassword()));
			}
			@Override
			public void focusLost(FocusEvent arg0) {
				model.setPassword(new String(password.getPassword()));
			}
		});
		signUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SwingWorker<Boolean, Void>() {
					@Override
					protected Boolean doInBackground() throws Exception {
						status.setText("Please wait, signing up for a new account...");
						enableFields(false);
						return model.signUp();
					}
				}.execute();
			}
		});
		logIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SwingWorker<Boolean, Void>() {
					@Override
					protected Boolean doInBackground() throws Exception {
						status.setText("Please wait, logging in...");
						enableFields(false);
						return model.login();
					}
				}.execute();
			}
		});
		forgotPassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						status.setText("Please wait, sending password reset request...");
						model.forgotPassword();
						return null;
					}
				}.execute();
			}
		});

		enableFields(true);
	}
	public HostPanel getPanel() {
		return view.getComponent(null, HostPanel.class);
	}
	@Subscribe
	public void setStatus(HostPerformedAction event) {
		switch (event.action) {
		case login:
			enableFields(!event.successful);
			if (event.successful) {
				status.setText(String.format("Logged in to %s.", model.getHostName()));
			} else {
				status.setText(String.format("Login failed."));
			}
			break;
		case reset:
			status.setText("Check your inbox for a password reset email with instructions.");
			break;
		case signup:
			enableFields(!event.successful);
			logIn.setEnabled(true);
			if (event.successful) {
				status.setText(String.format("Check your inbox for instructions to finish the signup. Then, come back here to log in.", model.getHostName()));
			} else {
				status.setText(String.format("Unable to create a new account. Try a different username or a stronger password."));
			}
			break;
		default:
			break;
		}
	}
}
