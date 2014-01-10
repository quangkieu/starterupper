package com.joeylawrance.starterupper.gui;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import java.awt.Font;
import java.awt.Toolkit;

import net.miginfocom.swing.MigLayout;

import com.joeylawrance.starterupper.model.GitUserModel;

@SuppressWarnings("serial")
public class GitConfigPanel extends JPanel {
	private final GitUserModel gitConfig;
	private JTextField fullName;
	private JTextField userName;
	private JTextField email;
	private JTextField firstName;
	private JTextField lastName;
	
	public GitConfigPanel() throws Exception {
		gitConfig = GitUserModel.getInstance();
		setLayout(new MigLayout("", "[45px,right][grow]", "[51.00][11.00,top][][][][][20px][11.00][][10.00][20px][10.00px]"));
		
		JLabel logo = new JLabel();
		logo.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(GitConfigPanel.class.getResource("/Git.png"))));
		add(logo, "cell 1 0");
		
		JLabel lblGitIsA = new JLabel("Configure git (a distributed version control system).");
		add(lblGitIsA, "cell 1 1");
		
		JLabel lblFirstName = new JLabel("First name");
		add(lblFirstName, "cell 0 2,alignx trailing");
		
		firstName = new JTextField(gitConfig.getFirstname());
		add(firstName, "cell 1 2,growx");
		firstName.setColumns(10);
		
		JLabel lblEnterYourFirst = new JLabel("Enter your first name (e.g., John).");
		lblEnterYourFirst.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(lblEnterYourFirst, "cell 1 3");
		
		JLabel lblLastName = new JLabel("Last name");
		add(lblLastName, "cell 0 4,alignx trailing");
		
		lastName = new JTextField(gitConfig.getLastname());
		add(lastName, "cell 1 4,growx");
		lastName.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Enter your last name (e.g., Smith).");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(lblNewLabel, "cell 1 5");
		
		JLabel lblFullName = new JLabel("Full name");
		add(lblFullName, "cell 0 6,alignx right,aligny center");
		
		fullName = new JTextField(gitConfig.getFullname());
		add(fullName, "cell 1 6,growx,aligny top");
		fullName.setColumns(10);
		
		JLabel label = new JLabel("Enter your first and last name  (e.g., John Smith).");
		label.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(label, "cell 1 7,aligny top");
		
		add(new JLabel("Username"), "cell 0 8,alignx right");
		
		userName = new JTextField(gitConfig.getUsername());
		userName.setColumns(10);
		add(userName, "cell 1 8,growx");
		
		JLabel label_2 = new JLabel("Enter your preferred username for project hosts (e.g., smithj).");
		label_2.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(label_2, "cell 1 9,aligny top");
		
		JLabel label_3 = new JLabel("Email address");
		add(label_3, "flowx,cell 0 10,alignx right");
		
		email = new JTextField(gitConfig.getEmail());
		email.setColumns(10);
		add(email, "cell 1 10,growx");
		
		JLabel label_4 = new JLabel("Enter your .edu email address.");
		label_4.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(label_4, "cell 1 11,aligny top");
		this.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent arg0) {
				System.out.println("Im visible now?");
			}
			@Override
			public void ancestorMoved(AncestorEvent arg0) {
			}
			@Override
			public void ancestorRemoved(AncestorEvent arg0) {
				System.out.println("Im not visible now?");
			}
		});
	}
}
