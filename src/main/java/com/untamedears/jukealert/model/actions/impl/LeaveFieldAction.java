package com.untamedears.jukealert.model.actions.impl;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import com.untamedears.jukealert.model.actions.abstr.SnitchAction;

import vg.civcraft.mc.civmodcore.inventorygui.DecorationStack;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;

public class LeaveFieldAction extends SnitchAction {

	public static final String ID = "LEAVE";

	public LeaveFieldAction(long time, UUID player) {
		super(time, player);
	}

	@Override
	public IClickable getGUIRepresentation() {
		ItemStack is = getSkullFor(getPlayer());
		super.enrichGUIItem(is);
		return new DecorationStack(is);
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	protected String getChatRepresentationIdentifier() {
		return "Leave";
	}

}
