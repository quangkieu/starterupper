package com.joeylawrance.starterupper;

import com.joeylawrance.starterupper.gui.GitConfigPanel;
import com.joeylawrance.starterupper.gui.HostConfigPanel;
import com.joeylawrance.starterupper.gui.PicturePanel;
import com.joeylawrance.starterupper.gui.RepositoryPanel;
import com.joeylawrance.starterupper.gui.Wizard;
import com.joeylawrance.starterupper.model.BitbucketModel;
import com.joeylawrance.starterupper.model.GitHubModel;
import com.joeylawrance.starterupper.model.GitLabModel;
import com.joeylawrance.starterupper.model.GitUserMap;
import com.joeylawrance.starterupper.model.GravatarModel;
import com.joeylawrance.starterupper.util.ObservableMap;

public class Main {
    public static void main(String[] args) throws Exception {
    	GitUserMap user = new GitUserMap();
    	ObservableMap<GitUserMap.Profile, String> map = new ObservableMap<GitUserMap.Profile, String>(user);

    	GravatarModel gravatar = new GravatarModel();
    	map.addObservableMapListener(gravatar);
    	BitbucketModel bb = new BitbucketModel();
    	map.addObservableMapListener(bb);
    	GitHubModel gh = new GitHubModel();
    	map.addObservableMapListener(gh);
    	GitLabModel gl = new GitLabModel();
    	map.addObservableMapListener(gl);
    	map.fire();

    	Wizard w = new Wizard();
    	
		w.addStep(new GitConfigPanel(map));
		w.addStep(new HostConfigPanel(gravatar));
		w.addStep(new PicturePanel(gravatar));
		w.addStep(new HostConfigPanel(bb));
		w.addStep(new HostConfigPanel(gh));
		w.addStep(new HostConfigPanel(gl));
		w.addStep(new RepositoryPanel());

    	w.setVisible(true);
    }
}
