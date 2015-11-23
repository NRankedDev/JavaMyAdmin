package javaMyAdmin.ui;

import java.sql.SQLException;

import javaMyAdmin.db.DBManager;
import javaMyAdmin.db.Table;
import javaMyAdmin.util.ui.FXUtil;
import javaMyAdmin.util.ui.Images;
import javaMyAdmin.util.ui.Lang;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * Repraesentiert das Logo und das SQL-Command-Feld
 * 
 * @author Nicolas
 * 		
 */
public class ToolBarPane extends BorderPane {
	
	private static final String sqlKey = "sql_commands.label";
	
	private BorderPane sql;
	private TextArea sqlArea;
	private Button execute;
	private Button clear;
	
	private String databaseEnvironment;
	private String tableEnvironment;
	
	public ToolBarPane() {
		ImageView img = new ImageView(Images.LOGO);
		
		sql = new BorderPane();
		sql.setPadding(new Insets(10));
		
		sqlArea = new TextArea();
		sqlArea.setMaxHeight(150);
		
		HBox box = new HBox();
		box.setAlignment(Pos.CENTER_RIGHT);
		
		execute = new Button(Lang.getString("sql_commands.execute"));
		execute.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					String sql = sqlArea.getText();
					Table table;
					
					if (databaseEnvironment == null && tableEnvironment == null) {
						table = DBManager.getInstance().executeSQL(sql);
						Frame.getInstance().getTableListPane().refreshDatabases();
					} else if (databaseEnvironment != null && tableEnvironment == null) {
						table = DBManager.getInstance().getDB(databaseEnvironment).executeSQL(sql);
						Frame.getInstance().getTableListPane().refresh(databaseEnvironment, null);
					} else if (databaseEnvironment != null && tableEnvironment != null) {
						table = DBManager.getInstance().getDB(databaseEnvironment).getTable(tableEnvironment).executeSQL(sql);
						Frame.getInstance().getTableListPane().refresh(databaseEnvironment, null);
					} else {
						throw new RuntimeException("database == null; table != null");
					}
					
					if (table != null) {
						Frame.getInstance().getTableListPane().getSelectionModel().clearSelection();
						Frame.getInstance().getTableContentPane().refresh(table);
					}
				} catch (SQLException e) {
					FXUtil.showErrorLog(e);
				}
			}
		});
		clear = new Button(Lang.getString("sql_commands.clear"));
		clear.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				sqlArea.setText("");
			}
		});
		
		box.getChildren().addAll(execute, clear);
		HBox.setMargin(execute, new Insets(0, 10, 0, 0));
		
		sql.setCenter(sqlArea);
		sql.setBottom(box);
		BorderPane.setMargin(sqlArea, new Insets(10, 0, 10, 0));
		
		setTop(new MenuPane());
		setLeft(img);
		setCenter(sql);
		
		BorderPane.setMargin(img, new Insets(10));
		setServerSQL();
	}
	
	public TextArea getSqlArea() {
		return sqlArea;
	}
	
	public void setServerSQL() {
		sql.setTop(new Label(String.format(Lang.getString(sqlKey), DBManager.getInstance().getUrl()) + ":"));
		databaseEnvironment = null;
		tableEnvironment = null;
	}
	
	public void setDatabaseSQL(String db) {
		sql.setTop(new Label(String.format(Lang.getString(sqlKey), db) + ":"));
		databaseEnvironment = db;
		tableEnvironment = null;
	}
	
	public void setTableSQL(String db, String table) {
		sql.setTop(new Label(String.format(Lang.getString(sqlKey), db + "." + table) + ":"));
		this.databaseEnvironment = db;
		this.tableEnvironment = table;
	}
	
}
