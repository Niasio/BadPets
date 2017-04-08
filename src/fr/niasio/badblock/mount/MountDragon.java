package fr.niasio.badblock.mount;

import fr.niasio.badblock.Pets;
import net.minecraft.server.v1_7_R4.EntityEnderDragon;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEnderDragon;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public class MountDragon extends Mount {

    public MountDragon(UUID owner) {
        super(EntityType.ENDER_DRAGON, Material.DRAGON_EGG, (byte) 0, "Dragon", "pets.dragon", owner, MountType.DRAGON);
        Pets.registerListener(this);
    }

    @Override
    void onUpdate() {
        if (ent.getPassenger() == null)
            clear();

        EntityEnderDragon ec = ((CraftEnderDragon) ent).getHandle();

        ec.hurtTicks = -1;

        Vector vector = getPlayer().getLocation().toVector();

        double rotX = getPlayer().getLocation().getYaw();
        double rotY = getPlayer().getLocation().getPitch();

        vector.setY(-Math.sin(Math.toRadians(rotY)));

        double h = Math.cos(Math.toRadians(rotY));

        vector.setX(-h * Math.sin(Math.toRadians(rotX)));
        vector.setZ(h * Math.cos(Math.toRadians(rotX)));

        ec.getBukkitEntity().setVelocity(vector);

        ec.pitch = getPlayer().getLocation().getPitch();
        ec.yaw = getPlayer().getLocation().getYaw() - 180;
    }

    @EventHandler
    public void stopDragonDamage(EntityExplodeEvent event) {
        Entity e = event.getEntity();
        if (e instanceof EnderDragonPart)
            e = ((EnderDragonPart) e).getParent();
        if (e instanceof EnderDragon && e == ent)
            event.setCancelled(true);

    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity e = event.getDamager();
        if (e instanceof EnderDragonPart) {
            e = ((EnderDragonPart) e).getParent();
        }
        if (e instanceof EnderDragon && e == ent) {
            event.setCancelled(true);

        }
    }
}