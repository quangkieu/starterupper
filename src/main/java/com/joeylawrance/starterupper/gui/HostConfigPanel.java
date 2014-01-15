package com.joeylawrance.starterupper.gui;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.Font;

import javax.swing.JPasswordField;
import javax.swing.JButton;

import com.joeylawrance.starterupper.model.interfaces.HostModel;

import java.awt.Toolkit;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class HostConfigPanel extends JPanel {
	private JTextField textField;
	private JPasswordField passwordField;
	public HostConfigPanel(HostModel model) {
		setLayout(new MigLayout("", "[48px][86px,grow]", "[76.00][][20px][][23px][]"));
		
		JLabel logo = new JLabel();
		logo.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(model.getLogo())));
		add(logo, "cell 1 0");
		
		JLabel description = new JLabel();
		description.setText(model.getDescription());
		add(description, "cell 1 1");
		
		JLabel lblNewLabel = new JLabel("Username");
		add(lblNewLabel, "cell 0 2,alignx left,aligny center");
		
		textField = new JTextField(model.getUsername());
		add(textField, "cell 1 2,growx,aligny top");
		textField.setColumns(10);
		
		JLabel label_1 = new JLabel("Password");
		add(label_1, "cell 0 3");
		
		passwordField = new JPasswordField();
		add(passwordField, "cell 1 3,growx");
		
		JButton signUp = new JButton("Sign up");
		signUp.setToolTipText("Click here if you do not have an account.");
		add(signUp, "flowx,cell 1 4");

		JButton logIn = new JButton("Log in");
		logIn.setToolTipText("Click here if you already have an account.");
		add(logIn, "cell 1 4");

		JLabel lblSignUpIf = new JLabel(" ");
		lblSignUpIf.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(lblSignUpIf, "cell 1 5");
		
		JButton btnNewButton = new JButton("Forgot password");
		btnNewButton.setToolTipText("Click if you need to reset your password via email.");
		add(btnNewButton, "cell 1 4");
		
	}
}
