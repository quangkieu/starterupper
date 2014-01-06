package com.joeylawrance.starterupper;

import com.joeylawrance.starterupper.gui.Wizard;
import com.joeylawrance.starterupper.model.GravatarConfig;

public class Main {
    public static void main(String[] args) throws Exception {
//    	System.out.println(System.getProperty("user.name"));
		GravatarConfig g = new GravatarConfig("lawrancej@wit.edu");
		System.out.println(g.getURL());

    	Wizard w = new Wizard();
    	w.setVisible(true);
    }
}
