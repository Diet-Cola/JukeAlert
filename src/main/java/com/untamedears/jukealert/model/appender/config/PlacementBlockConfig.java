package com.untamedears.jukealert.model.appender.config;

import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventPriority;

import com.untamedears.jukealert.model.appender.annotations.AppenderEventHandler;

import vg.civcraft.mc.citadel.events.ReinforcementCreationEvent;
import vg.civcraft.mc.civmodcore.util.ConfigParsing;

public class PlacementBlockConfig implements AppenderConfig {
	
	private DamageScalingConfig placementConfig;
	private DamageScalingConfig reinforcingConfig;
	
	public PlacementBlockConfig(ConfigurationSection config) {
		placementConfig = DamageScalingConfig.parse(config.getConfigurationSection("placement"));
		reinforcingConfig = DamageScalingConfig.parse(config.getConfigurationSection("reinforcing"));
	}
	
	public DamageScalingConfig getPlacementDamageConfig() {
		return placementConfig;
	}
	
	public DamageScalingConfig getReinforcementDamageConfig() {
		return reinforcingConfig;
	}

}
