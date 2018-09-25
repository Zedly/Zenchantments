package zedly.zenchantments.compatibility;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class EnumStorage<T> {

	private Set<T> enums;

	public EnumStorage(T[] t, EnumStorage<T>... enums) {
		this.enums = new HashSet<T>(Arrays.asList(t));
		for (int i = 0; i < enums.length; ++i) {
			this.enums.addAll(enums[i].enums);
		}
	}

	public EnumStorage(T[] t) {
		this.enums = new HashSet<T>(Arrays.asList(t));
	}

	public EnumStorage(EnumStorage<T>... enums) {
		for (int i = 0; i < enums.length; ++i) {
			this.enums.addAll(enums[i].enums);
		}
	}

	public boolean contains(T t) {
		return enums.contains(t);
	}

	public boolean contains(EnumStorage<T> enumStorage) {
		return Collections.disjoint(enumStorage.enums, enums);
	}

	public Set getEnums() {
		return Collections.unmodifiableSet(enums);
	}

}
