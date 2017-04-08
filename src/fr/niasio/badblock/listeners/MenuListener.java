package fr.niasio.badblock.listeners;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.niasio.badblock.CustomPlayer;
import fr.niasio.badblock.Pets;
import fr.niasio.badblock.config.MessageManager;
import fr.niasio.badblock.config.SettingsManager;
import fr.niasio.badblock.mount.Mount;
import fr.niasio.badblock.util.ItemFactory;

public class MenuListener implements Listener {

    private static Pets core;

	private InventoryType slotAmount;

    public MenuListener(Pets core) {
        MenuListener.core = core;
    }

        Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Main-Menu"));

        @SuppressWarnings("static-access")
		int a = core.enabledCategories.size();

        int b = 9;{
        if (a == 1)
            b = 13;
        else if (a == 2)
            b = 12;
        else if (a == 3)
            b = 11;
        else if (a == 4)
            b = 10;
        else if (a == 5)
            b = 9;

        int add = 0;
		b += add;

        for (int i = 0; i < a; i++) {
            ItemStack is = Pets.enabledCategories.get(i).getItemStack().clone();
            inv.setItem(b, is);
            b += 2;
        }
    }

    public static void openMountsMenu(Player p) {
        int listSize = 0;
        for (Mount m : Pets.getMounts()) {
            if (!m.getType().isEnabled()) continue;
            listSize++;
        }
        int slotAmount = 54;
        if (listSize < 22)
            slotAmount = 54;
        if (listSize < 15)
            slotAmount = 45;
        if (listSize < 8)
            slotAmount = 36;

        Inventory inv = Bukkit.createInventory(null, slotAmount, MessageManager.getMessage("Menus.Mounts"));

        int i = 10;
        for (Mount m : Pets.getMounts()) {
            if (!m.getType().isEnabled() && (boolean) SettingsManager.getConfig().get("Disabled-Items.Show-Custom-Disabled-Item")) {
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Type"));
                Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Data")));
                String name = String.valueOf(SettingsManager.getConfig().get("Disabled-Items.Custom-Disabled-Item.Name")).replace("&", "§");
                inv.setItem(i, ItemFactory.create(material, data, name));
                if (i == 25 || i == 34 || i == 16) {
                    i += 3;
                } else {
                    i++;
                }
                continue;
            }
            if (!m.getType().isEnabled()) continue;
            if (SettingsManager.getConfig().get("No-Permission.Dont-Show-Item"))
                if (!p.hasPermission(m.getType().getPermission()))
                    continue;
            if ((boolean) SettingsManager.getConfig().get("No-Permission.Custom-Item.enabled") && !p.hasPermission(m.getType().getPermission())) {
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("No-Permission.Custom-Item.Type"));
                Byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Data")));
                String name = String.valueOf(SettingsManager.getConfig().get("No-Permission.Custom-Item.Name")).replace("&", "§");
                inv.setItem(i, ItemFactory.create(material, data, name));
                if (i == 25 || i == 34 || i == 16) {
                    i += 3;
                } else {
                    i++;
                }
                continue;
            }
            String lore = null;
            if (SettingsManager.getConfig().get("No-Permission.Show-In-Lore")) {
                lore = ChatColor.translateAlternateColorCodes('&', String.valueOf(SettingsManager.getConfig().get("No-Permission.Lore-Message-" + ((p.hasPermission(m.getType().getPermission()) ? "Oui" : "Non")))));
            }
            String toggle = MessageManager.getMessage("Menu.Spawn");
            CustomPlayer cp = Pets.getCustomPlayer(p);
            if (cp.currentMount != null && cp.currentMount.getType() == m.getType())
                toggle = MessageManager.getMessage("Menu.Despawn");
            ItemStack is = ItemFactory.create(m.getMaterial(), m.getData(), toggle + " " + m.getMenuName(), (lore != null) ? lore : null);
            if (cp.currentMount != null && cp.currentMount.getType() == m.getType())
                is = addGlow(is);
            inv.setItem(i, is);
            if (i == 25 || i == 34 || i == 16) {
                i += 3;
            } else {
                i++;
            }
        }

        inv.setItem(inv.getSize() - 6, ItemFactory.create(Material.ARROW, (byte) 0x0, MessageManager.getMessage("Menus.Main-Menu")));
        inv.setItem(inv.getSize() - 4, ItemFactory.create(Material.TNT, (byte) 0x0, MessageManager.getMessage("Clear-Mount")));

        p.openInventory(inv);
    }
    
    public static Mount.MountType getMountByName(String name) {
        for (Mount mount : Pets.getMounts()) {
            if (mount.getMenuName().replace(" ", "").equals(name.replace(" ", ""))) {
                return mount.getType();
            }
        }
        return null;
    }

    static List<Player> playerList = new ArrayList<>();

    @SuppressWarnings("rawtypes")
	public static void activateMountByType(Mount.MountType type, final Player player) {
        if (!player.hasPermission(type.getPermission())) {
            if (!playerList.contains(player)) {
                player.sendMessage(MessageManager.getMessage("No-Permission"));
                playerList.add(player);
                Bukkit.getScheduler().runTaskLaterAsynchronously(Pets.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        playerList.remove(player);
                    }
                }, 1);
            }
            return;
        }

        for (Mount mount : Pets.getMounts()) {
            if (mount.getType().isEnabled() && mount.getType() == type) {
                Class<? extends Mount> mountClass = mount.getClass();

                Class[] cArg = new Class[1];
                cArg[0] = UUID.class;

                UUID uuid = player.getUniqueId();

                try {
                    mountClass.getDeclaredConstructor(UUID.class).newInstance(uuid);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }

   // @EventHandler
    public void mountsSelection(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(MessageManager.getMessage("Menus.Mounts"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Mounts"))) {
                    return;
                }
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu"))) {
                    openMountsMenu((Player) event.getWhoClicked());
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Clear-Mount"))) {
                    if (Pets.getCustomPlayer((Player) event.getWhoClicked()).currentMount != null) {
                        event.getWhoClicked().closeInventory();
                        Pets.getCustomPlayer((Player) event.getWhoClicked()).removeMount();
                        openMountsMenu((Player) event.getWhoClicked());
                    } else return;
                    return;
                }
                event.getWhoClicked().closeInventory();
                if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Despawn"))) {
                    Pets.getCustomPlayer((Player) event.getWhoClicked()).removeMount();
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().startsWith(MessageManager.getMessage("Menu.Spawn"))) {
                    Pets.getCustomPlayer((Player) event.getWhoClicked()).removeMount();
                    StringBuilder sb = new StringBuilder();
                    String name = event.getCurrentItem().getItemMeta().getDisplayName().replace(MessageManager.getMessage("Menu.Spawn"), "");
                    int j = name.split(" ").length;
                    if (name.contains("("))
                        j--;
                    for (int i = 1; i < j; i++) {
                        sb.append(name.split(" ")[i]);
                        try {
                            if (event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[i + 1] != null)
                                sb.append(" ");
                        } catch (Exception exc) {

                        }
                    }
                    activateMountByType(getMountByName(sb.toString()), (Player) event.getWhoClicked());
                }

            }
        }
    }

//	@EventHandler
    public void mainMenuSelection(final InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(MessageManager.getMessage("Menus.Main-Menu"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;
            if (event.getCurrentItem().getItemMeta().hasDisplayName()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Main-Menu")))
                    return;
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(MessageManager.getMessage("Menu.Mounts"))) {
                    openMountsMenu((Player) event.getWhoClicked());
                }
            }
        }
    
    public static ItemStack addGlow(ItemStack item) {
        net.minecraft.server.v1_7_R4.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null) tag = nmsStack.getTag();
        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    @EventHandler
    public void onInventoryMoveItem(PlayerPickupItemEvent event) {
        try {
            if (event.getItem().getItemStack().hasItemMeta()
                    && event.getItem().getItemStack().getItemMeta().hasDisplayName()
                    && UUID.fromString(event.getItem().getItemStack().getItemMeta().getDisplayName()) != null) {
                event.setCancelled(true);
            }
        } catch (Exception exception) {
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryPickupItemEvent event) {
        try {
            if (event.getInventory() != null
                    && event.getInventory().getType() == InventoryType.HOPPER) {
                if (event.getItem().getItemStack().hasItemMeta()
                        && event.getItem().getItemStack().getItemMeta().hasDisplayName()
                        && UUID.fromString(event.getItem().getItemStack().getItemMeta().getDisplayName()) != null) {
                    event.setCancelled(true);
                }
            }
        } catch (Exception exception) {
        }
    }
}