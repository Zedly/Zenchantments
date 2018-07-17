package zedly.zenchantments;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import zedly.zenchantments.annotations.EffectTask;
import zedly.zenchantments.compatibility.CompatibilityAdapter;
import zedly.zenchantments.enums.Frequency;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.BLAZE;
import static org.bukkit.potion.PotionEffectType.*;

// EnchantArrows is the defualt structure for these arrows. Each arrow below it will extend this class
//      and will override any methods as neccecary in its behavior
public class EnchantArrow implements AdvancedArrow {

    protected static final CompatibilityAdapter ADAPTER = Storage.COMPATIBILITY_ADAPTER;

    private final Projectile entity;    // The Arrow associated with the EnchantArrow
    private int tick;                   // The current tick
    private final int level;            // The enchantment's level
    private final double power;         // The enchantment's power level

    // Creates a new EnchantArrow with the given Projectile
    public EnchantArrow(Projectile entity) {
        this(entity, 0);
    }

    // Creates a new ElementalArrow with the given Projectile and enchant level
    public EnchantArrow(Projectile entity, int level) {
        this(entity, level, 1f);
    }

    // Creates a new ElementalArrow with the given Projectile, enchant level, and enchant power
    public EnchantArrow(Projectile entity, int level, double power) {
        this.entity = entity;
        this.level = level;
        this.power = power;
    }

    // Advances the arrow's tick forward one
    public void tick() {
        tick++;
    }

    // Returns the current tick of the arrow
    public int getTick() {
        return tick;
    }

    // Returns the Enchantment level for the Custom Enchantment on the arrow
    public int getLevel() {
        return level;
    }

    // Returns the Enchantment power for the Custom Enchantment on the arrow
    public double getPower() {
        return power;
    }

    // Returns the Arrow associated with the ElementalArrow
    public Projectile getArrow() {
        return entity;
    }

    // Called when the player shoots an arrow of this type
    public void onLaunch(LivingEntity player, List<String> lore) {
    }

    // Called throughout the flight of the arrow
    public void onFlight() {
    }

    // Called when the arrow hits a block
    public void onImpact() {
        die();
    }

    // Called when the arrow kills an entity
    public void onKill(EntityDeathEvent evt) {
    }

    // Called when the arrow hits an entity
    public boolean onImpact(EntityDamageByEntityEvent evt) {
        onImpact();
        return true;
    }

    // Called when the arrow has finished any functionality
    public void die() {
        final Entity e = entity;
        final EnchantArrow arrow = this;
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
            if (Storage.advancedProjectiles.containsKey(e)) {
                if (Storage.advancedProjectiles.get(e).size() == 1) {
                    Storage.advancedProjectiles.remove(e);
                } else {
                    Storage.advancedProjectiles.get(e).remove(arrow);
                }
            }
        }, 1);
    }

    @EffectTask(Frequency.HIGH)
    // Repeated actions for certain elemental arrows
    public static void elementalArrows() {
        // Remove arrows if they don't exist or if it's been longer than 30 seconds
        Iterator it = Storage.advancedProjectiles.values().iterator();
        while (it.hasNext()) {
            for (AdvancedArrow a : (Set<AdvancedArrow>) it.next()) {
                a.onFlight();
                a.tick();
                if (a.getArrow().isDead() || a.getTick() > 600) {
                    it.remove();
                    a.getArrow().remove();
                    break;
                }
            }
        }
    }

// Enchantment Arrows
    public static class ArrowEnchantBlizzard extends EnchantArrow {

        public ArrowEnchantBlizzard(Projectile entity, int level, double power) {
            super(entity, level, power);
        }

        public void onImpact() {
            Utilities.display(Utilities.getCenter(getArrow().getLocation()), Particle.CLOUD, 100 * getLevel(), 0.1f, getLevel(), 1.5f, getLevel());
            double radius = 1 + getLevel() * getPower();
            for (Entity e : getArrow().getNearbyEntities(radius, radius, radius)) {
                if (e instanceof LivingEntity && !e.equals(getArrow().getShooter()) && ADAPTER.attackEntity((LivingEntity) e, (Player) getArrow().getShooter(), 0)) {
                    Utilities.addPotion((LivingEntity) e, SLOW, (int) Math.round(50 + getLevel()
                            * getPower() * 50), (int) Math.round(getLevel() * getPower() * 2));
                }
            }
            die();
        }
    }

    public static class ArrowEnchantFirestorm extends EnchantArrow {

        public ArrowEnchantFirestorm(Projectile entity, int level, double power) {
            super(entity, level, power);
        }

        public void onImpact() {
            Utilities.display(Utilities.getCenter(getArrow().getLocation()), Particle.FLAME, 100 * getLevel(), 0.1f, getLevel(), 1.5f, getLevel());
            double radius = 1 + getLevel() * getPower();
            for (Entity e : getArrow().getNearbyEntities(radius, radius, radius)) {
                if (e instanceof LivingEntity && !e.equals(getArrow().getShooter()) && ADAPTER.attackEntity((LivingEntity) e, (Player) getArrow().getShooter(), 0)) {
                    ((LivingEntity) e).setFireTicks((int) Math.round(getLevel() * getPower() * 100));
                }
            }
            die();
        }
    }

    public static class ArrowEnchantFirework extends EnchantArrow {

        public ArrowEnchantFirework(Projectile entity, int level) {
            super(entity, level);
        }

        public void onImpact() {
            Location l = getArrow().getLocation();
            FireworkEffect.Type[] type = {FireworkEffect.Type.BALL, FireworkEffect.Type.BURST, FireworkEffect.Type.STAR, FireworkEffect.Type.BALL_LARGE};
            FireworkEffect.Builder b = FireworkEffect.builder();
            b = b.withColor(Color.LIME).withColor(Color.RED).withColor(Color.BLUE).withColor(Color.YELLOW).withColor(Color.fromRGB(0xFF00FF)).withColor(Color.ORANGE).withColor(Color.fromRGB(0x3E89FF));
            b = b.trail(true);
            b = b.with(type[(getLevel() > 4 ? 4 : getLevel()) - 1]);
            final Firework f = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
            FireworkMeta d = (FireworkMeta) f.getFireworkMeta();
            d.setPower(1);
            d.addEffect(b.build());
            f.setFireworkMeta(d);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                f.detonate();
            }, 1);
            die();
        }
    }

    public static class ArrowEnchantFuse extends EnchantArrow {

        public ArrowEnchantFuse(Projectile entity) {
            super(entity);
        }

        public void onImpact() {
            Location loc = getArrow().getLocation();
            for (int i = 1; i < 5; i++) {
                Vector vec = getArrow().getVelocity().multiply(.25 * i);
                Location hitLoc = new Location(loc.getWorld(), loc.getX() + vec.getX(), loc.getY() + vec.getY(), loc.getZ() + vec.getZ());
                if (hitLoc.getBlock().getType().equals(TNT)) {
                    BlockBreakEvent event = new BlockBreakEvent(hitLoc.getBlock(), (Player) getArrow().getShooter());
                    Bukkit.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        hitLoc.getBlock().setType(AIR);
                        hitLoc.getWorld().spawnEntity(hitLoc, EntityType.PRIMED_TNT);
                        die();
                    }
                    return;
                }
            }
            die();
        }

        public boolean onImpact(EntityDamageByEntityEvent evt) {
            Location l = evt.getEntity().getLocation();
            if (ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) getArrow().getShooter(), 0)) {
                if (evt.getEntity().getType().equals(EntityType.CREEPER)) {
                    Creeper c = (Creeper) evt.getEntity();
                    float power;
                    if (c.isPowered()) {
                        power = 6f;
                    } else {
                        power = 3.1f;
                    }
                    if (Config.get(evt.getDamager().getWorld()).explosionBlockBreak()) {
                        evt.getEntity().getWorld().createExplosion(evt.getEntity().getLocation(), power);
                    } else {
                        evt.getEntity().getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), power, false, false);
                    }
                    c.remove();
                } else if (evt.getEntity().getType().equals(EntityType.MUSHROOM_COW)) {
                    MushroomCow c = (MushroomCow) evt.getEntity();
                    if (c.isAdult()) {
                        Utilities.display(l, Particle.EXPLOSION_LARGE, 1, 1f, 0, 0, 0);
                        evt.getEntity().remove();
                        l.getWorld().spawnEntity(l, EntityType.COW);
                        l.getWorld().dropItemNaturally(l, new ItemStack(Material.RED_MUSHROOM, 5));
                    }
                }
            }
            die();
            return true;
        }
    }

    public static class ArrowEnchantLevel extends EnchantArrow {

        public ArrowEnchantLevel(Projectile entity, int level, double power) {
            super(entity, level, power);
        }

        public void onKill(EntityDeathEvent evt) {
            evt.setDroppedExp((int) (evt.getDroppedExp() * (1.3 + (getLevel() * getPower() * .5))));
            die();
        }
    }

    public static class ArrowEnchantPotion extends EnchantArrow {

        private final PotionEffectType[] POTIONS = new PotionEffectType[]{ABSORPTION,
            DAMAGE_RESISTANCE, FIRE_RESISTANCE, SPEED,
            JUMP, INVISIBILITY, INCREASE_DAMAGE,
            HEALTH_BOOST, HEAL, REGENERATION,
            NIGHT_VISION, SATURATION, FAST_DIGGING};

        public ArrowEnchantPotion(Projectile entity, int level, double power) {
            super(entity, level, power);
        }

        public boolean onImpact(EntityDamageByEntityEvent evt) {
            if (Storage.rnd.nextInt((int) Math.round(10 / (getLevel() * getPower() + 1))) == 1) {
                Utilities.addPotion((LivingEntity) getArrow().getShooter(), POTIONS[Storage.rnd.nextInt(12)],
                        150 + (int) Math.round(getLevel() * getPower() * 50), (int) Math.round(getLevel() * getPower()));
            }
            die();
            return true;
        }
    }

    public static class ArrowEnchantToxic extends EnchantArrow {

        public ArrowEnchantToxic(Projectile entity, int level, double power) {
            super(entity, level, power);
        }

        public boolean onImpact(final EntityDamageByEntityEvent evt) {
            if (ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) getArrow().getShooter(), 0)) {
                final int value = (int) Math.round(getLevel() * getPower());
                Utilities.addPotion((LivingEntity) evt.getEntity(), CONFUSION, 80 + 60 * value, 4);
                Utilities.addPotion((LivingEntity) evt.getEntity(), HUNGER, 40 + 60 * value, 4);
                if (evt.getEntity() instanceof Player) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                        ((LivingEntity) evt.getEntity()).removePotionEffect(HUNGER);
                        Utilities.addPotion((LivingEntity) evt.getEntity(), HUNGER, 60 + 40 * value, 0);
                    }, 20 + 60 * value);
                    Storage.hungerPlayers.put((Player) evt.getEntity(), (1 + value) * 100);
                }
            }
            die();
            return true;
        }
    }

    public static class ArrowEnchantQuickShot extends EnchantArrow {

        public ArrowEnchantQuickShot(Projectile entity) {
            super(entity);
        }

        public void onLaunch(LivingEntity player, List<String> lore) {
            getArrow().setVelocity(getArrow().getVelocity().normalize().multiply(3.5f));
            die();
        }
    }

    public static class ArrowEnchantReaper extends EnchantArrow {

        public ArrowEnchantReaper(Projectile entity, int level, double power) {
            super(entity, level, power);
        }

        public boolean onImpact(EntityDamageByEntityEvent evt) {
            if (ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) getArrow().getShooter(), 0)) {
                int pow = (int) Math.round(getLevel() * getPower());
                int dur = (int) Math.round(20 + getLevel() * 10 * getPower());
                Utilities.addPotion((LivingEntity) evt.getEntity(), PotionEffectType.WITHER, dur, pow);
                Utilities.addPotion((LivingEntity) evt.getEntity(), BLINDNESS, dur, pow);
            }
            die();
            return true;
        }

    }

    public static class ArrowEnchantSiphon extends EnchantArrow {

        public ArrowEnchantSiphon(Projectile entity, int level, double power) {
            super(entity, level, power);
        }

        public boolean onImpact(EntityDamageByEntityEvent evt) {
            if (evt.getEntity() instanceof LivingEntity && ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) getArrow().getShooter(), 0)) {
                Player p = (Player) ((Projectile) evt.getDamager()).getShooter();
                LivingEntity ent = (LivingEntity) evt.getEntity();
                int difference = (int) Math.round(.17 * getLevel() * getPower() * evt.getDamage());
                while (difference > 0) {
                    if (p.getHealth() <= 19) {
                        p.setHealth(p.getHealth() + 1);
                    }
                    difference--;
                }
            }
            die();
            return true;
        }
    }

    public static class ArrowEnchantStationary extends EnchantArrow {

        public ArrowEnchantStationary(Projectile entity) {
            super(entity);
        }

        public boolean onImpact(EntityDamageByEntityEvent evt) {
            if (ADAPTER.attackEntity((LivingEntity) evt.getEntity(), (Player) getArrow().getShooter(), 0)) {
                LivingEntity ent = (LivingEntity) evt.getEntity();
                if (evt.getDamage() < ent.getHealth()) {
                    evt.setCancelled(true);
                    ((LivingEntity) evt.getEntity()).damage(evt.getDamage());
                    if (evt.getDamager().getType() == EntityType.ARROW) {
                        evt.getDamager().remove();
                    }
                }
            }
            die();
            return true;
        }
    }

    public static class ArrowEnchantTracer extends EnchantArrow {

        public ArrowEnchantTracer(Projectile entity, int level, double power) {
            super(entity, level, power);
            Storage.tracer.put((Arrow) entity, (int) Math.round(level * power));
        }

        @Override
        public boolean onImpact(EntityDamageByEntityEvent evt) {
            if (evt.isCancelled()) {
                Storage.tracer.remove((Arrow) getArrow());
                die();
            }
            return true;
        }
    }

    public static class ArrowEnchantVortex extends EnchantArrow {

        public ArrowEnchantVortex(Projectile entity) {
            super(entity);
        }

        public void onKill(final EntityDeathEvent evt) {
            Storage.vortexLocs.put(evt.getEntity().getLocation().getBlock(), evt.getEntity().getKiller().getLocation());
            int i = evt.getDroppedExp();
            evt.setDroppedExp(0);
            evt.getEntity().getKiller().giveExp(i);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                Storage.vortexLocs.remove(evt.getEntity().getLocation().getBlock());
            }, 3);
            die();
        }
    }

// Arrows In-Development
    public static class ArrowGenericMulitple extends EnchantArrow {

        public ArrowGenericMulitple(Projectile entity) {
            super(entity);
        }

        public boolean onImpact(EntityDamageByEntityEvent evt) {
            final LivingEntity e = (LivingEntity) evt.getEntity();
            int temp = e.getMaximumNoDamageTicks();
            e.setMaximumNoDamageTicks(0);
            e.setNoDamageTicks(0);
            e.setMaximumNoDamageTicks(temp);
            die();
            return true;
        }

        public void onImpact() {
            //Arrow p = getArrow().getWorld().spawnArrow(getArrow().getLocation(), getArrow().getVelocity(), (float) (getArrow().getVelocity().length() / 10), 0);
            //p.setFireTicks(getArrow().getFireTicks());
            //p.getLocation().setDirection(getArrow().getLocation().getDirection());
            //p.setMetadata("ze.arrow", new FixedMetadataValue(Storage.zenchantments, null));
            die();
            this.getArrow().remove();
        }
    }

// Enchantment Admin Arrows
    public static class ArrowAdminApocalypse extends EnchantArrow {

        public ArrowAdminApocalypse(Projectile entity) {
            super(entity);
        }

        public void onImpact() {
            final Config config = Config.get(getArrow().getWorld());
            Location l2 = getArrow().getLocation().clone();
            l2.setY(l2.getY() + 1);
            Location[] locs = new Location[]{getArrow().getLocation(), l2};
            getArrow().getWorld().strikeLightning(l2);
            for (int ls = 0; ls < locs.length; ls++) {
                final Location l = locs[ls];
                final int lsf = ls;
                for (int i = 0; i <= 45; i++) {
                    final int c = i + 1;
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                        Entity ent = l.getWorld().spawnFallingBlock(l, FIRE, (byte) 0);
                        Vector v = l.toVector();
                        v.setY(Math.abs(Math.sin(c)));
                        if (lsf % 2 == 0) {
                            v.setZ((Math.sin(c) / 2));
                            v.setX((Math.cos(c) / 2));
                        } else {
                            v.setX((Math.sin(c) / 2));
                            v.setZ((Math.cos(c) / 2));
                        }
                        ent.setVelocity(v.multiply(1.5));
                        TNTPrimed prime = (TNTPrimed) getArrow().getWorld().spawnEntity(l, EntityType.PRIMED_TNT);
                        prime.setFuseTicks(200);
                        prime.setYield(config.explosionBlockBreak() ? 4 : 0);
                        Blaze blaze = (Blaze) getArrow().getWorld().spawnEntity(l, BLAZE);
                        blaze.addPotionEffect(new PotionEffect(ABSORPTION, 150, 100000));
                        blaze.addPotionEffect(new PotionEffect(HARM, 10000, 1));
                        if (config.explosionBlockBreak()) {
                            Entity crystal = getArrow().getWorld().spawnEntity(l, EntityType.ENDER_CRYSTAL);
                            ent.setPassenger(prime);
                            crystal.setPassenger(blaze);
                            prime.setPassenger(crystal);
                        } else {
                            ent.setPassenger(prime);
                            prime.setPassenger(blaze);
                        }
                    }, c);
                }
            }
            die();
        }
    }

    public static class ArrowAdminMissile extends EnchantArrow {

        public ArrowAdminMissile(Projectile entity) {
            super(entity);
        }

        public void onLaunch(LivingEntity player, List<String> lore) {
            final Config config = Config.get(player.getWorld());
            Location playLoc = player.getLocation();
            final Location target = Utilities.getCenter(player.getTargetBlock((HashSet<Material>) null, 220));
            target.setY(target.getY() + .5);
            final Location c = playLoc;
            c.setY(c.getY() + 1.1);
            final double d = target.distance(c);
            for (int i = 9; i <= ((int) (d * 5) + 9); i++) {
                final int i1 = i;
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                    Location loc = target.clone();
                    loc.setX(c.getX() + (i1 * ((target.getX() - c.getX()) / (d * 5))));
                    loc.setY(c.getY() + (i1 * ((target.getY() - c.getY()) / (d * 5))));
                    loc.setZ(c.getZ() + (i1 * ((target.getZ() - c.getZ()) / (d * 5))));
                    Location loc2 = target.clone();
                    loc2.setX(c.getX() + ((i1 + 10) * ((target.getX() - c.getX()) / (d * 5))));
                    loc2.setY(c.getY() + ((i1 + 10) * ((target.getY() - c.getY()) / (d * 5))));
                    loc2.setZ(c.getZ() + ((i1 + 10) * ((target.getZ() - c.getZ()) / (d * 5))));
                    Utilities.display(loc, Particle.FLAME, 10, .001f, 0, 0, 0);
                    Utilities.display(loc, Particle.FLAME, 1, .1f, 0, 0, 0);
                    if (i1 % 50 == 0) {
                        target.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 10f, .1f);
                    }
                    if (i1 >= ((int) (d * 5) + 9) || loc2.getBlock().getType() != AIR) {
                        Utilities.display(loc2, Particle.EXPLOSION_HUGE, 10, 0.1f, 0, 0, 0);
                        Utilities.display(loc, Particle.FLAME, 175, 1f, 0, 0, 0);
                        loc2.setY(loc2.getY() + 5);
                        loc2.getWorld().createExplosion(loc2.getX(), loc2.getY(), loc2.getZ(), 10, config.explosionBlockBreak(), config.explosionBlockBreak());
                    }
                }, (int) (i / 7));
            }
        }
    }

    public static class ArrowAdminSingularity extends EnchantArrow {

        public ArrowAdminSingularity(Projectile entity, int level) {
            super(entity, level);
        }

        public void onImpact() {
            final Location l = getArrow().getLocation().clone();
            Storage.blackholes.put(l, true);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                Storage.blackholes.put(l, false);
            }, 40);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                Storage.blackholes.remove(l);
            }, 60);
            for (int i = 1; i <= 61; i++) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Storage.zenchantments, () -> {
                    Utilities.display(l, Particle.SMOKE_LARGE, 50, .001f, .75f, .75f, .75f);
                    l.getWorld().playSound(l, Sound.ENTITY_ENDERDRAGON_GROWL, 10f, .1f);
                }, i);
            }
            die();
        }
    }
}
