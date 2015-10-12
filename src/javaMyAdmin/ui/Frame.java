package javaMyAdmin.ui;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javaMyAdmin.db.DBManager;
import javaMyAdmin.ui.util.Config;
import javaMyAdmin.ui.util.Lang;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Frame extends Application {

	public static final Config CONFIG = new Config();
	private static final File configFile = new File("config.ini");
	private static DBManager dbManager;
	private static Frame instance;

	private PaneToolbar toolbar;
	private PaneTableList tableList;
	private PaneTableContent tableContent;

	public Frame() {
		instance = this;
	}

	public static Frame getInstance() {
		return instance;
	}

	public static DBManager getDbManager() {
		return dbManager;
	}

	public static void setDbManager(DBManager dbManager) {
		Frame.dbManager = dbManager;
	}

	@Override
	public void start(Stage stage) throws Exception {
		/* Config laden */
		try {
			CONFIG.load(new FileReader(configFile));
		} catch (IOException e) {
			System.err.println("Couln't load config.");
			e.printStackTrace();
		}

		/* Login Dialog starten und auf Usereingaben warten */
		new LoginDialog();

		/* Root-Layout initialisieren */
		BorderPane pane = new BorderPane();
		SplitPane split = new SplitPane();
		split.getItems().addAll(tableList = new PaneTableList(), tableContent = new PaneTableContent());
		split.setDividerPosition(0, 0.2);

		pane.setTop(toolbar = new PaneToolbar());
		pane.setCenter(split);

		BorderPane.setMargin(toolbar, new Insets(0, 0, 10, 0));

		stage.setTitle(Lang.getString("frame.title", "javaMyAdmin"));

		/* Fenstergröße bestimmen und Frame Content zuweisen */
		stage.setScene(new Scene(pane, 800, 600));

		/* Icons setzen */
		stage.getIcons().add(new Image(Frame.class.getResource("/res/mario.png").toExternalForm()));

		/* Fenster anzeigen */
		stage.show();
	}

	@Override
	public void stop() throws Exception {
		/* Config speichern */
		if (!configFile.exists()) {
			configFile.createNewFile();
		}

		CONFIG.store(new FileWriter(configFile), "");

		System.exit(0);
	}

	public PaneToolbar getToolbar() {
		return toolbar;
	}

	public PaneTableList getTableList() {
		return tableList;
	}

	public PaneTableContent getTableValues() {
		return tableContent;
	}

}
