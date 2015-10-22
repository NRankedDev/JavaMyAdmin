package javaMyAdmin.ui.dialogs;

import java.util.ArrayList;

import javaMyAdmin.db.Table;
import javaMyAdmin.ui.util.ExtendedGridPane;
import javaMyAdmin.ui.util.ExtendedGridPane.GridOperation;
import javaMyAdmin.ui.util.Lang;
import javaMyAdmin.ui.util.OptionDialog;
import javaMyAdmin.util.Datatype;
import javaMyAdmin.util.Index;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public abstract class DialogEditTable extends OptionDialog {

	private final Table table;
	private final ExtendedGridPane grid;
	private BorderPane layout;
	private GridPane top;
	private int indexCounter = 0;

	private TextField tableName = new TextField();
	private ArrayList<TextField> titles = new ArrayList<TextField>();
	private ArrayList<ComboBox<String>> datatypes = new ArrayList<ComboBox<String>>();
	private ArrayList<TextField> lengths = new ArrayList<TextField>();
	private ArrayList<CheckBox> defaultNullOptions = new ArrayList<CheckBox>();
	private ArrayList<ComboBox<String>> indices = new ArrayList<ComboBox<String>>();

	public DialogEditTable(Table table) {
		super(table == null ? Lang.getString("table.add", "Add table") : Lang.getString("table.edit", "Edit table") + " `" + table.getName() + "`");
		tableName.setText(table == null ? "" : table.getName());
		this.table = table;
		this.grid = new ExtendedGridPane();
	}

	@Override
	protected void init(BorderPane root) {
		layout = new BorderPane();
		super.init(root);
		top = new GridPane();
		top.setPadding(new Insets(10));
		top.setHgap(10);
		top.setVgap(10);
		top.addRow(0, new Label(Lang.getString("table.edit.table", "Tablename")), tableName);
		layout.setTop(top);
		layout.setCenter(new Separator(Orientation.HORIZONTAL));
		root.setTop(layout);
	}

	@Override
	protected void initGrid(GridPane grid) {
		this.grid.setGrid(grid);
		addRow();
	}

	public void addRow() {
		addRow("", Datatype.VARCHAR, "", Index.NONE, false);
	}

	public void addRow(String defaultTitle, Datatype defaultDatatype, String defaultLength, Index defaultIndex, boolean defaultNull) {
		final TextField field = new TextField();
		field.setDisable(true);
		field.setMaxWidth(33);

		final TextField title = new TextField(defaultTitle);

		final ComboBox<String> datatype = new ComboBox<String>(FXCollections.observableArrayList(Datatype.nameValues()));
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

		final Button add = new Button("+");
		add.setTooltip(new Tooltip(Lang.getString("table.edit.add", "Add column")));
		add.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				add.setVisible(false);
				addRow();
			}
		});

		final Button remove = new Button("x");
		remove.setTooltip(new Tooltip(Lang.getString("table.edit.remove", "Remove column")));
		remove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (grid.getRowCount() == 1) {
					return;
				}

				removeRow(Integer.valueOf(field.getText()) - 1);
				layout.setBottom(grid.getGrid());
				dialogStage.sizeToScene();
			}
		});

		addRow(field, new Label(Lang.getString("column.edit.title", "Title")), title, new Separator(Orientation.VERTICAL), new Label(Lang.getString("column.edit.datatype", "Datatype")), datatype,
				new Separator(Orientation.VERTICAL), new Label(Lang.getString("column.edit.length", "Length")), length, new Separator(Orientation.VERTICAL),
				new Label(Lang.getString("table.edit.defaultNull", "Default Null")), nullCheckBox, new Separator(Orientation.VERTICAL), new Label(Lang.getString("table.edit.index", "Index")), index,
				remove, add);
		layout.setBottom(grid.getGrid());
		dialogStage.sizeToScene();
	}

	public void addRow(final Node... nodes) {
		if (grid.getColumnCount() > 0 && nodes.length != grid.getColumnCount()) {
			throw new IllegalArgumentException(grid.getColumnCount() + " != " + nodes.length);
		}

		grid.recreateGrid(new GridOperation() {
			@Override
			public void onPreRecreate(ObservableList<Node> oldItems) {
			}

			@Override
			public void onPostRecreate() {
				grid.getGrid().addRow(grid.getRowCount(), nodes);

				// Indices der Reihen anpassen
				for (int i = 0; i < grid.getChildren().size(); i++) {
					if (i % grid.getColumnCount() == 0) {
						((TextField) grid.getChildren().get(i)).setText(String.valueOf((i / grid.getColumnCount()) + 1));
					}
				}
			}
		}, nodes.length);
	}

	public void removeRow(final int index) {
		grid.recreateGrid(new GridOperation() {
			@Override
			public void onPreRecreate(ObservableList<Node> oldItems) {
				oldItems.remove(index * grid.getColumnCount(), index * grid.getColumnCount() + grid.getColumnCount());
			}

			@Override
			public void onPostRecreate() {
				// Indices der Reihen anpassen
				for (int i = 0; i < grid.getChildren().size(); i++) {
					if (i % grid.getColumnCount() == 0) {
						((TextField) grid.getChildren().get(i)).setText(String.valueOf((i / grid.getColumnCount()) + 1));
					}
				}

				// Letzten "Add" Button sichtbar machen
				grid.getChildren().get(grid.getChildren().size() - 1).setVisible(true);
			}
		});
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
