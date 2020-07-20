package com.untamedears.jukealert.model.appender;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;

import com.untamedears.jukealert.events.SnitchDestroyEvent;
import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.model.appender.annotations.AppenderEventHandler;

import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.group.Group;

public class ShowOwnerOnDestroyAppender extends AbstractSnitchAppender {

	public static final String ID = "showownerondestroy";

	public ShowOwnerOnDestroyAppender(Snitch snitch) {
		super(snitch);
	}

	@Override
	public boolean runWhenSnitchInactive() {
		return true;
	}

	@AppenderEventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void snitchBroken(SnitchDestroyEvent event) {
		Player player = event.getPlayer();
		if (player == null) {
			return;
		}
		Group group = snitch.getGroup();
		String groupName;
		String ownerName;
		if (group == null) {
			groupName = "unknown";
			ownerName = "unknown";
		} else {
			groupName = group.getName();
			ownerName = NameAPI.getCurrentName(group.getOwner());
		}
		player.sendMessage(String.format("%s%s %swas reinforced on %s%s%s owned by %s%s", ChatColor.GOLD,
				snitch.getType().getName(), ChatColor.YELLOW, ChatColor.GREEN, groupName, ChatColor.YELLOW,
				ChatColor.LIGHT_PURPLE, ownerName));
	}

}
