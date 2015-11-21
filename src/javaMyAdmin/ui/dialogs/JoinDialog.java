package javaMyAdmin.ui.dialogs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javaMyAdmin.db.DBManager;
import javaMyAdmin.db.Database;
import javaMyAdmin.db.Table;
import javaMyAdmin.util.sql.JoinMode;
import javaMyAdmin.util.ui.DynamicRowsDialog;
import javaMyAdmin.util.ui.FXUtil;
import javaMyAdmin.util.ui.Lang;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public abstract class JoinDialog extends DynamicRowsDialog {
	
	private final Table joinFrom;
	// private final Table joinTo;
	
	public JoinDialog(Table table1) {
		this(table1, null);
	}
	
	public JoinDialog(Table table1, Table table2) {
		super(Lang.getString("table.join.title"));
		this.joinFrom = table1;
		// this.joinTo = table2;
	}
	
	@Override
	protected void initGrid(GridPane grid) {
		super.initGrid(grid);
		
		TextField field = new TextField(joinFrom.getName());
		field.setDisable(true);
		
		top.addRow(0, new Label("SELECT * FROM "), field);
		
		addRow();
	}
	
	public void addRow() {
		addRow(null, null, JoinMode.INNER);
	}
	
	public void addRow(String db2, String table2, JoinMode mode) {
		ComboBox<String> joins = new ComboBox<String>();
		for (JoinMode joinMode : JoinMode.values()) {
			joins.getItems().add(joinMode.name() + " JOIN");
		}
		
		final HashMap<String, ArrayList<Table>> tables = new HashMap<String, ArrayList<Table>>();
		try {
			for (Database db : DBManager.getInstance().getDB()) {
				try {
					tables.put(db.getDbname(), db.getTable());
				} catch (SQLException e) {
					FXUtil.showErrorLog(e);
				}
			}
		} catch (SQLException e) {
			FXUtil.showErrorLog(e);
		}
		
		ObservableList<String> databases = FXCollections.observableArrayList(tables.keySet());
		databases.sort(null);
		
		final ComboBox<String> databaseBox = new ComboBox<String>(databases);
		final ComboBox<String> tableBox = new ComboBox<String>();
		tableBox.setMaxWidth(200);
		
		final TextField column1 = new TextField();
		column1.promptTextProperty().set("Column from " + joinFrom.getName());
		final TextField column2 = new TextField();
		
		databaseBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				tableBox.getItems().clear();
				
				for (Table table : tables.get(newValue)) {
					tableBox.getItems().add(table.getName());
				}
				
				tableBox.getSelectionModel().selectFirst();
			}
		});
		
		tableBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				column2.promptTextProperty().set("Column from " + newValue);
			}
		});
		
		joins.getSelectionModel().selectFirst();
		databaseBox.getSelectionModel().selectFirst();
		tableBox.getSelectionModel().selectFirst();
		
		if (mode != null) {
			joins.getSelectionModel().select(mode.name() + " JOIN");
		}
		if (db2 != null) {
			databaseBox.getSelectionModel().select(db2);
		}
		if (table2 != null) {
			tableBox.getSelectionModel().select(table2);
		}
		
		addDynamicRow(joins, databaseBox, tableBox, new Label("ON"), column1, new Label(" = "), column2);
	}
	
	@Override
	protected void onAddButtonPressed(Button addButton) {
		super.onAddButtonPressed(addButton);
		
		addRow();
	}
	
}
