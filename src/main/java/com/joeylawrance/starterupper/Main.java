package com.joeylawrance.starterupper;

import com.joeylawrance.starterupper.controller.GitConfigController;
import com.joeylawrance.starterupper.controller.HostConfigController;
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
    	// Create the Wizard UI
    	Wizard w = new Wizard();
    	
    	GitConfig config = new GitConfig();
    	GitConfigPanel configPanel = new GitConfigPanel();
    	new GitConfigController(configPanel, config);
    	Gravatar gravatar = new Gravatar();

		w.addStep(configPanel);
		w.addStep(new HostConfigController(new HostConfigPanel(), gravatar).getPanel());
		w.addStep(new PicturePanel(gravatar));
		w.addStep(new HostConfigController(new HostConfigPanel(), new Bitbucket()).getPanel());
		w.addStep(new HostConfigController(new HostConfigPanel(), new GitHub()).getPanel());
		w.addStep(new HostConfigController(new HostConfigPanel(), new GitLab()).getPanel());
		RepositoryPanel repo = new RepositoryPanel();
		w.addStep(repo);
		w.addActionListener(repo);
		
		// Tell all the hosts about the git configuration.
		config.postConfiguration();

		// Show
    	w.setVisible(true);
    }
}
