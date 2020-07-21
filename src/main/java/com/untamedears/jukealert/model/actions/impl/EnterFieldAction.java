package com.untamedears.jukealert.model.actions.impl;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import com.untamedears.jukealert.model.actions.abstr.SnitchAction;

import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;

public class EnterFieldAction extends SnitchAction {

	public static final String ID = "ENTRY";

	public EnterFieldAction(long time, UUID player) {
		super(time, player);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	public IClickable getGUIRepresentation() {
		ItemStack is = getSkullFor(getPlayer());
		super.enrichGUIItem(is);
		return new DecorationStack(is);
	}

	@Override
	protected String getChatRepresentationIdentifier() {
		return "Enter";
	}
}
