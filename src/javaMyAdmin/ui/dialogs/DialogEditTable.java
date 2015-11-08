package javaMyAdmin.ui.dialogs;

import java.sql.SQLException;
import java.util.ArrayList;

import javaMyAdmin.db.Table;
import javaMyAdmin.ui.dialogs.util.DialogDynamicRows;
import javaMyAdmin.util.Datatype;
import javaMyAdmin.util.FXUtil;
import javaMyAdmin.util.Index;
import javaMyAdmin.util.Lang;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Dialog, der beim Erstellen <u>und</u> Aendern einer Tabelle angezeigt wird.
 * Konstruktoren:
 * <ul>
 * <li>{@link #DialogEditTable()}, beim Erstellen einer Tabelle</li>
 * <li>{@link #DialogEditTable(Table)}, beim Editieren einer Tabelle</li>
 * </ul>
 * 
 * @author Nicolas
 */
public abstract class DialogEditTable extends DialogDynamicRows {
	
	private static final int MODE_ADD = 0;
	private static final int MODE_EDIT = 1;
	
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
	protected void initGrid(GridPane grid) {
		super.initGrid(grid);
		
		top.addRow(0, new Label(Lang.getString("table.edit.table", "Tablename")), tableName);
		top.addRow(1);
		top.addRow(2, new Label(Lang.getString("table.edit.columns", "Columns") + ":"));
		
		if (table != null) {
			try {
				for (String columnName : table.getColumnNames()) {
					addRow(columnName, Datatype.valueOfName(table.getDatentyp(columnName)), table.getLength(columnName), Index.valueOfName(table.getIndex(columnName)), table.getNull(columnName),
							MODE_EDIT);
				}
				
				if (this.grid.getRowCount() > 0) {
					return;
				}
			} catch (SQLException e) {
				FXUtil.showErrorLog(e);
			}
		}
		
		addRow();
	}
	
	public void addRow() {
		addRow("", Datatype.VARCHAR, "", Index.NONE, false, MODE_ADD);
	}
	
	public void addRow(String defaultTitle, Datatype<?> defaultDatatype, String defaultLength, Index defaultIndex, boolean defaultNull, int mode) {
		// Title
		final TextField title = new TextField(defaultTitle == null ? "" : defaultTitle);
		
		// Datatype
		final ComboBox<String> datatype = new ComboBox<String>();
		datatype.getItems().addAll(Datatype.nameValues());
		
		if (mode == MODE_EDIT) {
			datatype.getSelectionModel().select(defaultDatatype.getName());
			datatype.setDisable(true);
		} else {
			datatype.getSelectionModel().selectFirst();
		}
		
		// Length
		final TextField length = new TextField(defaultLength == null ? "" : defaultLength);
		if (mode == MODE_EDIT) {
			length.setDisable(true);
		}
		
		// Index
		final ComboBox<String> index = new ComboBox<String>(FXCollections.observableArrayList(Index.nameValues()));
		if (mode == MODE_EDIT) {
			index.getSelectionModel().select(defaultIndex.name());
			index.setDisable(true);
		} else {
			index.getSelectionModel().selectFirst();
		}
		
		// NullBox
		final CheckBox nullCheckBox = new CheckBox();
		nullCheckBox.setSelected(defaultNull);
		if (mode == MODE_EDIT) {
			nullCheckBox.setDisable(true);
		}
		
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
	
	@Override
	protected void onOkButtonPressed(ActionEvent event) {
		for (String index : getIndices()) {
			if (Index.valueOfName(index) == Index.PRIMARY) {
				super.onOkButtonPressed(event);
				return;
			}
		}
		
		Alert a = new Alert(AlertType.CONFIRMATION);
		a.setHeaderText(Lang.getString("table.edit.no_primary.header", "This table has no primary key."));
		a.setContentText(Lang.getString("table.edit.no_primary.content", "Are you sure you want to proceed without a primary key?"));
		if (a.showAndWait().get() == ButtonType.OK) {
			super.onOkButtonPressed(event);
		}
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
