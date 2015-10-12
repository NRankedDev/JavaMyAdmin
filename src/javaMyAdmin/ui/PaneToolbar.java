package javaMyAdmin.ui;

import javaMyAdmin.ui.util.Lang;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class PaneToolbar extends BorderPane {

	private TextArea sqlArea;
	private Button execute;
	private Button clear;

	public PaneToolbar() {
		ImageView img = new ImageView(PaneToolbar.class.getResource("/res/JavaMyAdmin.png").toExternalForm());
		img.setFitWidth(250);
		img.setFitHeight(125);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10));

		sqlArea = new TextArea();
		sqlArea.setMaxHeight(150);

		FlowPane flow = new FlowPane();
		flow.setHgap(10);
		flow.setVgap(10);
		flow.setAlignment(Pos.CENTER_RIGHT);

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

		flow.getChildren().addAll(execute, clear);

		grid.addRow(0, new Label(Lang.getString("sql_commands.label", "Execute SQL command(s)") + ":"));
		grid.addRow(1, sqlArea);
		grid.addRow(2, flow);

		setTop(new PaneMenu());
		setLeft(img);
		setCenter(grid);
	}

	public TextArea getSqlArea() {
		return sqlArea;
	}

	/**
	 * Zeigt im {@link #getSqlArea()} die SQL command line an und <b>führt diese
	 * aus</b>.
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
