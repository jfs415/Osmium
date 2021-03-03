package com.kmecpp.osmium.cache;

import java.util.Collection;
import java.util.HashMap;

import com.kmecpp.osmium.Platform;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.platform.UnsupportedPlatformException;
import com.kmecpp.osmium.platform.bukkit.BukkitPlayer;
import com.kmecpp.osmium.platform.bungee.BungeePlayer;
import com.kmecpp.osmium.platform.sponge.SpongePlayer;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerList {

	private static final HashMap<String, Player> players = new HashMap<>();

	public static void addPlayer(Object sourcePlayer) {
		Player wrapper = sourcePlayer instanceof org.bukkit.entity.Player ? new BukkitPlayer((org.bukkit.entity.Player) sourcePlayer)
				: sourcePlayer instanceof org.spongepowered.api.entity.living.player.Player ? new SpongePlayer((org.spongepowered.api.entity.living.player.Player) sourcePlayer)
						: null;

		if (wrapper == null) {
			throw new IllegalArgumentException("Not a player!");
		}
		players.put(wrapper.getName().toLowerCase(), wrapper);
	}

	public static void removePlayer(String name) {
		players.remove(name.toLowerCase());
	}

	public static boolean contains(String name) {
		return players.containsKey(name);
	}

	public static Player getPlayer(Object sourcePlayer) {
		Player osmiumPlayer;
		if (Platform.isBukkit()) {
			org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) sourcePlayer;
			osmiumPlayer = getPlayer(bukkitPlayer.getName());
			if (osmiumPlayer == null) {
				osmiumPlayer = new BukkitPlayer(bukkitPlayer);
			}
		} else if (Platform.isSponge()) {
			org.spongepowered.api.entity.living.player.Player spongePlayer = (org.spongepowered.api.entity.living.player.Player) sourcePlayer;
			osmiumPlayer = getPlayer(spongePlayer.getName());
			if (osmiumPlayer == null) {
				osmiumPlayer = new SpongePlayer(spongePlayer);
			}
		} else if (Platform.isProxy()) {
			ProxiedPlayer bungeePlayer = (ProxiedPlayer) sourcePlayer;
			osmiumPlayer = getPlayer(bungeePlayer.getName());
			if (osmiumPlayer == null) {
				osmiumPlayer = new BungeePlayer(bungeePlayer);
			}
		} else {
			throw new UnsupportedPlatformException();
		}
		return osmiumPlayer;
	}

	public static Player getPlayer(String name) {
		return players.get(name.toLowerCase());
	}

	public static Collection<Player> getPlayers() {
		return players.values();
	}

}
