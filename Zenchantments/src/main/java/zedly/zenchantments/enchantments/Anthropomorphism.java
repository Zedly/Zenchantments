package zedly.zenchantments.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.enums.Tool;
import zedly.zenchantments.Utilities;

import java.util.*;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.*;
import static zedly.zenchantments.enums.Tool.PICKAXE;

public class Anthropomorphism extends CustomEnchantment {

    public Anthropomorphism() {
        maxLevel = 1;
        loreName = "Anthropomorphism";
        probability = 0;
        enchantable = new Tool[]{PICKAXE};
        conflicting = new Class[]{Pierce.class, Switch.class};
        description =
                "Spawns blocks to protect you when right sneak clicking, and attacks entities when left clicking";
        cooldown = 0;
        power = 1.0;
        handUse = 3;

    }

    public int getEnchantmentId() {
        return 1;
    }

    @Override
    public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
        Player player = evt.getPlayer();
        ItemStack hand = Utilities.usedStack(player, usedHand);
        if(evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK) {
            if(player.isSneaking()) {
                if(!Storage.anthVortex.contains(player)) {
                    Storage.anthVortex.add(player);
                }
                int counter = 0;
                for(Entity p : Storage.idleBlocks.values()) {
                    if(p.equals(player)) {
                        counter++;
                    }
                }
                if(counter < 64 && player.getInventory().contains(COBBLESTONE)) {
                    Utilities.removeItem(player, COBBLESTONE, 1);
                    Utilities.damageTool(player, 2, usedHand);
                    player.updateInventory();
                    Location loc = player.getLocation();
                    Material mat[] = new Material[]{STONE, GRAVEL, DIRT, GRASS};
                    FallingBlock blockEntity =
                            loc.getWorld().spawnFallingBlock(loc, mat[Storage.rnd.nextInt(4)], (byte) 0x0);
                    blockEntity.setDropItem(false);
                    blockEntity.setGravity(false);
                    blockEntity
                            .setMetadata("ze.anthrothrower", new FixedMetadataValue(Storage.zenchantments, player));
                    Storage.idleBlocks.put(blockEntity, player);
                    return true;
                }
            }
            return false;
        } else if((evt.getAction() == LEFT_CLICK_AIR || evt.getAction() == LEFT_CLICK_BLOCK)
                  || hand.getType() == AIR) {
            Storage.anthVortex.remove(player);
            List<FallingBlock> toRemove = new ArrayList<>();
            for(FallingBlock blk : Storage.idleBlocks.keySet()) {
                if(Storage.idleBlocks.get(blk).equals(player)) {
                    Storage.attackBlocks.put(blk, power);
                    toRemove.add(blk);
                    blk.setVelocity(player.getTargetBlock((HashSet<Byte>) null, 7)
                                          .getLocation().subtract(player.getLocation()).toVector().multiply(.25));
                }
            }
            for(FallingBlock blk : toRemove) {
                Storage.idleBlocks.remove(blk);
                blk.setGravity(true);
                blk.setGlowing(true);
            }
        }
        return false;
    }
}
