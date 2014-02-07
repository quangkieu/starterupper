package com.joeylawrance.starterupper.controller;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Calendar;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
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

import com.google.common.eventbus.Subscribe;
import com.joeylawrance.starterupper.gui.ExtendedJPanel;
import com.joeylawrance.starterupper.gui.View;
import com.joeylawrance.starterupper.model.ConfigChanged;
import com.joeylawrance.starterupper.model.Email2School;
import com.joeylawrance.starterupper.model.Event;
import com.joeylawrance.starterupper.model.GitConfig;
import com.joeylawrance.starterupper.model.GitConfigKey;
import com.joeylawrance.starterupper.model.WebHelper;

/**
 * Set up all the listeners and interaction logic for the GitConfigPanel.
 *
 */
public class GitConfigController {
	private SwingValidationGroup fieldValidator = SwingValidationGroup.create();
	private final GitConfig config;
	private View view;
	private final WebHelper helper;
	private final JComboBox<Integer> combo;

	@SuppressWarnings("unchecked")
	public GitConfigController(final ExtendedJPanel view, final GitConfig config) {
		this.view = view;
		
		combo = view.getComponent(GitConfigKey.graduation, JComboBox.class);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		combo.setModel(new DefaultComboBoxModel<Integer>(new Integer[] {year, year+1, year+2, year+3, year+4, year+5, year+6, year+7}));
		
		helper = new WebHelper();
		helper.newWindow("school");
		final ValidationPanel p = new ValidationPanel(fieldValidator);
		this.config = config;
		Event.getBus().register(this);
		p.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				view.getComponent(null, JPanel.class).firePropertyChange("hasProblems", !p.isFatalProblem(), p.isFatalProblem());
			}
		});

		// We need to tell the wizard whether we've got problems as soon as we're visible.
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

		// Setup validators
		for (Component c : view.getComponent(null, JPanel.class).getComponents()) {
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
		view.getComponent(GitConfigKey.student, JRadioButton.class).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				combo.setVisible(true);
				view.getLabelForComponent(combo).setVisible(true);
			}
		});
		view.getComponent(GitConfigKey.teacher, JRadioButton.class).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				combo.setVisible(false);
				view.getLabelForComponent(combo).setVisible(false);
			}
		});
		//		view.getComponent(null, JPanel.class).add(fieldValidator.createProblemLabel(), String.format("cell 1 %s,growx", view.getRowCount()));
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
	/**
	 * Attempt to enter in the school information for the user, once we know their email.
	 * @param event
	 */
	@Subscribe
	public void configurationChanged(final ConfigChanged event) {
		if (event.key.equals(GitConfigKey.email)) {
			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() {
					JTextField school = view.getComponent(GitConfigKey.school, JTextField.class);
					// Don't clobber stuff
					String existingSchool = config.get(GitConfigKey.school);
					if (existingSchool != null) {
						return null;
					}
					
					// Check for the school using whois record
					school.setEnabled(false);
					school.setText("Checking school...");
					school.setText(Email2School.schoolFromEmail(event.value));
					config.put(GitConfigKey.school, school.getText());
					school.setEnabled(true);
					return null;
				}
			}.execute();
		}
	}
}
