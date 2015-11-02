package javaMyAdmin.ui.dialogs.util;

import javaMyAdmin.ui.util.ExtendedGridPane;
import javaMyAdmin.ui.util.ExtendedGridPane.GridOperation;
import javaMyAdmin.ui.util.Lang;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public abstract class DialogDynamicRows extends OptionDialog {

	protected final ExtendedGridPane grid = new ExtendedGridPane();
	protected BorderPane layout;
	protected GridPane top;
	protected ScrollPane bottom;

	public DialogDynamicRows(String title) {
		super(title);
	}

	public DialogDynamicRows(String title, String okButtonText) {
		super(title, okButtonText);
	}

	public DialogDynamicRows(String title, String okButtonText, String cancelButtonText) {
		super(title, okButtonText, cancelButtonText);
	}

	@Override
	protected void init(BorderPane root) {
		layout = new BorderPane();
		top = new GridPane();
		bottom = new ScrollPane();
		super.init(root);

		top.setPadding(new Insets(10));
		top.setHgap(10);
		top.setVgap(10);

		bottom.setFocusTraversable(false);
		bottom.setMaxHeight(500);

		layout.setTop(top);
		layout.setBottom(bottom);
		root.setTop(layout);
	}

	@Override
	protected void initGrid(GridPane grid) {
		this.grid.setGrid(grid);
	}

	public void addDynamicRow(final Node... nodes) {
		final TextField indexField = new TextField();
		indexField.setDisable(true);
		indexField.setMaxWidth(33);

		final Button remove = new Button("x");
		remove.setTooltip(new Tooltip(Lang.getString("table.edit.remove", "Remove column")));
		remove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (grid.getRowCount() == 1) {
					return;
				}

				onRemoveButtonPressed(remove, Integer.parseInt(indexField.getText()) - 1);
			}
		});

		final Button add = new Button("+");
		add.setTooltip(new Tooltip(Lang.getString("table.edit.add", "Add column")));
		add.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				onAddButtonPressed(add);
			}
		});

		final ObservableList<Node> totalNodes = FXCollections.observableArrayList();
		totalNodes.addAll(indexField);
		totalNodes.addAll(nodes);
		totalNodes.addAll(remove, add);

		grid.recreateGrid(new GridOperation() {

			@Override
			public void onPreRecreate(ObservableList<Node> oldItems) {
			}

			@Override
			public void onPostRecreate() {
				grid.getGrid().addRow(grid.getRowCount(), totalNodes.toArray(new Node[totalNodes.size()]));

				for (int i = 0; i < grid.getChildren().size(); i++) {
					if (i % grid.getColumnCount() == 0) {
						// Indices der Reihen anpassen
						((TextField) grid.getChildren().get(i)).setText(String.valueOf((i / grid.getColumnCount()) + 1));
					} else if (i % grid.getColumnCount() == grid.getColumnCount() - 1) {
						if (i == grid.getChildren().size() - 1) {
							// Letzten "Add" Button sichtbar machen
							grid.getChildren().get(i).setVisible(true);
						} else {
							// Alle anderen "Add" Button unsichtbar machen
							grid.getChildren().get(i).setVisible(false);
						}
					}
				}
			}
		}, totalNodes.size());
		bottom.setContent(grid.getGrid());
		dialogStage.sizeToScene();
	}

	public void removeDynamicRow(final int index) {
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

	protected void onAddButtonPressed(Button addButton) {
		addButton.setVisible(false);
	}

	protected void onRemoveButtonPressed(Button removeButton, int index) {
		removeDynamicRow(index);
		bottom.setContent(grid.getGrid());
		dialogStage.sizeToScene();
	}

}
