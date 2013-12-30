package com.joeylawrance.starterupper.gui;

import javax.swing.JFrame;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JSeparator;

import java.awt.Font;

import javax.swing.JButton;

import java.awt.Dimension;

public class Wizard extends JFrame {
	private Box horizontalBox;
	{
    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}
	public Wizard() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Setter Upper");
		
		Box verticalBox = Box.createVerticalBox();
		getContentPane().add(verticalBox, BorderLayout.NORTH);
		
		horizontalBox = Box.createHorizontalBox();
		horizontalBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		verticalBox.add(horizontalBox);
		
		Component rigidArea_1 = Box.createRigidArea(new Dimension(20, 40));
		horizontalBox.add(rigidArea_1);
		
		JLabel stepTitle = new JLabel("New label");
		stepTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
		horizontalBox.add(stepTitle);
		
		JSeparator separator = new JSeparator();
		verticalBox.add(separator);
		
		JTree tree = new JTree();
		tree.setRootVisible(false);
		tree.setToggleClickCount(0);
		tree.setFont(new Font("Tahoma", Font.PLAIN, 11));
		getContentPane().add(tree, BorderLayout.WEST);
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Root") {
				{
					DefaultMutableTreeNode node_1;
					node_1 = new DefaultMutableTreeNode("About me");
						node_1.add(new DefaultMutableTreeNode("Name & email"));
						node_1.add(new DefaultMutableTreeNode("Public SSH key"));
						node_1.add(new DefaultMutableTreeNode("Gravatar"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("Project host setup");
						node_1.add(new DefaultMutableTreeNode("Bitbucket"));
						node_1.add(new DefaultMutableTreeNode("Deveo"));
						node_1.add(new DefaultMutableTreeNode("GitEnterprise"));
						node_1.add(new DefaultMutableTreeNode("Github"));
						node_1.add(new DefaultMutableTreeNode("GitLab Cloud"));
					add(node_1);
					add(new DefaultMutableTreeNode("Repositories"));
				}
			}
		));
		for (int i = 0; i < tree.getRowCount(); i++) {
	         tree.expandRow(i);
		}
		
		Box controls = Box.createVerticalBox();
		getContentPane().add(controls, BorderLayout.SOUTH);
		
		JSeparator separator_1 = new JSeparator();
		controls.add(separator_1);
		
		Box horizontalBox_2 = Box.createHorizontalBox();
		controls.add(horizontalBox_2);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		horizontalBox_2.add(horizontalGlue);
		
		JButton btnNewButton = new JButton("Back");
		btnNewButton.setEnabled(false);
		horizontalBox_2.add(btnNewButton);
		
		Component rigidArea = Box.createRigidArea(new Dimension(5, 40));
		horizontalBox_2.add(rigidArea);
		
		JButton btnNewButton_1 = new JButton("Next");
		horizontalBox_2.add(btnNewButton_1);
		
		Component rigidArea_3 = Box.createRigidArea(new Dimension(5, 40));
		horizontalBox_2.add(rigidArea_3);
		
		JButton btnNewButton_2 = new JButton("Finish");
		horizontalBox_2.add(btnNewButton_2);
		
		Component rigidArea_2 = Box.createRigidArea(new Dimension(20, 20));
		horizontalBox_2.add(rigidArea_2);
		
		this.setMinimumSize(new Dimension(500, 400));
		this.setLocationRelativeTo(null);
	}

}
