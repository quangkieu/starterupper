package com.joeylawrance.starterupper.model.host;

/**
 * An event for whenever a host performs an action.
 *
 */
public class HostPerformedAction {
	public final Host host;
	public final HostAction action;
	public final boolean successful;
	public HostPerformedAction(Host host, HostAction action, boolean successful) {
		this.host = host;
		this.action = action;
		this.successful = successful;
	}
}