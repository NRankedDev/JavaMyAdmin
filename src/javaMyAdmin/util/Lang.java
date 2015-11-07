package javaMyAdmin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

/**
 * Klasse, die beim Start alle unterstuetzen Sprachen laedt. Wird fuer das
 * uebersetzen von Strings genutzt.
 * 
 * @see #getString(String, String)
 * 		
 * @author Nicolas
 */
public class Lang {
	
	private static final File langDirectory = new File("lang");
	private static final ArrayList<Locale> locales = new ArrayList<Locale>();
	private static final Properties strings = new Properties();
	private static Locale locale;
	
	static {
		loadLanguages();
		
		Locale locale = null;
		
		if (Config.getInstance() != null && Config.getInstance().containsKey("lang")) {
			try {
				String lang = Config.getInstance().getProperty("lang");
				if (lang.equals("debug")) {
					locale = new Locale(lang);
				} else {
					String[] split = lang.split("_");
					locale = new Locale(split[0], split[1]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (locale == null) {
			if (availableLanguages().contains(Locale.getDefault())) {
				locale = Locale.getDefault();
			} else {
				locale = Locale.US;
			}
		}
		
		changeLocale(locale);
	}
	
	private static void loadLanguages() {
		String[] files = new String[0];
		
		if (langDirectory.exists() && langDirectory.isDirectory()) {
			files = langDirectory.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					System.out.println(dir + " " + name);
					return name.contains("_") && name.endsWith(".lang");
				}
			});
		}
		
		locales.add(new Locale("debug"));
		locales.add(Locale.US);
		
		for (String lang : files) {
			locales.add(new Locale(lang.split("_")[0], lang.split("_")[1].split("\\.")[0]));
		}
	}
	
	/**
	 * @return Alle Sprachen ({@link Locale}), die vom Programm unterstuetzt
	 *         werden
	 */
	public static ArrayList<Locale> availableLanguages() {
		return locales;
	}
	
	/**
	 * Aendert die aktuell genutzte Sprache
	 * 
	 * @param locale
	 */
	public static void changeLocale(Locale locale) {
		if (locale != null && !locale.getLanguage().equals("debug")) {
			if (!locale.equals(Locale.US)) {
				strings.clear();
				try (InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(langDirectory, locale.toString() + ".lang")), "UTF-8")) {
					strings.load(isr);
				} catch (Exception e) {
					locale = Locale.US;
				}
			}
			
			System.out.println("Locale changed to:" + locale.toString());
		}
		
		Lang.locale = locale;
	}
	
	/**
	 * @return Aktuell verwendete Sprache
	 */
	public static Locale getLocale() {
		return locale;
	}
	
	/**
	 * Uebersetzt einen String. Der angegebene Key fuehrt zu einem uebersetzten
	 * String.
	 * 
	 * @param key
	 *            Der Key fuer den uebersetzten String
	 * @param defaultValue
	 *            Wenn der Key nicht uebersetzte ist, wird dieser Wert zurueck
	 *            gegeben
	 * @return Der uebersetzte String. Wenn {@link #getLocale()}
	 *         <code>null</code> ist, wird der Key zurueck gegeben.
	 */
	public static String getString(String key, String defaultValue) {
		if (locale == null || locale.getLanguage().equals("debug")) {
			return key;
		} else if (locale.equals(Locale.US)) {
			return defaultValue;
		} else {
			String value = strings.getProperty(key, defaultValue);
			
			return value.trim().isEmpty() ? key : value;
		}
	}
	
}
