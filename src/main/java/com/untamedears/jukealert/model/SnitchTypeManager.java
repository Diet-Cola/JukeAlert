package com.untamedears.jukealert.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.untamedears.jukealert.JukeAlert;
import com.untamedears.jukealert.model.appender.AbstractSnitchAppender;
import com.untamedears.jukealert.model.appender.BroadcastEntryAppender;
import com.untamedears.jukealert.model.appender.DormantCullingAppender;
import com.untamedears.jukealert.model.appender.LeverToggleAppender;
import com.untamedears.jukealert.model.appender.ShowOwnerOnDestroyAppender;
import com.untamedears.jukealert.model.appender.SnitchLogAppender;
import com.untamedears.jukealert.model.field.CuboidRangeManager;
import com.untamedears.jukealert.model.field.CylinderRangeManager;
import com.untamedears.jukealert.model.field.FieldManager;

public class SnitchTypeManager {

	private Map<String, Class<? extends AbstractSnitchAppender>> appenderClasses;
	private Map<ItemStack, SnitchFactoryType> configFactoriesByItem;
	private Map<Integer, SnitchFactoryType> configFactoriesById;

	public SnitchTypeManager() {
		appenderClasses = new HashMap<>();
		configFactoriesByItem = new HashMap<>();
		configFactoriesById = new HashMap<>();
		registerAppenderTypes();
	}

	private void registerAppenderTypes() {
		registerAppenderType(BroadcastEntryAppender.ID, BroadcastEntryAppender.class);
		registerAppenderType(SnitchLogAppender.ID, SnitchLogAppender.class);
		registerAppenderType(LeverToggleAppender.ID, LeverToggleAppender.class);
		registerAppenderType(DormantCullingAppender.ID, DormantCullingAppender.class);
		registerAppenderType(ShowOwnerOnDestroyAppender.ID, ShowOwnerOnDestroyAppender.class);
	}

	public Set<Class<? extends AbstractSnitchAppender>> getAllAppenderTypes() {
		return new HashSet<>(appenderClasses.values());
	}

	private void registerAppenderType(String id, Class<? extends AbstractSnitchAppender> clazz) {
		appenderClasses.put(id.toLowerCase(), clazz);
	}

	public boolean parseFromConfig(ConfigurationSection config) {
		Logger logger = JukeAlert.getInstance().getLogger();
		ItemStack item = config.getItemStack("item", null);
		StringBuilder sb = new StringBuilder();
		if (item == null) {
			logger.warning("Snitch type at " + config.getCurrentPath() + " had no item specified");
			return false;
		}
		if (!config.isInt("id")) {
			logger.warning("Snitch type at " + config.getCurrentPath() + " had no id specified");
			return false;
		}
		int id = config.getInt("id");
		if (!config.isString("name")) {
			logger.warning("Snitch type at " + config.getCurrentPath() + " had no name specified");
			return false;
		}
		String name = config.getString("name");
		sb.append("Successfully parsed type ");
		sb.append(name);
		sb.append(" with id: ");
		sb.append(id);
		sb.append(", item: ");
		sb.append(item.toString());
		sb.append(", appenders: ");
		List<Function<Snitch, AbstractSnitchAppender>> appenderInstanciations = new ArrayList<>();
		if (config.isConfigurationSection("appender")) {
			ConfigurationSection appenderSection = config.getConfigurationSection("appender");
			for (String key : appenderSection.getKeys(false)) {
				if (!appenderSection.isConfigurationSection(key)) {
					logger.warning("Ignoring invalid entry " + key + " at " + appenderSection);
					continue;
				}
				Class<? extends AbstractSnitchAppender> appenderClass = appenderClasses.get(key.toLowerCase());
				if (appenderClass == null) {
					logger.warning("Appender " + key + " at " + appenderSection + " is of an unknown type");
					// this is not something we should just ignore, disregard entire config in this
					// case
					return false;
				}
				ConfigurationSection entrySection = appenderSection.getConfigurationSection(key);
				Function<Snitch, AbstractSnitchAppender> instanciation = getAppenderInstanciation(appenderClass,
						entrySection);
				appenderInstanciations.add(instanciation);
				sb.append(appenderClass.getSimpleName());
				sb.append("   ");
			}
		}
		if (appenderInstanciations.isEmpty()) {
			logger.warning("Snitch config at " + config.getCurrentPath()
					+ " has no appenders, this is likely not what you intended");
		}
		Function<Snitch, FieldManager> fieldFunction = parseFieldManager(config.getConfigurationSection("field"));
		if (fieldFunction == null) {
			return false;
		}
		SnitchFactoryType configFactory = new SnitchFactoryType(item, name, id, appenderInstanciations, fieldFunction);
		configFactoriesById.put(configFactory.getID(), configFactory);
		configFactoriesByItem.put(configFactory.getItem(), configFactory);
		logger.info(sb.toString());
		return true;
	}

	private static Function<Snitch, FieldManager> parseFieldManager(ConfigurationSection config) {
		if (config == null) {
			return null;
		}
		int lowerY;
		if ((lowerY = readRange(config, "y_range", "lower_y_range")) == -1) {
			return null;
		}
		int upperY;
		if ((upperY = readRange(config, "y_range", "upper_y_range")) == -1) {
			return null;
		}
		switch (config.getString("type", "not_specified").toUpperCase()) {
		case "CUBOID":
		case "CUBE":
			int lowerX;
			if ((lowerX = readRange(config, "x_range", "lower_x_range")) == -1) {
				return null;
			}
			int upperX;
			if ((upperX = readRange(config, "x_range", "upper_x_range")) == -1) {
				return null;
			}
			int lowerZ;
			if ((lowerZ = readRange(config, "z_range", "lower_z_range")) == -1) {
				return null;
			}
			int upperZ;
			if ((upperZ = readRange(config, "z_range", "upper_z_range")) == -1) {
				return null;
			}
			return s -> new CuboidRangeManager(s, lowerX, upperX, lowerY, upperY, lowerZ, upperZ);
		case "CYLINDER":
			int radius = config.getInt("radius", -1);
			if (radius == -1) {
				JukeAlert.getInstance().getLogger()
						.severe("Failed to find radius for field at " + config.getCurrentPath());
				return null;
			}
			return s -> new CylinderRangeManager(s, radius, lowerY, upperY);
		default:
			JukeAlert.getInstance().getLogger().severe("Unrecognized field type at " + config.getCurrentPath());
			return null;
		}
	}

	private static int readRange(ConfigurationSection config, String iden2, String iden3) {
		int range = 1;
		range = config.getInt("range", range);
		range = config.getInt(iden2, range);
		range = config.getInt(iden3, range);
		if (range < 0) {
			JukeAlert.getInstance().getLogger().log(Level.SEVERE,
					"Failed to find valid value for range " + iden3 + " at " + config.getCurrentPath());
			return -1;
		}
		if (range > 10_000_000) {
			JukeAlert.getInstance().getLogger().log(Level.SEVERE,
					"Range of " + range + " at " + config.getCurrentPath() + " is too big");
			return -1;
		}
		return range;
	}

	/**
	 * Creates a function which will instanciate the appender based on the
	 * ConfigurationSection give to it
	 * 
	 * @param clazz Class of the appender
	 * @return Function to instanciate appenders of the given class or null if the
	 *         appender has no appropriate constructor
	 */
	private Function<Snitch, AbstractSnitchAppender> getAppenderInstanciation(
			Class<? extends AbstractSnitchAppender> clazz, ConfigurationSection config) {
		try {
			Constructor<? extends AbstractSnitchAppender> constructor = clazz.getConstructor(Snitch.class,
					ConfigurationSection.class);
			return s -> {
				try {
					return constructor.newInstance(s, config);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
					return null;
				}
			};
		} catch (NoSuchMethodException | SecurityException e) {
			// no config section constructor, which is fine if the appender does not have
			// any parameter, in which case it only has a constructor with the snitch as
			// parameter
			try {
				Constructor<? extends AbstractSnitchAppender> constructor = clazz.getConstructor(Snitch.class);
				return s -> {
					try {
						return constructor.newInstance(s);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e1) {
						e1.printStackTrace();
						return null;
					}
				};
			} catch (NoSuchMethodException | SecurityException e1) {
				// No appropriate constructor, the appender has a bug
				e1.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * Gets the configuration tied to the given ItemStack
	 * 
	 * @param is ItemStack to get configuration for
	 * @return Configuration with the given ItemStack or null if no such config
	 *         exists
	 */
	public SnitchFactoryType getConfig(ItemStack is) {
		if (is == null) {
			return null;
		}
		ItemStack copy = is.clone();
		copy.setAmount(1);
		return configFactoriesByItem.get(copy);
	}

	public SnitchFactoryType getConfig(int id) {
		return configFactoriesById.get(id);
	}

}
