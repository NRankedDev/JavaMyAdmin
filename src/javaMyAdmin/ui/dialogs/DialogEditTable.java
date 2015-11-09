package javaMyAdmin.ui.dialogs;

import java.sql.SQLException;

import javaMyAdmin.db.Table;
import javaMyAdmin.util.Datatype;
import javaMyAdmin.util.FXUtil;
import javaMyAdmin.util.Index;
import javaMyAdmin.util.Lang;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public abstract class DialogEditTable extends DialogAddTable {
	
	private final Table table;
	
	public DialogEditTable(Table table) {
		super(Lang.getString("table.edit", "Edit table") + " `" + table.getName() + "`");
		tableName.setText(table == null ? "" : table.getName());
		this.table = table;
	}
	
	@Override
	protected void initGrid(GridPane grid) {
		super.initGrid(grid);
		
		this.grid.removeRow(0);
		
		try {
			for (String columnName : table.getColumnNames()) {
				addRow(columnName, Datatype.valueOfName(table.getDatentyp(columnName)), table.getLength(columnName), Index.valueOfName(table.getIndex(columnName)),
						table.getNull(columnName));
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
	protected Node[] createRowNodes(int newRowIndex, String defaultTitle, Datatype<?> defaultDatatype, String defaultLength, Index defaultIndex, boolean defaultNull) {
		Node[] nodes = super.createRowNodes(newRowIndex, defaultTitle, defaultDatatype, defaultLength, defaultIndex, defaultNull);
		
		if (defaultTitle != null && !defaultTitle.isEmpty()) {
			for (int i = 0; i < nodes.length; i++) {
				if (i != 0) {
					nodes[i].setDisable(true);
				}
			}
		}
		
		return nodes;
	}
	
	@Override
	protected void onRemoveButtonPressed(Button removeButton, int index) {
		super.onRemoveButtonPressed(removeButton, index);
		
		String column = titles.get(index).getText();
		try {
			if (table.getColumnNames().contains(column)) {
				table.removeColumn(column);
			}
		} catch (SQLException e) {
			FXUtil.showErrorLog(e);
		}
	}
}
