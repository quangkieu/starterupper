package com.joeylawrance.starterupper.gui;

public interface View {
	/**
	 * Get the component associated with the name.
	 * @param name The component to retrieve.
	 * @param klass The type of the component to retrieve.
	 * @return A component with the given name. If null, return the entire view.
	 */
	<T> T getComponent(String name, Class<T> klass);
}
