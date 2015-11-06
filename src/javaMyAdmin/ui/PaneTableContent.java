package javaMyAdmin.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javaMyAdmin.db.Line;
import javaMyAdmin.db.Table;
import javaMyAdmin.ui.PaneTableContent.TableRecord;
import javaMyAdmin.ui.dialogs.DialogAddRecords;
import javaMyAdmin.ui.dialogs.DialogEditTable;
import javaMyAdmin.util.Lang;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

/**
 * Repraesentiert die Tabelle, in der alle Daten der SQL-Tabelle angezeigt
 * werden
 * 
 * @author Nicolas
 */
public class PaneTableContent extends TableView<TableRecord> {
	
	private Table table;
	
	public PaneTableContent() {
		setPlaceholder(new Label(Lang.getString("table.no_content", "No records in this table.")));
		setContextMenu(new CustomContextMenu());
		setEditable(true);
	}
	
	/**
	 * Laedt den Content einer Tabelle und zeigt ihn an
	 * 
	 * @param table
	 *            Die Tabelle
	 * @see #refresh(ArrayList, ArrayList)
	 */
	public void refresh(Table table) {
		this.table = table;
		
		if (table != null) {
			try {
				refresh(table.getColumnNames(), table.getLines());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			getItems().clear();
		}
	}
	
	private void refresh(ArrayList<String> columnNames, ArrayList<Line> tableLines) {
		// Clearing old data
		getItems().clear();
		getColumns().clear();
		
		// Generating columns
		@SuppressWarnings("unchecked")
		TableColumn<TableRecord, String>[] columns = new TableColumn[columnNames.size()];
		
		for (int i = 0; i < columns.length; i++) {
			TableColumn<TableRecord, String> column = new TableColumn<TableRecord, String>(columnNames.get(i));
			column.setCellValueFactory(new Callback<CellDataFeatures<TableRecord, String>, ObservableValue<String>>() {
				@Override
				public ObservableValue<String> call(CellDataFeatures<TableRecord, String> param) {
					return param.getValue().getData().get(param.getTableColumn().getText());
				}
			});
			column.setCellFactory(new Callback<TableColumn<TableRecord, String>, TableCell<TableRecord, String>>() {
				
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
<<<<<<< HEAD
							
							// TODO SQL
							System.out.println("EditRecord: TODO SQL");
=======
							try {
								getCurrentShownTable().setValue(getTableRow().getIndex(), getColumns().indexOf(getTableColumn()), newValue);
							} catch (SQLException e) {
								Frame.showErrorLog(e);
							}
>>>>>>> origin/master
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
			
			columns[i] = column;
		}
		
		getColumns().addAll(columns);
		
		// Loading data
		for (Line line : tableLines) {
			addRow(line.getValues());
		}
	}
	
	public Table getCurrentShownTable() {
		return table;
	}
	
	/**
	 * Fuegt der Tabelle eine neue Row hinzu. <b>KEIN</b> Aufruf und kein Update
	 * in SQL
	 * 
	 * @param data
	 *            Die Daten. Laenge muss mit Column Anzahl uebereinstimmen.
	 */
	public void addRow(ArrayList<String> data) {
		try {
			ArrayList<String> columns = table.getColumnNames();
			
			if (columns.size() != data.size()) {
				throw new RuntimeException("columns.size() != data.length");
			}
			
			TableRecord record = new TableRecord();
			
			for (int i = 0; i < data.size(); i++) {
				record.data.put(columns.get(i), new SimpleStringProperty(data.get(i)));
			}
			
			getItems().add(record);
		} catch (SQLException e) {
			Frame.showErrorLog(e);
		}
	}
	
	/**
	 * Wrapper Klasse, um aus einem String Daten fuer die Tabelle in JavaFX zu
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
<<<<<<< HEAD
						protected void handle() {
=======
						protected boolean handle() {
							return true;
>>>>>>> origin/master
							// TODO SQL
						}
					}.show();
				}
			});
			
			MenuItem addRecord = new MenuItem(Lang.getString("record.add", "Add record"));
			addRecord.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					new DialogAddRecords(getCurrentShownTable()) {
						@Override
<<<<<<< HEAD
						protected void handle() {
=======
						protected boolean handle() {
>>>>>>> origin/master
							for (TextField[] record : records) {
								ArrayList<String> strings = new ArrayList<String>();
								for (int i = 0; i < record.length; i++) {
									strings.add(record[i].getText());
								}
								
								try {
									table.addTupel(strings);
									PaneTableContent.this.addRow(strings);
								} catch (SQLException e) {
									Frame.showErrorLog(e);
<<<<<<< HEAD
								}
							}
=======
									return false;
								}
							}
							
							return true;
>>>>>>> origin/master
						};
					}.show();
				}
			});
			
			MenuItem removeRecord = new MenuItem(Lang.getString("record.remove", "Remove record"));
			removeRecord.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					TableRecord r = getSelectionModel().getSelectedItem();
					ArrayList<String> values = new ArrayList<String>();
					
					for (SimpleStringProperty ssp : r.getData().values()) {
						values.add(ssp.get());
					}
					
					Collections.reverse(values);
					
					try {
						getCurrentShownTable().rmTupel(values);
						PaneTableContent.this.getItems().remove(getSelectionModel().getSelectedIndex());
					} catch (SQLException e) {
						Frame.showErrorLog(e);
					}
				}
			});
			getItems().addAll(editColumn, new SeparatorMenuItem(), addRecord, removeRecord);
		}
	}
}
