package zedly.zenchantments.compatibility;

import java.util.*;

public class EnumStorage<T> {
	private static Random rnd = new Random();
	protected        Set<T> enums;
	protected        T[]    enumArray;

	@SafeVarargs
	public EnumStorage(T[] t, EnumStorage<T>... enums) {
		this(t);
		for (EnumStorage<T> anEnum : enums) {
			this.enums.addAll(anEnum.enums);
		}
		enumArray = (T[]) this.enums.toArray();
	}

	public EnumStorage(T[] t) {
		this.enums = new LinkedHashSet<>(Arrays.asList(t));
		enumArray = (T[]) this.enums.toArray();
	}

	public EnumStorage() {
		this.enums = new LinkedHashSet<>();
		enumArray = (T[]) this.enums.toArray();
	}

	public T getNext(T t) {
		int index = indexOf(t);
		return index == -1 ? null : enumArray[(index + 1) % enumArray.length];
	}

	public boolean contains(T t) {
		return enums.contains(t);
	}

	public boolean contains(EnumStorage<T> enumStorage) {
		return Collections.disjoint(enumStorage.enums, enums);
	}

	public List getEnumList() {
		return new ArrayList(enums);
	}

	public Set getEnumSet() {
		return Collections.unmodifiableSet(enums);
	}

	public int indexOf(T t) {
		for (int i = 0; i < enumArray.length; i++) {
			if (enumArray[i].equals(t)) {
				return i;
			}
		}
		return -1;
	}

	public T get(int i) {
		return i >= 0 && i < enumArray.length ?  enumArray[i] : null;
	}

	public T getRandom() {
		return enumArray[rnd.nextInt(enumArray.length)];
	}
}
