package com.kmecpp.osmium.api;

import java.util.Optional;
import java.util.UUID;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.entity.Player;

public interface User extends Abstraction {

	UUID getUniqueId();

	String getName();

	boolean isOp();

	long getLastPlayed();

	long getFirstPlayed();

	boolean hasPlayedBefore();

	boolean isOnline();

	default Optional<Player> getPlayer() {
		return Osmium.getPlayer(getName());
	}

}
