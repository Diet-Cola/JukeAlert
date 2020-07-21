package com.untamedears.jukealert.model.appender.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import com.untamedears.jukealert.SnitchManager;
import com.untamedears.jukealert.listener.DynamicListenerHandler;
import com.untamedears.jukealert.model.Snitch;
import com.untamedears.jukealert.model.appender.AbstractSnitchAppender;

import vg.civcraft.mc.civmodcore.util.Iteration;

public class AppenderEventManager {

	private static final class MethodAppenderTuple {
		private Method method;
		private boolean ignoreCancelled;
		private Class<? extends AbstractSnitchAppender> appenderClass;

		private MethodAppenderTuple(Method method, Class<? extends AbstractSnitchAppender> appenderClass,
				boolean ignoreCancelled) {
			this.method = method;
			this.appenderClass = appenderClass;
			this.ignoreCancelled = ignoreCancelled;
		}
	}

	private Map<EventClassPrioTuple, List<MethodAppenderTuple>> eventsToAppenders;
	private Logger logger;
	private SnitchManager snitchManager;

	public AppenderEventManager(Logger logger, SnitchManager snitchManager) {
		this.eventsToAppenders = new HashMap<>();
		this.logger = logger;
		this.snitchManager = snitchManager;
	}

	public void setup(Collection<Class<? extends AbstractSnitchAppender>> appenders,
			DynamicListenerHandler listenerHandler) {
		for (Class<? extends AbstractSnitchAppender> clazz : appenders) {
			registerListener(clazz, listenerHandler);
		}
	}

	public void handleEvent(Event event, EventPriority priority) {
		List<MethodAppenderTuple> handlers = eventsToAppenders.get(new EventClassPrioTuple(event.getClass(), priority));
		if (Iteration.isNullOrEmpty(handlers)) {
			return;
		}
		boolean cancelled;
		if (event instanceof Cancellable) {
			cancelled = ((Cancellable) event).isCancelled();
		} else {
			cancelled = false;
		}
		List<MethodAppenderTuple> relevantAppenderTypes;
		if (cancelled) {
			relevantAppenderTypes = new LinkedList<>();
			for (MethodAppenderTuple handler : handlers) {
				if (!handler.ignoreCancelled) {
					relevantAppenderTypes.add(handler);
				}
			}
		}
		else {
			relevantAppenderTypes = handlers;
		}
		if (relevantAppenderTypes.isEmpty()) {
			return;
		}
		Set<Snitch> snitchesCovering = snitchManager.getSnitchesCovering(location);
		if (snitchesCovering.isEmpty()) {
			return;
		}
		for (Snitch snitch : snitchesCovering) {
			for (MethodAppenderTuple handler : relevantAppenderTypes) {
				AbstractSnitchAppender appender = snitch.getAppender(handler.appenderClass);
				if (appender == null) {
					continue;
				}
				try {
					handler.method.invoke(appender, event);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					logger.log(Level.SEVERE, "Failed to invoke snitch appender listener", e);
				}
			}
		}

	}

	private void registerListener(Class<? extends AbstractSnitchAppender> appenderClass,
			DynamicListenerHandler listenerHandler) {
		for (Method method : appenderClass.getMethods()) {
			if (!Modifier.isPublic(method.getModifiers())) {
				// method should be public for any outside use case
				continue;
			}
			if (!method.getReturnType().equals(Void.TYPE)) {
				// method should not return anything
				continue;
			}
			boolean eventHandlerAnnotationFound = false;
			boolean ignoreCancelled = false;
			EventPriority priority = null;
			for (Annotation annotation : method.getAnnotations()) {
				if (annotation instanceof AppenderEventHandler) {
					eventHandlerAnnotationFound = true;
					ignoreCancelled = ((AppenderEventHandler) annotation).ignoreCancelled();
					priority = ((AppenderEventHandler) annotation).priority();
					break;
				}
			}
			if (!eventHandlerAnnotationFound) {
				// method is missing the annotation, we dont care about it
				continue;
			}
			if (method.getParameterCount() != 1) {
				// parameter should only be the event object
				continue;
			}
			Class<?> eventClass = method.getParameterTypes()[0];
			if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
				// only parameter is not a subtype of the event class
				continue;
			}
			// we found a valid listener method at this point
			@SuppressWarnings("unchecked")
			Class<? extends Event> eventClassCast = (Class<? extends Event>) eventClass;
			List<MethodAppenderTuple> existingListeners = this.eventsToAppenders.computeIfAbsent(new EventClassPrioTuple(eventClassCast, priority),
					s -> new ArrayList<>());
			method.setAccessible(true);
			existingListeners.add(new MethodAppenderTuple(method, appenderClass, ignoreCancelled));
			listenerHandler.createListenerIfNotExists(eventClassCast, priority);
		}
	}

}
