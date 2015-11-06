package javaMyAdmin.ui.dialogs;

import java.sql.SQLException;
import java.util.ArrayList;

import javaMyAdmin.db.Table;
import javaMyAdmin.ui.Frame;
import javaMyAdmin.ui.dialogs.util.DialogDynamicRows;
import javaMyAdmin.util.Lang;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public abstract class DialogAddRecords extends DialogDynamicRows {

	private final Table table;
	protected ArrayList<TextField[]> records = new ArrayList<TextField[]>();

	public DialogAddRecords(Table table) {
		super(Lang.getString("record.add", "Add records"));
		this.table = table;
	}

	@Override
	protected void initGrid(GridPane grid) {
		super.initGrid(grid);

		TextField tableField = new TextField(table.getName());
		tableField.setDisable(true);

		top.addRow(0, new Label(Lang.getString("record.add.description", "Add records for table")), tableField);
		top.addRow(1);
		top.addRow(2, new Label(Lang.getString("record.add.records", "Records")));

		addRow();
	}

	public void addRow() {
		try {
			TextField[] fields = new TextField[table.getColumnNames().size()];
			for (int i = 0; i < fields.length; i++) {
				TextField field = new TextField();
				field.promptTextProperty().set(table.getColumnNames().get(i));
				fields[i] = field;
			}

			addDynamicRow(fields);
			records.add(fields);
		} catch (SQLException e) {
			Frame.showErrorLog(e);
		}
	}

	@Override
	protected void onAddButtonPressed(Button addButton) {
		super.onAddButtonPressed(addButton);
		addRow();
	}

	@Override
	protected void onRemoveButtonPressed(Button removeButton, int index) {
		super.onRemoveButtonPressed(removeButton, index);
		records.remove(index);
	}

}