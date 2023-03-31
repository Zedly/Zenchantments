package zedly.zenchantments.arrows;

import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zedly.zenchantments.ZenchantmentPriority;
import zedly.zenchantments.ZenchantmentsPlugin;
import zedly.zenchantments.task.EffectTask;
import zedly.zenchantments.task.Frequency;

import java.util.*;

public class ZenchantedArrow {

    private static final LinkedList<ZenchantedArrow> ZENCHANTED_ARROWS = new LinkedList<>();
    public static final String ARROW_METADATA_NAME = "ze.enchanted_arrow";
    public static final String KILLED_BY_ARROW_METADATA_NAME = "ze.killed_by_ench_arrow";

    private static final Set<ZenchantedArrow> DIE_QUEUE = new HashSet<>();

    private final Projectile arrow;
    private final int level;
    private final double power;

    private int tick;

    public ZenchantedArrow(
        final @NotNull Projectile arrow,
        final int level,
        final double power
    ) {
        this.arrow = arrow;
        this.level = level;
        this.power = power;
    }

    public ZenchantedArrow(final @NotNull Projectile arrow, final int level) {
        this(arrow, level, 1);
    }

    public ZenchantedArrow(final @NotNull Projectile arrow) {
        this(arrow, 0);
    }

    public static void addZenchantedArrowToArrowEntity(
        final @NotNull Projectile arrow,
        final @NotNull ZenchantedArrow zenchantedArrow,
        final @NotNull Player player
    ) {
        addZenchantedArrowToEntity(arrow, ARROW_METADATA_NAME, zenchantedArrow);
        ZENCHANTED_ARROWS.add(zenchantedArrow);
        zenchantedArrow.onLaunch(player, null);
    }

    public static void addZenchantedArrowToEntity(
        final @NotNull Entity maybeKilledEntity,
        final @NotNull String metadataName,
        final @NotNull ZenchantedArrow zenchantedArrow
    ) {

        List<ZenchantedArrow> arrowMeta;
        if(maybeKilledEntity.hasMetadata(metadataName)) {
            MetadataValue m = maybeKilledEntity.getMetadata(metadataName).get(0);
            if(m.getOwningPlugin() != ZenchantmentsPlugin.getInstance()) {
                return;
            }
            arrowMeta = (List<ZenchantedArrow>) m.value();
        } else {
            arrowMeta = new LinkedList<>();
        }
        arrowMeta.add(zenchantedArrow);
        maybeKilledEntity.setMetadata(metadataName, new FixedMetadataValue(ZenchantmentsPlugin.getInstance(), arrowMeta));
    }

    @NotNull
    public final Projectile getArrow() {
        return this.arrow;
    }

    public final int getLevel() {
        return this.level;
    }

    public final double getPower() {
        return this.power;
    }

    public final int getTick() {
        return this.tick;
    }

    public ZenchantmentPriority getPriority() {
        return ZenchantmentPriority.NORMAL;
    }

    protected final void die(boolean removeArrow) {
        this.onDie();

        // Formally we should only remove the particular ZenchantedArrow that died here
        // However in practice they all begin and die at the same time, so we just wipe the metadata
        arrow.removeMetadata(ARROW_METADATA_NAME, ZenchantmentsPlugin.getInstance());

        if (removeArrow && arrow.getType() != EntityType.TRIDENT) {
            arrow.remove();
        }
    }

    private void tick() {
        this.tick++;
        this.onTick();
    }

    public void onLaunch(final @NotNull LivingEntity player, final @Nullable List<String> lore) { }

    protected void onTick() { }

    public void onImpact(ProjectileHitEvent event) {
        die(false);
    }

    public void onImpactEntity(final @NotNull ProjectileHitEvent event) {
        die(false);
    }

    public void onDamageEntity(final @NotNull EntityDamageByEntityEvent event) { }

    public void onKill(final @NotNull EntityDeathEvent event) { }



    protected void onDie() {
    }

    @EffectTask(Frequency.HIGH)
    public static void doTick() {
        Iterator<ZenchantedArrow> it = ZENCHANTED_ARROWS.iterator();
        while(it.hasNext()) {
            ZenchantedArrow arrow = it.next();
            if (arrow.getArrow().isDead() || arrow.getTick() > 600) {
                arrow.onDie();
                it.remove();
            } else {
                arrow.tick();
            }
        }
    }
}
