package com.joeylawrance.starterupper.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ExtendedJPanel extends JPanel implements View {
	/**
	 * Get the component in this panel with the matching named reference and class
	 * @param reference An enum reference to the component
	 * @param klass The type of the component
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getComponent(Enum<?> reference, Class<T> klass) {
		if (reference == null) {
			return (T) this;
		}
		for (Component c : this.getComponents()) {
			if (c.getName() != null && c.getName().equals(reference.name())) {
				return (T) c;
			}
		}
		return (T) this;
	}
	public JLabel getLabelForComponent(Component component) {
		for (Component c : this.getComponents()) {
			if (c instanceof JLabel && component.equals(((JLabel) c).getLabelFor())) {
				return (JLabel)c;
			}
		}
		return null;
	}
}
