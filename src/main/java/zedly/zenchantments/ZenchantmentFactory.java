package zedly.zenchantments;

import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Zenchantment.Constructor;
import zedly.zenchantments.enchantments.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ZenchantmentFactory {
    // Increase this when adding a new Zenchantment!
    private static final int ZENCHANTMENT_COUNT = 73;

    private static final Map<Class<? extends Zenchantment>, Constructor<? extends Zenchantment>> CONSTRUCTOR_MAP;

    private final ZenchantmentsPlugin plugin;

    static {
        CONSTRUCTOR_MAP = new HashMap<>(ZENCHANTMENT_COUNT);
        ZenchantmentFactory.addConstructor(Anthropomorphism.class, Anthropomorphism::new);
        ZenchantmentFactory.addConstructor(Apocalypse.class, Apocalypse::new);
        ZenchantmentFactory.addConstructor(Arborist.class, Arborist::new);
        ZenchantmentFactory.addConstructor(Bind.class, Bind::new);
        ZenchantmentFactory.addConstructor(BlazesCurse.class, BlazesCurse::new);
        ZenchantmentFactory.addConstructor(Blizzard.class, Blizzard::new);
        ZenchantmentFactory.addConstructor(Bounce.class, Bounce::new);
        ZenchantmentFactory.addConstructor(Burst.class, Burst::new);
        ZenchantmentFactory.addConstructor(Combustion.class, Combustion::new);
        ZenchantmentFactory.addConstructor(Conversion.class, Conversion::new);
        ZenchantmentFactory.addConstructor(Decapitation.class, Decapitation::new);
        ZenchantmentFactory.addConstructor(Ethereal.class, Ethereal::new);
        ZenchantmentFactory.addConstructor(Extraction.class, Extraction::new);
        ZenchantmentFactory.addConstructor(Fire.class, Fire::new);
        ZenchantmentFactory.addConstructor(Firestorm.class, Firestorm::new);
        ZenchantmentFactory.addConstructor(Fireworks.class, Fireworks::new);
        ZenchantmentFactory.addConstructor(Force.class, Force::new);
        ZenchantmentFactory.addConstructor(FrozenStep.class, FrozenStep::new);
        ZenchantmentFactory.addConstructor(Fuse.class, Fuse::new);
        ZenchantmentFactory.addConstructor(Germination.class, Germination::new);
        ZenchantmentFactory.addConstructor(Glide.class, Glide::new);
        ZenchantmentFactory.addConstructor(Gluttony.class, Gluttony::new);
        ZenchantmentFactory.addConstructor(GoldRush.class, GoldRush::new);
        ZenchantmentFactory.addConstructor(Grab.class, Grab::new);
        ZenchantmentFactory.addConstructor(GreenThumb.class, GreenThumb::new);
        ZenchantmentFactory.addConstructor(Gust.class, Gust::new);
        ZenchantmentFactory.addConstructor(Harvest.class, Harvest::new);
        ZenchantmentFactory.addConstructor(Haste.class, Haste::new);
        ZenchantmentFactory.addConstructor(IceAspect.class, IceAspect::new);
        ZenchantmentFactory.addConstructor(Jump.class, Jump::new);
        ZenchantmentFactory.addConstructor(Laser.class, Laser::new);
        ZenchantmentFactory.addConstructor(Level.class, Level::new);
        ZenchantmentFactory.addConstructor(LongCast.class, LongCast::new);
        ZenchantmentFactory.addConstructor(Lumber.class, Lumber::new);
        ZenchantmentFactory.addConstructor(Magnetism.class, Magnetism::new);
        ZenchantmentFactory.addConstructor(Meador.class, Meador::new);
        ZenchantmentFactory.addConstructor(Missile.class, Missile::new);
        ZenchantmentFactory.addConstructor(Mow.class, Mow::new);
        ZenchantmentFactory.addConstructor(MysteryFish.class, MysteryFish::new);
        ZenchantmentFactory.addConstructor(NetherStep.class, NetherStep::new);
        ZenchantmentFactory.addConstructor(NightVision.class, NightVision::new);
        ZenchantmentFactory.addConstructor(Persephone.class, Persephone::new);
        ZenchantmentFactory.addConstructor(Pierce.class, Pierce::new);
        ZenchantmentFactory.addConstructor(Plough.class, Plough::new);
        ZenchantmentFactory.addConstructor(Potion.class, Potion::new);
        ZenchantmentFactory.addConstructor(PotionResistance.class, PotionResistance::new);
        ZenchantmentFactory.addConstructor(QuickShot.class, QuickShot::new);
        ZenchantmentFactory.addConstructor(Rainbow.class, Rainbow::new);
        ZenchantmentFactory.addConstructor(RainbowSlam.class, RainbowSlam::new);
        ZenchantmentFactory.addConstructor(Reaper.class, Reaper::new);
        ZenchantmentFactory.addConstructor(Reveal.class, Reveal::new);
        ZenchantmentFactory.addConstructor(Saturation.class, Saturation::new);
        ZenchantmentFactory.addConstructor(ShortCast.class, ShortCast::new);
        ZenchantmentFactory.addConstructor(Shred.class, Shred::new);
        ZenchantmentFactory.addConstructor(Singularity.class, Singularity::new);
        ZenchantmentFactory.addConstructor(Siphon.class, Siphon::new);
        ZenchantmentFactory.addConstructor(SonicShock.class, SonicShock::new);
        ZenchantmentFactory.addConstructor(Spectral.class, Spectral::new);
        ZenchantmentFactory.addConstructor(Speed.class, Speed::new);
        ZenchantmentFactory.addConstructor(Spikes.class, Spikes::new);
        ZenchantmentFactory.addConstructor(Spread.class, Spread::new);
        ZenchantmentFactory.addConstructor(Stationary.class, Stationary::new);
        ZenchantmentFactory.addConstructor(Stock.class, Stock::new);
        ZenchantmentFactory.addConstructor(Stream.class, Stream::new);
        ZenchantmentFactory.addConstructor(Switch.class, Switch::new);
        ZenchantmentFactory.addConstructor(Terraformer.class, Terraformer::new);
        ZenchantmentFactory.addConstructor(Toxic.class, Toxic::new);
        ZenchantmentFactory.addConstructor(Tracer.class, Tracer::new);
        ZenchantmentFactory.addConstructor(Transformation.class, Transformation::new);
        ZenchantmentFactory.addConstructor(Unrepairable.class, Unrepairable::new);
        ZenchantmentFactory.addConstructor(Variety.class, Variety::new);
        ZenchantmentFactory.addConstructor(Vortex.class, Vortex::new);
        ZenchantmentFactory.addConstructor(Weight.class, Weight::new);
    }

    public ZenchantmentFactory(@NotNull ZenchantmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends Zenchantment> Constructor<T> getConstructor(@NotNull Class<T> zenchantmentClass) {
        return (Constructor<T>) CONSTRUCTOR_MAP.get(zenchantmentClass);
    }

    @NotNull
    public <T extends Zenchantment> T createZenchantment(
        @NotNull Class<T> zenchantmentClass,
        @NotNull Set<Tool> enchantable,
        int maxLevel,
        int cooldown,
        double probability,
        float power
    ) {
        return ZenchantmentFactory
            .getConstructor(zenchantmentClass)
            .construct(
                this.plugin,
                enchantable,
                maxLevel,
                cooldown,
                probability,
                power
            );
    }

    // This method allows the compiler to ensure that 'constructor' returns an instance of 'zenchantmentClass'.
    // CONSTRUCTOR_MAP.put() by itself does not ensure that the generic types are the same, so always use this method!
    private static <T extends Zenchantment> void addConstructor(
        @NotNull Class<T> zenchantmentClass,
        @NotNull Constructor<T> constructor
    ) {
        CONSTRUCTOR_MAP.put(zenchantmentClass, constructor);
    }
}