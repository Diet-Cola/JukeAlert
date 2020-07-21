package com.untamedears.jukealert.model.appender.annotations;

import java.util.Objects;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

public class EventClassPrioTuple {
	
	private Class <? extends Event> eventClass;
	private EventPriority priority;
	
	public EventClassPrioTuple(Class <? extends Event> eventClass, EventPriority priority) {
		this.eventClass = eventClass;
		this.priority = priority;
	}
	
	public EventPriority getPriority() {
		return priority;
	}
	
	public Class <? extends Event> getEventClass() {
		return eventClass;
	}
	
	public boolean equals(Object o) {
		if (o == null || o.getClass() != EventClassPrioTuple.class) {
			return false;
		}
		EventClassPrioTuple other = (EventClassPrioTuple) o;
		return other.eventClass == this.eventClass && other.priority == this.priority;
	}
	
	public int hashCode() {
		return Objects.hash(eventClass, priority);
	}
}
