package com.joeylawrance.starterupper.model;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RemoteConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines methods to create a repository from an upstream URL, add remotes and push to all of them.
 * 
 * @author Joey Lawrance
 *
 */
public class GitClient {
	final Logger logger = LoggerFactory.getLogger(GitClient.class);
	String upstreamRepositoryURL;
	String upstreamRepositoryHost;
	String upstreamRepositoryOwner;
	String upstreamRepositoryName;
	File localRepositoryLocation;
	
	StoredConfig config;
	Git git;
	
	public GitClient() {
		
	}
	
	/**
	 * Sets the upstream repository, using the provided URL.
	 * 
	 * @param repositoryURL
	 * @return Whether we were able to setup the upstream properly.
	 */
	public boolean setUpstreamRepository(String repositoryURL) {
		Pattern pattern = Pattern.compile("(//|@)(.*)[/:](.*)/(.+?)(\\.git)?$");
		Matcher matcher = pattern.matcher(repositoryURL);
		if (matcher.find()) {
			upstreamRepositoryHost = matcher.group(2);
			upstreamRepositoryOwner = matcher.group(3);
			upstreamRepositoryName = matcher.group(4);
			// Use HTTPS upstream URL in case we clone from a different host than the one we use.
			this.upstreamRepositoryURL = String.format("https://%s/%s/%s.git", upstreamRepositoryHost, upstreamRepositoryOwner, upstreamRepositoryName);
			localRepositoryLocation = new File(new File(System.getProperty("user.home")),upstreamRepositoryName);
			return true;
		}
		return false;
	}

	public String getUpstreamRepositoryOwner() {
		return upstreamRepositoryOwner;
	}
	
	public String getUpstreamRepositoryHost() {
		return upstreamRepositoryHost;
	}

	public String getUpstreamRepositoryName() {
		return upstreamRepositoryName;
	}

	/**
	 * Need to set a reasonable location to place the repo.
	 * @param path
	 */
	public void setLocalRepositoryLocation(File path) {
		this.localRepositoryLocation = path;
	}
	
	/**
	 * Get the local repository location.
	 * @return The repository location
	 */
	public File getLocalRepositoryLocation() {
		return this.localRepositoryLocation;
	}
	
	/**
	 * git init, more or less.
	 * @throws Exception
	 */
	public void initRepository() {
		try {
			Git.init().setDirectory(localRepositoryLocation).setBare(false).call();
			git = Git.open(localRepositoryLocation);
			config = git.getRepository().getConfig();
		} catch (GitAPIException e) {
			logger.error("Unable to initialize repository. {}",e.getMessage());
		} catch (IOException e) {
			logger.error("Unable to initialize repository. {}",e.getMessage());
		}
	}
	
	/**
	 * git remote add name url
	 * 
	 * @param name
	 * @param url
	 * @throws Exception
	 */
	public void addRemote(String name, String url) {
		config.setString("remote", name, "url", url);
		config.setString("remote", name, "fetch", String.format("+refs/heads/*:refs/remotes/%s/*", name));
		try {
			config.save();
		} catch (IOException e) {
			logger.error("Unable to save remote. {}", e.getMessage());
		}
	}

	/**
	 * Doesn't actually clone. Instead it does:
	 * 
	 * git remote add upstream upstreamURL
	 * git pull upstream master
	 * 
	 * @throws Exception
	 */
	public void cloneUpstreamRepository() throws Exception {
		addRemote("upstream",upstreamRepositoryURL);
		
		// JGit is too hard, so I'll temporarily set the remote to upstream
		config.setString("branch", "master", "remote", "upstream");
		config.setString("branch", "master", "merge", "refs/heads/master");
		config.save();
		
		git.pull().call();

		// And... now we're back go pulling from origin by default, like normal people.
		config.setString("branch", "master", "remote", "origin");
		config.save();
	}
	
	/**
	 * Push to all remotes.
	 */
	public void pushAll() {
		List<RemoteConfig> remotes;
		try {
			remotes = RemoteConfig.getAllRemoteConfigs(git.getRepository().getConfig());
			for (RemoteConfig remote : remotes) {
				try {
					git.push().setRemote(remote.getName()).call();
				} catch (GitAPIException e) {
					logger.error("Encountered a problem with the push. {}", e.getMessage());
				}
			}
		} catch (URISyntaxException e) {
			logger.error("Unable to load remotes for repository. {}", e.getMessage());
		}
	}
}
