package fr.niasio.badblock.listeners;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import fr.niasio.badblock.CustomPlayer;
import fr.niasio.badblock.Pets;
import fr.niasio.badblock.config.SettingsManager;
import fr.niasio.badblock.mount.MountNyanSheep;
import fr.niasio.badblock.util.ItemFactory;

public class PlayerListener implements Listener {

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		Pets.getCustomPlayers().add(
				new CustomPlayer(event.getPlayer().getUniqueId()));
		if ((boolean) SettingsManager.getConfig().get("Menu-Item.Give-On-Join")
				&& !((List<String>) SettingsManager.getConfig().get(
						"Disabled-Worlds")).contains(event.getPlayer()
						.getWorld().getName())) {
			Bukkit.getScheduler().runTaskLater(Pets.getPlugin(),
					new Runnable() {
						@SuppressWarnings("deprecation")
						@Override
						public void run() {
							int slot = SettingsManager.getConfig().get(
									"Menu-Item.Slot");
							if (event.getPlayer().getInventory().getItem(slot) != null) {
								if (event.getPlayer().getInventory()
										.getItem(slot).hasItemMeta()
										&& event.getPlayer().getInventory()
												.getItem(slot).getItemMeta()
												.hasDisplayName()
										&& event.getPlayer()
												.getInventory()
												.getItem(slot)
												.getItemMeta()
												.getDisplayName()
												.equalsIgnoreCase(
														(String) SettingsManager
																.getConfig()
																.get("Menu-Item.Displayname"))) {
									event.getPlayer().getInventory()
											.remove(slot);
									event.getPlayer().getInventory()
											.setItem(slot, null);
								}
								event.getPlayer()
										.getWorld()
										.dropItemNaturally(
												event.getPlayer().getLocation(),
												event.getPlayer()
														.getInventory()
														.getItem(slot));
								event.getPlayer().getInventory().remove(slot);
							}
							String name = String.valueOf(
									SettingsManager.getConfig().get(
											"Menu-Item.Displayname")).replace(
									"&", "§");
							Material material = Material
									.valueOf((String) SettingsManager
											.getConfig().get("Menu-Item.Type"));
							byte data = Byte.valueOf(String
									.valueOf(SettingsManager.getConfig().get(
											"Menu-Item.Data")));
							event.getPlayer()
									.getInventory()
									.setItem(
											slot,
											ItemFactory.create(material, data,
													name));
						}
					}, 5);
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (event.getItemDrop().getItemStack().hasItemMeta()
				&& event.getItemDrop().getItemStack().getItemMeta()
						.hasDisplayName()
				&& event.getItemDrop()
						.getItemStack()
						.getItemMeta()
						.getDisplayName()
						.equals(String.valueOf(
								SettingsManager.getConfig().get(
										"Menu-Item.Displayname")).replace("&",
								"§"))) {
			event.setCancelled(true);
			event.getItemDrop().remove();
			ItemStack chest = event.getPlayer().getItemInHand().clone();
			chest.setAmount(1);
			event.getPlayer().setItemInHand(chest);
			event.getPlayer().updateInventory();
		}
	}

	@EventHandler
	public void onQuit(PlayerKickEvent event) {

	}

	@EventHandler
	public void onClick1(InventoryClickEvent e) {

		if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) {
			return;
		}

		e.setCancelled(true);
		
		ItemStack current = e.getCurrentItem();
		switch (current.getType()) {
		case STAINED_GLASS:

			((Player) e.getWhoClicked()).sendMessage("§6Voici ta monture !");

			MountNyanSheep mns = new MountNyanSheep(e.getWhoClicked().getUniqueId());
		//	CraftWorld cw = (CraftWorld) e.getWhoClicked().getWorld();
		//	cw.getHandle().addEntity((Entity) mns.getEntity());
			
			MenuListener.activateMountByType(mns.getType(), (Player) e.getWhoClicked());
			
			break;

		default:
			break;
		}

	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {

		if (e.getItem() == null) {
			return;
		}

		if (!e.getItem().hasItemMeta()) {
			return;
		}

		if (e.getItem().getItemMeta().hasDisplayName()) {
			if (e.getItem().getItemMeta().getDisplayName().contains("Pets")
					&& e.getItem().getType() == Material.ENDER_CHEST) {
				MenuListener.openMountsMenu(e.getPlayer());
			}
		}

	}

	// @EventHandler
	public void onPickUp(InventoryClickEvent event) {
		if (event.getCurrentItem() != null
				&& event.getCurrentItem().hasItemMeta()
				&& event.getCurrentItem().getItemMeta().hasDisplayName()
				&& event.getCurrentItem()
						.getItemMeta()
						.getDisplayName()
						.equals(String.valueOf(
								SettingsManager.getConfig().get(
										"Menu-Item.Displayname")).replace("&",
								"§"))) {
			event.setCancelled(true);
		}
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		if (!((List<String>) SettingsManager.getConfig().get("Disabled-Worlds"))
				.contains(event.getPlayer().getWorld().getName())) {
			int slot = SettingsManager.getConfig().get("Menu-Item.Slot");
			if (event.getPlayer().getInventory().getItem(slot) != null) {
				event.getPlayer()
						.getWorld()
						.dropItemNaturally(event.getPlayer().getLocation(),
								event.getPlayer().getInventory().getItem(slot));
				event.getPlayer().getInventory().remove(slot);
			}
			String name = String.valueOf(
					SettingsManager.getConfig().get("Menu-Item.Displayname"))
					.replace("&", "§");
			Material material = Material.valueOf((String) SettingsManager
					.getConfig().get("Menu-Item.Type"));
			byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig()
					.get("Menu-Item.Data")));
			event.getPlayer().getInventory()
					.setItem(slot, ItemFactory.create(material, data, name));
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
			if (Pets.noFallDamageEntities.contains(event.getEntity())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerPickUpItem(PlayerPickupItemEvent event) {
		if (event.getItem().getItemStack() != null
				&& event.getItem().getItemStack().hasItemMeta()
				&& event.getItem().getItemStack().getItemMeta()
						.hasDisplayName()
				&& event.getItem()
						.getItemStack()
						.getItemMeta()
						.getDisplayName()
						.equals(String.valueOf(
								SettingsManager.getConfig().get(
										"Menu-Item.Displayname")).replace("&",
								"§"))) {
			event.setCancelled(true);
			event.getItem().remove();
		}
	}

}