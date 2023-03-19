package zedly.zenchantments;

public @interface AZenchantment {
    Slots runInSlots();
    Class<? extends Zenchantment>[] conflicting();
}
