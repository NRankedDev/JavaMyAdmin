package javaMyAdmin.ui;

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
 * Repaesentiert das Logo und das SQL-Command-Feld
 * 
 * @author Nicolas
 * 		
 */
public class PaneToolbar extends BorderPane {
	
	private TextArea sqlArea;
	private Button execute;
	private Button clear;
	
	public PaneToolbar() {
		ImageView img = new ImageView(PaneToolbar.class.getResource("/res/logo.png").toExternalForm());
		
		BorderPane sql = new BorderPane();
		sql.setPadding(new Insets(10));
		
		sqlArea = new TextArea();
		sqlArea.setMaxHeight(150);
		
		HBox box = new HBox();
		box.setAlignment(Pos.CENTER_RIGHT);
		
		execute = new Button(Lang.getString("sql_commands.execute", "Execute"));
		execute.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String[] commands = sqlArea.getText().split(";");
				for (String string : commands) {
					string = string.replace("\t", " ").replace("\n", "");
					System.err.println("[PaneToolbar][TODO] SQL: " + string);
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
		
		sql.setTop(new Label(Lang.getString("sql_commands.label", "Execute SQL command(s)") + ":"));
		sql.setCenter(sqlArea);
		sql.setBottom(box);
		BorderPane.setMargin(sqlArea, new Insets(10, 0, 10, 0));
		
		setTop(new PaneMenu());
		setLeft(img);
		setCenter(sql);
		
		BorderPane.setMargin(img, new Insets(10));
	}
	
	public TextArea getSqlArea() {
		return sqlArea;
	}
	
	/**
	 * Zeigt im {@link #getSqlArea()} die SQL command line an und <b>fuehrt
	 * diese aus</b>.
	 * 
	 * @param sql
	 *            SQL command line
	 */
	public void executeSql(String sql) {
		clear.fire();
		sqlArea.setText(sql);
		execute.fire();
	}
	
}
