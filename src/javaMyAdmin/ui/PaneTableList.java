package javaMyAdmin.ui;

import java.sql.SQLException;

import javaMyAdmin.db.Database;
import javaMyAdmin.db.Table;
import javaMyAdmin.ui.dialogs.DialogEditTable;
import javaMyAdmin.ui.dialogs.DialogStringInput;
import javaMyAdmin.util.FX;
import javaMyAdmin.util.Lang;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 * Repraesentiert die aller Tabellen am linken Rand
 * 
 * @author Nicolas
 */
public class PaneTableList extends TreeView<String> {
	
	private static final int databaseLayer = 1;
	private static final int tableLayer = 2;
	
	private static final Image connectionIcon = new Image(PaneTableList.class.getResourceAsStream("/res/connection.png"));
	private static final Image databaseIcon = new Image(PaneTableList.class.getResourceAsStream("/res/database.png"));
	private static final Image dababaseAddIcon = new Image(PaneTableList.class.getResourceAsStream("/res/database_add.png"));
	private static final Image dababaseRemoveIcon = new Image(PaneTableList.class.getResourceAsStream("/res/database_remove.png"));
	
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
							if (FX.getLayer(getTreeItem()) == tableLayer) {
								try {
									Database db = Frame.getDbManager().getDB(getTreeItem().getParent().getValue());
									
									if (db != null) {
										if (db.getDbname().equals(getTreeItem().getParent().getValue())) {
											Frame.getInstance().getTableValues().refresh(db.getTable(getTreeItem().getValue()));
										}
									}
								} catch (SQLException e) {
									e.printStackTrace();
								}
								Frame.getInstance().getToolbar().setTableSQL(getTreeItem().getParent().getValue(), getTreeItem().getValue());
							} else if(FX.getLayer(getTreeItem()) == databaseLayer) {
								Frame.getInstance().getToolbar().setDatabaseSQL(getTreeItem().getValue());
							} else {
								Frame.getInstance().getToolbar().setServerSQL();
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
							
							if (empty || FX.isRoot(getTreeItem())) {
								setContextMenu(emptyContextMenu);
							} else if (FX.getLayer(getTreeItem()) == databaseLayer) {
								setContextMenu(databaseItemContextMenu);
							} else if (FX.getLayer(getTreeItem()) == tableLayer) {
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
				name.setGraphic(new ImageView(databaseIcon));
				
				try {
					for (Table table : db.getTable()) {
						name.getChildren().add(new TreeItem<String>(table.getName()));
					}
				} catch (SQLException e) {
					Frame.showErrorLog(new SQLException("Error while loading tables for " + db.getDbname(), e));
				}
				
				root.getChildren().add(name);
			}
		} catch (SQLException e) {
			Frame.showErrorLog(e);
		}
		
		root.setGraphic(new ImageView(connectionIcon));
		root.setExpanded(true);
		setRoot(root);
	}
	
	/**
	 * ContextMenu, wenn nichts selektiert ist
	 */
	private class EmptyContextMenu extends ContextMenu {
		
		public EmptyContextMenu() {
			final MenuItem addDatabase = new MenuItem(Lang.getString("database.add", "Add database"));
			addDatabase.setGraphic(new ImageView(dababaseAddIcon));
			addDatabase.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					new DialogStringInput(addDatabase.getText(), Lang.getString("database.add.name", "Name")) {
						@Override
						protected void handle() {
							try {
								Frame.getDbManager().addDB(input.getText());
								refresh();
							} catch (SQLException e) {
								Frame.showErrorLog(e);
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
			removeDatabase.setGraphic(new ImageView(dababaseRemoveIcon));
			removeDatabase.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					try {
						Frame.getDbManager().rmDB(getSelectionModel().getSelectedItem().getValue());
						refresh();
					} catch (SQLException e) {
						Frame.showErrorLog(e);
					}
				}
			});
			
			MenuItem addTable = new MenuItem(Lang.getString("table.add", "Add table"));
			addTable.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					new DialogEditTable(null) {
						@Override
						protected void handle() {
							TreeItem<String> item = getSelectionModel().getSelectedItem();
							String db = null;
							
							if (FX.getLayer(item) == databaseLayer) {
								db = item.getValue();
							} else if (FX.getLayer(item) == tableLayer) {
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
								Frame.showErrorLog(e);
							}
							
							refresh();
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
			removeTable.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					try {
						TreeItem<String> item = getSelectionModel().getSelectedItem();
						Frame.getDbManager().getDB(item.getParent().getValue()).rmTable(item.getValue());
						refresh();
					} catch (SQLException e) {
						Frame.showErrorLog(e);
					}
				}
			});
			getItems().addAll(removeTable);
		}
		
	}
	
}
