package zedly.zenchantments.task;

/**
 * Frequencies for {@link EffectTask}
 */
public enum Frequency {
    HIGH(1), MEDIUM_HIGH(5), /*MEDIUM_LOW(10), LOW(20),*/ SLOW(200);

    private final int period;

    /**
     * Constructs a Frequency object which is run every {@code i} seconds.
     *
     * @param period Period of execution for annotation method, in seconds.
     * @see TaskRunner
     */
    Frequency(int period) {
        this.period = period;
    }

    public int getPeriod() {
        return this.period;
    }
}