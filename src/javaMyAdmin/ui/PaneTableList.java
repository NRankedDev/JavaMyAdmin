package javaMyAdmin.ui;

import java.sql.SQLException;
import java.util.Optional;

import javaMyAdmin.db.DBManager;
import javaMyAdmin.db.Database;
import javaMyAdmin.db.Table;
import javaMyAdmin.ui.dialogs.DialogEditTable;
import javaMyAdmin.ui.dialogs.DialogStringInput;
import javaMyAdmin.util.FXUtil;
import javaMyAdmin.util.Images;
import javaMyAdmin.util.Lang;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Repraesentiert die aller Tabellen am linken Rand
 * 
 * @author Nicolas
 */
public class PaneTableList extends TreeView<String> {
	
	private static final int databaseLayer = 1;
	private static final int tableLayer = 2;
	
	private final ContextMenu emptyContextMenu = new EmptyContextMenu();
	private final ContextMenu databaseItemContextMenu = new DatabaseItemContextMenu();
	private final ContextMenu tableItemContextMenu = new TableItemContextMenu();
	
	public PaneTableList() {
		getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
			
			@Override
			public TreeCell<String> call(TreeView<String> param) {
				return new TreeCell<String>() {
					@Override
					public void updateSelected(boolean selected) {
						super.updateSelected(selected);
						if (selected) {
							if (FXUtil.getLayer(getTreeItem()) == tableLayer) {
								try {
									Database db = DBManager.getInstance().getDB(getTreeItem().getParent().getValue());
									
									if (db != null) {
										if (db.getDbname().equals(getTreeItem().getParent().getValue())) {
											Frame.getInstance().getTableContentPane().refresh(db.getTable(getTreeItem().getValue()));
										}
									}
								} catch (SQLException e) {
									FXUtil.showErrorLog(e);
								}
								Frame.getInstance().getToolbarPane().setTableSQL(getTreeItem().getParent().getValue(), getTreeItem().getValue());
							} else if (FXUtil.getLayer(getTreeItem()) == databaseLayer) {
								Frame.getInstance().getToolbarPane().setDatabaseSQL(getTreeItem().getValue());
								Frame.getInstance().getTableContentPane().refresh(null);
							} else {
								Frame.getInstance().getToolbarPane().setServerSQL();
								Frame.getInstance().getTableContentPane().refresh(null);
							}
						}
					}
					
					@Override
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						
						if (empty) {
							setText(null);
							setGraphic(null);
							setContextMenu(emptyContextMenu);
						} else {
							setText(getTreeItem().getValue());
							setGraphic(getTreeItem().getGraphic());
							
							if (empty || FXUtil.isRoot(getTreeItem())) {
								setContextMenu(emptyContextMenu);
							} else if (FXUtil.getLayer(getTreeItem()) == databaseLayer) {
								setContextMenu(databaseItemContextMenu);
							} else if (FXUtil.getLayer(getTreeItem()) == tableLayer) {
								setContextMenu(tableItemContextMenu);
							}
						}
					}
				};
			}
		});
		
		setPrefSize(200, 0);
		
		refresh();
	}
	
	/**
	 * Zeigt alle Datenbanken und Tabellen neu an
	 */
	public void refresh() {
		TreeItem<String> root = new TreeItem<String>(Lang.getString("connection", "Connection"));
		root.setGraphic(new ImageView(Images.CONNECTION));
		root.setExpanded(true);
		setRoot(root);
		
		try {
			for (Database db : DBManager.getInstance().getDB()) {
				// if (db.getDbname().equals("information_schema")) {
				// continue;
				// TODO remove DEBUG
				// }
				
				refresh(db.getDbname());
			}
		} catch (SQLException e) {
			FXUtil.showErrorLog(e);
		}
	}
	
	/**
	 * Zeigt <b>eine</b> Datenbank und ihre Tabellen neu an
	 */
	public void refresh(String database) {
		try {
			TreeItem<String> root = getRoot();
			if (root == null) {
				refresh();
				return;
			}
			
			Database db = DBManager.getInstance().getDB(database);
			TreeItem<String> dbItem = null;
			
			for (TreeItem<String> item : root.getChildren()) {
				if (item.getValue().equalsIgnoreCase(db.getDbname())) {
					dbItem = item;
					break;
				}
			}
			
			if (dbItem == null) {
				dbItem = new TreeItem<String>(db.getDbname());
				dbItem.setGraphic(new ImageView(Images.DATABASE));
				root.getChildren().add(dbItem);
			}
			
			try {
				dbItem.getChildren().clear();
				
				for (Table table : db.getTable()) {
					TreeItem<String> item = new TreeItem<String>(table.getName());
					item.setGraphic(new ImageView(Images.TABLE));
					dbItem.getChildren().add(item);
				}
			} catch (SQLException e) {
				FXUtil.showErrorLog(new SQLException("Error while loading tables for " + db.getDbname(), e));
			}
		} catch (SQLException e) {
			FXUtil.showErrorLog(e);
		}
	}
	
	/**
	 * ContextMenu, wenn nichts selektiert ist
	 */
	private class EmptyContextMenu extends ContextMenu {
		
		public EmptyContextMenu() {
			final MenuItem addDatabase = new MenuItem(Lang.getString("database.add", "Add database"));
			addDatabase.setGraphic(new ImageView(Images.DATABASE_ADD));
			addDatabase.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					new DialogStringInput(addDatabase.getText(), Lang.getString("database.add.name", "Name")) {
						@Override
						protected boolean handle() {
							try {
								DBManager.getInstance().addDB(input.getText());
								refresh();
								return true;
							} catch (SQLException e) {
								FXUtil.showErrorLog(e);
								return false;
							}
						}
					}.show();
				}
			});
			getItems().addAll(addDatabase);
		}
		
	}
	
	/**
	 * ContextMenu, wenn eine Datenbank selektiert ist
	 */
	private class DatabaseItemContextMenu extends EmptyContextMenu {
		
		public DatabaseItemContextMenu() {
			MenuItem renameDatasase = new MenuItem(Lang.getString("database.rename", "Rename database"));
			renameDatasase.setGraphic(new ImageView(Images.DATABASE_EDIT));
			renameDatasase.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					new DialogStringInput(Lang.getString("database.rename", "Rename database"), Lang.getString("database.add.name", "Name")) {
						@Override
						protected boolean handle() {
							try {
								DBManager.getInstance().getDB(getSelectionModel().getSelectedItem().getValue()).renameDatabase(input.getText());
								return true;
							} catch (SQLException e) {
								FXUtil.showErrorLog(e);
							}
							
							return false;
						}
					}.show();
				}
			});
			
			MenuItem removeDatabase = new MenuItem(Lang.getString("database.remove", "Remove database"));
			removeDatabase.setGraphic(new ImageView(Images.DATABASE_REMOVE));
			removeDatabase.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					try {
						String db = Lang.getString("database", "database");
						TextInputDialog dialog = new TextInputDialog();
						dialog.setTitle(Lang.getString("dialog.remove.title", "Do you really want to proceed?"));
						dialog.setHeaderText(
								String.format(Lang.getString("dialog.remove.header", "If you delete the %s `%s`, all data will be lost."), db, getSelectionModel().getSelectedItem().getValue()));
						dialog.setContentText(String.format(Lang.getString("dialog.remove.content", "Enter the name of the %s to delete it:"), db));
						((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().addAll(Images.ICONS);
						
						Optional<String> result = dialog.showAndWait();
						if (result.isPresent()) {
							if (result.get().equals(getSelectionModel().getSelectedItem().getValue())) {
								DBManager.getInstance().rmDB(getSelectionModel().getSelectedItem().getValue());
								refresh();
							} else {
								Alert a = new Alert(AlertType.INFORMATION);
								a.setHeaderText(Lang.getString("dialog.remove.cancel.header", "Operation was terminated."));
								a.setContentText(String.format(Lang.getString("dialog.remove.cancel.content", "The entered name doesn't equal the name of the %s."), db));
								((Stage) a.getDialogPane().getScene().getWindow()).getIcons().addAll(Images.ICONS);
								a.show();
							}
						}
					} catch (SQLException e) {
						FXUtil.showErrorLog(e);
					}
				}
			});
			
			MenuItem addTable = new MenuItem(Lang.getString("table.add", "Add table"));
			addTable.setGraphic(new ImageView(Images.TABLE_ADD));
			addTable.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					new DialogEditTable(null) {
						@Override
						protected boolean handle() {
							TreeItem<String> item = getSelectionModel().getSelectedItem();
							String db = null;
							
							if (FXUtil.getLayer(item) == databaseLayer) {
								db = item.getValue();
							} else if (FXUtil.getLayer(item) == tableLayer) {
								db = item.getParent().getValue();
							}
							
							try {
								Database database = DBManager.getInstance().getDB(db);
								if (database != null) {
									database.addTable(getTableName().getText(), getTitles(), getDatatypes(), getLength(), getDefaultNull(), getIndices());
								} else {
									throw new RuntimeException("Database `" + db + "` doesn't exists");
								}
								
								refresh(database.getDbname());
							} catch (Exception e) {
								FXUtil.showErrorLog(e);
								return false;
							}
							
							return true;
						}
					}.show();
				}
			});
			
			getItems().addAll(renameDatasase, removeDatabase, new SeparatorMenuItem(), addTable);
		}
	}
	
	/**
	 * ContextMenu, wenn eine Tabelle selektiert ist
	 */
	private class TableItemContextMenu extends DatabaseItemContextMenu {
		
		public TableItemContextMenu() {
			MenuItem editTable = new MenuItem(Lang.getString("table.edit", "Edit table") + "...");
			editTable.setGraphic(new ImageView(Images.TABLE_EDIT));
			editTable.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					TreeItem<String> item = getSelectionModel().getSelectedItem();
					Table table = null;
					
					try {
						table = DBManager.getInstance().getDB(item.getParent().getValue()).getTable(item.getValue());
					} catch (NullPointerException | SQLException e) {
						FXUtil.showErrorLog(e);
					}
					
					if (table != null) {
						new DialogEditTable(table) {
							@Override
							protected boolean handle() {
								return true;
							}
						}.show();
					}
				}
			});
			
			MenuItem removeTable = new MenuItem(Lang.getString("table.remove", "Remove table"));
			removeTable.setGraphic(new ImageView(Images.TABLE_REMOVE));
			removeTable.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					try {
						TreeItem<String> item = getSelectionModel().getSelectedItem();
						String table = Lang.getString("table", "table");
						
						TextInputDialog dialog = new TextInputDialog();
						dialog.setTitle(Lang.getString("dialog.remove.title", "Do you really want to proceed?"));
						dialog.setHeaderText(String.format(Lang.getString("dialog.remove.header", "If you delete the %s `%s`, all data will be lost."), table, item.getValue()));
						dialog.setContentText(String.format(Lang.getString("dialog.remove.content", "Enter the name of the %s to delete it:"), table));
						((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().addAll(Images.ICONS);
						
						Optional<String> result = dialog.showAndWait();
						if (result.get().equals(getSelectionModel().getSelectedItem().getValue())) {
							DBManager.getInstance().getDB(item.getParent().getValue()).rmTable(item.getValue());
							refresh(item.getParent().getValue());
						} else {
							Alert a = new Alert(AlertType.INFORMATION);
							a.setHeaderText(Lang.getString("dialog.remove.cancel.header", "Operation was terminated."));
							a.setContentText(String.format(Lang.getString("dialog.remove.cancel.content", "The entered name doesn't equal the name of the %s."), table));
							((Stage) a.getDialogPane().getScene().getWindow()).getIcons().addAll(Images.ICONS);
							a.show();
						}
					} catch (SQLException e) {
						FXUtil.showErrorLog(e);
					}
				}
			});
			getItems().addAll(editTable, removeTable);
		}
		
	}
	
}
