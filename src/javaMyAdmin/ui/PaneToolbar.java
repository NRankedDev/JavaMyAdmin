package javaMyAdmin.ui;

import java.sql.SQLException;

import javaMyAdmin.db.Table;
import javaMyAdmin.util.FXUtil;
import javaMyAdmin.util.Lang;
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
public class PaneToolbar extends BorderPane {
	
	private static final String sqlKey = "sql_commands.label";
	private static final String sqlDefault = "Execute SQL command(s) in %s";
	
	private BorderPane sql;
	private TextArea sqlArea;
	private Button execute;
	private Button clear;
	
	private String databaseEnvironment;
	private String tableEnvironment;
	
	public PaneToolbar() {
		ImageView img = new ImageView(PaneToolbar.class.getResource("/res/logo.png").toExternalForm());
		
		sql = new BorderPane();
		sql.setPadding(new Insets(10));
		
		sqlArea = new TextArea();
		sqlArea.setMaxHeight(150);
		
		HBox box = new HBox();
		box.setAlignment(Pos.CENTER_RIGHT);
		
		execute = new Button(Lang.getString("sql_commands.execute", "Execute"));
		execute.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					String sql = sqlArea.getText();
					Table table;
					
					if (databaseEnvironment == null && tableEnvironment == null) {
						table = Frame.getDbManager().executeSQL(sql);
						Frame.getInstance().getTableListPane().refresh();
					} else if (databaseEnvironment != null && tableEnvironment == null) {
						table = Frame.getDbManager().getDB(databaseEnvironment).executeSQL(sql);
						Frame.getInstance().getTableListPane().refresh(databaseEnvironment);
					} else if (databaseEnvironment != null && tableEnvironment != null) {
						table = Frame.getDbManager().getDB(databaseEnvironment).getTable(tableEnvironment).executeSQL(sql);
						Frame.getInstance().getTableListPane().refresh(databaseEnvironment);
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
		clear = new Button(Lang.getString("sql_commands.clear", "Clear"));
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
		
		setTop(new PaneMenu());
		setLeft(img);
		setCenter(sql);
		
		BorderPane.setMargin(img, new Insets(10));
		setServerSQL();
	}
	
	public TextArea getSqlArea() {
		return sqlArea;
	}
	
	public void setServerSQL() {
		sql.setTop(new Label(String.format(Lang.getString(sqlKey, sqlDefault), "127.0.0.1") + ":"));
		databaseEnvironment = null;
		tableEnvironment = null;
	}
	
	public void setDatabaseSQL(String db) {
		sql.setTop(new Label(String.format(Lang.getString(sqlKey, sqlDefault), "'" + db + "'") + ":"));
		databaseEnvironment = db;
		tableEnvironment = null;
	}
	
	public void setTableSQL(String db, String table) {
		sql.setTop(new Label(String.format(Lang.getString(sqlKey, sqlDefault), "'" + db + "." + table + "'") + ":"));
		this.databaseEnvironment = db;
		this.tableEnvironment = table;
	}
	
}
