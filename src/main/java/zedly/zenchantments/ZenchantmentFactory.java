package zedly.zenchantments;

import org.jetbrains.annotations.NotNull;
import zedly.zenchantments.Zenchantment.Constructor;
import zedly.zenchantments.enchantments.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ZenchantmentFactory {
    private static final Map<Class<? extends Zenchantment>, Constructor<? extends Zenchantment>> CONSTRUCTOR_MAP
        = new HashMap<>();

    private final ZenchantmentsPlugin plugin;

    static {
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