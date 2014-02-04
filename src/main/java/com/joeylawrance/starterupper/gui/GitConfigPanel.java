package com.joeylawrance.starterupper.gui;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.joeylawrance.starterupper.model.GitConfigKey;

import java.awt.Toolkit;

import net.miginfocom.swing.MigLayout;


/**
 * GitConfigPanel is just a view into ~/.gitconfig
 *
 */
@SuppressWarnings("serial")
public class GitConfigPanel extends ExtendedJPanel {
	/**
	 * The user interface for a GitUserModel.
	 * It fires a "hasProblems" property change event if any field in it has problems.
	 * 
	 * @param gitUserModel
	 * @throws Exception
	 */
	public GitConfigPanel() throws Exception {
		setName("Name & email");
		setLayout(new MigLayout("", "[70.00px,right][grow]", "[76.00][11.00,top][][][][][][][][grow,top]"));
		
		JLabel logo = new JLabel();
		logo.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(GitConfigPanel.class.getResource("/Git.png"))));
		add(logo, "cell 1 0");
		
		add(new JLabel("Enter your profile information for git and hosting services."), "cell 1 1");
		
		add(new JLabel("Name"), "cell 0 2,alignx trailing");
		
		JTextField field = new JTextField();
		field.setName(GitConfigKey.firstname.name());
		field.setToolTipText("First name (e.g., John)");
		field.setColumns(10);
		add(field, "flowx,cell 1 2,growx");
		
		field = new JTextField();
		field.setName(GitConfigKey.lastname.name());
		field.setToolTipText("Last name (e.g., Smith)");
		field.setColumns(10);
		add(field, "cell 1 2,growx");

		add(new JLabel("Username"), "cell 0 3,alignx trailing");
		field = new JTextField();
		field.setName(GitConfigKey.defaultname.name());
		field.setToolTipText("Preferred username (e.g., smithj)");
		add(field, "cell 1 3,growx");
		
		add(new JLabel("Email address"), "cell 0 4,alignx trailing");
		field = new JTextField();
		field.setName(GitConfigKey.email.name());
		field.setToolTipText("Your .edu email address");
		add(field, "cell 1 4,growx");
		
		add(new JLabel("School"), "cell 0 5,alignx trailing");
		field = new JTextField();
		field.setName(GitConfigKey.organization.name());
		field.setToolTipText("School name");
		add(field, "cell 1 5,growx");
	}
}
