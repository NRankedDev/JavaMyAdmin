package javaMyAdmin.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javaMyAdmin.db.Line;
import javaMyAdmin.db.Table;
import javaMyAdmin.ui.TableContentPane.TableRecord;
import javaMyAdmin.ui.dialogs.AddRecordsDialog;
import javaMyAdmin.ui.dialogs.EditTableDialog;
import javaMyAdmin.util.ui.FXUtil;
import javaMyAdmin.util.ui.Images;
import javaMyAdmin.util.ui.Lang;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

/**
 * Repraesentiert die Tabelle, in der alle Daten der SQL-Tabelle angezeigt
 * werden
 * 
 * @author Nicolas
 */
public class TableContentPane extends TableView<TableRecord> {
	
	private Table table;
	
	public TableContentPane() {
		setPlaceholder(new Label(Lang.getString("table.no_content")));
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
		
		// Clearing old data
		getItems().clear();
		getColumns().clear();
		
		Frame.getInstance().getStatusBar().setTableInfo(table);
		
		if (table != null) {
			if (table.getAbstract()) {
				for (MenuItem item : getContextMenu().getItems()) {
					item.setDisable(true);
				}
				
				setEditable(false);
			} else {
				for (MenuItem item : getContextMenu().getItems()) {
					item.setDisable(false);
				}
				
				setEditable(true);
			}
			
			try {
				refresh(table.getColumnNames(), table.getLines());
			} catch (SQLException e) {
				FXUtil.showErrorLog(e);
			}
		}
	}
	
	private void refresh(ArrayList<String> columnNames, ArrayList<Line> tableLines) {
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
							try {
								getCurrentShownTable().setValue(getTableRow().getIndex(), getColumns().indexOf(getTableColumn()), newValue);
							} catch (SQLException e) {
								FXUtil.showErrorLog(e);
							}
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
	
	public void addRow(ArrayList<String> values) {
		TableRecord record = new TableRecord();
		ArrayList<String> columns;
		try {
			columns = table.getColumnNames();
			
			if (columns.size() != values.size()) {
				throw new SQLException("columns.size() != values.size() [" + columns.size() + " != " + values.size() + "]");
			}
		} catch (SQLException e) {
			FXUtil.showErrorLog(e);
			return;
		}
		
		for (int i = 0; i < columns.size(); i++) {
			record.data.put(columns.get(i), new SimpleStringProperty(values.get(i)));
		}
		
		getItems().add(record);
	}
	
	public Table getCurrentShownTable() {
		return table;
	}
	
	public class TableRecord {
		private final HashMap<String, SimpleStringProperty> data = new HashMap<String, SimpleStringProperty>();
		
		public HashMap<String, SimpleStringProperty> getData() {
			return data;
		}
	}
	
	public class CustomContextMenu extends ContextMenu {
		
		public CustomContextMenu() {
			MenuItem addRecord = new MenuItem(Lang.getString("record.add"));
			addRecord.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (getCurrentShownTable() != null) {
						new AddRecordsDialog(getCurrentShownTable()).show();
					}
				}
			});
			
			MenuItem removeRecord = new MenuItem(Lang.getString("record.remove"));
			removeRecord.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (getCurrentShownTable() != null) {
						TableRecord r = getSelectionModel().getSelectedItem();
						ArrayList<String> values = new ArrayList<String>();
						
						for (SimpleStringProperty ssp : r.getData().values()) {
							values.add(ssp.get());
						}
						
						Collections.reverse(values);
						
						try {
							getCurrentShownTable().rmTupel(values);
							TableContentPane.this.getItems().remove(getSelectionModel().getSelectedIndex());
						} catch (SQLException e) {
							FXUtil.showErrorLog(e);
						}
					}
				}
			});
			
			MenuItem editTable = new MenuItem(Lang.getString("table.edit"));
			editTable.setGraphic(new ImageView(Images.TABLE_EDIT));
			editTable.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (getCurrentShownTable() != null) {
						new EditTableDialog(getCurrentShownTable()).show();
					}
				}
			});
			
			getItems().addAll(addRecord, removeRecord, new SeparatorMenuItem(), editTable);
		}
	}
}
