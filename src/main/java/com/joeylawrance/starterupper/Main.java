package com.joeylawrance.starterupper;

import com.joeylawrance.starterupper.gui.GitConfigPanel;
import com.joeylawrance.starterupper.gui.HostConfigPanel;
import com.joeylawrance.starterupper.gui.PicturePanel;
import com.joeylawrance.starterupper.gui.RepositoryPanel;
import com.joeylawrance.starterupper.gui.Wizard;
import com.joeylawrance.starterupper.model.GitUserMap;
import com.joeylawrance.starterupper.model.host.impl.Bitbucket;
import com.joeylawrance.starterupper.model.host.impl.GitHub;
import com.joeylawrance.starterupper.model.host.impl.GitLab;
import com.joeylawrance.starterupper.model.host.impl.Gravatar;
import com.joeylawrance.starterupper.util.ObservableMap;

public class Main {
    public static void main(String[] args) throws Exception {
    	GitUserMap user = new GitUserMap();
    	ObservableMap<GitUserMap.Profile, String> map = new ObservableMap<GitUserMap.Profile, String>(user);

    	Gravatar gravatar = new Gravatar();
    	map.addObservableMapListener(gravatar);
    	Bitbucket bb = new Bitbucket();
    	map.addObservableMapListener(bb);
    	GitHub gh = new GitHub();
    	map.addObservableMapListener(gh);
    	GitLab gl = new GitLab();
    	map.addObservableMapListener(gl);
    	map.fire();

    	Wizard w = new Wizard();
    	
		w.addStep(new GitConfigPanel(map));
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
