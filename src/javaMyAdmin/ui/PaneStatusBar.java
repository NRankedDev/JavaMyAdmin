package javaMyAdmin.ui;

import javaMyAdmin.db.DBManager;
import javaMyAdmin.db.Table;
import javaMyAdmin.util.Lang;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class PaneStatusBar extends BorderPane {
	
	private Label tableInfo;
	private Label readOnly;
	
	public PaneStatusBar() {
		HBox left = new HBox();
		
		left.getChildren().addAll(new Label(String.format(Lang.getString("status.connected", "Connected to server '%s'."), DBManager.getInstance().getUrl())));
		
		HBox right = new HBox();
		
		right.getChildren().addAll(tableInfo = new Label(), readOnly = new Label());
		readOnly.setTextFill(Color.RED);
		
		setLeft(left);
		setRight(right);
		BorderPane.setMargin(left, new Insets(5));
		BorderPane.setMargin(right, new Insets(5));
		
		setTableInfo(null);
	}
	
	public void setTableInfo(Table table) {
		readOnly.setText("");
		
		if (table == null) {
			tableInfo.setText(Lang.getString("status.table.none", "No table selected."));
			readOnly.setVisible(false);
		} else {
			tableInfo.setText(String.format(Lang.getString("status.table.selected", "Table '%s' selected."), table.getName()));
			
			if (table.getAbstract()) {
				readOnly.setText(" " + Lang.getString("status.table.read_only", "(Read Only)"));
				readOnly.setVisible(true);
			} else {
				readOnly.setVisible(false);
			}
		}
	}
}
