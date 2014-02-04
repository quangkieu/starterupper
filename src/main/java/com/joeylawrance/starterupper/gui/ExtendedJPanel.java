package com.joeylawrance.starterupper.gui;

import java.awt.Component;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ExtendedJPanel extends JPanel implements View {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getComponent(String name, Class<T> klass) {
		for (Component c : this.getComponents()) {
			if (c.getName() != null && c.getName().equals(name)) {
				return (T) c;
			}
		}
		return (T) this;
	}

}
