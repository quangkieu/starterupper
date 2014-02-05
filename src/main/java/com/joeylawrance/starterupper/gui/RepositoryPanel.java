package com.joeylawrance.starterupper.gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class RepositoryPanel extends ExtendedJPanel {
	public static enum Controls {
		UPSTREAM, LOCAL, REMOTES, BROWSE, STATUS, PROGRESS
	}
	public RepositoryPanel() {
		setLayout(new MigLayout("", "[70px,right][grow]", "[][][100.00][][][]"));
		setName("Repository setup");
		
		add(new JLabel("Upstream"), "cell 0 0");
		JTextField upstream = new JTextField();
		upstream.setName(Controls.UPSTREAM.name());
		add(upstream, "cell 1 0,growx");
		upstream.setToolTipText("Enter the upstream repository URL to fork");
		upstream.setColumns(10);
		
		add(new JLabel("Local"), "cell 0 1");
		JTextField local = new JTextField();
		local.setName(Controls.LOCAL.name());
		local.setEditable(false);
		local.setToolTipText("Select the local repository path");
		add(local, "flowx,cell 1 1,growx");
		local.setColumns(10);
		
		JButton browse = new JButton("Browse...");
		browse.setName(Controls.BROWSE.name());
		browse.setToolTipText("Select the local repository path");
		add(browse, "cell 1 1");
		
		add(new JLabel("Remotes"), "cell 0 2,alignx right,aligny top");
		JList<String> list = new JList<String>();
		list.setVisibleRowCount(5);
		list.setName(Controls.REMOTES.name());
		list.setToolTipText("Log in to at least one git host and select the default (origin)");
		add(list, "cell 1 2,grow");
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setName(Controls.PROGRESS.name());
		progressBar.setVisible(false);
		add(progressBar, "cell 1 4,growx");

		JLabel status = new JLabel("Status message here");
		status.setVisible(false);
		status.setName(Controls.STATUS.name());
		add(status, "cell 1 5,growx");
	}
}
