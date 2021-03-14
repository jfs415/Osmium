package com.kmecpp.osmium.platform.bungee.events;

import java.net.InetAddress;
import java.util.UUID;

import com.kmecpp.osmium.BungeeAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.PlayerConnectionEvent;

import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;

public abstract class BungeePlayerConnectionEvent implements PlayerConnectionEvent {

	public static class BungeePlayerAuthEvent extends BungeePlayerConnectionEvent implements PlayerConnectionEvent.Auth {

		private PreLoginEvent event;

		public BungeePlayerAuthEvent(PreLoginEvent event) {
			this.event = event;
		}

		@Override
		public PreLoginEvent getSource() {
			return event;
		}

		@Override
		public String getPlayerName() {
			return event.getConnection().getName();
		}

		@Override
		public UUID getUniqueId() {
			return event.getConnection().getUniqueId();
		}

		@SuppressWarnings("deprecation")
		@Override
		public InetAddress getAddress() {
			return event.getConnection().getAddress().getAddress();
		}

	}

	public static class BungeePlayerLoginEvent extends BungeePlayerConnectionEvent implements PlayerConnectionEvent.Login {

		private LoginEvent event;

		public BungeePlayerLoginEvent(LoginEvent event) {
			this.event = event;

		}

		@Override
		public LoginEvent getSource() {
			return event;
		}

		@Override
		public String getPlayerName() {
			return event.getConnection().getName();
		}

		@Override
		public UUID getUniqueId() {
			return event.getConnection().getUniqueId();
		}

		@SuppressWarnings("deprecation")
		@Override
		public InetAddress getAddress() {
			return event.getConnection().getAddress().getAddress();
		}

	}

	public static class BungeePlayerJoinEvent extends BungeePlayerConnectionEvent implements PlayerConnectionEvent.Join {

		private ServerConnectEvent event;

		public BungeePlayerJoinEvent(ServerConnectEvent event) {
			this.event = event;
		}

		@Override
		public ServerConnectEvent getSource() {
			return event;
		}

		@Override
		public Player getPlayer() {
			return BungeeAccess.getPlayer(event.getPlayer());
		}

		@Override
		public UUID getUniqueId() {
			return event.getPlayer().getUniqueId();
		}

		@Override
		public String getPlayerName() {
			return event.getPlayer().getName();
		}

		@SuppressWarnings("deprecation")
		@Override
		public InetAddress getAddress() {
			return event.getPlayer().getAddress().getAddress();
		}

	}

	public static class BungeePlayerQuitEvent extends BungeePlayerConnectionEvent implements PlayerConnectionEvent.Quit {

		private PlayerDisconnectEvent event;

		public BungeePlayerQuitEvent(PlayerDisconnectEvent event) {
			this.event = event;
		}

		@Override
		public PlayerDisconnectEvent getSource() {
			return event;
		}

		@Override
		public Player getPlayer() {
			return BungeeAccess.getPlayer(event.getPlayer());
		}

		@Override
		public UUID getUniqueId() {
			return event.getPlayer().getUniqueId();
		}

		@Override
		public String getPlayerName() {
			return event.getPlayer().getName();
		}

		@SuppressWarnings("deprecation")
		@Override
		public InetAddress getAddress() {
			return event.getPlayer().getAddress().getAddress();
		}

	}

}