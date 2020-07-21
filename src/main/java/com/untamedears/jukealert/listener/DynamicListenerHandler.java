package com.untamedears.jukealert.listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;

import com.untamedears.jukealert.JukeAlert;
import com.untamedears.jukealert.model.appender.AbstractSnitchAppender;
import com.untamedears.jukealert.model.appender.annotations.AppenderEventManager;
import com.untamedears.jukealert.model.appender.annotations.EventClassPrioTuple;

public class DynamicListenerHandler {

	private Logger logger;
	private Set<EventClassPrioTuple> registeredListeners;
	private AppenderEventManager eventManager;

	public DynamicListenerHandler(Logger logger, AppenderEventManager eventManager) {
		this.logger = logger;
		this.eventManager = eventManager;
		this.registeredListeners = new HashSet<>();
		setup();
	}

	public void createListenerIfNotExists(Class<? extends Event> eventClass, EventPriority priority) {
		if (registeredListeners.contains(new EventClassPrioTuple(eventClass, priority))) {
			return;
		}
		Method handlerGetter;
		try {
			handlerGetter = eventClass.getMethod("getHandlerList");
		} catch (NoSuchMethodException | SecurityException e) {
			logger.log(Level.SEVERE, "Failed to find handler list for event " + eventClass, e);
			return;
		}
		if (handlerGetter.getReturnType() != HandlerList.class) {
			logger.log(Level.SEVERE, "Handler list method had wrong return type");
			return;
		}
		HandlerList handlerList;
		try {
			handlerList = (HandlerList) handlerGetter.invoke(eventClass);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.log(Level.SEVERE, "Could not invoke handler getter for " + eventClass, e);
			return;
		}
		Listener fakeListener = new Listener() {
		};
		List<RegisteredListener> listeners = new LinkedList<>();
		RegisteredListener listener = new RegisteredListener(fakeListener, new EventExecutor() {

			@Override
			public void execute(Listener lis, Event event) throws EventException {
				eventManager.handleEvent(event, priority);
			}
		}, priority, JukeAlert.getInstance(), true);
		listeners.add(listener);
		handlerList.register(listener);
		registeredListeners.add(new EventClassPrioTuple(eventClass, priority));
	}

	private void setup() {
		Set<Class<? extends AbstractSnitchAppender>> appenderClasses = JukeAlert.getInstance().getSnitchConfigManager()
				.getAllAppenderTypes();
		eventManager.setup(appenderClasses, this);
	}

}
