package javaMyAdmin.ui;

import java.lang.Thread.UncaughtExceptionHandler;

import javaMyAdmin.db.DBManager;
import javaMyAdmin.ui.dialogs.LoginDialog;
import javaMyAdmin.util.Config;
import javaMyAdmin.util.Reference;
import javaMyAdmin.util.ui.FXUtil;
import javaMyAdmin.util.ui.Images;
import javaMyAdmin.util.ui.Lang;
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
	
	private ToolBarPane toolbar;
	private TableListPane tableList;
	private TableContentPane tableContent;
	private StatusBarPane statusBar;
	
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
			LoginDialog login = new LoginDialog();
			if (login.getExit()) {
				return;
			}
			
			/* Root-Layout initialisieren */
			BorderPane root = new BorderPane();
			SplitPane split = new SplitPane();
			split.getItems().addAll(tableList = new TableListPane(), tableContent = new TableContentPane());
			split.setDividerPosition(0, 0.2);
			
			root.setTop(toolbar = new ToolBarPane());
			root.setCenter(split);
			root.setBottom(statusBar = new StatusBarPane());
			
			BorderPane.setMargin(toolbar, new Insets(0, 0, 10, 0));
			
			stage.setTitle(String.format(Lang.getString("frame.title"), Reference.LOCALIZED_VERION, DBManager.getInstance().getUrl()));
			
			/* Fenstergroesse bestimmen und Frame Content zuweisen */
			stage.setScene(new Scene(root, 800, 600));
			
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
	 * {@link ToolBarPane} der Anwendung.
	 * 
	 * @return
	 */
	public ToolBarPane getToolbarPane() {
		return toolbar;
	}
	
	/**
	 * {@link TableListPane} der Anwendung.
	 * 
	 * @return
	 */
	public TableListPane getTableListPane() {
		return tableList;
	}
	
	/**
	 * {@link TableContentPane} der Anwendung.
	 * 
	 * @return
	 */
	public TableContentPane getTableContentPane() {
		return tableContent;
	}
	
	/**
	 * {@link StatusBarPane} der Anwendung.
	 * 
	 * @return
	 */
	public StatusBarPane getStatusBar() {
		return statusBar;
	}
	
}
