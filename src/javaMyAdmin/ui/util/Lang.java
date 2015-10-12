package javaMyAdmin.ui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

/**
 * Klasse, die beim Start alle unterst�tzen Sprachen l�dt. Wird f�r das
 * �bersetzen von Strings genutzt.
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

		if (availableLanguages().contains(Locale.getDefault())) {
			changeLocale(Locale.getDefault());
		} else {
			locale = Locale.US;
			System.out.println(Locale.getDefault());
		}
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
	 * @return Alle Sprachen ({@link Locale}), die vom Programm unterst�tzt
	 *         werden
	 */
	public static ArrayList<Locale> availableLanguages() {
		return locales;
	}

	/**
	 * �ndert die aktuell genutzte Sprache
	 * 
	 * @param locale
	 */
	public static void changeLocale(Locale locale) {
		if (locale != null && !locale.getLanguage().equals("debug")) {
			if (!locale.equals(Locale.US)) {
				strings.clear();
				try (FileInputStream fis = new FileInputStream(new File(langDirectory, locale.toString() + ".lang"))) {
					strings.load(fis);
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
	 * �bersetzt einen String. Der angegebene Key f�hrt zu einem �bersetzten
	 * String.
	 * 
	 * @param key
	 *            Der Key f�r den �bersetzten String
	 * @param defaultValue
	 *            Wenn der Key nicht �bersetzte ist, wird dieser Wert zur�ck
	 *            gegeben
	 * @return Der �bersetzte String. Wenn {@link #getLocale()}
	 *         <code>null</code> ist, wird der Key zur�ck gegeben.
	 */
	public static String getString(String key, String defaultValue) {
		if (locale == null) {
			return key;
		} else if (locale.equals(Locale.US)) {
			return defaultValue;
		} else {
			String value = strings.getProperty(key, defaultValue);

			return value.trim().isEmpty() ? key : value;
		}
	}

}
