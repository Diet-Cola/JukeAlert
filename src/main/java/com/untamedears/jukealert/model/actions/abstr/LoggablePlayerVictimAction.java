package com.untamedears.jukealert.model.actions.abstr;

import java.util.UUID;

import org.bukkit.Location;

import com.untamedears.jukealert.model.actions.LoggedActionPersistence;

public abstract class LoggablePlayerVictimAction extends SnitchAction {
	
	protected final String victim;
	protected final Location location;

	public LoggablePlayerVictimAction(long time, UUID player, Location location, String victim) {
		super(time, player);
		this.victim = victim;
		this.location = location;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public String getVictim() {
		return victim;
	}
 	
	@Override
	public LoggedActionPersistence getPersistence() {
		return new LoggedActionPersistence(player, null, time, victim);
	}

}
