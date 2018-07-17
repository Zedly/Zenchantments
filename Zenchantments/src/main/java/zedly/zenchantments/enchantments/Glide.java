package zedly.zenchantments.enchantments;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import zedly.zenchantments.Config;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.Map;

import static org.bukkit.Material.AIR;
import static zedly.zenchantments.enums.Tool.LEGGINGS;

public class Glide extends CustomEnchantment {

    public Glide() {
        super(20);
        maxLevel = 3;
        loreName = "Glide";
        probability = 0;
        enchantable = new Tool[]{LEGGINGS};
        conflicting = new Class[]{};
        description = "Gently brings the player back to the ground when sneaking";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.NONE;
    }

    @Override
    public boolean onFastScan(Player player, int level, boolean usedHand) {
        if(!Storage.sneakGlide.containsKey(player)) {
            Storage.sneakGlide.put(player, player.getLocation().getY());
        }
        if(!player.isSneaking() || Storage.sneakGlide.get(player) == player.getLocation().getY()) {
            return false;
        }
        boolean b = false;
        for(int i = -5; i < 0; i++) {
            if(player.getLocation().getBlock().getRelative(0, i, 0).getType() != AIR) {
                b = true;
            }
        }
        if(player.getVelocity().getY() > -0.5) {
            b = true;
        }
        if(!b) {
            double sinPitch = Math.sin(Math.toRadians(player.getLocation().getPitch()));
            double cosPitch = Math.cos(Math.toRadians(player.getLocation().getPitch()));
            double sinYaw = Math.sin(Math.toRadians(player.getLocation().getYaw()));
            double cosYaw = Math.cos(Math.toRadians(player.getLocation().getYaw()));
            double y = -1 * (sinPitch);
            Vector v = new Vector(-cosPitch * sinYaw, 0, -1 * (-cosPitch * cosYaw));
            v.multiply(level * power / 2);
            v.setY(-1);
            player.setVelocity(v);
            player.setFallDistance((float) (6 - level * power) - 4);
            Location l = player.getLocation().clone();
            l.setY(l.getY() - 3);
            Utilities.display(l, Particle.CLOUD, 1, .1f, 0, 0, 0);
        }
        if(Storage.rnd.nextInt(5 * level) == 5) { // Slowly damage all armor
            ItemStack[] s = player.getInventory().getArmorContents();
            for(int i = 0; i < 4; i++) {
                if(s[i] != null) {
                    Map<CustomEnchantment, Integer> map = Config.get(player.getWorld()).getEnchants(s[i]);
                    if(map.containsKey(this)) {
                        Utilities.addUnbreaking(player, s[i], 1);
                    }
                    if(s[i].getDurability() > s[i].getType().getMaxDurability()) {
                        s[i] = null;
                    }
                }
            }
            player.getInventory().setArmorContents(s);
        }
        Storage.sneakGlide.put(player, player.getLocation().getY());
        return true;
    }

}
