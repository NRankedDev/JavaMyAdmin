package javaMyAdmin.ui.dialogs.util;

import javaMyAdmin.ui.dialogs.util.ExtendedGridPane.GridOperation;
import javaMyAdmin.util.Lang;
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

/**
 * Abstrakte Klasse, mit deren Hilfe man dynamisch Rows hinzufuegen oder
 * loeschen kann.
 * 
 * @author Nicolas
 * @see #top
 * @see #addDynamicRow(Node...)
 * @see #removeDynamicRow(int)
 */
public abstract class DialogDynamicRows extends OptionDialog {
	
	/**
	 * ExtendedGridPane API
	 */
	protected final ExtendedGridPane grid = new ExtendedGridPane();
	
	/**
	 * GridPane ueber den dynamischen Reihen fuer z.B. Beschreibungen
	 */
	protected GridPane top;
	
	private BorderPane layout;
	private ScrollPane bottom;
	
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
		bottom.setMaxWidth(1250);
		bottom.setFitToHeight(true);
		
		layout.setTop(top);
		layout.setCenter(bottom);
		root.setTop(null);
		root.setCenter(layout);
	}
	
	@Override
	protected void initGrid(GridPane grid) {
		this.grid.setGrid(grid);
	}
	
	/**
	 * Fuegt eine dynamische Reihe hinzu. Ein TextFeld mit dem Reihen-Index, ein
	 * RemoveRow-Button und ein AddRow-Button werden automatisch erstellt.
	 * 
	 * @param nodes
	 *            Die Elemente, die in der Reihe stehen sollen.
	 */
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
		
		grid.addRow(new GridOperation() {
			
			@Override
			public void onPreRecreate(ObservableList<Node> oldItems) {
			}
			
			@Override
			public void onPostRecreate() {
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
		}, totalNodes.toArray(new Node[totalNodes.size()]));
		bottom.setContent(grid.getGrid());
		dialogStage.sizeToScene();
	}
	
	/**
	 * Loescht eine Reihe
	 * 
	 * @param index
	 */
	public void removeDynamicRow(final int index) {
		grid.removeRow(new GridOperation() {
			@Override
			public void onPreRecreate(ObservableList<Node> oldItems) {
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
		}, index);
	}
	
	/**
	 * Wird ausgefuehrt, wenn der AddRow-Button gedrueckt wird. Methode sollte
	 * nicht voll ueberschrieben werden.
	 * 
	 * @param addButton
	 */
	protected void onAddButtonPressed(Button addButton) {
		addButton.setVisible(false);
	}
	
	/**
	 * Wird ausgefuehrt, wenn der RemoveRow-Button gedrueckt wird. Methode
	 * sollte nicht voll ueberschrieben werden.
	 * 
	 * @param removeButton
	 * @param index
	 */
	protected void onRemoveButtonPressed(Button removeButton, int index) {
		removeDynamicRow(index);
		bottom.setContent(grid.getGrid());
		dialogStage.sizeToScene();
	}
	
}
