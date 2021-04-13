package com.untamedears.jukealert;

import com.untamedears.jukealert.database.JukeAlertDAO;
import com.untamedears.jukealert.listener.LoggableActionListener;
import com.untamedears.jukealert.listener.SnitchLifeCycleListener;
import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.model.SnitchTypeManager;
import com.untamedears.jukealert.model.actions.LoggedActionFactory;
import com.untamedears.jukealert.util.JASettingsManager;
import com.untamedears.jukealert.util.JukeAlertPermissionHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.locations.chunkmeta.api.ChunkMetaAPI;
import vg.civcraft.mc.civmodcore.locations.chunkmeta.api.SingleBlockAPIView;

public class JukeAlert extends ACivMod {

	private static JukeAlert instance;

	public static JukeAlert getInstance() {
		return instance;
	}

	private JukeAlertDAO dao;
	private JAConfigManager configManager;
	private SnitchTypeManager snitchConfigManager;
	private SnitchManager snitchManager;
	private LoggedActionFactory loggedActionFactory;
	private JASettingsManager settingsManager;
	private SnitchCullManager cullManager;

	public JAConfigManager getConfigManager() {
		return configManager;
	}

	public JASettingsManager getSettingsManager() {
		return settingsManager;
	}

	public LoggedActionFactory getLoggedActionFactory() {
		return loggedActionFactory;
	}

	public SnitchTypeManager getSnitchConfigManager() {
		return snitchConfigManager;
	}

	public JukeAlertDAO getDAO() {
		return dao;
	}

	public SnitchManager getSnitchManager() {
		return snitchManager;
	}

	public SnitchCullManager getSnitchCullManager() {
		return cullManager;
	}

	@Override
	public void onDisable() {
		snitchManager.shutDown();
	}

	@Override
	public void onEnable() {
		long currentTime = System.currentTimeMillis();
		instance = this;
		super.onEnable();
		info("Time taken after super.onEnable: " + (System.currentTimeMillis() - currentTime) + " ms");
		snitchConfigManager = new SnitchTypeManager();
		info("Time taken after SnitchTypeManager: " + (System.currentTimeMillis() - currentTime) + " ms");
		cullManager = new SnitchCullManager();
		info("Time taken after SnitchCullManager: " + (System.currentTimeMillis() - currentTime) + " ms");
		configManager = new JAConfigManager(this, snitchConfigManager);
		info("Time taken after ConfigManager: " + (System.currentTimeMillis() - currentTime) + " ms");
		saveDefaultConfig();
		dao = new JukeAlertDAO(getLogger(), configManager.getDatabase(getConfig()));
		info("Time taken after DB init: " + (System.currentTimeMillis() - currentTime) + " ms");
		if (!dao.updateDatabase()) {
			getLogger().severe("Errors setting up database, shutting down");
			Bukkit.shutdown();
			return;
		}
		info("Time taken after Database updated: " + (System.currentTimeMillis() - currentTime) + " ms");
		loggedActionFactory = new LoggedActionFactory();
		info("Time taken after LoggedActionFactory: " + (System.currentTimeMillis() - currentTime) + " ms");
		SingleBlockAPIView<Snitch> api = ChunkMetaAPI.registerSingleTrackingPlugin(this, dao);
		info("Time taken after ChunkMeta start: " + (System.currentTimeMillis() - currentTime) + " ms");
		if (api == null) {
			getLogger().severe("Errors setting up chunk metadata API, shutting down");
			Bukkit.shutdown();
			return;
		}
		info("Time taken after ChunkMeta finish: " + (System.currentTimeMillis() - currentTime) + " ms");
		if (!configManager.parse()) {
			Bukkit.shutdown();
			return;
		}
		info("Time taken after config check: " + (System.currentTimeMillis() - currentTime) + " ms");
		snitchManager = new SnitchManager(api);
		info("Time taken after SnitchManager init: " + (System.currentTimeMillis() - currentTime) + " ms");
		settingsManager = new JASettingsManager();
		info("Time taken after SettingManager: " + (System.currentTimeMillis() - currentTime) + " ms");
		registerJukeAlertEvents();
		info("Time taken after Registering events: " + (System.currentTimeMillis() - currentTime) + " ms");
		JukeAlertPermissionHandler.setup();
		info("Time taken after Permissions: " + (System.currentTimeMillis() - currentTime) + " ms");
	}

	private void registerJukeAlertEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new LoggableActionListener(snitchManager), this);
		pm.registerEvents(new SnitchLifeCycleListener(snitchManager, snitchConfigManager, getLogger()), this);
	}
}
