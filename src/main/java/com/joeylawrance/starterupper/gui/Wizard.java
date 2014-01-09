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
	private JList list;
	private ListSelectionListener selectionListener;
	private DefaultListModel steps;
	private int currentIndex = 0;

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

		stepTitle = new JLabel();
		stepTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
		horizontalBox.add(stepTitle, "cell 0 0,alignx left,aligny center");

		JSeparator separator = new JSeparator();
		header.add(separator);

		steps = new DefaultListModel();
		list = new JList(steps);
		list.setBorder(new EmptyBorder(0, 3, 0, 0));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setPreferredSize(new Dimension(120,320));
		list.setFont(new Font("Tahoma", Font.PLAIN, 11));

		selectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				gotoStep(list.getSelectedIndex());
			}
		};
		
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
		nextButton = new JButton("Next");
		finishButton = new JButton("Finish");

		navigationControls.add(backButton, "cell 1 0,alignx left,aligny center");
		navigationControls.add(nextButton, "cell 2 0,alignx left,aligny center");
		navigationControls.add(finishButton, "cell 3 0,alignx left,aligny center");
		
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				goBack();
			}
		});
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				goNext();
			}
		});
		finishButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
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
		gotoStep(0);
	}
	private void updateState() {
		// Don't fire events, silly
		list.removeListSelectionListener(selectionListener);
		list.setSelectedIndex(currentIndex);
		list.addListSelectionListener(selectionListener);
		
		backButton.setEnabled(currentIndex > 0);
		nextButton.setEnabled(currentIndex < steps.getSize() - 1);
		finishButton.setEnabled(currentIndex == steps.getSize() - 1);
	}
	private void gotoStep(int index) {
		CardLayout panelLayout = (CardLayout) panel.getLayout();
		String name = steps.getElementAt(index).toString();
		stepTitle.setText(name);
		panelLayout.show(panel, name);
		panel.getComponents()[index].setVisible(true);
		currentIndex = index;
		updateState();
	}
	private void goNext() {
		gotoStep(currentIndex+1);
	}
	private void goBack() {
		gotoStep(currentIndex-1);
	}
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		list.addListSelectionListener(selectionListener);
	}
}
