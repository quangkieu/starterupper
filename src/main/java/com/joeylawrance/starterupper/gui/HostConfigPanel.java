package com.joeylawrance.starterupper.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.Font;

import javax.swing.JPasswordField;
import javax.swing.Box;
import javax.swing.JButton;

import com.joeylawrance.starterupper.model.HostModel;

import java.awt.Component;
import java.awt.Dimension;

import net.miginfocom.swing.MigLayout;

public class HostConfigPanel extends JPanel {
	private JTextField textField;
	private JPasswordField passwordField;
	public HostConfigPanel(HostModel model) {
		setLayout(new MigLayout("", "[48px][86px]", "[20px][][][29.00][23px][]"));
		
		JLabel lblNewLabel = new JLabel("Username");
		add(lblNewLabel, "cell 0 0,alignx left,aligny center");
		
		textField = new JTextField();
		add(textField, "cell 1 0,growx,aligny top");
		textField.setColumns(10);
		
		JLabel label = new JLabel("Enter a username for this service.");
		label.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(label, "cell 1 1");
		
		JLabel label_1 = new JLabel("Password");
		add(label_1, "cell 0 2");
		
		passwordField = new JPasswordField();
		add(passwordField, "cell 1 2,growx");
		
		JLabel label_2 = new JLabel("Enter a password for this service.");
		label_2.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(label_2, "cell 1 3,aligny top");
		
		JButton signUp = new JButton("Sign up");
		add(signUp, "flowx,cell 1 4");

		JButton logIn = new JButton("Log in");
		add(logIn, "cell 1 4");

		JLabel lblSignUpIf = new JLabel(" ");
		lblSignUpIf.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(lblSignUpIf, "cell 1 5");
		
		JButton btnNewButton = new JButton("Reset password");
		add(btnNewButton, "cell 1 4");
		
	}
}
