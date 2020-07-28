package com.untamedears.jukealert.model.field;

import java.util.Collection;

import org.bukkit.Location;

import com.google.common.collect.Lists;
import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.model.SnitchQTEntry;

public class CuboidRangeManager extends YBoundSingleField {

	public CuboidRangeManager(Snitch snitch, int lowerXRange, int upperXRange, int lowerYRange, int upperYRange, int lowerZRange, int upperZRange) {
		super(snitch, lowerXRange, upperXRange, lowerYRange, upperYRange, lowerZRange, upperZRange);
	}
	
	public CuboidRangeManager(Snitch snitch, int xRange, int yRange, int zRange) {
		this(snitch, xRange, xRange, yRange, yRange, zRange, zRange);
	}
	
	public CuboidRangeManager(Snitch snitch, int range) {
		this(snitch, range, range, range);
	}
}
