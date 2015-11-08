package javaMyAdmin.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
	
	private static final File configFile = new File("config.ini");
	private static final long serialVersionUID = 1L;
	private static final Config instance = new Config();
	
	public static Config getInstance() {
		return instance;
	}
	
	public Config() {
		try {
			if (configFile.exists()) {
				load(new FileReader(configFile));
			}
		} catch (IOException e) {
			FXUtil.showErrorLog(new IOException("Couldn't load config", e));
		}
	}
	
	public void save() {
		try {
			if (!configFile.exists()) {
				configFile.createNewFile();
			}
			
			store(new FileWriter(configFile), "");
		} catch (IOException e) {
			FXUtil.showErrorLog(e);
		}
	}
	
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
