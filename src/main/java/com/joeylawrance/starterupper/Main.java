package com.joeylawrance.starterupper;

import javax.swing.UnsupportedLookAndFeelException;

import com.joeylawrance.starterupper.gui.Wizard;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    	Wizard w = new Wizard();
    	w.setVisible(true);
    	// Steps:
    	// Introduce yourself:
    	// What is your full name? (e.g., John Smith)
    	// What is your .edu email address? (e.g., smithj@wit.edu)
    	// Smile! (Snap)
    	// Select a project host:
    	// Bitbucket [username]
    	// Deveo [username]
    	// Github [username]
    	// GitEnterprise [username]
    	// GitLab Cloud [username]
    	// Use same username across hosts
    }
}
