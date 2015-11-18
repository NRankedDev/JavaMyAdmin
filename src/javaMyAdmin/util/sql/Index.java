package javaMyAdmin.util.sql;

/**
 * Sammlung aller Indices
 * 
 * @author Nicolas
 */
public enum Index {
	
	NONE("---"), PRIMARY("PRIMARY", "PRI"), UNIQUE("UNIQUE"), INDEX("INDEX"), FULLTEXT("FULLTEXT");
	
	private final String[] names;
	
	private Index(String... names) {
		this.names = names;
	}
	
	public String[] getNames() {
		return names;
	}
	
	@Override
	public String toString() {
		return name();
	}
	
	public static String[] nameValues() {
		String[] values = new String[values().length];
		for (int i = 0; i < values.length; i++) {
			values[i] = values()[i].name();
		}
		return values;
	}
	
	public static Index valueOfName(String name) {
		for (Index index : values()) {
			for (String s : index.names) {
				if (s.equalsIgnoreCase(name)) {
					return index;
				}
			}
		}
		
		return Index.NONE;
	}
	
}
