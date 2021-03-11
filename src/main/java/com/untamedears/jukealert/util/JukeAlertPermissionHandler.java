package com.untamedears.jukealert.util;

import vg.civcraft.mc.namelayer.core.DefaultPermissionLevel;
import vg.civcraft.mc.namelayer.core.PermissionTracker;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.mc.GroupAPI;

public final class JukeAlertPermissionHandler {

	private final static String SNITCH_IMMUNE = "SNITCH_IMMUNE";
	private final static String LIST_SNITCHES = "LIST_SNITCHES";
	private final static String CLEAR_LOGS = "CLEAR_SNITCHLOG";
	private final static String READ_LOGS = "READ_SNITCHLOG";
	private final static String RENAME_SNITCH = "RENAME_SNITCH";
	private final static String RECEIVE_ALERTS = "SNITCH_NOTIFICATIONS";
	private final static String LOOKUP_SNITCH = "DETECT_SNITCH";
	private final static String TOGGLE_LEVER = "SNITCH_TOGGLE_LEVER";
	
	private PermissionTracker permTracker;
	
	public JukeAlertPermissionHandler(PermissionTracker permTracker) {
		this.permTracker = permTracker;
		setup();
	}

	private static void setup() {
		// Also tied to refreshing snitches
		GroupAPI.registerPermission(LIST_SNITCHES, DefaultPermissionLevel.MOD, "Allows a player to see all snitches in this group.");
		GroupAPI.registerPermission(RECEIVE_ALERTS, DefaultPermissionLevel.MEMBER, "Allows a player to see snitch chat messages.");
		GroupAPI.registerPermission(READ_LOGS, DefaultPermissionLevel.MEMBER,  "Allows a player to see previous actions that have occurred \n in a snitch radius.");
		GroupAPI.registerPermission(RENAME_SNITCH, DefaultPermissionLevel.MOD, "Allows a player to rename a snitch.");
		GroupAPI.registerPermission(SNITCH_IMMUNE, DefaultPermissionLevel.MEMBER, "Stops a snitch from recording a players actions.");
		GroupAPI.registerPermission(LOOKUP_SNITCH, DefaultPermissionLevel.MEMBER, "Allows a player to use /jalookup to view what group a snitch is on");
		GroupAPI.registerPermission(CLEAR_LOGS, DefaultPermissionLevel.MOD, "Permits a player to clear a snitch log.");
		GroupAPI.registerPermission(TOGGLE_LEVER, DefaultPermissionLevel.MOD, "Determines whether a player can toggle whether a lever is \n pulled when a player enters the snitch radius.");
	}

	public PermissionType getRenameSnitch() {
		return permTracker.getPermission(RENAME_SNITCH);
	}

	public PermissionType getSnitchImmune() {
		return permTracker.getPermission(SNITCH_IMMUNE);
	}

	public PermissionType getListSnitches() {
		return permTracker.getPermission(LIST_SNITCHES);
	}

	public PermissionType getClearLogs() {
		return permTracker.getPermission(CLEAR_LOGS);
	}

	public PermissionType getReadLogs() {
		return permTracker.getPermission(READ_LOGS);
	}

	public PermissionType getSnitchAlerts() {
		return permTracker.getPermission(RECEIVE_ALERTS);
	}
	
	public PermissionType getToggleLevers() {
		return permTracker.getPermission(TOGGLE_LEVER);
	}

	public PermissionType getLookupSnitch() {
		return permTracker.getPermission(LOOKUP_SNITCH);
	}
}
