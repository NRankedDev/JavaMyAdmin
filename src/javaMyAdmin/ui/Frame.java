package javaMyAdmin.ui;

import java.lang.Thread.UncaughtExceptionHandler;

import javaMyAdmin.db.DBManager;
import javaMyAdmin.ui.dialogs.DialogLogin;
import javaMyAdmin.util.Config;
import javaMyAdmin.util.FXUtil;
import javaMyAdmin.util.Images;
import javaMyAdmin.util.Lang;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * 'Hauptklasse' der UI-Anwendung
 * 
 * @author Nicolas
 */
public class Frame extends Application {
	
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
	
	@Override
	public void start(Stage stage) throws Exception {
		try {
			Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread thread, Throwable t) {
					FXUtil.showErrorLog(t);
				}
			});
			
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
			
			/* Fenstergroesse bestimmen und Frame Content zuweisen */
			stage.setScene(new Scene(pane, 800, 600));
			
			/* Icons setzen */
			stage.getIcons().addAll(Images.ICONS);
			
			/* Fenster anzeigen */
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() throws Exception {
		DBManager.getInstance().close();
		Config.getInstance().save();
		System.exit(0);
	}
	
	/**
	 * {@link PaneToolbar} der Anwendung.
	 * 
	 * @return
	 */
	public PaneToolbar getToolbarPane() {
		return toolbar;
	}
	
	/**
	 * {@link PaneTableList} der Anwendung.
	 * 
	 * @return
	 */
	public PaneTableList getTableListPane() {
		return tableList;
	}
	
	/**
	 * {@link PaneTableContent} der Anwendung.
	 * 
	 * @return
	 */
	public PaneTableContent getTableContentPane() {
		return tableContent;
	}
	
}
