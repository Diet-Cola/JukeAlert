package com.untamedears.jukealert.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Called when a player moves and his block coordinate changes as a result
 *
 */
public class PlayerMoveBlockEvent extends PlayerMoveEvent {

	public PlayerMoveBlockEvent(Player player, Location from, Location to) {
		super(player, from, to);
	}

}
