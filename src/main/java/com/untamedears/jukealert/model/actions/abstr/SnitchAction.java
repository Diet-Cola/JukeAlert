package com.untamedears.jukealert.model.actions.abstr;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.model.actions.ActionCacheState;
import com.untamedears.jukealert.model.actions.LoggedActionPersistence;
import com.untamedears.jukealert.util.JAUtility;

import net.md_5.bungee.api.chat.TextComponent;
import vg.civcraft.mc.civmodcore.api.ItemAPI;
import vg.civcraft.mc.civmodcore.inventorygui.IClickable;
import vg.civcraft.mc.namelayer.NameAPI;

public abstract class SnitchAction implements Cloneable {
	
	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	protected final long time;
	protected Snitch snitch;
	protected final UUID player;
	private ActionCacheState state;
	private int id;

	public SnitchAction(long time, UUID player) {
		this.time = time;
		this.player = player;
		state = ActionCacheState.NEW;
	}
	
	public void setSnitch(Snitch snitch) {
		if (this.snitch != null) {
			throw new IllegalStateException();
		}
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
	 * @return Player who commited the action
	 */
	public UUID getPlayer() {
		return player;
	}

	protected String getFormattedTime() {
		return timeFormatter.format(LocalDateTime.ofEpochSecond(time / 1000, 0, ZoneOffset.UTC));
	}

	public String getPlayerName() {
		return NameAPI.getCurrentName(player);
	}

	/**
	 * @return Unique identifier for this type of action, one per class
	 */
	public abstract String getIdentifier();
	
	public abstract IClickable getGUIRepresentation();

	public LoggedActionPersistence getPersistence() {
		return new LoggedActionPersistence(player, null, time, null);
	}
	
	public void setID(int id) {
		this.id = id;
		state = ActionCacheState.NORMAL;
	}
	
	public int getID() {
		return id;
	}
	
	public void setCacheState(ActionCacheState state) {
		this.state = state;
	}
	
	public ActionCacheState getCacheState() {
		return state;
	}
	
	/**
	 * Creates a chat representation of this action to show to players
	 * 
	 * @param reference Current location of the player to show the output to
	 * @param live      Whether the action is happening right now or being retrieved
	 *                  as a record
	 * @return TextComponent representing this instance ready for sending to a
	 *         player
	 */
	public TextComponent getChatRepresentation(Location reference, boolean live) {
		Location referenceLoc = getLocationForStringRepresentation();
		boolean sameWorld = JAUtility.isSameWorld(referenceLoc, reference);
		TextComponent comp = new TextComponent(
				String.format("%s%s  %s%s  ", ChatColor.GOLD, getChatRepresentationIdentifier(), ChatColor.GREEN, NameAPI.getCurrentName(getPlayer())));
		if (live) {
			comp.addExtra(JAUtility.genTextComponent(snitch));
			comp.addExtra(String.format("  %s%s", ChatColor.YELLOW,
					JAUtility.formatLocation(referenceLoc, !sameWorld)));
		}
		else {
			//dont need to explicitly list location when retrieving logs and its the snitch location
			if (referenceLoc != snitch.getLocation()) {
				comp.addExtra(String.format("%s%s", ChatColor.YELLOW,
						JAUtility.formatLocation(referenceLoc, !sameWorld)));
			}
			comp.addExtra(new TextComponent(ChatColor.AQUA + getFormattedTime()));
		}
		return comp;
	}
	
	protected void enrichGUIItem(ItemStack item) {
		ItemAPI.addLore(item, String.format("%sPlayer: %s", ChatColor.GOLD, getPlayerName()),
				String.format("%sTime: %s", ChatColor.LIGHT_PURPLE,getFormattedTime()));
		ItemAPI.setDisplayName(item, ChatColor.GOLD + getGUIName());
	}
	
	protected String getGUIName() {
		return getChatRepresentationIdentifier();
	}
	
	protected Location getLocationForStringRepresentation() {
		return snitch.getLocation();
	}
	
	protected abstract String getChatRepresentationIdentifier();
	
	protected ItemStack getSkullFor(UUID uuid) {
		ItemStack is = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta skullMeta = (SkullMeta) is.getItemMeta();
		skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
		is.setItemMeta(skullMeta);
		return is;
	}

}
