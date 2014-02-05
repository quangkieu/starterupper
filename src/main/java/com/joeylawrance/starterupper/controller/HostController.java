package com.joeylawrance.starterupper.controller;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joeylawrance.starterupper.gui.HostPanel;
import com.joeylawrance.starterupper.gui.View;
import com.joeylawrance.starterupper.model.host.Host;

public class HostController {
//	private SwingValidationGroup fieldValidator = SwingValidationGroup.create();
	private static final Logger logger = LoggerFactory.getLogger(HostController.class);
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

	private void doLogin() {
		new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				status.setText("Please wait, logging in...");
				enableFields(false);
				return model.login();
			}
			@Override
			public void done() {
				boolean loggedIn = false;
				try {
					loggedIn = get();
				} catch (InterruptedException e) {
					logger.info("Login interrupted.", e.fillInStackTrace());
				} catch (ExecutionException e) {
					logger.info("Login encountered a problem.", e.fillInStackTrace());
				}
				enableFields(!loggedIn);
				if (loggedIn) {
					status.setText(String.format("Logged in to %s.", model.getHostName()));
				} else {
					status.setText(String.format("Login failed."));
				}
			}
		}.execute();
	}

	private void doSignup() {
		new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				status.setText("Please wait, signing up for a new account...");
				enableFields(false);
				return model.signUp();
			}
			@Override
			public void done() {
				boolean signedUp = false;
				try {
					signedUp = get();
				} catch (InterruptedException e) {
					logger.info("Signup interrupted.", e.fillInStackTrace());
				} catch (ExecutionException e) {
					logger.info("Signup encountered a problem: {}", e.fillInStackTrace());
				}
				enableFields(!signedUp);
				logIn.setEnabled(true);
				if (signedUp) {
					status.setText(String.format("Check your inbox for instructions to finish the signup. Then, come back here to log in.", model.getHostName()));
				} else {
					status.setText(String.format("Unable to create a new account. Try a different username or a stronger password."));
				}
			}
		}.execute();
	}
	private void doReset() {
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				status.setText("Please wait, sending password reset request...");
				model.forgotPassword();
				return null;
			}
			@Override
			public void done() {
				try {
					get();
					status.setText("Check your inbox for a password reset email with instructions.");
				} catch (InterruptedException e) {
					logger.info("Forgot password interrupted.", e.fillInStackTrace());
				} catch (ExecutionException e) {
					logger.info("Forgot password encountered a problem. ", e.fillInStackTrace());
				}
			}
		}.execute();
	}

	
	public HostController(final View view, final Host model) {
//		final ValidationPanel p = new ValidationPanel(fieldValidator);
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
				doSignup();
			}
		});
		logIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doLogin();
			}
		});
		forgotPassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doReset();
			}
		});
		
		enableFields(true);
	}
	public HostPanel getPanel() {
		return view.getComponent(null, HostPanel.class);
	}
}
