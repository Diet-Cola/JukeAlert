package com.untamedears.jukealert.events;

import java.util.function.Supplier;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.untamedears.jukealert.model.actions.abstr.SnitchAction;

public class SnitchActionEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	private Supplier<SnitchAction> supplier;
	private Location location;
	
	public SnitchActionEvent(Supplier<SnitchAction> supplier, Location location) {
		this.supplier = supplier;
		this.location = location;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public SnitchAction getAction() {
		return supplier.get();
	}

}
