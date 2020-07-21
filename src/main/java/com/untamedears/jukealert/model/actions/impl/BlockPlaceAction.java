package com.untamedears.jukealert.model.actions.impl;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;

import com.untamedears.jukealert.model.actions.abstr.LoggableBlockAction;
import com.untamedears.jukealert.util.JAUtility;

public class BlockPlaceAction extends LoggableBlockAction {

	public static final String ID = "BLOCK_PLACE";

	public BlockPlaceAction(long time, UUID player, Location location, String materialString) {
		this(time, player, location, JAUtility.parseMaterial(materialString));
	}

	public BlockPlaceAction(long time, UUID player, Location location, Material material) {
		super(time, player, location, material);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	protected String getChatRepresentationIdentifier() {
		return "Place";
	}

}
