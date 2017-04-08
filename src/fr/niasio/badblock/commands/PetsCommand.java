package fr.niasio.badblock.commands;

import fr.niasio.badblock.Pets;
import fr.niasio.badblock.config.MessageManager;
import fr.niasio.badblock.config.SettingsManager;
import fr.niasio.badblock.mount.Mount;
import fr.niasio.badblock.listeners.MenuListener;
import fr.niasio.badblock.util.ItemFactory;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PetsCommand implements CommandExecutor {


    @SuppressWarnings("deprecation")
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
		if (args == null || args.length == 0) {
            sender.sendMessage(getHelp());
            return true;
		}
            else if(args.length > 0) {    
        
                String argZero = args[0];
            	
            if (argZero.equalsIgnoreCase("mount")) {
                if (!Pets.Category.MOUNTS.isEnabled()) {
                    sender.sendMessage("§c§lPets éteint !");
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage("§l§oBadBlock > §7/pets mount <mount>");
                    return true;
                }
                if (args[1].equalsIgnoreCase("clear")) {
                    Pets.getCustomPlayer((Player) sender).removeMount();
                    return true;
                }
            
                boolean mountFound = false;
                for (Mount mount : Pets.getMounts()) {
                    if (mount.getType().toString().toLowerCase().equalsIgnoreCase(args[1].toLowerCase())) {
                        if (!mount.getType().isEnabled()) {
                            sender.sendMessage(MessageManager.getMessage("Cosmetic-Disabled"));
                            return true;
                        } else {
                            MenuListener.activateMountByType(mount.getType(), (Player) sender);
                        }
                        mountFound = true;
                    }
                }
            
                if (!mountFound) {
                    sender.sendMessage(MessageManager.getMessage("Invalid-Mount"));
                }
                
                return true;
            
                
            
            } else if (argZero.equalsIgnoreCase("clear")) {
                Pets.getCustomPlayer((Player) sender).clear();
                return true;
            } else if (argZero.equalsIgnoreCase("chest")) {
                int slot = SettingsManager.getConfig().get("Menu-Item.Slot");
                if (player.getInventory().getItem(slot) != null) {
                    player.getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getItem(slot));
                    player.getInventory().remove(slot);
                    player.getInventory().setItem(slot, null);
                }
                String name = String.valueOf(SettingsManager.getConfig().get("Menu-Item.Displayname")).replace("&", "§");
                Material material = Material.valueOf((String) SettingsManager.getConfig().get("Menu-Item.Type"));
                byte data = Byte.valueOf(String.valueOf(SettingsManager.getConfig().get("Menu-Item.Data")));
                player.getInventory().setItem(slot, ItemFactory.create(material, data, name));
                return true;
            } else if (argZero.equalsIgnoreCase("menu")) {
                if (args.length == 1) {
                    MenuListener.openMountsMenu((Player) sender);
                    return true;
                } else if (args.length > 1) {
                    } else if (args[1].equalsIgnoreCase("mounts")) {
                        if (Pets.Category.MOUNTS.isEnabled())
                            MenuListener.openMountsMenu((Player) sender);
                    } else {
                        sender.sendMessage(MessageManager.getMessage("Invalid-Menu"));
                    }
                    return true;
                }
            }
            return true;
        }

    public String getHelp() {
        return "\n§r"
                + "                  §8┃ §7/pets menu [menu] §f- Ouvre le menu" + "\n§r"
                + "                  §8┃ §7/pets mount <mount/clear> §f- Montre les pets" + "\n§r"
                + "                  §8┃ §7/pets chest §f- Avoir sont GUI." + "\n§r";
    }
}