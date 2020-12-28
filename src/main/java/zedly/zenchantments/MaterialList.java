package zedly.zenchantments;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractList;
import java.util.concurrent.ThreadLocalRandom;

public final class MaterialList extends AbstractList<Material> {
    private final Material[] values;

    private MaterialList(final @NotNull Material... values) {
        this.values = values;
    }

    @Override
    public Material get(int index) {
        return this.values[index];
    }

    @Override
    public int size() {
        return this.values.length;
    }

    @Override
    public int indexOf(final @NotNull Object object) {
        if (!(object instanceof Material)) {
            throw new IllegalArgumentException("Object must be a Material.");
        }

        final Material search = (Material) object;

        for (int i = 0; i < this.values.length; i++) {
            if (this.values[i] == search) {
                return i;
            }
        }

        return -1;
    }

    @NotNull
    public Material getNext(final @NotNull Material material) {
        final int index = this.indexOf(material);

        if (index == -1) {
            throw new IllegalArgumentException("Material is not contained in the MaterialList.");
        }

        return this.values[(index + 1) % this.values.length];
    }

    @NotNull
    public Material getRandom() {
        return this.values[ThreadLocalRandom.current().nextInt(this.values.length)];
    }
}
