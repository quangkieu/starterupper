package com.joeylawrance.starterupper.model;

import com.google.common.eventbus.EventBus;

/**
 * A singleton holder for an event bus.
 * 
 * @author Joey Lawrance
 *
 */
public class Event {
	private static class SingletonHolder { 
		public static final EventBus INSTANCE = new EventBus();
	}
	public static EventBus getBus() {
		return SingletonHolder.INSTANCE;
	}
}
