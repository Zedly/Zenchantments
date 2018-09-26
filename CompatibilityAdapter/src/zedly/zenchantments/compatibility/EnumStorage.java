package zedly.zenchantments.compatibility;

import java.util.*;

public class EnumStorage<T> {
	private static Random rnd = new Random();
	private        Set<T> enums;
	private        T[]    enumArray;

	@SafeVarargs
	public EnumStorage(T[] t, EnumStorage<T>... enums) {
		this(t);
		for (EnumStorage<T> anEnum : enums) {
			this.enums.addAll(anEnum.enums);
		}
		enumArray = (T[]) this.enums.toArray();
	}

	public EnumStorage(T[] t) {
		this.enums = new HashSet<>(Arrays.asList(t));
		enumArray = (T[]) this.enums.toArray();
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

	public T getRandom() {
		return enumArray[rnd.nextInt(enumArray.length)];
	};
}
