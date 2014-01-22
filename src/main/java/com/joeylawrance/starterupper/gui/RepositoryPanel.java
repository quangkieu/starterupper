package com.joeylawrance.starterupper.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextField;
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

import com.joeylawrance.starterupper.model.GitClient;
import com.joeylawrance.starterupper.model.host.GitHostRepository;
import com.joeylawrance.starterupper.model.host.Host;
import com.joeylawrance.starterupper.model.host.HostAction;
import com.joeylawrance.starterupper.model.host.HostListener;

@SuppressWarnings("serial")
public class RepositoryPanel extends JPanel implements HostListener {
	private JTextField upstream;
	private JTextField local;
	private JList<String> list;
	private SwingValidationGroup fieldValidator = SwingValidationGroup.create();
	private final DefaultListModel<String> remotes;
	public RepositoryPanel(final GitHostRepository... models) {
		setLayout(new MigLayout("", "[70px,right][grow]", "[][][100.00][]"));
		setName("Repository setup");
		
		final GitClient client = new GitClient();
		// Listen to login events
		for (GitHostRepository model : models) {
			model.addHostListener(this);
		}
		
		String hint;
		
		// Upstream UI
		add(new JLabel("Upstream"), "cell 0 0");
		upstream = new JTextField();
		upstream.setName("Upstream repository URL");
		add(upstream, "cell 1 0,growx");
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
		// Upstream hints
		PromptSupport.setFocusBehavior(PromptSupport.FocusBehavior.SHOW_PROMPT, upstream);
		hint = "Enter the upstream repository URL to fork";
		PromptSupport.setPrompt(hint, upstream);
		upstream.setToolTipText(hint);
		upstream.setColumns(10);
		
		// Local stuff
		add(new JLabel("Local"), "cell 0 1");
		local = new JTextField();
		local.setName("Local repository");
		local.setEditable(false);
		add(local, "flowx,cell 1 1,growx");
		fieldValidator.add(local, StringValidators.trimString(ValidatorUtils.merge(StringValidators.REQUIRE_NON_EMPTY_STRING,StringValidators.FILE_MUST_NOT_EXIST)));
		PromptSupport.setFocusBehavior(PromptSupport.FocusBehavior.SHOW_PROMPT, local);
		hint = "Select the local repository path";
		PromptSupport.setPrompt(hint, local);
		local.setColumns(10);
		
		JButton browse = new JButton("Browse...");
		browse.setToolTipText(hint);
		add(browse, "cell 1 1");
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
		
		add(new JLabel("Remotes"), "cell 0 2,alignx right,aligny top");
		list = new JList<String>();
		list.setVisibleRowCount(5);
		remotes = new DefaultListModel<String>();
		list.setModel(remotes);
		list.setName("Remote repositories");
		list.setToolTipText("Log in to at least one git host and select the default (origin)");
		add(list, "cell 1 2,grow");
		
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
		
		add(fieldValidator.createProblemLabel(), "cell 1 3,growx");
		final ValidationPanel p = new ValidationPanel(fieldValidator);
		p.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				RepositoryPanel.this.firePropertyChange("hasProblems", !p.isFatalProblem(), p.isFatalProblem());
			}
		});
		this.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent arg0) {
				RepositoryPanel.this.firePropertyChange("hasProblems", !p.isFatalProblem(), p.isFatalProblem());
			}
			@Override
			public void ancestorMoved(AncestorEvent arg0) {
			}
			@Override
			public void ancestorRemoved(AncestorEvent arg0) {
			}
		});
	}
	@Override
	public void actionPerformed(Host host, HostAction action) {
		if (action == HostAction.login) {
			remotes.addElement(host.getHostName());
		}
	}
}
