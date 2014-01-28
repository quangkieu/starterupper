package com.joeylawrance.starterupper;

import com.joeylawrance.starterupper.gui.GitConfigPanel;
import com.joeylawrance.starterupper.gui.HostConfigPanel;
import com.joeylawrance.starterupper.gui.PicturePanel;
import com.joeylawrance.starterupper.gui.RepositoryPanel;
import com.joeylawrance.starterupper.gui.Wizard;
import com.joeylawrance.starterupper.model.GitConfig;
import com.joeylawrance.starterupper.model.host.impl.Bitbucket;
import com.joeylawrance.starterupper.model.host.impl.GitHub;
import com.joeylawrance.starterupper.model.host.impl.GitLab;
import com.joeylawrance.starterupper.model.host.impl.Gravatar;

public class Main {
    public static void main(String[] args) throws Exception {
    	Gravatar gravatar = new Gravatar();
    	Bitbucket bb = new Bitbucket();
    	GitHub gh = new GitHub();
    	GitLab gl = new GitLab();

    	Wizard w = new Wizard();
    	
		w.addStep(new GitConfigPanel(new GitConfig()));
		w.addStep(new HostConfigPanel(gravatar));
		w.addStep(new PicturePanel(gravatar));
		w.addStep(new HostConfigPanel(bb));
		w.addStep(new HostConfigPanel(gh));
		w.addStep(new HostConfigPanel(gl));
		RepositoryPanel repo = new RepositoryPanel(bb, gh, gl);
		w.addStep(repo);
		w.addActionListener(repo);

    	w.setVisible(true);
    }
}
