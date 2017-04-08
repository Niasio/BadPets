package fr.niasio.badblock.mount;

import fr.niasio.badblock.Pets;
import fr.niasio.badblock.config.MessageManager;
import fr.niasio.badblock.config.SettingsManager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public abstract class Mount implements Listener {

    private Material material;
    private Byte data;
    private String name;

    private MountType type = MountType.DEFAULT;

    public EntityType entityType = EntityType.HORSE;

    private UUID owner;

    public Entity ent;

    public int repeatDelay = 2;

    public Mount(EntityType entityType, Material material, Byte data, String configName, String permission, final UUID owner, final MountType type) {
        this.material = material;
        this.data = data;
        this.name = configName;
        this.type = type;
        this.entityType = entityType;
        if (owner != null) {
            this.owner = owner;
            if (!getPlayer().hasPermission(permission)) {
                getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
                return;
            }
            if (type == MountType.NYANSHEEP || type == MountType.DRAGON)
                repeatDelay = 1;
            if (Pets.getCustomPlayer(getPlayer()).currentMount != null)
                Pets.getCustomPlayer(getPlayer()).removeMount();
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (Bukkit.getPlayer(owner) != null
                            && Pets.getCustomPlayer(Bukkit.getPlayer(owner)).currentMount != null
                            && Pets.getCustomPlayer(Bukkit.getPlayer(owner)).currentMount.getType() == type) {
                        onUpdate();
                    } else {
                        cancel();
                    }
                }
            };
            runnable.runTaskTimer(Pets.getPlugin(), 0, repeatDelay);
            new MountListener(this);

            this.ent = getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), getEntityType());
            if (ent instanceof Ageable) {
                ((Ageable) ent).setAdult();
            } else {
                if (ent instanceof Slime) {
                    ((Slime) ent).setSize(4);
                }
            }
            ((LivingEntity) ent).setCustomNameVisible(true);
            ((LivingEntity) ent).setCustomName(getName());
            ent.setPassenger(getPlayer());
            if (ent instanceof Horse) {
                ((Horse) ent).setDomestication(1);
                ((Horse) ent).getInventory().setSaddle(new ItemStack(Material.SADDLE));
            }

            getPlayer().sendMessage(MessageManager.getMessage("Mounts.Spawn").replace("%mountname%", getMenuName()));
            Pets.getCustomPlayer(getPlayer()).currentMount = this;
        }
    }
    
    public Entity getEntity() {
    	return ent;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public String getName() {
        return MessageManager.getMessage("Mounts." + name + ".entity-displayname").replace("%playername%", getPlayer().getName());
    }

    public String getMenuName() {
        return MessageManager.getMessage("Mounts." + name + ".menu-name");
    }

    public String getConfigName() {
        return name;
    }

    public Material getMaterial() {
        return this.material;
    }


    public MountType getType() {
        return this.type;
    }

    public Byte getData() {
        return this.data;
    }

    abstract void onUpdate();

    public void clear() {
        getPlayer().sendMessage(MessageManager.getMessage("Mounts.Despawn").replace("%mountname%", getMenuName()));
        Pets.getCustomPlayer(getPlayer()).currentMount = null;
        try {
            ent.getPassenger().eject();
        } catch (Exception exc) {
        }
        ent.remove();
        getPlayer().removePotionEffect(PotionEffectType.CONFUSION);
    }

    protected UUID getOwner() {
        return owner;
    }

    protected Player getPlayer() {
        return Bukkit.getPlayer(owner);
    }

    public class MountListener implements Listener {
        public MountListener(Mount mount) {
            Pets.registerListener(this);
        }

        @EventHandler
        public void onPlayerToggleSneakEvent(VehicleExitEvent event) {
            
            String name = null;
            try {
                name = getName();
            } catch ( Exception e ) {}
            
            if ( name != null && ((LivingEntity) event.getVehicle()).getCustomName().equals(name) && event.getExited() == getPlayer()) {
                Pets.getCustomPlayer(getPlayer()).removeMount();
            }
        }

        @EventHandler
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.getEntity() == ent)
                event.setCancelled(true);
            if (event.getEntity() == getPlayer()
                    && Pets.getCustomPlayer(getPlayer()).currentMount != null
                    && Pets.getCustomPlayer(getPlayer()).currentMount.getType() == getType()) {
                event.setCancelled(true);
            }
        }

    }

    public enum MountType {

        DEFAULT("", ""),
        NYANSHEEP("pets.nyansheep", "NyanSheep"),
        DRAGON("pets.dragon", "Dragon"),
        SNAKE("pets.snake", "Snake");

        String permission;
        String configName;

        MountType(String permission, String configName) {
            this.permission = permission;
            this.configName = configName;
        }

        public String getPermission() {
            return permission;
        }

        public boolean isEnabled() {
            return SettingsManager.getConfig().get("Mounts." + configName + ".Enabled");
        }

    }

} 