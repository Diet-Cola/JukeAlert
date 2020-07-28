package com.untamedears.jukealert.model.field;

import java.util.Collection;

import org.bukkit.Location;

import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.model.SnitchQTEntry;

public class CylinderRangeManager extends YBoundSingleField {
	
	private final int radiusSquared;

	public CylinderRangeManager(Snitch snitch, int radius, int lowerYRange, int upperYRange) {
		super(snitch, radius, radius, lowerYRange, upperYRange, radius, radius);
		this.radiusSquared = radius * radius;
	}

	@Override
	public boolean isInside(Location location) {
		if (!super.isInside(location)) {
			return false;
		}
		int xDiff = location.getBlockX() - this.location.getBlockX();
		int zDiff = location.getBlockZ() - this.location.getBlockZ();
		return xDiff * xDiff + zDiff * zDiff <= radiusSquared;
	}
}
