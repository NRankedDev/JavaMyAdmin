package javaMyAdmin.ui.util;

import java.util.Properties;

/**
 * Erweiterung der Klasse {@link Properties}
 * 
 * @see #set(String, Object)
 * @see #getBoolean(String)
 * @see #getInt(String)
 * @see #getDouble(String)
 * @author Nicolas
 */
public class Config extends Properties {

	private static final long serialVersionUID = 1L;

	public void set(String key, Object value) {
		setProperty(key, value.toString());
	}

	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(getProperty(key));
	}

	public int getInt(String key) {
		try {
			return Integer.parseInt(key);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public double getDouble(String key) {
		try {
			return Double.parseDouble(key);
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}

}
