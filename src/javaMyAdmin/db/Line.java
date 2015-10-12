package javaMyAdmin.db;

import java.util.ArrayList;

public class Line {
	private ArrayList<String> values;

	public Line() {
		values = new ArrayList();
	}

	public void AddValue(String value) {
		values.add(value);
	}

	public ArrayList<String> getValues() {
		return values;
	}

	public String getValues(int i) {
		return values.get(i);
	}

}
