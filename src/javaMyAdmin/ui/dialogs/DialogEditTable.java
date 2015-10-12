package javaMyAdmin.ui.dialogs;

import java.util.ArrayList;

import javaMyAdmin.db.Table;
import javaMyAdmin.ui.util.Lang;
import javaMyAdmin.ui.util.OptionDialog;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public abstract class DialogEditTable extends OptionDialog {

	private final Table table;
	private BorderPane layout;
	private GridPane top;
	private GridPane bottom;
	private int rowIndex = 0;

	private TextField tableName = new TextField();
	private ArrayList<TextField> titles = new ArrayList<TextField>();
	private ArrayList<TextField> datatypes = new ArrayList<TextField>();
	private ArrayList<TextField> lengths = new ArrayList<TextField>();

	public DialogEditTable(Table table) {
		super(table == null ? Lang.getString("table.add", "Add table") : Lang.getString("table.edit", "Edit table") + " `" + table.getName() + "`");
		tableName.setText(table == null ? "" : table.getName());
		this.table = table;
	}

	@Override
	protected void init(BorderPane root) {
		super.init(root);
		layout = new BorderPane();
		top = new GridPane();
		top.setPadding(new Insets(10));
		top.setHgap(10);
		top.setVgap(10);
		top.addRow(0, new Label(Lang.getString("table.edit.table", "Tablename")), tableName);
		layout.setTop(top);
		layout.setCenter(new Separator(Orientation.HORIZONTAL));
		layout.setBottom(bottom);
		root.setTop(layout);
	}

	@Override
	protected void initGrid(GridPane grid) {
		this.bottom = grid;
		grid.addRow(rowIndex++, new Label(Lang.getString("table.edit.column", "Column")));
		addRow();
	}

	public void addRow() {
		addRow("", "", "");
	}

	public void addRow(String defaultTitle, String defaultDatatype, String defaultLength) {
		final int currentIndex = rowIndex;
		final TextField field = new TextField(String.format("%02d", rowIndex));
		field.setDisable(true);
		field.setMaxWidth(33);

		TextField title = new TextField(defaultTitle);
		TextField datatype = new TextField(defaultDatatype);
		TextField length = new TextField(defaultLength);
		titles.add(title);
		datatypes.add(datatype);
		lengths.add(length);

		final Button add = new Button("+");
		add.setTooltip(new Tooltip(Lang.getString("table.edit.add", "Add column")));
		add.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				add.setVisible(false);
				addRow();
				getDialogStage().sizeToScene();
			}
		});

		final Button remove = new Button("x");
		remove.setTooltip(new Tooltip(Lang.getString("table.edit.remove", "Remove column")));
		remove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				removeRow(currentIndex);
			}
		});

		bottom.addRow(rowIndex++, field, new Label(Lang.getString("column.edit.title", "Title")), title, new Separator(Orientation.VERTICAL),
				new Label(Lang.getString("column.edit.datatype", "Datatype")), datatype, new Separator(Orientation.VERTICAL), new Label(Lang.getString("column.edit.length", "Length")), length,
				remove, add);
	}

	public void removeRow(int index) {
		int nodesInRow = ((bottom.getChildren().size() - 1) / (rowIndex - 1));
		ObservableList<Node> items = bottom.getChildren();
		items.remove(nodesInRow * index + 1, nodesInRow * index + nodesInRow);

		bottom = new GridPane();
		bottom.setPadding(new Insets(10));
		bottom.setHgap(10);
		bottom.setVgap(10);

		bottom.addRow(0, items.get(0));
		items.remove(0);

		for (int i = 0; i < (items.size() - 1) / nodesInRow; i++) {
			Node[] nodes = new Node[nodesInRow];
			for (int j = 0; j < nodes.length; j++) {
				nodes[j] = items.get(i * nodesInRow + j);
			}

			bottom.addRow(i, nodes);
		}

		layout.setBottom(bottom);
		getDialogStage().sizeToScene();
	}

	public TextField getTableName() {
		return tableName;
	}

	public ArrayList<TextField> getTitles() {
		return titles;
	}

	public ArrayList<TextField> getDatatypes() {
		return datatypes;
	}

	public ArrayList<TextField> getLength() {
		return lengths;
	}
}
