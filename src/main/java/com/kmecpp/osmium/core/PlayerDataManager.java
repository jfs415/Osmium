package com.kmecpp.osmium.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.kmecpp.osmium.Osmium;
import com.kmecpp.osmium.api.database.PlayerData;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;
import com.kmecpp.osmium.api.plugin.OsmiumPlugin;
import com.kmecpp.osmium.api.util.Reflection;

public class PlayerDataManager {

	private HashMap<OsmiumPlugin, ArrayList<Class<?>>> registeredTypes = new HashMap<>();
	private HashMap<UUID, HashMap<Class<?>, Object>> playerData = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T> T getData(Player player, Class<T> dataType) {
		System.out.println("GETTING PLAYER FOR: " + player.getName() + " :: " + playerData);
		return (T) playerData.get(player.getUniqueId()).get(dataType);
	}

	public void registerType(OsmiumPlugin plugin, Class<?> dataType) {
		ArrayList<Class<?>> types = registeredTypes.get(plugin);
		if (types == null) {
			types = new ArrayList<>();
			registeredTypes.put(plugin, types);
		}
		types.add(dataType);
	}

	public HashMap<UUID, HashMap<Class<?>, Object>> getData() {
		return playerData;
	}

	@SuppressWarnings("unchecked")
	public <T> Set<Entry<UUID, T>> get(Class<T> type) {
		HashMap<UUID, T> map = new HashMap<>();
		for (Entry<UUID, HashMap<Class<?>, Object>> entry : playerData.entrySet()) {
			map.put(entry.getKey(), (T) entry.getValue().get(type));
		}
		return map.entrySet();
	}

	public ArrayList<Class<?>> getRegisteredTypes(OsmiumPlugin plugin) {
		return registeredTypes.getOrDefault(plugin, new ArrayList<>());
	}

	public <T> void forEach(Class<T> type, Consumer<T> consumer) {
		//				for()
	}

	//This is not in core so it can't listen to events
	public void onPlayerAuthenticate(PlayerConnectionEvent.Auth e) {
		for (Entry<OsmiumPlugin, ArrayList<Class<?>>> entry : registeredTypes.entrySet()) {
			for (Class<?> type : entry.getValue()) {
				Object value = entry.getKey().getDatabase().getOrDefault(type, Reflection.cast(Reflection.createInstance(type)), e.getUniqueId());
				System.out.println("VALUE: " + value);

				if (value instanceof PlayerData) {
					System.out.println("UPDATITNG PLAYER: " + playerData);
					((PlayerData) value).updatePlayerData(e.getUniqueId(), e.getPlayerName());
				}

				HashMap<Class<?>, Object> data = this.playerData.get(e.getUniqueId());
				if (data == null) {
					data = new HashMap<>();
					this.playerData.put(e.getUniqueId(), data);
				}
				System.out.println();
				data.put(type, value);
			}
		}
	}

	public void onPlayerQuit(PlayerConnectionEvent.Quit e) {
		savePlayer(e.getPlayer());
	}

	public void savePlayer(Player player) {
		HashMap<Class<?>, Object> playerData = this.playerData.remove(player.getUniqueId());

		if (playerData == null) {
			return;
		}

		for (Entry<Class<?>, Object> data : playerData.entrySet()) {
			Osmium.getPlugin(data.getKey()).getDatabase().replaceInto(data.getKey(), data.getValue());
		}
	}

	public void saveAllPlayers() {
		for (Player player : Osmium.getOnlinePlayers()) {
			savePlayer(player);
		}
	}

}
