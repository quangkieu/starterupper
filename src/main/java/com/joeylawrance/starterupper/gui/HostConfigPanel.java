package com.joeylawrance.starterupper.gui;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joeylawrance.starterupper.model.host.Host;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.concurrent.ExecutionException;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class HostConfigPanel extends JPanel {
	static final Logger logger = LoggerFactory.getLogger(HostConfigPanel.class);
	private final JTextField username;
	final JPasswordField passwordField;
	private JLabel status;
	private JButton signUp;
	private JButton logIn;
	private JButton forgotPassword;
	private Host model;
	
	private void enableFields(boolean enable) {
		username.setEnabled(enable && !model.haveLoggedInBefore());
		passwordField.setEnabled(enable);
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
					logger.info("Login interrupted.");
				} catch (ExecutionException e) {
					logger.info("Login encountered a problem: {}", e.getMessage());
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
					logger.info("Signup interrupted.");
				} catch (ExecutionException e) {
					logger.info("Signup encountered a problem: {}", e.getMessage());
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
					logger.info("Forgot password interrupted.");
				} catch (ExecutionException e) {
					logger.info("Forgot password encountered a problem: {}", e.getMessage());
				}
			}
		}.execute();
	}
	public HostConfigPanel(final Host model) {
		this.model = model;
		setName(model.getHostName());
		setLayout(new MigLayout("", "[70px][grow]", "[76.00][][20px][][23px][]"));

		JLabel logo = new JLabel();
		logo.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(model.getLogo())));
		add(logo, "cell 1 0");

		JLabel description = new JLabel();
		description.setText(model.getDescription());
		add(description, "cell 1 1");

		add(new JLabel("Username"), "cell 0 2,alignx trailing");

		username = new JTextField(model.getUsername());
		username.setName("Username");
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
		add(username, "cell 1 2,growx");
		username.setColumns(10);

		add(new JLabel("Password"), "cell 0 3,alignx trailing");

		passwordField = new JPasswordField();
		passwordField.setName("Password");
		passwordField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				model.setPassword(new String(passwordField.getPassword()));
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				model.setPassword(new String(passwordField.getPassword()));
			}

		});
		add(passwordField, "cell 1 3,growx");

		signUp = new JButton("Sign up");
		signUp.setToolTipText("Sign up if you do not already have an account");
		signUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doSignup();
			}
		});
		add(signUp, "flowx,cell 1 4");

		logIn = new JButton("Log in");
		logIn.setToolTipText("Log in if you already have an account");
		logIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doLogin();
			}
		});
		add(logIn, "cell 1 4");

		forgotPassword = new JButton("Forgot password");
		forgotPassword.setToolTipText("Click if you need to reset your password via email");
		forgotPassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doReset();
			}
		});
		add(forgotPassword, "cell 1 4");

		status = new JLabel();
		add(status, "growx,cell 1 5");
		this.addAncestorListener(new AncestorListener() {
			// We need to update the view to reflect model changes (if any)
			@Override
			public void ancestorAdded(AncestorEvent arg0) {
				username.setText(model.getUsername());
			}
			@Override
			public void ancestorMoved(AncestorEvent arg0) {
			}
			@Override
			public void ancestorRemoved(AncestorEvent arg0) {
			}
		});
		enableFields(true);
	}
}
