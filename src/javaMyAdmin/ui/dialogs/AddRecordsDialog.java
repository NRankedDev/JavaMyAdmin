package javaMyAdmin.ui.dialogs;

import java.sql.SQLException;
import java.util.ArrayList;

import javaMyAdmin.db.Table;
import javaMyAdmin.ui.Frame;
import javaMyAdmin.util.ui.DynamicRowsDialog;
import javaMyAdmin.util.ui.FXUtil;
import javaMyAdmin.util.ui.Lang;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Dialog beim erstellen von Datensaetzen
 * 
 * @author Nicolas
 */
public class AddRecordsDialog extends DynamicRowsDialog {
	
	private final Table table;
	protected ArrayList<TextField[]> records = new ArrayList<TextField[]>();
	
	public AddRecordsDialog(Table table) {
		super(Lang.getString("record.add.title"));
		this.table = table;
	}
	
	@Override
	protected void initGrid(GridPane grid) {
		super.initGrid(grid);
		
		TextField tableField = new TextField(table.getName());
		tableField.setDisable(true);
		
		top.addRow(0, new Label(String.format(Lang.getString("record.add.description"), table.getName())), tableField);
		top.addRow(1);
		top.addRow(2, new Label(Lang.getString("record.add.records")));
		
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
			FXUtil.showErrorLog(e);
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
	
	@Override
	protected boolean handle() {
		for (TextField[] record : records) {
			ArrayList<String> strings = new ArrayList<String>();
			for (int i = 0; i < record.length; i++) {
				strings.add(record[i].getText());
			}
			
			try {
				table.addTupel(strings);
				Frame.getInstance().getTableContentPane().addRow(strings);
			} catch (SQLException e) {
				FXUtil.showErrorLog(e);
				return false;
			}
		}
		
		return true;
	}
	
}