package com.joeylawrance.starterupper.gui;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import java.awt.Font;

import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joeylawrance.starterupper.model.host.HostModel;

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
	private JButton signUp;
	private JButton logIn;
	private HostModel model;

	private void checkNameTaken() {
		new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				return model.nameTaken();
			}
			@Override
			public void done() {
				try {
					boolean taken = get();
					signUp.setEnabled(!taken);
					logIn.setEnabled(taken);
				} catch (InterruptedException e) {
					logger.info("Username taken check interrupted.");
				} catch (ExecutionException e) {
					logger.info("Username taken check encountered a problem: {}", e.getMessage());
				}
			}
		}.execute();
	}

	private void doLogin() {
		new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				return model.login();
			}
			@Override
			public void done() {
				try {
					boolean loggedIn = get();
					username.setEnabled(!loggedIn);
					passwordField.setEnabled(!loggedIn);
				} catch (InterruptedException e) {
					logger.info("Login interrupted.");
				} catch (ExecutionException e) {
					logger.info("Login encountered a problem: {}", e.getMessage());
				}
			}
		}.execute();
	}

	private void doSignup() {
		new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				return model.signUp();
			}
			@Override
			public void done() {
				try {
					boolean signedUp = get();
				} catch (InterruptedException e) {
					logger.info("Signup interrupted.");
				} catch (ExecutionException e) {
					logger.info("Signup encountered a problem: {}", e.getMessage());
				}
			}
		}.execute();
	}
	private void doReset() {
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				model.forgotPassword();
				return null;
			}
			@Override
			public void done() {
				try {
					get();
				} catch (InterruptedException e) {
					logger.info("Forgot password interrupted.");
				} catch (ExecutionException e) {
					logger.info("Forgot password encountered a problem: {}", e.getMessage());
				}
			}
		}.execute();
	}
	public HostConfigPanel(final HostModel model) {
		this.model = model;
		setName(model.getHostName());
		checkNameTaken();
		setLayout(new MigLayout("", "[48px][86px,grow]", "[76.00][][20px][][23px][]"));

		JLabel logo = new JLabel();
		logo.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(model.getLogo())));
		add(logo, "cell 1 0");

		JLabel description = new JLabel();
		description.setText(model.getDescription());
		add(description, "cell 1 1");

		JLabel lblNewLabel = new JLabel("Username");
		add(lblNewLabel, "cell 0 2,alignx left,aligny center");

		username = new JTextField(model.getUsername());
		username.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				model.setUsername(username.getText().trim());
				checkNameTaken();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				model.setUsername(username.getText().trim());
				checkNameTaken();
			}
		});
		add(username, "cell 1 2,growx,aligny top");
		username.setColumns(10);

		JLabel label_1 = new JLabel("Password");
		add(label_1, "cell 0 3");

		passwordField = new JPasswordField();
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
		signUp.setToolTipText("Sign up if you do not already have an account.");
		signUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doSignup();
			}
		});
		add(signUp, "flowx,cell 1 4");

		logIn = new JButton("Log in");
		logIn.setToolTipText("Log in if you already have an account.");
		logIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doLogin();
			}
		});
		add(logIn, "cell 1 4");

		JLabel lblSignUpIf = new JLabel(" ");
		lblSignUpIf.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(lblSignUpIf, "cell 1 5");

		JButton forgotPassword = new JButton("Forgot password");
		forgotPassword.setToolTipText("Click if you need to reset your password via email.");
		forgotPassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doReset();
			}
		});
		add(forgotPassword, "cell 1 4");
		this.addAncestorListener(new AncestorListener() {
			// We need to update the view to reflect model changes (if any)
			// figure out if the username is taken already once we're visible.
			@Override
			public void ancestorAdded(AncestorEvent arg0) {
				logger.info("{} configuration visible", model.getHostName());
				username.setText(model.getUsername());
				checkNameTaken();
			}
			@Override
			public void ancestorMoved(AncestorEvent arg0) {
			}
			@Override
			public void ancestorRemoved(AncestorEvent arg0) {
			}
		});
	}
}
