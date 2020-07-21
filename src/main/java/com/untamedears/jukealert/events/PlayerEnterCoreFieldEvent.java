package com.untamedears.jukealert.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import vg.civcraft.mc.citadel.model.Reinforcement;

public class PlayerEnterCoreFieldEvent extends PlayerEvent implements Cancellable {
	
	
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}
	private Location from;
	private Location to;
	private boolean cancelled;

	public PlayerEnterCoreFieldEvent(Player who, Location from, Location to) {
		super(who);
		this.from = from;
		this.to = to;
	}
	
	public Location getTo() {
		return to;
	}
	
	public Location getFrom() {
		return from;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;		
	}

}
