package javaMyAdmin.util;

public enum Index {

	NONE("---"),
	PRIMARY,
	UNIQUE,
	INDEX,
	FULLTEXT;

	private final String name;

	private Index() {
		this(null);
	}

	private Index(String name) {
		this.name = name == null ? name() : name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public static String[] nameValues() {
		String[] values = new String[values().length];
		for (int i = 0; i < values.length; i++) {
			values[i] = values()[i].getName();
		}
		return values;
	}

	public static Index valueOfName(String name) {
		for (Index index : values()) {
			if (index.getName().equals(name)) {
				return index;
			}
		}

		return null;
	}

}
