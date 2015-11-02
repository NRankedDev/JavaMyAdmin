package javaMyAdmin.ui.dialogs;

import java.sql.SQLException;
import java.util.ArrayList;

import javaMyAdmin.db.Table;
import javaMyAdmin.ui.Frame;
import javaMyAdmin.ui.dialogs.util.DialogDynamicRows;
import javaMyAdmin.ui.util.Lang;
import javaMyAdmin.util.Datatype;
import javaMyAdmin.util.Index;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public abstract class DialogEditTable extends DialogDynamicRows {

	private final Table table;
	private TextField tableName = new TextField();
	private ArrayList<TextField> titles = new ArrayList<TextField>();
	private ArrayList<ComboBox<String>> datatypes = new ArrayList<ComboBox<String>>();
	private ArrayList<TextField> lengths = new ArrayList<TextField>();
	private ArrayList<CheckBox> defaultNullOptions = new ArrayList<CheckBox>();
	private ArrayList<ComboBox<String>> indices = new ArrayList<ComboBox<String>>();

	public DialogEditTable() {
		this(null);
	}

	public DialogEditTable(Table table) {
		super(table == null ? Lang.getString("table.add", "Add table") : Lang.getString("table.edit", "Edit table") + " `" + table.getName() + "`");
		tableName.setText(table == null ? "" : table.getName());
		this.table = table;
	}

	@Override
	protected void init(BorderPane root) {
		super.init(root);
		top.addRow(0, new Label(Lang.getString("table.edit.table", "Tablename")), tableName);
		top.addRow(1);
		top.addRow(2, new Label(Lang.getString("table.edit.columns", "Columns") + ":"));
	}

	@Override
	protected void initGrid(GridPane grid) {
		super.initGrid(grid);

		if (table != null) {
			try {
				for (String columnName : table.getColumnNames()) {
					addRow(columnName, Datatype.VARCHAR, "", Index.NONE, false);
				}

				if (this.grid.getRowCount() > 0) {
					return;
				}
			} catch (SQLException e) {
				Frame.showErrorLog(e);
			}
		}

		addRow();
	}

	public void addRow() {
		addRow("", Datatype.VARCHAR, "", Index.NONE, false);
	}

	public void addRow(String defaultTitle, Datatype defaultDatatype, String defaultLength, Index defaultIndex, boolean defaultNull) {
		final TextField title = new TextField(defaultTitle);

		final ComboBox<String> datatype = new ComboBox<String>();
		for (Datatype.Kind kind : Datatype.Kind.values()) {
			for (Datatype type : Datatype.values(kind)) {
				datatype.getItems().add(type.getName());
			}
		}
		datatype.getSelectionModel().select(defaultDatatype.getName());

		final TextField length = new TextField(defaultLength);

		final ComboBox<String> index = new ComboBox<String>(FXCollections.observableArrayList(Index.nameValues()));
		index.getSelectionModel().select(defaultIndex.getName());

		final CheckBox nullCheckBox = new CheckBox();
		nullCheckBox.setSelected(defaultNull);

		titles.add(title);
		datatypes.add(datatype);
		lengths.add(length);
		indices.add(index);
		defaultNullOptions.add(nullCheckBox);

		addDynamicRow(new Label(Lang.getString("table.edit.title", "Title")), title, new Separator(Orientation.VERTICAL), new Label(Lang.getString("table.edit.datatype", "Datatype")), datatype,
				new Separator(Orientation.VERTICAL), new Label(Lang.getString("table.edit.length", "Length")), length, new Separator(Orientation.VERTICAL),
				new Label(Lang.getString("table.edit.defaultNull", "Default Null")), nullCheckBox, new Separator(Orientation.VERTICAL), new Label(Lang.getString("table.edit.index", "Index")), index);
	}

	@Override
	protected void onAddButtonPressed(Button addButton) {
		super.onAddButtonPressed(addButton);
		addRow();
	}

	@Override
	protected void onRemoveButtonPressed(Button removeButton, int index) {
		super.onRemoveButtonPressed(removeButton, index);
		titles.remove(index);
		datatypes.remove(index);
		lengths.remove(index);
		indices.remove(index);
		defaultNullOptions.remove(index);
	}

	public TextField getTableName() {
		return tableName;
	}

	public ArrayList<String> getTitles() {
		return convertTextFields(titles);
	}

	public ArrayList<String> getDatatypes() {
		return convertComboBoxes(datatypes);
	}

	public ArrayList<String> getLength() {
		return convertTextFields(lengths);
	}

	public ArrayList<Boolean> getDefaultNull() {
		return convertCheckBoxes(defaultNullOptions);
	}

	public ArrayList<String> getIndices() {
		return convertComboBoxes(indices);
	}

	private ArrayList<String> convertTextFields(ArrayList<TextField> fields) {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (TextField textField : fields) {
			arrayList.add(textField.getText());
		}

		return arrayList;
	}

	private ArrayList<Boolean> convertCheckBoxes(ArrayList<CheckBox> boxes) {
		ArrayList<Boolean> arrayList = new ArrayList<Boolean>();
		for (CheckBox checkBox : boxes) {
			arrayList.add(checkBox.isSelected());
		}

		return arrayList;
	}

	private ArrayList<String> convertComboBoxes(ArrayList<ComboBox<String>> boxes) {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (ComboBox<String> comboBox : boxes) {
			arrayList.add(comboBox.getValue());
		}

		return arrayList;
	}

}
