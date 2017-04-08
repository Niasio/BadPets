package fr.niasio.badblock.mount;

import fr.niasio.badblock.Pets;
import fr.niasio.badblock.config.MessageManager;
import fr.niasio.badblock.util.UtilParticles;
import net.minecraft.server.v1_7_R4.EntityCreature;
import net.minecraft.server.v1_7_R4.Navigation;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftCreature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MountNyanSheep extends Mount {

    public MountNyanSheep(UUID owner) {
        super(EntityType.SHEEP, Material.STAINED_GLASS, (byte) new Random().nextInt(15), "NyanSheep", "pets.nyansheep", owner, MountType.NYANSHEEP);

        if (owner == null) return;
        ((LivingEntity) ent).setNoDamageTicks(Integer.MAX_VALUE);
    }

    @Override
    public void clear() {
        getPlayer().sendMessage(MessageManager.getMessage("Mounts.Despawn").replace("%mountname%", getMenuName()));
        Pets.getCustomPlayer(getPlayer()).currentMount = null;
        ent.remove();
    }

    @Override
    void onUpdate() {
        if (ent.getPassenger() == null)
            clear();
        move();

        ((Sheep) ent).setColor(DyeColor.values()[new Random().nextInt(15)]);

        List<RGBColor> colors = new ArrayList<>();

        colors.add(new RGBColor(255, -255, -255));
        colors.add(new RGBColor(255, 165, -255));
        colors.add(new RGBColor(255, 255, -255));
        colors.add(new RGBColor(154, 205, 50));
        colors.add(new RGBColor(30, 144, 255));
        colors.add(new RGBColor(148, -255, 211));

        float y = 1.2f;
        for (RGBColor rgbColor : colors) {
            for (int i = 0; i < 10; i++)
                UtilParticles.play(ent.getLocation().add(ent.getLocation().getDirection().normalize().multiply(-1).multiply(1.4)).add(0, y, 0), Effect.COLOURED_DUST, 0, 0, rgbColor.getRed() / 255, rgbColor.getGreen() / 255, rgbColor.getBlue() / 255, 1, 0);
            y -= 0.2;
        }

    }

    private void move() {
        Player player = getPlayer();
        double mult = 0.4D;
        Vector vel = player.getLocation().getDirection().setY(0).normalize().multiply(4);
        Location loc = player.getLocation().add(vel);
        EntityCreature ec = ((CraftCreature) ent).getHandle();
        Navigation nav = (Navigation) ec.getNavigation();
        nav.a(loc.getX(), loc.getY(), loc.getZ(), (1.0D + 2.0D * mult) * 1.0D);
    }

    class RGBColor {

        int red;
        int green;
        int blue;

        public RGBColor(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public int getBlue() {
            return blue;
        }

        public int getGreen() {
            return green;
        }

        public int getRed() {
            return red;
        }
    }

}