package com.joeylawrance.starterupper.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JPasswordField;
import javax.swing.Box;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.Dimension;
import net.miginfocom.swing.MigLayout;

public class HostConfigPanel extends JPanel {
	private JTextField textField;
	private JPasswordField passwordField;
	public HostConfigPanel() {
		setLayout(new MigLayout("", "[48px][5px][86px,grow]", "[20px][][][29.00][23px][]"));
		
		JLabel lblNewLabel = new JLabel("Username");
		add(lblNewLabel, "cell 0 0,alignx left,aligny center");
		
		textField = new JTextField();
		add(textField, "cell 2 0,growx,aligny top");
		textField.setColumns(10);
		
		JLabel label = new JLabel("Enter a username for this service.");
		label.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(label, "cell 2 1");
		
		JLabel label_1 = new JLabel("Password");
		add(label_1, "cell 0 2");
		
		passwordField = new JPasswordField();
		add(passwordField, "cell 2 2,growx");
		
		JLabel label_2 = new JLabel("Enter a password for this service.");
		label_2.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(label_2, "cell 2 3,aligny top");
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		add(horizontalBox_1, "cell 2 4");
		
		JButton signUp = new JButton("Sign up");
		horizontalBox_1.add(signUp);
		
		Component rigidArea_1 = Box.createRigidArea(new Dimension(5, 20));
		horizontalBox_1.add(rigidArea_1);
		
		JButton logIn = new JButton("Log in");
		horizontalBox_1.add(logIn);
		
		JLabel lblSignUpIf = new JLabel("Sign up if you do not have an account, log in if you do.");
		lblSignUpIf.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(lblSignUpIf, "cell 2 5");
		
	}
}
