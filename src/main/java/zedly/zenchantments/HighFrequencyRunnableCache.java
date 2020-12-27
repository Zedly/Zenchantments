package zedly.zenchantments;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author wicden
 * <p>
 * Helper class for efficient execution of fast continuous item effects.
 * This class scans the list of online players for effects to be performed and keeps them
 * in a cache where the executions are repeated without re-scanning player inventory.
 * Inventory scans are also staggered across the configured time period, smoothing out lag spikes.
 */
public class HighFrequencyRunnableCache implements Runnable {
    private final int                                             refreshPeriodTicks;
    private final BiConsumer<Player, Consumer<Supplier<Boolean>>> cacheFeeder;
    private final ArrayList<Player>                               players = new ArrayList<>();
    private       ArrayList<Supplier<Boolean>>                    cache0  = new ArrayList<>();
    private       ArrayList<Supplier<Boolean>>                    cache1  = new ArrayList<>();

    private int feedFraction = 0;

    /**
     * Create a cache for continuous player-based effects
     *
     * @param cacheFeeder        a function that determines the set of tasks to be executed for each player and inserts them into the provided Collection
     * @param refreshPeriodTicks the number of ticks over which spread out the player sweep
     */
    public HighFrequencyRunnableCache(
        BiConsumer<Player, Consumer<Supplier<Boolean>>> cacheFeeder,
        int refreshPeriodTicks
    ) {
        this.cacheFeeder = cacheFeeder;
        this.refreshPeriodTicks = refreshPeriodTicks;
    }

    @Override
    public void run() {
        this.cache0.removeIf(booleanSupplier -> !booleanSupplier.get());

        if (this.feedFraction == 0) {
            this.players.clear();
            this.players.addAll(Bukkit.getOnlinePlayers());

            ArrayList<Supplier<Boolean>> cache2 = this.cache0;

            this.cache0 = cache1;
            this.cache1 = cache2;
            this.cache1.clear();
        }

        int listStart = this.players.size() * this.feedFraction / this.refreshPeriodTicks;
        int listEnd = this.players.size() * (this.feedFraction + 1) / this.refreshPeriodTicks;


        for (int i = listStart; i < listEnd; i++) {
            Player player = this.players.get(i);

            if (!player.isOnline()) {
                continue;
            }

            this.cacheFeeder.accept(player, this.cache1::add);
        }

        this.feedFraction = (this.feedFraction + 1) % this.refreshPeriodTicks;
    }
}
