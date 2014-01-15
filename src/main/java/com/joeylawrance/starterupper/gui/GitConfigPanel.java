package com.joeylawrance.starterupper.gui;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.prompt.PromptSupport;
import org.netbeans.validation.api.AbstractValidator;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Severity;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.ValidatorUtils;
import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;
import org.netbeans.validation.api.ui.swing.ValidationPanel;

import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import net.miginfocom.swing.MigLayout;

import com.joeylawrance.starterupper.model.GitUserModel;

@SuppressWarnings("serial")
public class GitConfigPanel extends JPanel {
	private final GitUserModel gitConfig;
	private HashMap<String, JTextField> fields = new HashMap<String, JTextField>();
	private SwingValidationGroup fieldValidator = SwingValidationGroup.create();
	private final ValidationPanel p;
	
	private int row;
	
	/**
	 * Create a form text area with a label, tooltip and validator.
	 * @param name
	 * @param description
	 */
	private void addForm(String name, String tooltip, Validator<String> validator) {
		add(new JLabel(name), String.format("cell 0 %s,alignx trailing", row));
		JTextField field = new JTextField(gitConfig.getByName(name));
		field.setName(name);
		fieldValidator.add(field, validator);
		PromptSupport.setFocusBehavior(PromptSupport.FocusBehavior.SHOW_PROMPT, field);
		PromptSupport.setPrompt(tooltip, field);
		field.setToolTipText(tooltip);
		field.setColumns(10);
		fields.put(name, field);
		add(field, String.format("cell 1 %s,growx",row));
		row++;

	}
	
	public GitConfigPanel() throws Exception {
		gitConfig = GitUserModel.getInstance();
		setLayout(new MigLayout("", "[45px,right][grow]", "[51.00][11.00,top][][][][][][][][][][grow,top]"));
		
		JLabel logo = new JLabel();
		logo.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(GitConfigPanel.class.getResource("/Git.png"))));
		add(logo, "cell 1 0");
		
		add(new JLabel("Enter your name, username and email for git and hosting services."), "cell 1 1");
		row = 2;
		
		addForm("First name", "Enter your first name (e.g., John).", StringValidators.trimString(StringValidators.REQUIRE_NON_EMPTY_STRING));
		addForm("Last name", "Enter your last name (e.g., Smith).", StringValidators.trimString(StringValidators.REQUIRE_NON_EMPTY_STRING));
		addForm("Full name", "Enter your first and last name (e.g., John Smith).", StringValidators.trimString(StringValidators.REQUIRE_NON_EMPTY_STRING));
		addForm("Username", "Enter your preferred username for project hosts (e.g., smithj).", StringValidators.trimString(ValidatorUtils.merge(StringValidators.REQUIRE_NON_EMPTY_STRING,StringValidators.NO_WHITESPACE)));
		addForm("Email address", "Enter your .edu email address.", StringValidators.trimString(ValidatorUtils.merge(StringValidators.REQUIRE_NON_EMPTY_STRING,StringValidators.NO_WHITESPACE,StringValidators.EMAIL_ADDRESS)));

		add(fieldValidator.createProblemLabel(), String.format("cell 1 %s,growx", row));
		
		p = new ValidationPanel(fieldValidator);
		p.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				GitConfigPanel.this.firePropertyChange("hasProblems", !p.isFatalProblem(), p.isFatalProblem());
			}
		});

		row++;

		this.addAncestorListener(new AncestorListener() {
			
			// We need to tell the wizard whether we've got problems as soon as we're visible.
			@Override
			public void ancestorAdded(AncestorEvent arg0) {
				GitConfigPanel.this.firePropertyChange("hasProblems", false, p.isFatalProblem());
			}
			@Override
			public void ancestorMoved(AncestorEvent arg0) {
			}
			@Override
			public void ancestorRemoved(AncestorEvent arg0) {
				System.out.println("Im not visible now?");
			}
		});
	}
}