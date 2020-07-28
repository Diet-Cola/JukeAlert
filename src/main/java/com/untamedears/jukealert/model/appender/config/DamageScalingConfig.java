package com.untamedears.jukealert.model.appender.config;

import org.bukkit.configuration.ConfigurationSection;

import vg.civcraft.mc.civmodcore.util.ConfigParsing;

public class DamageScalingConfig {
	
	private long maturationTime;
	private float startingMultiplier;
	private float finalMultiplier;
	
	public DamageScalingConfig(long maturationTime, float startingMultiplier, float finalMultiplier) {
		this.maturationTime = maturationTime;
		this.startingMultiplier= startingMultiplier;
		this.finalMultiplier = finalMultiplier;
	}
	
	public static DamageScalingConfig parse(ConfigurationSection config) {
		if (config == null) {
			return null;
		}
		long maturationTime = ConfigParsing.parseTime(config.getString("maturation_time", "1d"));
		float startingMultiplier = (float) config.getDouble("starting_multiplier", 1.0);
		float finalMultiplier = (float) config.getDouble("final_multiplier", 1.0);
		return new DamageScalingConfig(maturationTime, startingMultiplier, finalMultiplier);
	}
	
	public long getMaturationTime() {
		return maturationTime;
	}
	
	public float getStartingMultiplier() {
		return startingMultiplier;
	}
	
	public float getFinalMultiplier() {
		return finalMultiplier;
	}
	
	public float getCurrentDamage(long creationTime) {
		long now = System.currentTimeMillis();
		long elapsed = now - creationTime;
		if (elapsed > maturationTime) {
			return finalMultiplier;
		}
		float progress = ((float) elapsed) / maturationTime; 
		return startingMultiplier + (1 - progress) * (finalMultiplier - startingMultiplier);
	}
	

}
