package zedly.zenchantments.enchantments;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.*;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.requireNonNull;
import static org.bukkit.entity.EntityType.*;

public final class Transformation extends Zenchantment {
    public static final String KEY = "transformation";

    private static final String                             NAME        = "Transformation";
    private static final String                             DESCRIPTION = "Occasionally causes the attacked mob to be transformed into its similar cousin";
    private static final Set<Class<? extends Zenchantment>> CONFLICTING = ImmutableSet.of();
    private static final Hand                               HAND_USE    = Hand.LEFT;

    private static final List<EntityType> ENTITY_TYPES_FROM = ImmutableList.<EntityType>builder().add(
        HUSK,
        WITCH,
        EntityType.COD,
        PHANTOM,
        HORSE,
        SKELETON,
        EntityType.CHICKEN,
        SQUID,
        OCELOT,
        POLAR_BEAR,
        COW,
        PIG,
        SPIDER,
        SLIME,
        GUARDIAN,
        ENDERMITE,
        SKELETON_HORSE,
        EntityType.RABBIT,
        SHULKER,
        SNOWMAN,
        DROWNED,
        VINDICATOR,
        EntityType.SALMON,
        BLAZE,
        DONKEY,
        STRAY,
        PARROT,
        DOLPHIN,
        WOLF,
        SHEEP,
        MUSHROOM_COW,
        ZOMBIFIED_PIGLIN,
        CAVE_SPIDER,
        MAGMA_CUBE,
        ELDER_GUARDIAN,
        SILVERFISH,
        ZOMBIE_HORSE,
        EntityType.RABBIT,
        ENDERMAN,
        IRON_GOLEM,
        ZOMBIE,
        EVOKER,
        PUFFERFISH,
        VEX,
        MULE,
        WITHER_SKELETON,
        BAT,
        TURTLE,
        ZOMBIE_VILLAGER,
        VILLAGER,
        EntityType.TROPICAL_FISH,
        GHAST,
        LLAMA,
        CREEPER
    ).build();

    private static final List<EntityType> ENTITY_TYPES_TO = ImmutableList.<EntityType>builder().add(
        DROWNED,
        VINDICATOR,
        EntityType.SALMON,
        BLAZE,
        DONKEY,
        STRAY,
        PARROT,
        DOLPHIN,
        WOLF,
        SHEEP,
        MUSHROOM_COW,
        ZOMBIFIED_PIGLIN,
        CAVE_SPIDER,
        MAGMA_CUBE,
        ELDER_GUARDIAN,
        SILVERFISH,
        ZOMBIE_HORSE,
        EntityType.RABBIT,
        ENDERMAN,
        IRON_GOLEM,
        ZOMBIE,
        EVOKER,
        PUFFERFISH,
        VEX,
        MULE,
        WITHER_SKELETON,
        BAT,
        TURTLE,
        OCELOT,
        POLAR_BEAR,
        COW,
        PIG,
        SPIDER,
        SLIME,
        GUARDIAN,
        ENDERMITE,
        SKELETON_HORSE,
        EntityType.RABBIT,
        SHULKER,
        SNOWMAN,
        ZOMBIE_VILLAGER,
        VILLAGER,
        EntityType.TROPICAL_FISH,
        GHAST,
        LLAMA,
        SKELETON,
        EntityType.CHICKEN,
        SQUID,
        HUSK,
        WITCH,
        EntityType.COD,
        PHANTOM,
        HORSE,
        CREEPER
    ).build();

    private final NamespacedKey key;

    public Transformation(
        final @NotNull Set<Tool> enchantable,
        final int maxLevel,
        final int cooldown,
        final double probability,
        final float power
    ) {
        super(enchantable, maxLevel, cooldown, probability, power);
        this.key = new NamespacedKey(ZenchantmentsPlugin.getInstance(), KEY);
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getName() {
        return NAME;
    }

    @Override
    @NotNull
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    @NotNull
    public Set<Class<? extends Zenchantment>> getConflicting() {
        return CONFLICTING;
    }

    @Override
    @NotNull
    public Hand getHandUse() {
        return HAND_USE;
    }

    @Override
    public boolean onEntityHit(final @NotNull EntityDamageByEntityEvent event, final int level, final boolean usedHand) {
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return false;
        }

        if (event.getEntity() instanceof Tameable) {
            final Tameable tameable = (Tameable) event.getEntity();
            if (tameable.isTamed()) {
                return false;
            }
        }

        if (!(event.getEntity() instanceof LivingEntity)) {
            return true;
        }

        final LivingEntity entity = (LivingEntity) event.getEntity();
        if (hasValuableItems(entity)) {
            return true;
        }

        if (!ZenchantmentsPlugin.getInstance().getCompatibilityAdapter().attackEntity(entity, (Player) event.getDamager(), 0)) {
            return true;
        }

        if (!(ThreadLocalRandom.current().nextInt(100) < (level * this.getPower() * 8))) {
            return true;
        }

        final LivingEntity newEntity = transformationCycle(entity, ThreadLocalRandom.current());

        if (newEntity == null) {
            return true;
        }

        if (event.getDamage() > entity.getHealth()) {
            event.setCancelled(true);
        }

        Utilities.displayParticle(
            Utilities.getCenter(event.getEntity().getLocation()),
            Particle.HEART,
            70,
            0.1f,
            0.5f,
            2,
            0.5f
        );

        newEntity.setHealth(
            Math.max(
                1,
                Math.min(
                    entity.getHealth(),
                    requireNonNull(newEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue()
                )
            )
        );

        event.getEntity().remove();

        return true;
    }

    private static boolean hasValuableItems(final @NotNull LivingEntity entity) {
        if (entity.getEquipment() == null) {
            return false;
        }

        for (final ItemStack stack : entity.getEquipment().getArmorContents()) {
            if (stack.hasItemMeta() && requireNonNull(stack.getItemMeta()).hasEnchants()) {
                return true;
            }

            switch (stack.getType()) {
                case AIR:
                    continue;
                case GOLDEN_SWORD:
                    if (entity.getType() != EntityType.ZOMBIFIED_PIGLIN) {
                        return true;
                    }
                    break;
                case BOW:
                    if (entity.getType() != EntityType.SKELETON) {
                        return true;
                    }
                    break;
                case STONE_SWORD:
                    if (entity.getType() != EntityType.WITHER_SKELETON) {
                        return true;
                    }
                    break;
                default:
                    return true;
            }
        }
        return false;
    }

    @Nullable
    private static LivingEntity transformationCycle(final @NotNull LivingEntity entity, final @NotNull Random random) {
        final int newTypeIndex = ENTITY_TYPES_FROM.indexOf(entity.getType());

        if (newTypeIndex == -1) {
            return null;
        }

        final EntityType newType = ENTITY_TYPES_TO.get(newTypeIndex);
        final LivingEntity newEntity = (LivingEntity) entity.getWorld().spawnEntity(entity.getLocation(), newType);

        switch (newType) {
            case HORSE:
                final Horse horse = (Horse) newEntity;
                horse.setColor(Horse.Color.values()[random.nextInt(Horse.Color.values().length)]);
                horse.setStyle(Horse.Style.values()[random.nextInt(Horse.Style.values().length)]);
                break;
            case RABBIT:
                final Rabbit oldRabbit = (Rabbit) entity;
                final Rabbit newRabbit = (Rabbit) newEntity;
                if (oldRabbit.getRabbitType() == Rabbit.Type.THE_KILLER_BUNNY) {
                    newRabbit.setRabbitType(Rabbit.Type.values()[random.nextInt(Rabbit.Type.values().length - 1)]);
                } else {
                    newRabbit.setRabbitType(Rabbit.Type.THE_KILLER_BUNNY);
                }
                break;
            case VILLAGER:
                final Villager villager = (Villager) newEntity;
                villager.setProfession(Villager.Profession.values()[random.nextInt(Villager.Profession.values().length)]);
                villager.setVillagerType(Villager.Type.values()[random.nextInt(Villager.Type.values().length)]);
                break;
            case LLAMA:
                final Llama llama = (Llama) newEntity;
                llama.setColor(Llama.Color.values()[random.nextInt(Llama.Color.values().length)]);
                break;
            case TROPICAL_FISH:
                final TropicalFish tropicalFish = (TropicalFish) newEntity;
                tropicalFish.setBodyColor(DyeColor.values()[random.nextInt(DyeColor.values().length)]);
                tropicalFish.setPatternColor(DyeColor.values()[random.nextInt(DyeColor.values().length)]);
                tropicalFish.setPattern(TropicalFish.Pattern.values()[random.nextInt(TropicalFish.Pattern.values().length)]);
                break;
            case PARROT:
                final Parrot parrot = (Parrot) newEntity;
                parrot.setVariant(Parrot.Variant.values()[random.nextInt(Parrot.Variant.values().length)]);
                break;
            case CAT:
                final Cat cat = (Cat) newEntity;
                cat.setCatType(Cat.Type.values()[random.nextInt(Cat.Type.values().length)]);
                break;
            case SHEEP:
                final Sheep sheep = (Sheep) newEntity;
                sheep.setColor(DyeColor.values()[random.nextInt(DyeColor.values().length)]);
                break;
            case CREEPER:
                final Creeper oldCreeper = (Creeper) entity;
                final Creeper newCreeper = (Creeper) newEntity;
                newCreeper.setPowered(!oldCreeper.isPowered());
                break;
            case MUSHROOM_COW:
                final MushroomCow mooshroom = (MushroomCow) newEntity;
                mooshroom.setVariant(MushroomCow.Variant.values()[random.nextInt(MushroomCow.Variant.values().length)]);
                break;
            case FOX:
                final Fox fox = (Fox) newEntity;
                fox.setFoxType(Fox.Type.values()[random.nextInt(Fox.Type.values().length)]);
                break;
            case ILLUSIONER:
                final Panda panda = (Panda) newEntity;
                panda.setHiddenGene(Panda.Gene.values()[random.nextInt(Panda.Gene.values().length)]);
                panda.setMainGene(Panda.Gene.values()[random.nextInt(Panda.Gene.values().length)]);
                break;
        }

        newEntity.setCustomName(entity.getCustomName());
        newEntity.setCustomNameVisible(entity.isCustomNameVisible());

        return entity;
    }
}
