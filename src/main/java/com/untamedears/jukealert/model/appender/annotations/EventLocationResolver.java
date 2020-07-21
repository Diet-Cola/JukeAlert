package com.untamedears.jukealert.model.appender.annotations;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

public class EventLocationResolver {

	private static Map<Class<? extends Event>, Function<Event, Location>> locationGetter = new HashMap<>();

	public EventLocationResolver() {
		registerAll();
	}

	private void registerAll() {
		register(BlockBreakEvent.class, b -> b.getBlock().getLocation());
	}

	public Location getLocation(Event event) {
		Function<Event, Location> func = locationGetter.get(event.getClass());
		if (func == null) {
			return null;
		}
		return func.apply(event);
	}

	@SuppressWarnings("unchecked")
	private static <T extends Event> void register(Class<T> eventClass, Function<T, Location> locationConverter) {
		//this madness is needed to appease the java type gods. Feel free to fix it if you know a better way
		locationGetter.put(eventClass, e -> locationConverter.apply((T) e));
	}
}
