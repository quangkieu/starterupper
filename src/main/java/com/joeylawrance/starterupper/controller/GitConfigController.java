package com.joeylawrance.starterupper.controller;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.prompt.PromptSupport;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.ValidatorUtils;
import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;
import org.netbeans.validation.api.ui.swing.ValidationPanel;

import com.joeylawrance.starterupper.gui.View;
import com.joeylawrance.starterupper.model.GitConfig;
import com.joeylawrance.starterupper.model.GitConfigKey;

/**
 * Set up all the listeners and interaction logic for the GitConfigPanel.
 *
 */
public class GitConfigController {
	private SwingValidationGroup fieldValidator = SwingValidationGroup.create();
	private final GitConfig config;

	@SuppressWarnings("unchecked")
	public GitConfigController(final View view, final GitConfig config) {
		final ValidationPanel p = new ValidationPanel(fieldValidator);
		this.config = config;
		
		p.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				view.getAssociatedJComponent().firePropertyChange("hasProblems", !p.isFatalProblem(), p.isFatalProblem());
			}
		});

		// We need to tell the wizard whether we've got problems as soon as we're visible.
		view.getAssociatedJComponent().addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent arg0) {
				view.getAssociatedJComponent().firePropertyChange("hasProblems", !p.isFatalProblem(), p.isFatalProblem());
			}
			@Override
			public void ancestorMoved(AncestorEvent arg0) {
			}
			@Override
			public void ancestorRemoved(AncestorEvent arg0) {
			}
		});

		// Setup validators
		for (Component c : view.getAssociatedJComponent().getComponents()) {
			if (c instanceof JTextField) {
				JTextField field = (JTextField) c;
				Validator<String> validator = StringValidators.trimString(StringValidators.REQUIRE_NON_EMPTY_STRING);
				
				// The default text comes from the model
				field.setText(config.get(field.getName()));
				
				// Unless we've got something special, the validator requires just non-empty strings.
				if (field.getName().equals(GitConfigKey.defaultname.name())) {
					validator = StringValidators.trimString(ValidatorUtils.merge(StringValidators.REQUIRE_NON_EMPTY_STRING,StringValidators.NO_WHITESPACE));
				} else if (field.getName().equals(GitConfigKey.email.name())) {
					validator = StringValidators.trimString(ValidatorUtils.merge(StringValidators.REQUIRE_NON_EMPTY_STRING,StringValidators.NO_WHITESPACE,StringValidators.EMAIL_ADDRESS));
				}
				setupValidator(field, validator);
				
				// Unset text will get a prompt.
				PromptSupport.setFocusBehavior(PromptSupport.FocusBehavior.SHOW_PROMPT, field);
				PromptSupport.setPrompt(field.getToolTipText(), field);
			}
		}
//		view.getAssociatedJComponent().add(fieldValidator.createProblemLabel(), String.format("cell 1 %s,growx", view.getRowCount()));
	}
	private void setupValidator(final JTextField field, final Validator<String> validator) {
		fieldValidator.add(field, validator);
		field.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
			}
			@Override
			public void focusLost(FocusEvent arg0) {
				Problems problems = new Problems();
				validator.validate(problems, field.getName(), field.getText());
				if (!problems.hasFatal()) {
					config.put(field.getName(), field.getText().trim());
				}
			}
		});
	}
}
