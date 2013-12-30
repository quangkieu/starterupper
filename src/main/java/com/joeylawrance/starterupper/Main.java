package com.joeylawrance.starterupper;

import com.joeylawrance.starterupper.gui.Wizard;
import com.joeylawrance.starterupper.model.GitConfig;
import com.joeylawrance.starterupper.model.GravatarConfig;
import com.joeylawrance.starterupper.model.KeyConfig;

public class Main {
    public static void main(String[] args) throws Exception {
    	KeyConfig k = new KeyConfig();
//    	System.out.println(System.getProperty("user.name"));
		GravatarConfig g = new GravatarConfig("lawrancej@wit.edu");
		System.out.println(g.getURL());

    	System.out.println(new String(k.getPublicKey()));
    	Wizard w = new Wizard();
    	w.setVisible(true);
    }
}
