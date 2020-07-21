package com.untamedears.jukealert.model.actions.impl;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;

import com.untamedears.jukealert.model.actions.abstr.LoggableBlockAction;

public class OpenContainerAction extends LoggableBlockAction {
	
	public static final String ID = "OPEN_CONTAINER";

	public OpenContainerAction(long time, UUID player, Location location, Material material) {
		super(time, player, location, material);
	}

	@Override
	protected String getChatRepresentationIdentifier() {
		return "Opened";
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
