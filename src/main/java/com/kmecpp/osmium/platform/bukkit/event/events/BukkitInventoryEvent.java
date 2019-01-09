package com.kmecpp.osmium.platform.bukkit.event.events;

import java.util.Set;

import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import com.kmecpp.osmium.BukkitAccess;
import com.kmecpp.osmium.api.entity.Player;
import com.kmecpp.osmium.api.event.events.InventoryEvent;
import com.kmecpp.osmium.api.inventory.ClickType;
import com.kmecpp.osmium.api.inventory.Inventory;

public abstract class BukkitInventoryEvent implements InventoryEvent {

	private org.bukkit.event.inventory.InventoryEvent event;

	@Override
	public Player getPlayer() {
		return BukkitAccess.getPlayer((org.bukkit.entity.Player) event.getView().getPlayer());
	}

	@Override
	public Inventory getInventory() {
		return BukkitAccess.getInventory(event.getInventory());
	}

	@Override
	public boolean shouldFire() {
		return event.getView().getPlayer() instanceof org.bukkit.entity.Player;
	}

	public static class BukkitInventoryOpenEvent extends BukkitInventoryEvent implements InventoryEvent.Open {

		private InventoryOpenEvent event;

		public BukkitInventoryOpenEvent(InventoryOpenEvent event) {
			this.event = event;
		}

		@Override
		public InventoryOpenEvent getSource() {
			return event;
		}

		@Override
		public boolean isCancelled() {
			return event.isCancelled();
		}

		@Override
		public void setCancelled(boolean cancel) {
			event.setCancelled(cancel);
		}

	}

	public static class BukkitInventoryCloseEvent extends BukkitInventoryEvent implements InventoryEvent.Close {

		private InventoryCloseEvent event;
		private boolean cancelled;

		public BukkitInventoryCloseEvent(InventoryCloseEvent event) {
			this.event = event;
		}

		@Override
		public InventoryCloseEvent getSource() {
			return event;
		}

		@Override
		public boolean isCancelled() {
			return cancelled;
		}

		@Override
		public void setCancelled(boolean cancel) {
			this.cancelled = cancel;
		}

	}

	public static class BukkitInventoryClickEvent extends BukkitInventoryEvent implements InventoryEvent.Click {

		private InventoryClickEvent event;

		public BukkitInventoryClickEvent(org.bukkit.event.inventory.InventoryClickEvent event) {
			this.event = event;
		}

		@Override
		public InventoryClickEvent getSource() {
			return event;
		}

		@Override
		public boolean isCancelled() {
			return event.isCancelled();
		}

		@Override
		public void setCancelled(boolean cancel) {
			event.setCancelled(cancel);
		}

		@Override
		public boolean isLeftClick() {
			return event.isLeftClick();
		}

		@Override
		public boolean isRightClick() {
			return event.isRightClick();
		}

		@Override
		public ClickType getClick() {
			return ClickType.fromSource(event.getClick());
		}

		@Override
		public int getSlot() {
			return event.getRawSlot();
		}

	}

	public static class BukkitInventoryDragEvent extends BukkitInventoryEvent implements InventoryEvent.Drag {

		private InventoryDragEvent event;

		public BukkitInventoryDragEvent(org.bukkit.event.inventory.InventoryDragEvent event) {
			this.event = event;
		}

		@Override
		public InventoryDragEvent getSource() {
			return event;
		}

		@Override
		public boolean isCancelled() {
			return event.isCancelled();
		}

		@Override
		public void setCancelled(boolean cancel) {
			event.setCancelled(cancel);
		}

		@Override
		public boolean isEvenDrag() {
			return event.getType() == DragType.EVEN;
		}

		@Override
		public boolean isSingleDrag() {
			return event.getType() == DragType.SINGLE;
		}

		@Override
		public Set<Integer> getSlots() {
			return event.getRawSlots();
		}

	}

}
