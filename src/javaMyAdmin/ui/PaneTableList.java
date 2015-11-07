package javaMyAdmin.ui;

import java.sql.SQLException;

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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
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
									Database db = Frame.getDbManager().getDB(getTreeItem().getParent().getValue());
									
									if (db != null) {
										if (db.getDbname().equals(getTreeItem().getParent().getValue())) {
											Frame.getInstance().getTableContentPane().refresh(db.getTable(getTreeItem().getValue()));
										}
									}
								} catch (SQLException e) {
									e.printStackTrace();
								}
								Frame.getInstance().getToolbarPane().setTableSQL(getTreeItem().getParent().getValue(), getTreeItem().getValue());
							} else if (FXUtil.getLayer(getTreeItem()) == databaseLayer) {
								Frame.getInstance().getToolbarPane().setDatabaseSQL(getTreeItem().getValue());
							} else {
								Frame.getInstance().getToolbarPane().setServerSQL();
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
		
		try {
			for (Database db : Frame.getDbManager().getDB()) {
				if (db.getDbname().equals("information_schema")) {
					continue;
					// TODO remove DEBUG
				}
				
				TreeItem<String> name = new TreeItem<String>(db.getDbname());
				name.setGraphic(new ImageView(Images.DATABASE));
				
				try {
					for (Table table : db.getTable()) {
						TreeItem<String> item = new TreeItem<String>(table.getName());
						item.setGraphic(new ImageView(Images.TABLE));
						name.getChildren().add(item);
					}
				} catch (SQLException e) {
					FXUtil.showErrorLog(new SQLException("Error while loading tables for " + db.getDbname(), e));
				}
				
				root.getChildren().add(name);
			}
		} catch (SQLException e) {
			FXUtil.showErrorLog(e);
		}
		
		root.setGraphic(new ImageView(Images.CONNECTION));
		root.setExpanded(true);
		setRoot(root);
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
								Frame.getDbManager().addDB(input.getText());
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
			MenuItem removeDatabase = new MenuItem(Lang.getString("database.remove", "Remove database"));
			removeDatabase.setGraphic(new ImageView(Images.DATABASE_REMOVE));
			removeDatabase.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					try {
						Alert a = new Alert(AlertType.CONFIRMATION);
						a.setHeaderText(Lang.getString("dialog.remove.header", "Do you really want to proceed?"));
						a.setContentText(String.format(Lang.getString("dialog.remove.content", "If you delete the %s `%s`, all data will be lost."), Lang.getString("database", "database"),
								getSelectionModel().getSelectedItem().getValue()));
						((Stage) a.getDialogPane().getScene().getWindow()).getIcons().addAll(Images.ICONS);
						if (a.showAndWait().get() == ButtonType.OK) {
							Frame.getDbManager().rmDB(getSelectionModel().getSelectedItem().getValue());
							refresh();
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
								Database database = Frame.getDbManager().getDB(db);
								if (database != null) {
									database.addTable(getTableName().getText(), getTitles(), getDatatypes(), getLength(), getDefaultNull(), getIndices());
								} else {
									throw new RuntimeException("Database `" + db + "` doesn't exists");
								}
							} catch (Exception e) {
								FXUtil.showErrorLog(e);
								return false;
							}
							
							refresh();
							return true;
						}
					}.show();
				}
			});
			
			getItems().addAll(removeDatabase, new SeparatorMenuItem(), addTable);
		}
	}
	
	/**
	 * ContextMenu, wenn eine Tabelle selektiert ist
	 */
	private class TableItemContextMenu extends DatabaseItemContextMenu {
		
		public TableItemContextMenu() {
			MenuItem removeTable = new MenuItem(Lang.getString("table.remove", "Remove table"));
			removeTable.setGraphic(new ImageView(Images.TABLE_REMOVE));
			removeTable.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					try {
						TreeItem<String> item = getSelectionModel().getSelectedItem();
						Alert a = new Alert(AlertType.CONFIRMATION);
						a.setHeaderText(Lang.getString("dialog.remove.header", "Do you really want to proceed?"));
						a.setContentText(
								String.format(Lang.getString("dialog.remove.content", "If you delete the %s `%s`, all data will be lost."), Lang.getString("table", "table"), item.getValue()));
						((Stage) a.getDialogPane().getScene().getWindow()).getIcons().addAll(Images.ICONS);
						if (a.showAndWait().get() == ButtonType.OK) {
							Frame.getDbManager().getDB(item.getParent().getValue()).rmTable(item.getValue());
							refresh();
						}
					} catch (SQLException e) {
						FXUtil.showErrorLog(e);
					}
				}
			});
			getItems().addAll(removeTable);
		}
		
	}
	
}
