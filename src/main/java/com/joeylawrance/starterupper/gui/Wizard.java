package com.joeylawrance.starterupper.gui;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.Box;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.miginfocom.swing.MigLayout;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class Wizard extends JFrame {
	private JLabel stepTitle;
	private JButton backButton;
	private JButton nextButton;
	private JButton finishButton;
	private JPanel panel = new JPanel();
	private JList<String> list;
	private ListSelectionListener selectionListener;
	private DefaultListModel<String> steps;
	private int currentIndex = 0;
	private boolean hasProblems = false;

	private JPanel horizontalBox;
	private JLabel status;
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

		header.add(new JSeparator());

		steps = new DefaultListModel<String>();
		list = new JList<String>(steps);
		list.setBorder(new EmptyBorder(0, 3, 0, 0));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setPreferredSize(new Dimension(120,320));
		list.setFont(new Font("Tahoma", Font.PLAIN, 11));

		selectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!hasProblems) {
					gotoStep(list.getSelectedIndex());
				} else if (hasProblems && list.getSelectedIndex() < currentIndex) {
					hasProblems = false;
					gotoStep(list.getSelectedIndex());
				} else {
					updateState();
				}
			}
		};
		
		getContentPane().add(list, BorderLayout.WEST);

		Box footer = Box.createVerticalBox();
		getContentPane().add(footer, BorderLayout.SOUTH);

		footer.add(new JSeparator());

		JPanel navigationControls = new JPanel();
		footer.add(navigationControls);
		navigationControls.setLayout(new MigLayout("", "[14.00px,grow,fill][55px][55px][59px]", "[30.00px]"));
		
		status = new JLabel();
		navigationControls.add(status, "flowx,cell 0 0");

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

		this.setMinimumSize(new Dimension(640, 480));
		// Center on the screen
		this.setLocationRelativeTo(null);
	}
	public void addStep(JPanel card) {
		steps.addElement(card.getName());
		panel.add(card, card.getName());
		card.addPropertyChangeListener("hasProblems", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent hasProblem) {
				hasProblems = (Boolean)hasProblem.getNewValue();
				updateState();
			}
		});
		gotoStep(0);
	}
	// Set the button and list selection states.
	private void updateState() {
		// Don't fire events, silly
		list.removeListSelectionListener(selectionListener);
		list.setSelectedIndex(currentIndex);
		list.addListSelectionListener(selectionListener);
		
		if (hasProblems) {
			status.setText(String.format("You need to address any errors in the form above%s before continuing.", (currentIndex > 0) ? " or go back":""));
		} else {
			status.setText("");
		}
		
		// If we can go back, we should always be able to
		backButton.setEnabled(currentIndex > 0);
		// We can't go forward or finish with fatal problems.
		nextButton.setEnabled(!hasProblems && currentIndex < steps.getSize() - 1);
		finishButton.setEnabled(!hasProblems && currentIndex == steps.getSize() - 1);
	}
	// Change the step title, show the step, update the button and list selection states.
	private void gotoStep(int index) {
		CardLayout panelLayout = (CardLayout) panel.getLayout();
		String name = steps.getElementAt(index);
		stepTitle.setText(name);
		hasProblems = false;
		panelLayout.show(panel, name);
		currentIndex = index;
		updateState();
	}
	private void goNext() {
		gotoStep(currentIndex+1);
	}
	private void goBack() {
		gotoStep(currentIndex-1);
	}
}
