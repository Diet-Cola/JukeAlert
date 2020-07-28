package com.untamedears.jukealert.model.field;

import java.util.Collection;

import org.bukkit.Location;

import com.google.common.collect.Lists;
import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.model.SnitchQTEntry;

public abstract class YBoundSingleField implements FieldManager {
	
	protected final SnitchQTEntry entry;
	protected final int lowerYLimit;
	protected final int upperYLimit;
	protected final Location location;
	
	public YBoundSingleField(Snitch snitch, int lowerXRange, int upperXRange, int lowerYRange, int upperYRange, int lowerZRange, int upperZRange) {
		entry = new SnitchQTEntry(snitch, snitch.getLocation(), lowerXRange, upperXRange, lowerZRange, upperZRange);
		this.lowerYLimit = snitch.getLocation().getBlockY() - lowerYRange;
		this.upperYLimit = snitch.getLocation().getBlockY() + upperYRange;
		this.location = snitch.getLocation();
	}

	@Override
	public boolean isInside(Location location) {
		int y = location.getBlockY();
		return y <= upperYLimit && y >= lowerYLimit;
	}

	@Override
	public Collection<SnitchQTEntry> getQTEntries() {
		return Lists.asList(entry, new SnitchQTEntry[0]);
	}

}
