package javaMyAdmin.ui.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 * Erweiterung eines {@link GridPane}, welches das Grid neu erstellen kann. Bei
 * dieser Operation können eigene Aenderungen vorgenommen werden.
 * 
 * @see #recreateGrid(GridOperation)
 * @see GridOperation
 * @author Nicolas
 */
public class ExtendedGridPane {

	private GridPane grid;

	public ExtendedGridPane() {
		this(new GridPane());
	}

	public ExtendedGridPane(GridPane grid) {
		this.grid = grid;
	}

	public void setGrid(GridPane grid) {
		this.grid = grid == null ? new GridPane() : grid;
	}

	public GridPane getGrid() {
		return grid;
	}

	public ObservableList<Node> getChildren() {
		return grid.getChildren();
	}

	public ObservableList<Node> getChildrenCopy() {
		return FXCollections.observableArrayList(grid.getChildren());
	}

	public int getRowCount() {
		int rows = 0;
		for (Node node : grid.getChildren()) {
			if (node.isManaged()) {
				Integer rowIndex = GridPane.getRowIndex(node);
				if (rowIndex != null) {
					rows = Math.max(rows, rowIndex + 1);
				}
			}
		}
		return rows;
	}

	public int getColumnCount() {
		int columns = 0;
		for (Node node : grid.getChildren()) {
			if (node.isManaged()) {
				Integer columnIndex = GridPane.getColumnIndex(node);
				if (columnIndex != null) {
					columns = Math.max(columns, columnIndex + 1);
				}
			}
		}
		return columns;
	}

	public void addRow(final Node... nodes) {
		if (getColumnCount() > 0 && nodes.length != getColumnCount()) {
			throw new IllegalArgumentException(getColumnCount() + " != " + nodes.length);
		}

		recreateGrid(new GridOperation() {
			@Override
			public void onPreRecreate(ObservableList<Node> oldItems) {
			}

			@Override
			public void onPostRecreate() {
				grid.addRow(getRowCount(), nodes);
			}
		}, nodes.length);
	}

	/**
	 * Erstellt das Grid neu.
	 * 
	 * @param gridOperation
	 *            Listener, bei dem Aenderungen am Grid vorgenommen werden
	 *            koennen.
	 */
	public void recreateGrid(GridOperation gridOperation) {
		recreateGrid(gridOperation, getColumnCount());
	}

	/**
	 * Erstellt das Grid neu.
	 * 
	 * @param gridOperation
	 *            Listener, bei dem Aenderungen am Grid vorgenommen werden
	 *            koennen.
	 * @param newColumnCount
	 *            Neu Anzahl an Spalten, die erstellt werden sollen.
	 */
	public void recreateGrid(GridOperation gridOperation, int newColumnCount) {
		if (gridOperation == null) {
			gridOperation = new GridOperation() {
				@Override
				public void onPreRecreate(ObservableList<Node> oldItems) {
				}

				@Override
				public void onPostRecreate() {
				}
			};
		}

		ObservableList<Node> oldItems = getChildrenCopy();

		gridOperation.onPreRecreate(oldItems);

		if (oldItems.size() % newColumnCount != 0) {
			throw new IllegalArgumentException(oldItems.size() + " % " + newColumnCount + " != 0");
		}

		grid = new GridPane();
		grid.setPadding(new Insets(10));
		grid.setHgap(10);
		grid.setVgap(10);

		int newRowCount = oldItems.size() / (newColumnCount <= 0 ? 1 : newColumnCount);

		for (int row = 0; row < newRowCount; row++) {
			Node[] nodesInRow = new Node[newColumnCount];

			for (int column = 0; column < newColumnCount; column++) {
				nodesInRow[column] = oldItems.get(row * newColumnCount + column);
			}

			grid.addRow(row, nodesInRow);
		}

		gridOperation.onPostRecreate();
	}

	/**
	 * Eigenes Listener Interface, dessen Methoden bei
	 * {@link ExtendedGridPane#recreateGrid(GridOperation)} aufgerufen werden.
	 * 
	 * @author Nicolas
	 */
	public static interface GridOperation {

		/**
		 * Aufruf vor dem Neuerstellen des Grids.
		 * 
		 * @param oldItems
		 *            Die Items, die vom alten Grid in das neue Grid uebernommen
		 *            werden. Kann bearbeitet werden, wenn zum Beispiel Items
		 *            geloescht werden sollen.
		 */
		void onPreRecreate(ObservableList<Node> oldItems);

		/**
		 * Aufruf nach dem Neuerstellen des Grids.
		 */
		void onPostRecreate();

	}

}
