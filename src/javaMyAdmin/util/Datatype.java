package javaMyAdmin.util;

import java.sql.Date;
import java.util.ArrayList;

public class Datatype<T> {
	
	private static final ArrayList<Datatype<?>> datatypes = new ArrayList<Datatype<?>>();
	
	/* Error */
	public static final Datatype<String> ERROR = new Datatype<String>(Kind.IGNORE, "ERROR");
	
	/* Text Types */
	public static final Datatype<String> VARCHAR = new Datatype<String>(Kind.TEXT, "VARCHAR", true);
	public static final Datatype<String> TEXT = new Datatype<String>(Kind.TEXT, "TEXT");
	public static final Datatype<String> BLOB = new Datatype<String>(Kind.TEXT, "BLOB");
	
	/* Numeric types */
	public static final Datatype<Byte> BYTE = new Datatype<Byte>(Kind.NUMERIC, "BYTE");
	public static final Datatype<Byte> TINYINT = new Datatype<Byte>(Kind.NUMERIC, "TINYINT");
	public static final Datatype<Short> SMALLINT = new Datatype<Short>(Kind.NUMERIC, "SMALLINT");
	public static final Datatype<Integer> INTEGER = new Datatype<Integer>(Kind.NUMERIC, "INT");
	public static final Datatype<Long> BIGINT = new Datatype<Long>(Kind.NUMERIC, "BIGINT");
	public static final Datatype<Float> FLOAT = new Datatype<Float>(Kind.NUMERIC, "FLOAT");
	public static final Datatype<Double> DOUBLE = new Datatype<Double>(Kind.NUMERIC, "DOUBLE");
	
	/* Other */
	public static final Datatype<Date> DATE = new Datatype<Date>(Kind.OTHER, "DATE");
	public static final Datatype<String> TIMESTAMP = new Datatype<String>(Kind.OTHER, "TIMESTAMP");
	public static final Datatype<String> TIME = new Datatype<String>(Kind.OTHER, "TIME");
	
	private final Kind kind;
	private final String name;
	private final boolean sizeNeeded;
	
	public Datatype(Kind kind, String name) {
		this(kind, name, false);
	}
	
	public Datatype(Kind kind, String name, boolean sizeNeeded) {
		this.kind = kind;
		this.name = name;
		this.sizeNeeded = sizeNeeded;
		
		datatypes.add(this);
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
	
	public static ArrayList<Datatype<?>> values() {
		return datatypes;
	}
	
	public static ArrayList<Datatype<?>> values(Kind kind) {
		ArrayList<Datatype<?>> arrayList = new ArrayList<Datatype<?>>();
		for (Datatype<?> datatype : values()) {
			if (datatype.getKind() == kind) {
				arrayList.add(datatype);
			}
		}
		
		return arrayList;
	}
	
	public static ArrayList<String> nameValues() {
		ArrayList<Datatype<?>> types = values();
		ArrayList<String> values = new ArrayList<String>();
		for (int i = 0; i < types.size(); i++) {
			if (types.get(i).getKind() != Kind.IGNORE) {
				values.add(types.get(i).getName());
			}
		}
		return values;
	}
	
	public static Datatype<?> valueOfName(String name) {
		for (Datatype<?> datatype : values()) {
			if (datatype.getName().equalsIgnoreCase(name)) {
				return datatype;
			}
		}
		
		return Datatype.ERROR;
	}
	
	public enum Kind {
		IGNORE, TEXT, NUMERIC, OTHER;
	}
	
}
