package javaMyAdmin.ui;

import java.sql.SQLException;

import javaMyAdmin.db.Database;
import javaMyAdmin.db.Table;
import javaMyAdmin.ui.dialogs.DialogEditTable;
import javaMyAdmin.ui.dialogs.DialogStringInput;
import javaMyAdmin.ui.util.Lang;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public class PaneTableList extends TreeView<String> {

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
							if (getTreeItem().isLeaf()) {
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
							}
						}
					}

					@Override
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);

						if (empty) {
							setText(null);
							setContextMenu(emptyContextMenu);
						} else {
							setText(item);

							if (getTreeItem().getParent() == null) {
								setContextMenu(emptyContextMenu);
							} else if (getTreeItem().isLeaf()) {
								setContextMenu(tableItemContextMenu);
							} else {
								setContextMenu(databaseItemContextMenu);
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
				TreeItem<String> name = new TreeItem<String>(db.getDbname());

				for (Table table : db.getTable()) {
					name.getChildren().add(new TreeItem<String>(table.getName()));
				}

				root.getChildren().add(name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		root.setExpanded(true);
		setRoot(root);
	}

	/**
	 * ContextMenu, wenn nichts selektiert ist
	 */
	private class EmptyContextMenu extends ContextMenu {

		public EmptyContextMenu() {
			MenuItem addDatabase = new MenuItem(Lang.getString("database.add", "Add database"));
			addDatabase.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					new DialogStringInput(addDatabase.getText(), Lang.getString("database.add.name", "Name")) {
						@Override
						protected void handle() {
							input.getText();
							// TODO SQL
							System.err.println("AddDatabase: TODO SQL");
							refresh();
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
			removeDatabase.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					System.out.println(getSelectionModel().getSelectedItem());
					// TODO SQL
					System.err.println("RemoveDatabase: TODO SQL");
					refresh();
				}
			});

			MenuItem addTable = new MenuItem(Lang.getString("table.add", "Add table"));
			addTable.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					new DialogEditTable(null) {
						@Override
						protected void handle() {
							getTableName().getText();

							// TODO SQL
							System.err.println("AddTable: TODO SQL");
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
					System.out.println(getSelectionModel().getSelectedItem());
					// TODO SQL
					System.err.println("RemoveTable: TODO SQL");
					refresh();
				}
			});
			getItems().addAll(removeTable);
		}

	}

}
