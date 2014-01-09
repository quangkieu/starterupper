package com.joeylawrance.starterupper.gui;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.Font;
import java.awt.Toolkit;

import net.miginfocom.swing.MigLayout;

import javax.swing.JPasswordField;

import com.joeylawrance.starterupper.model.GitConfig;

public class GitConfigPanel extends JPanel {
	private JTextField fullName;
	private JTextField textField;
	private JTextField textField_1;
	private GitConfig gitConfig;
	public GitConfigPanel() throws Exception {
		setName("About me");
		
		gitConfig = GitConfig.getInstance();
		setLayout(new MigLayout("", "[45px][]", "[90.00][24.00,top][20px][20.00][][20.00][20px][20.00px]"));
		
		JLabel logo = new JLabel();
		logo.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(GitConfigPanel.class.getResource("/Git.png"))));
		add(logo, "cell 1 0");
		
		JLabel lblGitIsA = new JLabel("Configure git (a distributed version control system).");
		add(lblGitIsA, "cell 1 1");
		
		JLabel lblFullName = new JLabel("Full name");
		add(lblFullName, "cell 0 2,alignx right,aligny center");
		
		fullName = new JTextField(gitConfig.getUserFullName());
		add(fullName, "cell 1 2,growx,aligny top");
		fullName.setColumns(10);
		
		JLabel label = new JLabel("Enter your first and last name  (e.g., John Smith).");
		label.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(label, "cell 1 3,aligny top");
		
		add(new JLabel("Username"), "cell 0 4,alignx right");
		
		textField = new JTextField(gitConfig.getUsername());
		textField.setColumns(10);
		add(textField, "cell 1 4,growx");
		
		JLabel label_2 = new JLabel("Enter your preferred username for project hosts (e.g., smithj).");
		label_2.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(label_2, "cell 1 5,aligny top");
		
		JLabel label_3 = new JLabel("Email address");
		add(label_3, "flowx,cell 0 6,alignx right");
		
		textField_1 = new JTextField(gitConfig.getUserEmail());
		textField_1.setColumns(10);
		add(textField_1, "cell 1 6,growx");
		
		JLabel label_4 = new JLabel("Enter your .edu email address.");
		label_4.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(label_4, "cell 1 7,aligny top");
	}

}
