package com.untamedears.jukealert.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.untamedears.jukealert.JukeAlert;
import com.untamedears.jukealert.gui.SnitchOverviewGUI;
import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.util.JukeAlertPermissionHandler;

import vg.civcraft.mc.civmodcore.command.CivCommand;
import vg.civcraft.mc.civmodcore.command.StandaloneCommand;
import vg.civcraft.mc.namelayer.core.Group;
import vg.civcraft.mc.namelayer.mc.GroupAPI;
import vg.civcraft.mc.namelayer.mc.commands.NameLayerTabCompletion;

@CivCommand(id = "jalist")
public class ListCommand extends StandaloneCommand {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (true) {
			sender.sendMessage("Command disabled, please ask awoo#7053 to implement it properly");
			return true;
		}
		List<String> groups = null;
		Player player = (Player) sender;
		if (args.length == 0) {
			//groups = GroupAPI.getAllGroupNames(player.getUniqueId());
		} else {
			groups = Arrays.asList(args);
		}
		List<Integer> groupIds = new ArrayList<>();
		for (String groupName : groups) {
			Group group = GroupAPI.getGroup(groupName);
			if (group == null) {
				sender.sendMessage(ChatColor.RED + "The group " + groupName + " does not exist");
				continue;
			}
			if (!GroupAPI.hasPermission(player, group, JukeAlert.getInstance().getPermissionHandler().getListSnitches())) {
				sender.sendMessage(
						ChatColor.RED + "You do not have permission to list snitches for the group " + group.getName());
				continue;
			}
			groupIds.add(group.getPrimaryId());
			groupIds.addAll(group.getSecondaryIds());
		}
		sender.sendMessage(ChatColor.GREEN + "Retrieving snitches for a total of " + groupIds.size()
		+ " group instances. This may take a moment");
		//retrieve snitches async and show them sync again
		new BukkitRunnable() {

			@Override
			public void run() {
				List<Snitch> snitches = JukeAlert.getInstance().getDAO().loadSnitchesByGroupID(groupIds);
				new BukkitRunnable() {
					
					@Override
					public void run() {
						new SnitchOverviewGUI(player, snitches, "Your snitches", false).showScreen();;
						
					}
				}.runTask(JukeAlert.getInstance());
			}
		}.runTaskAsynchronously(JukeAlert.getInstance());
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		String last;
		if (args.length == 0) {
			last = "";
		} else {
			last = args[args.length - 1];
		}
		return NameLayerTabCompletion.completeGroupName(last, (Player) sender);
	}

}
