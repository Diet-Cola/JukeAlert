package com.untamedears.jukealert.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.untamedears.jukealert.model.actions.abstr.LoggableAction;
import com.untamedears.jukealert.model.actions.abstr.SnitchAction;

public class LoggableActionEvent extends Event {

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public SnitchAction getAction() {
		return null; //TODO
	}

}
