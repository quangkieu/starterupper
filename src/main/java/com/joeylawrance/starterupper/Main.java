package com.joeylawrance.starterupper;

import com.joeylawrance.starterupper.gui.GitConfigPanel;
import com.joeylawrance.starterupper.gui.HostConfigPanel;
import com.joeylawrance.starterupper.gui.PicturePanel;
import com.joeylawrance.starterupper.gui.RepositoryPanel;
import com.joeylawrance.starterupper.gui.Wizard;
import com.joeylawrance.starterupper.model.BitbucketModel;
import com.joeylawrance.starterupper.model.GitHubModel;
import com.joeylawrance.starterupper.model.GitLabModel;
import com.joeylawrance.starterupper.model.GravatarModel;

public class Main {
    public static void main(String[] args) throws Exception {
    	GravatarModel gravatar = new GravatarModel();

    	Wizard w = new Wizard();
    	
		w.addStep("Name & email", new GitConfigPanel());
		w.addStep("Gravatar setup", new HostConfigPanel(gravatar));
		w.addStep("Profile picture", new PicturePanel(gravatar));
		w.addStep("Bitbucket setup", new HostConfigPanel(new BitbucketModel()));
		w.addStep("GitHub setup", new HostConfigPanel(new GitHubModel()));
		w.addStep("GitLab setup", new HostConfigPanel(new GitLabModel()));
		w.addStep("Repository setup", new RepositoryPanel());

    	w.setVisible(true);
    }
}
