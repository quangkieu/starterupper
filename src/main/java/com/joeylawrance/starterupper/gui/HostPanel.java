package com.joeylawrance.starterupper.gui;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class HostPanel extends ExtendedJPanel {
	public static enum Controls {
		USERNAME, PASSWORD, SIGNUP, LOGIN, FORGOT, STATUS, LOGO, DESCRIPTION;
	}
	
	public HostPanel() {
		setLayout(new MigLayout("", "[70px][grow]", "[76.00][][20px][][23px][]"));

		JLabel logo = new JLabel();
		logo.setName(Controls.LOGO.name());
		add(logo, "cell 1 0");

		JLabel description = new JLabel();
		description.setName(Controls.DESCRIPTION.name());
		add(description, "cell 1 1");

		add(new JLabel("Username"), "cell 0 2,alignx trailing");

		JTextField username = new JTextField();
		username.setName(Controls.USERNAME.name());
		add(username, "cell 1 2,growx");
		username.setColumns(10);

		add(new JLabel("Password"), "cell 0 3,alignx trailing");

		JPasswordField passwordField = new JPasswordField();
		passwordField.setName(Controls.PASSWORD.name());
		add(passwordField, "cell 1 3,growx");

		JButton button = new JButton("Sign up");
		button.setName(Controls.SIGNUP.name());
		button.setToolTipText("Sign up if you do not already have an account");
		add(button, "flowx,cell 1 4");

		button = new JButton("Log in");
		button.setName(Controls.LOGIN.name());
		button.setToolTipText("Log in if you already have an account");
		add(button, "cell 1 4");

		button = new JButton("Forgot password");
		button.setName(Controls.FORGOT.name());
		button.setToolTipText("Click if you need to reset your password via email");
		add(button, "cell 1 4");

		JLabel status = new JLabel();
		status.setName(Controls.STATUS.name());
		add(status, "growx,cell 1 5");
	}
}
