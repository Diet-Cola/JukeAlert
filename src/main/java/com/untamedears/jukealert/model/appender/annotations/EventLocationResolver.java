package com.untamedears.jukealert.model.appender.annotations;

import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.event.Event;

public class EventLocationResolver {

	private Map <Class<? extends Event>, Method> locationGetter;
}
