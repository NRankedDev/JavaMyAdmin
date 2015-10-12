package javaMyAdmin.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javaMyAdmin.db.Line;
import javaMyAdmin.db.Table;
import javaMyAdmin.ui.PaneTableContent.TableRecord;
import javaMyAdmin.ui.dialogs.DialogChooseValue;
import javaMyAdmin.ui.dialogs.DialogEditTable;
import javaMyAdmin.ui.util.Lang;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

public class PaneTableContent extends TableView<TableRecord> {

	private Table table;

	public PaneTableContent() {
		setPlaceholder(new Label(Lang.getString("table.no_content", "No records in this table.")));
		setContextMenu(new CustomContextMenu());
		setEditable(true);
	}

	/**
	 * Lädt den Content einer Tabelle und zeigt ihn an
	 * 
	 * @param table
	 *            Die Tabelle
	 * @see #refresh(ArrayList, ArrayList)
	 */
	public void refresh(Table table) {
		if (table != null) {
			try {
				refresh(table.getColumnNames(), table.getLines());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			getItems().clear();
		}

		this.table = table;
	}

	private void refresh(ArrayList<String> columnNames, ArrayList<Line> tableLines) {
		// Clearing old data
		getItems().clear();
		getColumns().clear();

		// Generating columns
		@SuppressWarnings("unchecked")
		TableColumn<TableRecord, String>[] columns = new TableColumn[columnNames.size()];

		for (int i = 0; i < columns.length; i++) {
			columns[i] = new TableColumn<TableRecord, String>(columnNames.get(i));
			columns[i].setCellValueFactory(new Callback<CellDataFeatures<TableRecord, String>, ObservableValue<String>>() {
				@Override
				public ObservableValue<String> call(CellDataFeatures<TableRecord, String> param) {
					return param.getValue().getData().get(param.getTableColumn().getText());
				}
			});
			columns[i].setCellFactory(new Callback<TableColumn<TableRecord, String>, TableCell<TableRecord, String>>() {

				@Override
				public TableCell<TableRecord, String> call(TableColumn<TableRecord, String> param) {
					return new TableCell<TableRecord, String>() {

						private TextField textField;

						@Override
						public void startEdit() {
							super.startEdit();

							setGraphic(textField);
							setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
							textField.selectAll();
						}

						@Override
						public void commitEdit(String newValue) {
							super.commitEdit(newValue);

							// TODO SQL
							System.out.println("EditRecord: TODO SQL");
						}

						@Override
						public void cancelEdit() {
							super.cancelEdit();

							setText(getItem());
							setContentDisplay(ContentDisplay.TEXT_ONLY);
						}

						@Override
						protected void updateItem(String item, boolean empty) {
							super.updateItem(item, empty);

							if (textField == null) {
								textField = new TextField();
								textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
									@Override
									public void handle(KeyEvent event) {
										if (event.getCode() == KeyCode.ENTER) {
											commitEdit(textField.getText());
										} else if (event.getCode() == KeyCode.ESCAPE) {
											cancelEdit();
										}
									}
								});
							}

							if (empty) {
								setText(null);
								setGraphic(null);
							} else {
								textField.setText(item);
								setGraphic(textField);
								setText(item);

								if (isEditing()) {
									setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
								} else {
									setContentDisplay(ContentDisplay.TEXT_ONLY);
								}
							}
						}
					};
				}
			});
		}

		getColumns().addAll(columns);

		// Loading data
		ArrayList<TableRecord> values = new ArrayList<TableRecord>();
		for (Line line : tableLines) {
			ArrayList<String> data = line.getValues();

			TableRecord value = new TableRecord();

			for (int i = 0; i < data.size(); i++) {
				value.getData().put(columnNames.get(i), new SimpleStringProperty(data.get(i)));
			}

			values.add(value);
		}

		getItems().addAll(values);
	}

	public Table getCurrentShownTable() {
		return table;
	}

	/**
	 * Wrapper Klasse, um aus einem String Daten für die Tabelle in JavaFX zu
	 * erzeugen
	 */
	public final class TableRecord {

		private final HashMap<String, SimpleStringProperty> data = new HashMap<String, SimpleStringProperty>();

		public HashMap<String, SimpleStringProperty> getData() {
			return data;
		}

	}

	/**
	 * ContextMenu der Tabelle
	 */
	private final class CustomContextMenu extends ContextMenu {

		public CustomContextMenu() {
			MenuItem editColumn = new MenuItem(Lang.getString("column.edit", "Edit columns..."));
			editColumn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					ArrayList<String> columns = new ArrayList<String>();
					for (TableColumn<TableRecord, ?> column : getColumns()) {
						columns.add(column.getText());
					}

					new DialogEditTable(getCurrentShownTable()) {

						@Override
						protected void handle() {

							// TODO SQL
							System.err.println("EditColumn: TODO SQL");

							refresh(getCurrentShownTable());
						}
					}.show();
				}
			});

			MenuItem removeColumn = new MenuItem(Lang.getString("column.remove", "Remove columns..."));
			removeColumn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					ArrayList<String> columns = new ArrayList<String>();
					for (TableColumn<TableRecord, ?> column : getColumns()) {
						columns.add(column.getText());
					}

					new DialogChooseValue(removeColumn.getText(), Lang.getString("column.remove.column", "Column"), columns) {
						@Override
						protected void handle() {
							ObservableList<TableColumn<TableRecord, ?>> columns = PaneTableContent.this.getColumns();
							for (int i = 0; i < columns.size(); i++) {
								if (columns.get(i).getText().equals(comboBox.getValue())) {
									Alert alert = new Alert(AlertType.CONFIRMATION);
									alert.setHeaderText(Lang.getString("column.remove.header", "This option will delete the column '" + comboBox.getValue() + "'."));
									alert.setContentText(Lang.getString("column.remove.content", "Do you really want to do this?"));
									if (alert.showAndWait().get() == ButtonType.OK) {
										columns.remove(i);
										// TODO SQL
										System.err.println("Remove column: TODO SQL");
									}
								}
							}

							hideDialog();
						}
					}.show();
				}
			});

			MenuItem addRecord = new MenuItem(Lang.getString("record.add", "Add record"));
			addRecord.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					// TODO SQL
					System.err.println("AddDialog: TODO SQL");
				}
			});

			MenuItem removeRecord = new MenuItem(Lang.getString("record.remove", "Remove record"));
			removeRecord.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					// TODO SQL
					System.err.println("RemoveDialog: TODO SQL");
				}
			});
			getItems().addAll(editColumn, removeColumn, new SeparatorMenuItem(), addRecord, removeRecord);
		}
	}
}
