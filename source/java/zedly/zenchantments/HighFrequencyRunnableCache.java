package zedly.zenchantments;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HighFrequencyRunnableCache implements Runnable {
    private final ZenchantmentsPlugin          plugin;
    private final ZenchantmentsBiConsumer      cacheFeeder;
    private final int                          refreshPeriodTicks;
    private final ArrayList<Player>            players = new ArrayList<>();
    private       ArrayList<Supplier<Boolean>> cache0  = new ArrayList<>();
    private       ArrayList<Supplier<Boolean>> cache1  = new ArrayList<>();

    private int feedFraction = 0;

    public HighFrequencyRunnableCache(
        final @NotNull ZenchantmentsPlugin plugin,
        final @NotNull ZenchantmentsBiConsumer cacheFeeder,
        final int refreshPeriodTicks
    ) {
        this.plugin = plugin;
        this.cacheFeeder = cacheFeeder;
        this.refreshPeriodTicks = refreshPeriodTicks;
    }

    @Override
    public void run() {
        this.cache0.removeIf(booleanSupplier -> !booleanSupplier.get());

        if (this.feedFraction == 0) {
            this.players.clear();
            this.players.addAll(this.plugin.getServer().getOnlinePlayers());

            final ArrayList<Supplier<Boolean>> cache2 = this.cache0;

            this.cache0 = cache1;
            this.cache1 = cache2;
            this.cache1.clear();
        }

        final int listStart = this.players.size() * this.feedFraction / this.refreshPeriodTicks;
        final int listEnd = this.players.size() * (this.feedFraction + 1) / this.refreshPeriodTicks;

        for (int i = listStart; i < listEnd; i++) {
            final Player player = this.players.get(i);

            if (!player.isOnline()) {
                continue;
            }

            this.cacheFeeder.accept(this.plugin, player, this.cache1::add);
        }

        this.feedFraction = (this.feedFraction + 1) % this.refreshPeriodTicks;
    }

    @FunctionalInterface
    public interface ZenchantmentsBiConsumer {
        void accept(@NotNull ZenchantmentsPlugin plugin, @NotNull Player player, @NotNull Consumer<Supplier<Boolean>> consumer);
    }
}
