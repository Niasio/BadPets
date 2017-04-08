package fr.niasio.badblock;

import fr.niasio.badblock.commands.PetsCommand;
import fr.niasio.badblock.config.MessageManager;
import fr.niasio.badblock.config.SettingsManager;
import fr.niasio.badblock.mount.*;
import fr.niasio.badblock.listeners.PlayerListener;
import fr.niasio.badblock.util.ItemFactory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Pets extends JavaPlugin {

	public static ArrayList<Entity> noFallDamageEntities = new ArrayList<>();

    private static List<CustomPlayer> customPlayers = new ArrayList<>();

    private static List<Mount> mountList = new ArrayList<>();

    public static List<Category> enabledCategories = new ArrayList<>();

    public static Pets core;

    @SuppressWarnings({ "deprecation", "unchecked" })
	@Override
    public void onEnable() {

        core = this;

        if (getDescription().getVersion().startsWith("Pre")) {
            getServer().getConsoleSender().sendMessage("§c§l----------------------------");
            getServer().getConsoleSender().sendMessage("");
            getServer().getConsoleSender().sendMessage("  §4§lVersion Instable !");
            getServer().getConsoleSender().sendMessage("  §4§lIl y a un bug");
            getServer().getConsoleSender().sendMessage("");
            getServer().getConsoleSender().sendMessage("§c§l----------------------------");
        }
        new MessageManager();
        registerListener(new PlayerListener());


        // Register Mounts
        mountList.add(new MountNyanSheep(null));
        mountList.add(new Snake(null));
        mountList.add(new MountDragon(null));

        // Register the command
        getCommand("pets").setExecutor(new PetsCommand());
        ArrayList<String> arrayList = new ArrayList<>();
        getCommand("pets").setAliases(arrayList);

        List<String> disabledWorlds = new ArrayList<>();

        disabledWorlds.add("worldDisabled1");
        disabledWorlds.add("worldDisabled2");
        disabledWorlds.add("worldDisabled3");

        SettingsManager.getConfig().addDefault("Disabled-Worlds", disabledWorlds);

        SettingsManager.getConfig().addDefault("Categories-Enabled.Mounts", true);

        // Set config things.
        SettingsManager.getConfig().addDefault("Menu-Item.Give-On-Join", true);
        SettingsManager.getConfig().addDefault("Menu-Item.Slot", 3);
        SettingsManager.getConfig().addDefault("Menu-Item.Type", "ENDER_CHEST");
        SettingsManager.getConfig().addDefault("Menu-Item.Data", 0);
        SettingsManager.getConfig().addDefault("Menu-Item.Displayname", "&6&lPets");
        SettingsManager.getConfig().addDefault("No-Permission.Show-In-Lore", true);
        SettingsManager.getConfig().addDefault("No-Permission.Lore-Message-Yes", "&o&7Permission: &a&lOui !");
        SettingsManager.getConfig().addDefault("No-Permission.Lore-Message-No", "&o&7Permission: &4&lNon !");
        SettingsManager.getConfig().addDefault("No-Permission.Dont-Show-Item", false);
        SettingsManager.getConfig().addDefault("No-Permission.Custom-Item.enabled", false);
        SettingsManager.getConfig().addDefault("No-Permission.Custom-Item.Type", "INK_SACK");
        SettingsManager.getConfig().addDefault("No-Permission.Custom-Item.Data", 8);
        SettingsManager.getConfig().addDefault("No-Permission.Custom-Item.Name", "&c&lPermission refusé");
        SettingsManager.getConfig().addDefault("Disabled-Items.Show-Custom-Disabled-Item", false);
        SettingsManager.getConfig().addDefault("Disabled-Items.Custom-Disabled-Item.Type", "INK_SACK");
        SettingsManager.getConfig().addDefault("Disabled-Items.Custom-Disabled-Item.Data", 8);
        SettingsManager.getConfig().addDefault("Disabled-Items.Custom-Disabled-Item.Name", "&c&lEteint");

        for (Mount m : mountList)
            SettingsManager.getConfig().addDefault("Mounts." + m.getConfigName() + ".Enabled", true);

        for (Player p : Bukkit.getOnlinePlayers()) {
            customPlayers.add(new CustomPlayer(p.getUniqueId()));
            if ((boolean) SettingsManager.getConfig().get("Menu-Item.Give-On-Join") && !((List<String>) SettingsManager.getConfig().get("Disabled-Worlds")).contains(p.getWorld().getName())) {
                int slot = SettingsManager.getConfig().get("Menu-Item.Slot");
                if (p.getInventory().getItem(slot) != null) {
                    if (p.getInventory().getItem(slot).hasItemMeta()
                            && p.getInventory().getItem(slot).getItemMeta().hasDisplayName()
                            && p.getInventory().getItem(slot).getItemMeta().getDisplayName().equalsIgnoreCase((String) SettingsManager.getConfig().get("Menu-Item.Displayname"))) {
                        p.getInventory().remove(slot);
                        p.getInventory().setItem(slot, null);
                    }
                    p.getWorld().dropItemNaturally(p.getLocation(), p.getInventory().getItem(slot));
                    p.getInventory().remove(slot);
                }
                String name = String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§");
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("Menu-Item.Type"));
                byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Data")));
                p.getInventory().setItem(slot, ItemFactory.create(material, data, name));
            }
        }

        final BukkitRunnable countdownRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Iterator<Entity> iter = noFallDamageEntities.iterator();
                    while (iter.hasNext()) {
                        Entity ent = iter.next();
                        if (ent.isOnGround())
                            iter.remove();
                    }
                } catch (Exception exc) {
                }
                Iterator<CustomPlayer> customPlayerIterator = customPlayers.iterator();
                while (customPlayerIterator.hasNext()) {
                    CustomPlayer customPlayer = customPlayerIterator.next();
                    if (customPlayer.getPlayer() == null)
                        customPlayerIterator.remove();
                        }
                    }
        };
        countdownRunnable.runTaskTimerAsynchronously(Pets.getPlugin(), 0, 1);
        Pets.customPlayers.clear();
        try {
        } catch (Exception e) {
        }
    }

    public static List<Mount> getMounts() {
        return mountList;
    }
    
    public static Plugin getPlugin() {
        return core;
    }

    public static void registerListener(Listener listenerClass) {
        Bukkit.getPluginManager().registerEvents(listenerClass, getPlugin());
    }

    public static CustomPlayer getCustomPlayer(Player player) {
        for (CustomPlayer cp : customPlayers)
            if (cp.getPlayer().getName().equals(player.getName()))
                return cp;
        return new CustomPlayer(player.getUniqueId());
    }

    public enum Category {
        MOUNTS("Mounts", ItemFactory.create(Material.SADDLE, (byte) 0, MessageManager.getMessage("Menu.Mounts")));

        String configPath;
        ItemStack is;

        Category(String configPath, ItemStack is) {
            this.configPath = configPath;
            this.is = is;
        }

        public ItemStack getItemStack() {
            return is;
        }

        public boolean isEnabled() {
            return SettingsManager.getConfig().get("Categories-Enabled." + configPath);
		}
	}

	public static List<CustomPlayer> getCustomPlayers() {
		// TODO Auto-generated method stub
		return customPlayers;
	}

}