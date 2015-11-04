package javaMyAdmin.ui;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.SQLException;

import javaMyAdmin.db.DBManager;
import javaMyAdmin.ui.dialogs.DialogLogin;
import javaMyAdmin.util.Config;
import javaMyAdmin.util.Lang;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * 'Hauptklasse' der UI-Anwendung
 * 
 * @author Nicolas
 */
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

	/**
	 * Aktuelle Instanz der 'Hauptklasse'
	 * 
	 * @return
	 */
	public static Frame getInstance() {
		return instance;
	}

	/**
	 * Anbindung an die Datenbank
	 * 
	 * @return
	 */
	public static DBManager getDbManager() {
		return dbManager;
	}

	/**
	 * Setzt die Anbindung an die Datenbank
	 * 
	 * @param dbManager
	 */
	public static void setDbManager(DBManager dbManager) {
		Frame.dbManager = dbManager;
	}

	@Override
	public void start(Stage stage) throws Exception {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable t) {
				showErrorLog(t);
			}
		});

		/* Config laden */
		try {
			CONFIG.load(new FileReader(configFile));
		} catch (IOException e) {
			showErrorLog(new IOException("Couldn't load config", e));
		}

		/* Login Dialog starten und auf Usereingaben warten */
		new DialogLogin();

		/* Root-Layout initialisieren */
		BorderPane pane = new BorderPane();
		SplitPane split = new SplitPane();
		split.getItems().addAll(tableList = new PaneTableList(), tableContent = new PaneTableContent());
		split.setDividerPosition(0, 0.2);

		pane.setTop(toolbar = new PaneToolbar());
		pane.setCenter(split);

		BorderPane.setMargin(toolbar, new Insets(0, 0, 10, 0));

		stage.setTitle(Lang.getString("frame.title", "javaMyAdmin"));

		/* Fenstergr��e bestimmen und Frame Content zuweisen */
		stage.setScene(new Scene(pane, 800, 600));

		/* Icons setzen */
		stage.getIcons().addAll(getIcons());

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

	public static Image[] getIcons() {
		return new Image[] { new Image(Frame.class.getResource("/res/mario.png").toExternalForm()) };
	}

	/**
	 * Zeigt einen Fehler in einem Fenster an. Dieser Fehler wird außerdem
	 * automatisch in der Konsole ausgegeben.
	 * 
	 * @param t
	 *            Der Stacktrace des Fehlers
	 */
	public static void showErrorLog(Throwable t) {
		t.printStackTrace();
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(Lang.getString("error", "Error: " + t.getLocalizedMessage()));
		alert.setHeaderText(Lang.getString("error.header", "Ein Fehler ist aufgetreten."));
		alert.setContentText(t.getClass().equals(SQLException.class) ? Lang.getString("error.sql", "Couldn't connect to a database") : Lang.getString("error.unknown", ""));

		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String exceptionText = sw.toString();

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.addRow(1, textArea);

		alert.getDialogPane().setExpandableContent(expContent);
		alert.getDialogPane().setMinWidth(500);
		((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(getIcons());
		alert.showAndWait();
	}

}
