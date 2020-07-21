package com.untamedears.jukealert.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CoreDestroyEvent extends Event {
	
	public enum Cause {
		PLAYER, CULL, CLEANUP;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Player getPlayer() {
		return null;
	}
}
