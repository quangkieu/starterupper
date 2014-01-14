package com.joeylawrance.starterupper.gui;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;

import java.awt.Toolkit;
import java.util.HashMap;

import net.miginfocom.swing.MigLayout;

import com.joeylawrance.starterupper.model.GitUserModel;

@SuppressWarnings("serial")
public class GitConfigPanel extends JPanel {
	private final GitUserModel gitConfig;
	private HashMap<String, JTextField> fields = new HashMap<String, JTextField>();
	private HashMap<String, SwingValidationGroup> validationGroups = new HashMap<String, SwingValidationGroup>();
	
	private int row;
	
	/**
	 * Create a form text area with a label, tooltip and validator.
	 * @param name
	 * @param description
	 */
	private void addForm(String name, String description) {
		add(new JLabel(name), String.format("cell 0 %s,alignx trailing", row));
		JTextField field = new JTextField(gitConfig.getByName(name));
		field.setToolTipText(description);
		field.setName(name);
		field.setColumns(10);
		fields.put(name, field);
		add(field, String.format("cell 1 %s,growx", row));
		row++;
		
		SwingValidationGroup fieldValidator = SwingValidationGroup.create();
		validationGroups.put(name, fieldValidator);
		fieldValidator.add(field, StringValidators.REQUIRE_NON_EMPTY_STRING);
		add(fieldValidator.createProblemLabel(), String.format("cell 1 %s,growx", row));
		row++;
	}
	
	private void addValidator(String name, StringValidators validator) {
		validationGroups.get(name).add(fields.get(name), validator);
	}
	
	public GitConfigPanel() throws Exception {
		gitConfig = GitUserModel.getInstance();
		setLayout(new MigLayout("", "[45px,right][grow]", "[51.00][11.00,top][][][][][20px][][][][20px][grow,top]"));
		
		JLabel logo = new JLabel();
		logo.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(GitConfigPanel.class.getResource("/Git.png"))));
		add(logo, "cell 1 0");
		
		add(new JLabel("Enter your information below to configure git."), "cell 1 1");
		row = 2;
		addForm("First name", "Enter your first name (e.g., John).");
		addForm("Last name", "Enter your last name (e.g., Smith).");
		addForm("Full name", "Enter your first and last name  (e.g., John Smith).");
		addForm("Username", "Enter your preferred username for project hosts (e.g., smithj).");
		addForm("Email address", "Enter your .edu email address.");
		addValidator("Username", StringValidators.NO_WHITESPACE);
		addValidator("Email address", StringValidators.NO_WHITESPACE);
		addValidator("Email address", StringValidators.EMAIL_ADDRESS);

		this.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent arg0) {
				System.out.println("Im visible now?");
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
