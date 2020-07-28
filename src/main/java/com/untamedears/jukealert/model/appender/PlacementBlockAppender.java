package com.untamedears.jukealert.model.appender;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.model.appender.annotations.AppenderEventHandler;
import com.untamedears.jukealert.model.appender.config.DamageScalingConfig;
import com.untamedears.jukealert.model.appender.config.PlacementBlockConfig;

import vg.civcraft.mc.citadel.ReinforcementLogic;
import vg.civcraft.mc.citadel.events.ReinforcementCreationEvent;
import vg.civcraft.mc.citadel.model.Reinforcement;

public class PlacementBlockAppender extends ConfigurableSnitchAppender<PlacementBlockConfig> {

	public static final String ID = "placementblock";
	private static final DecimalFormat formatter = new DecimalFormat("0.###");

	public PlacementBlockAppender(Snitch snitch, ConfigurationSection configSection) {
		super(snitch, configSection);
	}

	@AppenderEventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void reinforcementCreation(ReinforcementCreationEvent event) {
		DamageScalingConfig reinConfig = config.getReinforcementDamageConfig();
		if (reinConfig == null) {
			return;
		}
		Reinforcement rein = snitch.getReinforcement();
		float dmg = reinConfig.getCurrentDamage(rein.getCreationTime());
		ReinforcementLogic.damageReinforcement(rein, dmg, event.getPlayer());
		if (event.getPlayer() != null) {
			event.getPlayer().sendMessage(String.format("%sDealt %f damage to %s%s", ChatColor.RED,
					formatter.format(dmg), ChatColor.AQUA, snitch.getType().getName()));
		}
	}
	
	@AppenderEventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void blockPlace(BlockPlaceEvent event) {
		
	}

	@Override
	public Class<PlacementBlockConfig> getConfigClass() {
		return PlacementBlockConfig.class;
	}

	@Override
	public boolean runWhenSnitchInactive() {
		return false;
	}

}
