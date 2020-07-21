package com.untamedears.jukealert.model.actions;

import java.util.UUID;

import org.bukkit.Location;

import com.untamedears.jukealert.model.actions.abstr.SnitchAction;

@FunctionalInterface
public interface SnitchActionProvider {
	
	public SnitchAction get(UUID player, Location location, long time, String victim);

}
