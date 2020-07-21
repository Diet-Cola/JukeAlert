package com.untamedears.jukealert.model.appender.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.bukkit.event.EventPriority;

/**
 * This annotation is used to mark methods in appenders, which want to listen
 * for specific events happen within their range
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AppenderEventHandler {
	
	/**
	 * @return Priority at which this listener is called, order is identical to EventHandler, LOWEST --> HIGHEST --> MONITOR
	 */
	EventPriority priority() default EventPriority.NORMAL;
	
	/**
	 * @return If ignoreCancelled is true and the event is cancelled, the method is
	 *         not called. Otherwise, the method is always called
	 */
	boolean ignoreCancelled() default false;
	
	
	
	
}
