package com.joeylawrance.starterupper.model.host;


public class HostPerformedAction {
	public Host host;
	public HostAction action;
	public HostPerformedAction(Host host, HostAction action) {
		this.host = host;
		this.action = action;
	}
}