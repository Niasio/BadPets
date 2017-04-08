package fr.niasio.badblock;

import fr.niasio.badblock.config.SettingsManager;
import fr.niasio.badblock.mount.Mount;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CustomPlayer {

    public UUID uuid;
    public Mount currentMount;

    public CustomPlayer(UUID uuid) {
        this.uuid = uuid;
        SettingsManager.getData(getPlayer());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void removeMount() {
        if (currentMount != null) {
            currentMount.ent.remove();
            currentMount.clear();
            currentMount = null;
            getPlayer().removePotionEffect(PotionEffectType.CONFUSION);
        }
    }
    
    public UUID getUuid() {
        return uuid;
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public boolean hasGadgetsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

}