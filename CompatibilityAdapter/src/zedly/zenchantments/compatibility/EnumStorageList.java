package zedly.zenchantments.compatibility;

import java.util.*;

public class EnumStorageList<T> extends EnumStorage<T> {
	private static Random rnd = new Random();
	protected        List<T> enums;
	protected        T[]    enumArray;

	@SafeVarargs
	public EnumStorageList(T[] t, EnumStorage<T>... enums) {
		this(t);
		for (EnumStorage<T> anEnum : enums) {
			this.enums.addAll(anEnum.enums);
		}
		enumArray = (T[]) this.enums.toArray();
	}

	public EnumStorageList(T[] t) {
		this.enums = new ArrayList<>(Arrays.asList(t));
		enumArray = (T[]) this.enums.toArray();
	}

	public boolean contains(T t) {
		return enums.contains(t);
	}

	public boolean contains(EnumStorageList<T> enumStorage) {
		return Collections.disjoint(enumStorage.enums, enums);
	}

	public List getEnumList() {
		return Collections.unmodifiableList(enums);
	}

	public Set getEnumSet() { return new HashSet(enums); }

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
	};
}
