package com.untamedears.jukealert.model;

import org.bukkit.Location;

import vg.civcraft.mc.civmodcore.locations.QTBoxImpl;

public class SnitchQTEntry extends QTBoxImpl {

	private final Snitch snitch;

	public SnitchQTEntry(Snitch snitch, Location loc, int range) {
		super(loc, range);
		this.snitch = snitch;
	}

	public SnitchQTEntry(Snitch snitch, Location center, int lowerXRange, int upperXRange, int lowerZRange,
			int upperZRange) {
		super(center.getBlockX() - lowerXRange, center.getBlockX() + upperXRange, center.getBlockZ() - lowerZRange,
				center.getBlockZ() + upperZRange);
		this.snitch = snitch;
	}

	public Snitch getSnitch() {
		return snitch;
	}

}
