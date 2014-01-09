package com.joeylawrance.starterupper.gui;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import java.awt.Font;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Component;

import net.miginfocom.swing.MigLayout;

import java.awt.Color;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.border.EmptyBorder;

public class Wizard extends JFrame {
	private JLabel stepTitle;
	private JButton backButton;
	private JButton nextButton;
	private JButton finishButton;
	private JPanel panel = new JPanel();
	private DefaultListModel steps;

	private JPanel horizontalBox;
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}
	public Wizard() throws Exception {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Wizard.class.getResource("/Start.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Starter Upper");

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

		steps = new DefaultListModel();
		JList list = new JList(steps);
		list.setBorder(new EmptyBorder(0, 3, 0, 0));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setPreferredSize(new Dimension(120,320));
		list.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				gotoStep(steps.getElementAt(arg0.getFirstIndex()).toString());
			}
			
		});
		
		getContentPane().add(list, BorderLayout.WEST);

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
		
		backButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
			
		});

		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new CardLayout(0, 0));

		this.setMinimumSize(new Dimension(500, 400));
		// Center on the screen
		this.setLocationRelativeTo(null);
	}
	public void addStep(String name, JPanel card) {
		steps.addElement(name);
		panel.add(card, name);
	}
	private void gotoStep(String name) {
		CardLayout panelLayout = (CardLayout) panel.getLayout();
		panelLayout.show(panel, name);
	}
	private boolean hasNext() {
		return true;
	}
	private boolean hasBack() {
		return false;
	}
	private void goNext() {
		
	}
	private void goBack() {
		
	}
}
