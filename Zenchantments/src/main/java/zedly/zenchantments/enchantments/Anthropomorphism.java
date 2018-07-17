package zedly.zenchantments.enchantments;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import zedly.zenchantments.CustomEnchantment;
import zedly.zenchantments.Storage;
import zedly.zenchantments.Utilities;
import zedly.zenchantments.annotations.EffectTask;
import zedly.zenchantments.enums.Frequency;
import zedly.zenchantments.enums.Hand;
import zedly.zenchantments.enums.Tool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.*;
import static zedly.zenchantments.enums.Tool.PICKAXE;

public class Anthropomorphism extends CustomEnchantment {
	private static final Material[] MAT = new Material[]{STONE, GRAVEL, DIRT, GRASS};

    public Anthropomorphism() {
	    super(1);
	    maxLevel = 1;
	    loreName = "Anthropomorphism";
	    probability = 0;
	    enchantable = new Tool[]{PICKAXE};
	    conflicting = new Class[]{Pierce.class, Switch.class};
	    description =
                "Spawns blocks to protect you when right sneak clicking, and attacks entities when left clicking";
	    cooldown = 0;
	    power = 1.0;
	    handUse = Hand.BOTH;
    }

	@EffectTask(Frequency.MEDIUM)
	// Removes Anthropomorphism blocks when they are dead
	public static void anthropomorphism2() {
		Iterator it = Storage.idleBlocks.keySet().iterator();
		while (it.hasNext()) {
			FallingBlock b = (FallingBlock) it.next();
			if (b.isDead()) {
				it.remove();
			}
		}
		it = Storage.attackBlocks.keySet().iterator();
		while (it.hasNext()) {
			FallingBlock b = (FallingBlock) it.next();
			if (b.isDead()) {
				it.remove();
			}
		}
	}

	// Moves Anthropomorphism blocks around depending on their state
	@EffectTask(Frequency.HIGH)
	public static void anthropomorphism() {
		// Move agressive Anthropomorphism Blocks towards a target & attack
		Iterator<FallingBlock> anthroIterator = Storage.attackBlocks.keySet().iterator();
		while (anthroIterator.hasNext()) {
			FallingBlock blockEntity = anthroIterator.next();
			if (!Storage.anthVortex.contains(Storage.idleBlocks.get(blockEntity))) {
				for (Entity e : blockEntity.getNearbyEntities(7, 7, 7)) {
					if (e instanceof Monster) {
						LivingEntity targetEntity = (LivingEntity) e;
						blockEntity.setVelocity(
								e.getLocation().subtract(blockEntity.getLocation()).toVector().multiply(.25));
						if (targetEntity.getLocation().getWorld().equals(blockEntity.getLocation().getWorld())) {
							if (targetEntity.getLocation().distance(blockEntity.getLocation()) < 1.2
									&& blockEntity.hasMetadata("ze.anthrothrower")) {
								Player attacker = (Player) blockEntity.getMetadata("ze.anthrothrower").get(0).value();
								if (targetEntity.getNoDamageTicks() == 0
										&& Storage.COMPATIBILITY_ADAPTER.attackEntity(targetEntity, attacker,
										.5 * Storage.attackBlocks.get(blockEntity))) {
									targetEntity.setNoDamageTicks(0);
									anthroIterator.remove();
									blockEntity.remove();
								}
							}
						}
					}
				}
			}
		}
		// Move passive Anthropomorphism Blocks around
		Storage.fallBool = !Storage.fallBool;
		for (FallingBlock b : Storage.idleBlocks.keySet()) {
			if (Storage.anthVortex.contains(Storage.idleBlocks.get(b))) {
				Location loc = Storage.idleBlocks.get(b).getLocation();
				Vector v;
				if (b.getLocation().getWorld().equals(Storage.idleBlocks.get(b).getLocation().getWorld())) {
					if (Storage.fallBool && b.getLocation().distance(Storage.idleBlocks.get(b).getLocation()) < 10) {
						v = b.getLocation().subtract(loc).toVector();
					} else {
						double x = 6f * Math.sin(b.getTicksLived() / 10f);
						double z = 6f * Math.cos(b.getTicksLived() / 10f);
						Location tLoc = loc.clone();
						tLoc.setX(tLoc.getX() + x);
						tLoc.setZ(tLoc.getZ() + z);
						v = tLoc.subtract(b.getLocation()).toVector();
					}
					v.multiply(.05);
					boolean close = false;
					for (int x = -3; x < 0; x++) {
						if (b.getLocation().getBlock().getRelative(0, x, 0).getType() != AIR) {
							close = true;
						}
					}
					if (close) {
						v.setY(Math.abs(Math.sin(b.getTicksLived() / 10f)));
					} else {
						v.setY(0);
					}
					b.setVelocity(v);
				}
			}
		}
	}

	@Override
	public boolean onBlockInteract(PlayerInteractEvent evt, int level, boolean usedHand) {
		Player player = evt.getPlayer();
		ItemStack hand = Utilities.usedStack(player, usedHand);

		if (evt.getAction() == RIGHT_CLICK_AIR || evt.getAction() == RIGHT_CLICK_BLOCK) {
			if (player.isSneaking()) {
				if (!Storage.anthVortex.contains(player)) {
					Storage.anthVortex.add(player);
				}
				int counter = 0;
				for (Entity p : Storage.idleBlocks.values()) {
					if (p.equals(player)) {
						counter++;
					}
				}
				if (counter < 64 && player.getInventory().contains(COBBLESTONE)) {
					Utilities.removeItem(player, COBBLESTONE, 1);
					Utilities.damageTool(player, 2, usedHand);
					player.updateInventory();
					Location loc = player.getLocation();
					FallingBlock blockEntity =
							loc.getWorld().spawnFallingBlock(loc, MAT[Storage.rnd.nextInt(4)], (byte) 0x0);
					blockEntity.setDropItem(false);
					blockEntity.setGravity(false);
					blockEntity
							.setMetadata("ze.anthrothrower", new FixedMetadataValue(Storage.zenchantments, player));
					Storage.idleBlocks.put(blockEntity, player);
					return true;
				}
			}
			return false;
		} else if ((evt.getAction() == LEFT_CLICK_AIR || evt.getAction() == LEFT_CLICK_BLOCK)
				|| hand.getType() == AIR) {
			Storage.anthVortex.remove(player);
			List<FallingBlock> toRemove = new ArrayList<>();
			for (FallingBlock blk : Storage.idleBlocks.keySet()) {
				if (Storage.idleBlocks.get(blk).equals(player)) {
					Storage.attackBlocks.put(blk, power);
					toRemove.add(blk);
					Block targetBlock = player.getTargetBlock((Set<Material>) null, 7);
					Bukkit.getLogger().info(targetBlock.toString());
					blk.setVelocity(targetBlock
							.getLocation().subtract(player.getLocation()).toVector().multiply(.25));
				}
			}
			for (FallingBlock blk : toRemove) {
				Storage.idleBlocks.remove(blk);
				blk.setGravity(true);
				blk.setGlowing(true);
			}
		}
		return false;
	}
}
