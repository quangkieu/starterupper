package com.joeylawrance.starterupper.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.BoxLayout;
import javax.swing.JTextPane;
import javax.swing.ButtonGroup;
import net.miginfocom.swing.MigLayout;

public class HostSelectionPanel extends JPanel {
	private final ButtonGroup buttonGroup = new ButtonGroup();
	public HostSelectionPanel() {
		setLayout(new MigLayout("", "[146px][93px,grow]", "[28.00,top][grow,top]"));
		
		JLabel lblSelectAProject = new JLabel("Select a default project host.");
		add(lblSelectAProject, "cell 0 0,alignx left,aligny top");
		
		JPanel panel = new JPanel();
		add(panel, "cell 0 1");
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("Bitbucket");
		buttonGroup.add(rdbtnNewRadioButton);
		panel.add(rdbtnNewRadioButton);
		
		JRadioButton rdbtnDeveo = new JRadioButton("Deveo");
		buttonGroup.add(rdbtnDeveo);
		panel.add(rdbtnDeveo);
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("GitEnterprise");
		buttonGroup.add(rdbtnNewRadioButton_1);
		panel.add(rdbtnNewRadioButton_1);
		
		JRadioButton rdbtnNewRadioButton_2 = new JRadioButton("Github");
		buttonGroup.add(rdbtnNewRadioButton_2);
		panel.add(rdbtnNewRadioButton_2);
		
		JRadioButton rdbtnNewRadioButton_3 = new JRadioButton("GitLab Cloud");
		buttonGroup.add(rdbtnNewRadioButton_3);
		panel.add(rdbtnNewRadioButton_3);
		
		JTextPane textPane = new JTextPane();
		textPane.setText("Explanation is a good thing. does this word wrap?");
		textPane.setOpaque(false);
		textPane.setEnabled(false);
		textPane.setEditable(false);
		add(textPane, "cell 1 1,grow");
	}

}
