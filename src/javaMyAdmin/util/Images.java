package javaMyAdmin.util;

import javafx.scene.image.Image;

/**
 * Sammlung alle Images
 * 
 * @author Hannes
 */
public class Images {
	
	/* Database */
	public static final Image DATABASE = getResource("database");
	public static final Image DATABASE_ADD = getResource("database_add");
	public static final Image DATABASE_REMOVE = getResource("database_remove");
	public static final Image DATABASE_EDIT = getResource("database_edit");
	
	/* Table */
	public static final Image TABLE = getResource("table");
	public static final Image TABLE_ADD = getResource("table_add");
	public static final Image TABLE_REMOVE = getResource("table_remove");
	public static final Image TABLE_EDIT = getResource("table_edit");
	
	/* General */
	public static final Image[] ICONS = new Image[] { /* 16x16 */ DATABASE, /* 32x32 */ getResource("icon") };
	public static final Image CONNECTION = getResource("connection");
	public static final Image LOGO = getResource("logo");
	
	/**
	 * Lädt eine Resourcedatei als Image
	 * 
	 * @param resFileName
	 * @return
	 */
	public static final Image getResource(String resFileName) {
		return getResource(resFileName, "png");
	}
	
	/**
	 * Lädt eine Resourcedatei als Image
	 * 
	 * @param resFileName
	 * @param fileFormat
	 * @return
	 */
	public static final Image getResource(String resFileName, String fileFormat) {
		try {
			return new Image(Images.class.getResourceAsStream("/res/img/" + resFileName + "." + fileFormat.toLowerCase()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
