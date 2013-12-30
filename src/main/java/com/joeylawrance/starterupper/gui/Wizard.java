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
import java.awt.Toolkit;

import javax.swing.JPanel;

import java.awt.CardLayout;
import net.miginfocom.swing.MigLayout;

public class Wizard extends JFrame {
	private JLabel stepTitle;
	private JButton backButton;
	private JButton nextButton;
	private JButton finishButton;
	private JPanel panel = new JPanel();

	private JPanel horizontalBox;
	{
    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}
	public Wizard() throws Exception {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Wizard.class.getResource("/Start.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Setter Upper");
		
		Box header = Box.createVerticalBox();
		getContentPane().add(header, BorderLayout.NORTH);
		
		horizontalBox = new JPanel();
		horizontalBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.add(horizontalBox);
		horizontalBox.setLayout(new MigLayout("", "[68px,grow]", "[30.00px]"));
		
		stepTitle = new JLabel("About me");
		stepTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
		horizontalBox.add(stepTitle, "cell 0 0,alignx left,aligny center");
		
		JSeparator separator = new JSeparator();
		header.add(separator);
		
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
		
		Box footer = Box.createVerticalBox();
		getContentPane().add(footer, BorderLayout.SOUTH);
		
		JSeparator separator_1 = new JSeparator();
		footer.add(separator_1);
		
		JPanel navigationControls = new JPanel();
		footer.add(navigationControls);
		navigationControls.setLayout(new MigLayout("", "[14.00px,grow,fill][55px][55px][59px]", "[30.00px]"));
		
		navigationControls.add(Box.createHorizontalGlue(), "cell 0 0,alignx left,aligny center");
		
		backButton = new JButton("Back");
		backButton.setEnabled(false);
		nextButton = new JButton("Next");
		finishButton = new JButton("Finish");
		
		navigationControls.add(backButton, "cell 1 0,alignx left,aligny center");
		navigationControls.add(nextButton, "cell 2 0,alignx left,aligny center");
		navigationControls.add(finishButton, "cell 3 0,alignx left,aligny center");
		
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new CardLayout(0, 0));
		panel.add(new GitConfigPanel());
		panel.add(new HostSelectionPanel());
		panel.add(new HostConfigPanel());
		panel.add(new RepositoryPanel());
		
		this.setMinimumSize(new Dimension(500, 400));
		// Center on the screen
		this.setLocationRelativeTo(null);
	}
}
