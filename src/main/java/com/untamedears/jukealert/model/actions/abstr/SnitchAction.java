package com.untamedears.jukealert.model.actions.abstr;

import org.bukkit.Location;

import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.model.actions.ActionCacheState;
import com.untamedears.jukealert.model.actions.LoggedActionPersistence;

import net.md_5.bungee.api.chat.TextComponent;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;

public abstract class SnitchAction {

	protected final long time;
	protected final Snitch snitch;

	public SnitchAction(long time, Snitch snitch) {
		this.time = time;
		this.snitch = snitch;
	}

	/**
	 * @return UNIX timestamp of when the action happened
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Every action is owned by exactly one snitch, which can be retrieved with this
	 * method
	 * 
	 * @return Snitch owning this instance
	 */
	public Snitch getSnitch() {
		return snitch;
	}

	/**
	 * @return Unique identifier for this type of action, one per class
	 */
	public abstract String getIdentifier();
	
	public abstract IClickable getGUIRepresentation();

	/**
	 * Creates a chat representation of this action to show to players
	 * 
	 * @param reference Current location of the player to show the output to
	 * @param live      Whether the action is happening right now or being retrieved
	 *                  as a record
	 * @return TextComponent representing this instance ready for sending to a
	 *         player
	 */
	public abstract TextComponent getChatRepresentation(Location reference, boolean live);

	public abstract LoggedActionPersistence getPersistence();

	public abstract void setID(int id);

	public abstract int getID();

	public abstract void setCacheState(ActionCacheState state);

	public abstract ActionCacheState getCacheState();

}
