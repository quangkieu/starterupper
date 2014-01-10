package com.joeylawrance.starterupper.gui;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.Font;

import javax.swing.JTextField;
import javax.swing.Box;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class RepositoryPanel extends JPanel {
	private JTextField textField;
	private JTextField textField_1;
	private JTable table;
	public RepositoryPanel() {
		setLayout(new MigLayout("", "[][grow]", "[][][][][100.00,grow][][][]"));
		
		JLabel lblRepositoryLocation = new JLabel("Upstream repository");
		add(lblRepositoryLocation, "cell 0 0,alignx right");
		
		textField = new JTextField();
		add(textField, "cell 1 0,growx");
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Enter the repository URL to fork.");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(lblNewLabel_1, "cell 1 1");
		
		JLabel lblLocalRepository = new JLabel("Local repository");
		add(lblLocalRepository, "cell 0 2,alignx right");
		
		Box horizontalBox = Box.createHorizontalBox();
		add(horizontalBox, "flowx,cell 1 2,growx");
		
		textField_1 = new JTextField();
		horizontalBox.add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnNewButton = new JButton("Browse...");
		horizontalBox.add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel("(Optional) Change where to place the local repository.");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(lblNewLabel, "cell 1 3");
		
		JLabel lblRemoteRepositories = new JLabel("Remote repositories");
		add(lblRemoteRepositories, "cell 0 4,alignx right,aligny top");
		
		table = new JTable();
		add(table, "cell 1 4,grow");
		
		JLabel lblNewLabel_2 = new JLabel("The repositories to generate.");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(lblNewLabel_2, "cell 1 5");
		
		JButton btnGenerate = new JButton("Generate repositories");
		add(btnGenerate, "cell 1 6");
		
		JLabel lblNewLabel_3 = new JLabel("Nothing will happen until you click above.");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 10));
		add(lblNewLabel_3, "cell 1 7");
	}

}
