package com.joeylawrance.starterupper.controller;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.prompt.PromptSupport;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Severity;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.ValidatorUtils;
import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;
import org.netbeans.validation.api.ui.swing.ValidationPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.joeylawrance.starterupper.gui.RepositoryPanel;
import com.joeylawrance.starterupper.gui.View;
import com.joeylawrance.starterupper.model.Event;
import com.joeylawrance.starterupper.model.GitClient;
import com.joeylawrance.starterupper.model.host.GitHostRepository;
import com.joeylawrance.starterupper.model.host.HostAction;
import com.joeylawrance.starterupper.model.host.HostPerformedAction;

public class RepositoryController implements ActionListener {
	private final Logger logger = LoggerFactory.getLogger(RepositoryController.class);
	private SwingValidationGroup fieldValidator = SwingValidationGroup.create();
	final Set<GitHostRepository> models;
	private JTextField upstream;
	private JTextField local;
	private JList<String> list;
	private JButton browse;
	private final DefaultListModel<String> remotes;
	private JProgressBar progressBar;
	JLabel status;
	int remoteCounter = 0;
	final GitClient client;
	@SuppressWarnings("unchecked")
	public RepositoryController(final View view) {
		this.models = new HashSet<GitHostRepository>();
		client = new GitClient();
		upstream = view.getComponent(RepositoryPanel.Controls.UPSTREAM, JTextField.class);
		local = view.getComponent(RepositoryPanel.Controls.LOCAL, JTextField.class);
		list = (JList<String>)view.getComponent(RepositoryPanel.Controls.REMOTES, JList.class);
		browse = view.getComponent(RepositoryPanel.Controls.BROWSE, JButton.class);
		progressBar = view.getComponent(RepositoryPanel.Controls.PROGRESS, JProgressBar.class);
		status = view.getComponent(RepositoryPanel.Controls.STATUS, JLabel.class);

		// Listen to login events
		Event.getBus().register(this);



		final ValidationPanel p = new ValidationPanel(fieldValidator);
		// Upstream validation
		final Validator<String> upstreamValidator = new Validator<String> () {
			@Override
			public void validate(Problems problems, String compName,
					String model) {
				if (model.trim().isEmpty()) {
					problems.add("Upstream repository URL may not be empty", Severity.FATAL);
				} else if (!client.setUpstreamRepository(model.trim())) {
					problems.add("Upstream repository URL is not valid", Severity.FATAL);
				} else {
					local.setText(client.getLocalRepositoryLocation().getAbsolutePath());
					for (GitHostRepository m : models) {
						m.setPrivateRepositoryName(client.getUpstreamRepositoryName());
					}
				}
			}

			@Override
			public Class<String> modelType() {
				return String.class;
			}
		};
		fieldValidator.add(upstream, upstreamValidator);
		PromptSupport.setFocusBehavior(PromptSupport.FocusBehavior.SHOW_PROMPT, upstream);
		PromptSupport.setPrompt(upstream.getToolTipText(), upstream);

		fieldValidator.add(local, StringValidators.trimString(ValidatorUtils.merge(StringValidators.REQUIRE_NON_EMPTY_STRING,StringValidators.FILE_MUST_NOT_EXIST)));
		PromptSupport.setFocusBehavior(PromptSupport.FocusBehavior.SHOW_PROMPT, local);
		PromptSupport.setPrompt(local.getToolTipText(), local);

		browse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					local.setText(chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		view.getComponent(null, JPanel.class).add(fieldValidator.createProblemLabel(), "cell 1 3,growx");

		remotes = new DefaultListModel<String>();
		list.setModel(remotes);

		// Eclipse isn't smart enough to figure this one out.
		/*
		fieldValidator.add(list, new Validator<Integer[]>() {
			@Override
			public void validate(Problems problems, String compName,
					Integer[] model) {
				if (model.length == 0) {
					problems.add("Log into at least one project host and select a default (origin).", Severity.FATAL);
				}
			}
			@Override
			public Class<Integer[]> modelType() {
				return Integer[].class;
			}
		});
		*/

		p.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				view.getComponent(null, JPanel.class).firePropertyChange("hasProblems", !p.isFatalProblem(), p.isFatalProblem());
			}
		});

		view.getComponent(null, JPanel.class).addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent arg0) {
				view.getComponent(null, JPanel.class).firePropertyChange("hasProblems", !p.isFatalProblem(), p.isFatalProblem());
			}
			@Override
			public void ancestorMoved(AncestorEvent arg0) {
			}
			@Override
			public void ancestorRemoved(AncestorEvent arg0) {
			}
		});
	}
	private void bumpProgress(int value) {
		progressBar.setValue(progressBar.getValue() + value);
	}
	/**
	 * Add to remotes whenever the user logs in to a host.
	 */
	@Subscribe
	public void userLoggedIn(HostPerformedAction event) {
		if (event.action.equals(HostAction.login) && event.host instanceof GitHostRepository) {
			remotes.addElement(event.host.getHostName());
			remoteCounter++;
		}
	}
	/**
	 * Let's get it started in here when the user clicks finish.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// Disable everything
		upstream.setEnabled(false);
		local.setEnabled(false);
		browse.setEnabled(false);
		list.setEnabled(false);
		progressBar.setVisible(true);
		status.setVisible(true);
		// Finally, the moment we've been waiting for...
		starterUp();
	}

	/**
	 * This method is why we're here.
	 */
	private void starterUp() {
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				int stepsize = 100 / (3 + 3 * remoteCounter);
				status.setText("Initializing git repository and remotes...");
				client.initRepository();
				for (GitHostRepository model : models) {
					if (model.loggedIn()) {
						client.addRemote(model.getHostName().toLowerCase(), model.getRepositoryURL());
						if (list.getSelectedValue().equals(model.getHostName())) {
							client.addRemote("origin", model.getRepositoryURL());
						}
					}
				}
				bumpProgress(stepsize);

				status.setText("Cloning upstream repository...");
				client.cloneUpstreamRepository();
				bumpProgress(stepsize);
				
				// Do configuration per host
				for (GitHostRepository model : models) {
					if (model.loggedIn()) {
						status.setText(String.format("Testing SSH public/private keypair on %s...", model.getHostName()));
						if (!model.testLogin()) {
							status.setText(String.format("Sharing public SSH key with %s...", model.getHostName()));
							model.sharePublicKey();
						}
						bumpProgress(stepsize);

						status.setText(String.format("Creating private repository '%s' on %s...", client.getUpstreamRepositoryName(), model.getHostName()));
						model.createPrivateRepository();
						bumpProgress(stepsize);
						
						status.setText(String.format("Sharing private repository '%s' on %s with '%s'...", client.getUpstreamRepositoryName(), model.getHostName(), client.getUpstreamRepositoryOwner()));
						model.addCollaboratorToRepository(client.getUpstreamRepositoryOwner());
						bumpProgress(stepsize);
					}
				}
				status.setText("Pushing to all remotes...");
				client.pushAll();
				bumpProgress(stepsize);
				
				status.setText(String.format("<html>Done! <a href=\"\">Open private repository %s</a></html>", (remoteCounter > 1) ? "pages" : "page"));
				status.setCursor(new Cursor(Cursor.HAND_CURSOR));
				status.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						try {
							for (GitHostRepository model : models) {
								if (model.loggedIn()) {
									Desktop.getDesktop().browse(new URI(model.getRepositoryWebPage()));
								}
							}
						} catch (URISyntaxException ex) {
							logger.error("There's something wrong with the private repository URL.", ex);
						} catch (IOException ex) {
							logger.error("Unable to launch web browser.", ex);
						}
					}
				});
				progressBar.setValue(100);
				return null;
			}
		}.execute();
	}
}
