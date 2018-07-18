package zedly.zenchantments.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import zedly.zenchantments.Config;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.Map;

import static org.bukkit.Material.*;
import static zedly.zenchantments.enums.Tool.LEGGINGS;

public class GreenThumb extends CustomEnchantment {

    public GreenThumb() {
        super(24);
        maxLevel = 3;
        loreName = "Green Thumb";
        probability = 0;
        enchantable = new Tool[]{LEGGINGS};
        conflicting = new Class[]{};
        description = "Grows the foliage around the player";
        cooldown = 0;
        power = 1.0;
        handUse = Hand.NONE;
    }

    @Override
    public boolean onScan(Player player, int level, boolean usedHand) {
        Location loc = player.getLocation().clone();
        Block centerBlock = (Block) loc.getBlock();
        int radius = (int) Math.round(power * level + 2);
        for(int x = -(radius); x <= radius; x++) {
            for(int y = -(radius) - 1; y <= radius - 1; y++) {
                for(int z = -(radius); z <= radius; z++) {
                    Block relativeBlock = centerBlock.getRelative(x, y, z);
                    if(relativeBlock.getLocation().distance(loc) < radius) {
                        if(level != 10 && Storage.rnd.nextInt((int) (300 / (power * level / 2))) != 0) {
                            continue;
                        }
                        boolean applied = false;
                        switch(relativeBlock.getType()) {
                            case DIRT:
                                if(relativeBlock.getData() != 2 && relativeBlock.getData() != 1
                                   && relativeBlock.getRelative(0, 1, 0).getType() == AIR) {
                                    byte data = 0;
                                    Material mat;
                                    switch(centerBlock.getBiome()) {
                                        case MUSHROOM_ISLAND:
                                        case MUSHROOM_ISLAND_SHORE:
                                            mat = MYCEL;
                                            break;
                                        case REDWOOD_TAIGA:
                                        case REDWOOD_TAIGA_HILLS:
                                        case TAIGA_COLD:
                                        case TAIGA_COLD_HILLS:
                                            data = (byte) 2;
                                        default:
                                            mat = GRASS;
                                    }
                                    applied = ADAPTER.placeBlock(relativeBlock, player, mat, data);
                                }
                                break;
                            default:
                                applied = ADAPTER.grow(centerBlock.getRelative(x, y, z), player);
                                break;
                        }
                        if(applied) { // Display particles and damage armor
                            Utilities.display(Utilities.getCenter(centerBlock.getRelative(x, y + 1, z)),
                                              Particle.VILLAGER_HAPPY, 20, 1f, .3f, .3f, .3f);
                            int chc = Storage.rnd.nextInt(50);
                            if(chc > 42 && level != 10) {
                                ItemStack[] s = player.getInventory().getArmorContents();
                                for(int i = 0; i < 4; i++) {
                                    if(s[i] != null) {
                                        Map<CustomEnchantment, Integer> map =
                                                Config.get(player.getWorld()).getEnchants(s[i]);
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
                        }
                    }
                }
            }
        }

        return true;
    }
}
