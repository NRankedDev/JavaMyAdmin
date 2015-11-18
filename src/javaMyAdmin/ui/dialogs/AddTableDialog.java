package javaMyAdmin.ui.dialogs;

import java.util.ArrayList;

import javaMyAdmin.util.sql.Datatype;
import javaMyAdmin.util.sql.Index;
import javaMyAdmin.util.ui.DynamicRowsDialog;
import javaMyAdmin.util.ui.Lang;
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

public abstract class AddTableDialog extends DynamicRowsDialog {
	
	protected TextField tableName = new TextField();
	protected ArrayList<TextField> titles = new ArrayList<TextField>();
	protected ArrayList<ComboBox<String>> datatypes = new ArrayList<ComboBox<String>>();
	protected ArrayList<TextField> lengths = new ArrayList<TextField>();
	protected ArrayList<CheckBox> defaultNullOptions = new ArrayList<CheckBox>();
	protected ArrayList<ComboBox<String>> indices = new ArrayList<ComboBox<String>>();
	
	public AddTableDialog() {
		this(Lang.getString("table.add.title"));
	}
	
	public AddTableDialog(String title) {
		super(title);
	}
	
	@Override
	protected void initGrid(GridPane grid) {
		super.initGrid(grid);
		
		top.addRow(0, new Label(Lang.getString("table.edit.table")), tableName);
		top.addRow(1);
		top.addRow(2, new Label(Lang.getString("table.edit.columns") + ":"));
		
		addRow();
	}
	
	public void addRow() {
		addRow(null, null, null, null, false);
	}
	
	public void addRow(String defaultTitle, Datatype<?> defaultDatatype, String defaultLength, Index defaultIndex, boolean defaultNull) {
		// Title
		final TextField title = new TextField(defaultTitle == null ? "" : defaultTitle);
		
		// Datatype
		final ComboBox<String> datatype = new ComboBox<String>(FXCollections.observableArrayList(Datatype.nameValues()));
		
		if (defaultDatatype == null) {
			datatype.getSelectionModel().selectFirst();
		} else {
			datatype.getSelectionModel().select(defaultDatatype.getName());
		}
		
		// Length
		final TextField length = new TextField(defaultLength == null ? "" : defaultLength);
		
		// Null Box
		final CheckBox nullBox = new CheckBox();
		nullBox.setSelected(defaultNull);
		
		// Index
		final ComboBox<String> index = new ComboBox<String>(FXCollections.observableArrayList(Index.nameValues()));
		if (defaultIndex == null) {
			index.getSelectionModel().selectFirst();
		} else {
			index.getSelectionModel().select(defaultIndex.name());
		}
		
		titles.add(title);
		datatypes.add(datatype);
		lengths.add(length);
		defaultNullOptions.add(nullBox);
		indices.add(index);
		
		addDynamicRow(new Label(Lang.getString("table.edit.column")), title, new Separator(Orientation.VERTICAL), new Label(Lang.getString("table.edit.datatype")), datatype,
				new Separator(Orientation.VERTICAL), new Label(Lang.getString("table.edit.length")), length, new Separator(Orientation.VERTICAL), new Label(Lang.getString("table.edit.defaultNull")),
				nullBox, new Separator(Orientation.VERTICAL), new Label(Lang.getString("table.edit.index")), index);
	}
	
	@Override
	public void removeDynamicRow(int index) {
		titles.remove(index);
		datatypes.remove(index);
		lengths.remove(index);
		indices.remove(index);
		defaultNullOptions.remove(index);
		super.removeDynamicRow(index);
	}
	
	@Override
	protected void onAddButtonPressed(Button addButton) {
		super.onAddButtonPressed(addButton);
		addRow();
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
		a.setHeaderText(Lang.getString("table.edit.no_primary.header"));
		a.setContentText(Lang.getString("table.edit.no_primary.content"));
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
