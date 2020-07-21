package com.untamedears.jukealert.model.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;

import com.untamedears.jukealert.JukeAlert;
import com.untamedears.jukealert.model.actions.abstr.SnitchAction;
import com.untamedears.jukealert.model.actions.impl.BlockBreakAction;
import com.untamedears.jukealert.model.actions.impl.BlockPlaceAction;
import com.untamedears.jukealert.model.actions.impl.DestroyVehicleAction;
import com.untamedears.jukealert.model.actions.impl.DismountEntityAction;
import com.untamedears.jukealert.model.actions.impl.EmptyBucketAction;
import com.untamedears.jukealert.model.actions.impl.EnterFieldAction;
import com.untamedears.jukealert.model.actions.impl.EnterVehicleAction;
import com.untamedears.jukealert.model.actions.impl.ExitVehicleAction;
import com.untamedears.jukealert.model.actions.impl.FillBucketAction;
import com.untamedears.jukealert.model.actions.impl.IgniteBlockAction;
import com.untamedears.jukealert.model.actions.impl.KillLivingEntityAction;
import com.untamedears.jukealert.model.actions.impl.KillPlayerAction;
import com.untamedears.jukealert.model.actions.impl.LeaveFieldAction;
import com.untamedears.jukealert.model.actions.impl.LoginAction;
import com.untamedears.jukealert.model.actions.impl.LogoutAction;
import com.untamedears.jukealert.model.actions.impl.MountEntityAction;

public class LoggedActionFactory {

	private Map<String, SnitchActionProvider> providers;
	private Map<String, Integer> identifierToInternal;

	public LoggedActionFactory() {
		this.providers = new HashMap<>();
		this.identifierToInternal = new HashMap<>();
		registerInternalProviders();
	}

	public void registerProvider(String identifier, SnitchActionProvider provider) {
		int internal = JukeAlert.getInstance().getDAO().getOrCreateActionID(identifier);
		if (internal != -1) {
			providers.put(identifier, provider);
			identifierToInternal.put(identifier, internal);
		}
	}

	public SnitchAction produce(String id, UUID player, Location location, long time, String victim) {
		SnitchActionProvider provider = providers.get(id);
		if (provider == null) {
			return null;
		}
		return provider.get(player, location, time, victim);
	}

	public int getInternalID(String name) {
		Integer id = identifierToInternal.get(name);
		if (id == null) {
			return -1;
		}
		return id;
	}

	private void registerInternalProviders() {
		// java 8 is sexy
		registerProvider(EnterFieldAction.ID, (player, loc, time, victim) -> new EnterFieldAction(time, player));
		registerProvider(LeaveFieldAction.ID, (player, loc, time, victim) -> new LeaveFieldAction(time, player));
		registerProvider(BlockBreakAction.ID,
				(player, loc, time, victim) -> new BlockBreakAction(time, player, loc, victim));
		registerProvider(BlockPlaceAction.ID,
				(player, loc, time, victim) -> new BlockPlaceAction(time, player, loc, victim));
		registerProvider(LoginAction.ID, (player, loc, time, victim) -> new LoginAction(time, player));
		registerProvider(LogoutAction.ID, (player, loc, time, victim) -> new LogoutAction(time, player));
		registerProvider(KillLivingEntityAction.ID,
				(player, loc, time, victim) -> new KillLivingEntityAction(time, player, loc, victim));
		registerProvider(KillPlayerAction.ID,
				(player, loc, time, victim) -> new KillPlayerAction(time, player, loc, UUID.fromString(victim)));
		registerProvider(FillBucketAction.ID,
				(player, loc, time, victim) -> new FillBucketAction(time, player, loc, Material.valueOf(victim)));
		registerProvider(EmptyBucketAction.ID, (player, loc, time, victim) -> new EmptyBucketAction(time, 				player, loc, Material.valueOf(victim)));
		registerProvider(EnterVehicleAction.ID,
				(player, loc, time, victim) -> new EnterVehicleAction(time, player, loc, victim));
		registerProvider(ExitVehicleAction.ID,
				(player, loc, time, victim) -> new ExitVehicleAction(time, player, loc, victim));
		registerProvider(MountEntityAction.ID,
				(player, loc, time, victim) -> new MountEntityAction(time, player, loc, victim));
		registerProvider(DismountEntityAction.ID,
				(player, loc, time, victim) -> new DismountEntityAction(time, player, loc, victim));
		registerProvider(IgniteBlockAction.ID,
				(player, loc, time, victim) -> new IgniteBlockAction(time, player, loc));
		registerProvider(DestroyVehicleAction.ID,
				(player, loc, time, victim) -> new DestroyVehicleAction(time, player, loc, victim));

	}

}
