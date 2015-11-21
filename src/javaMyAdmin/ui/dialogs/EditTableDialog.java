package javaMyAdmin.ui.dialogs;

import java.sql.SQLException;
import java.util.ArrayList;

import javaMyAdmin.db.Table;
import javaMyAdmin.ui.Frame;
import javaMyAdmin.util.sql.Datatype;
import javaMyAdmin.util.sql.Index;
import javaMyAdmin.util.ui.FXUtil;
import javaMyAdmin.util.ui.Lang;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class EditTableDialog extends AddTableDialog {
	
	private final ArrayList<String> toRemoveColumns = new ArrayList<String>();
	private final Table table;
	private int newColumnsStart = 0;
	
	public EditTableDialog(Table table) {
		super(String.format(Lang.getString("table.edit.title"), table.getName()));
		tableName.setText(table.getName());
		this.table = table;
	}
	
	@Override
	protected void initGrid(GridPane grid) {
		super.initGrid(grid);
		
		removeDynamicRow(0);
		
		try {
			for (String columnName : table.getColumnNames()) {
				addRow(columnName, Datatype.valueOfName(table.getDatentyp(columnName)), table.getLength(columnName), Index.valueOfName(table.getIndex(columnName)), table.getNull(columnName));
			}
			
			if (this.grid.getRowCount() > 0) {
				return;
			}
		} catch (SQLException e) {
			FXUtil.showErrorLog(e);
		}
		
		addRow();
	}
	
	@Override
	public void addRow(String defaultTitle, Datatype<?> defaultDatatype, String defaultLength, Index defaultIndex, boolean defaultNull) {
		super.addRow(defaultTitle, defaultDatatype, defaultLength, defaultIndex, defaultNull);
		Node[] nodes = getCustomNodes(grid.getRowCount() - 1);
		
		if (defaultTitle != null && !defaultTitle.isEmpty()) {
			for (int i = 0; i < nodes.length; i++) {
				if (i > 1) {
					nodes[i].setDisable(true);
				}
			}
			newColumnsStart++;
		}
	}
	
	@Override
	protected void onRemoveButtonPressed(Button removeButton, int index) {
		if (index < newColumnsStart) {
			toRemoveColumns.add(titles.get(index).getText());
			newColumnsStart--;
		}
		
		super.onRemoveButtonPressed(removeButton, index);
	}
	
	@Override
	protected void onOkButtonPressed(ActionEvent event) {
		// Renaming table
		if (!table.getName().equalsIgnoreCase(tableName.getText())) {
			try {
				table.renameTable(tableName.getText());
			} catch (SQLException e) {
				FXUtil.showErrorLog(e);
				return;
			}
		}
		
		// Removing old columns
		for (String column : toRemoveColumns) {
			try {
				table.removeColumn(column);
			} catch (SQLException e) {
				FXUtil.showErrorLog(e);
				return;
			}
		}
		
		// Adding new columns
		ArrayList<String> titles = getTitles();
		
		for (int i = 0; i < titles.size(); i++) {
			try {
				if (i < newColumnsStart) {
					if (!table.getColumnNames().get(i).equals(titles.get(i))) {
						table.renameColumn(table.getColumnNames().get(i), titles.get(i));
					}
				} else {
					table.addColumn(titles.get(i), getDatatypes().get(i), getLength().get(i), getDefaultNull().get(i), getIndices().get(i));
				}
			} catch (SQLException e) {
				FXUtil.showErrorLog(e);
				return;
			}
		}
		
		super.onOkButtonPressed(event);
	}
	
	@Override
	protected boolean handle() {
		try {
			Frame.getInstance().getTableListPane().refresh(table.getDatabase().getDbname());
		} catch (Exception e) {
			FXUtil.showErrorLog(e);
		}
		Frame.getInstance().getTableContentPane().refresh(table);
		return true;
	}
	
}
