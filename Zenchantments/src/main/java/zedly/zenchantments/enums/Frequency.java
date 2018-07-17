package zedly.zenchantments.enums;

import zedly.zenchantments.TaskRunner;
import zedly.zenchantments.annotations.EffectTask;

/**
 * Frequencies for {@link EffectTask}
 *
 * @author rfrowe
 */
public enum Frequency {
	HIGH(1), MEDIUM(5);

	public final int period;

	/**
	 * Constructs a Frequency object which is run every {@code i} seconds.
	 *
	 * @param period Period of execution for annotation method, in seconds.
	 *
	 * @see TaskRunner
	 */
	Frequency(int period) {
		this.period = period;
	}
}
