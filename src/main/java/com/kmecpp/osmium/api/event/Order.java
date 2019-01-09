package com.kmecpp.osmium.api.event;

import org.bukkit.event.EventPriority;

import com.kmecpp.osmium.api.Abstraction;
import com.kmecpp.osmium.api.platform.Platform;

public enum Order implements Abstraction {

	FIRST,
	EARLY,
	DEFAULT,
	LATE,
	LAST,
	POST,

	;

	private Object source;

	static {
		if (Platform.isBukkit()) {
			FIRST.source = EventPriority.LOWEST;
			EARLY.source = EventPriority.LOW;
			DEFAULT.source = EventPriority.NORMAL;
			LATE.source = EventPriority.HIGH;
			LAST.source = EventPriority.HIGHEST;
			POST.source = EventPriority.MONITOR;
		} else if (Platform.isSponge()) {
			FIRST.source = org.spongepowered.api.event.Order.FIRST;
			EARLY.source = org.spongepowered.api.event.Order.EARLY;
			DEFAULT.source = org.spongepowered.api.event.Order.DEFAULT;
			LATE.source = org.spongepowered.api.event.Order.LATE;
			LAST.source = org.spongepowered.api.event.Order.LAST;
			POST.source = org.spongepowered.api.event.Order.POST;
		}
	}

	@Override
	public Object getSource() {
		return source;
	}

}
