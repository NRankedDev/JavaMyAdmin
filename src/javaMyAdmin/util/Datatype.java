package javaMyAdmin.util;

import java.util.ArrayList;

public enum Datatype {
	
	/* Text Types */
	VARCHAR(Kind.TEXT, true), TEXT(Kind.TEXT), BLOB(Kind.TEXT),
	
	/* Numeric types */
	BYTE(Kind.NUMERIC), TINYINT(Kind.NUMERIC), SMALLINT(Kind.NUMERIC), INT(Kind.NUMERIC), BIGINT(Kind.NUMERIC), FLOAT(Kind.NUMERIC), REAL(Kind.NUMERIC),
	
	/* Other */
	DATE(Kind.OTHER), TIMESTAMP(Kind.OTHER), TIME(Kind.OTHER);
	
	private final Kind kind;
	private final String name;
	private final boolean sizeNeeded;
	
	private Datatype(Kind kind) {
		this(kind, null, false);
	}
	
	private Datatype(Kind kind, boolean sizeNeeded) {
		this(kind, null, sizeNeeded);
	}
	
	private Datatype(Kind kind, String name, boolean sizeNeeded) {
		this.kind = kind;
		this.name = name == null ? name() : name;
		this.sizeNeeded = sizeNeeded;
	}
	
	public Kind getKind() {
		return kind;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isSizeNeeded() {
		return sizeNeeded;
	}
	
	public static Datatype[] values(Kind kind) {
		ArrayList<Datatype> arrayList = new ArrayList<Datatype>();
		for (Datatype datatype : values()) {
			if (datatype.getKind() == kind) {
				arrayList.add(datatype);
			}
		}
		
		return arrayList.toArray(new Datatype[arrayList.size()]);
	}
	
	public static String[] nameValues() {
		String[] values = new String[values().length];
		for (int i = 0; i < values.length; i++) {
			values[i] = values()[i].getName();
		}
		return values;
	}
	
	public static Datatype valueOfName(String name) {
		for (Datatype datatype : values()) {
			if (datatype.getName().equals(name)) {
				return datatype;
			}
		}
		
		return Datatype.VARCHAR;
	}
	
	public enum Kind {
		TEXT, NUMERIC, OTHER;
	}
	
}
