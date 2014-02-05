package com.joeylawrance.starterupper.gui;

import java.awt.Component;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ExtendedJPanel extends JPanel implements View {
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getComponent(Enum<?> option, Class<T> klass) {
		if (option == null) {
			return (T) this;
		}
		for (Component c : this.getComponents()) {
			if (c.getName() != null && c.getName().equals(option.name())) {
				return (T) c;
			}
		}
		return (T) this;
	}

}
