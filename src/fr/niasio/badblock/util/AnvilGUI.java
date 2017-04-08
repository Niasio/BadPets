package fr.niasio.badblock.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AnvilGUI {

	Inventory i;

	public AnvilGUI() {
	}

	public void createGUI() {

		i = Bukkit.createInventory(null, InventoryType.ANVIL,
				"Renommer votre familier :");

	}

	public void setItemToRename(ItemStack s) {
		i.setItem(0, s);
	}

	public void open(Player p) {
		p.openInventory(i);
	}

}
