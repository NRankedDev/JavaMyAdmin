package javaMyAdmin.ui.dialogs.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 * Erweiterung eines {@link GridPane}, welches das Grid neu erstellen kann. Bei
 * dieser Operation koennen eigene Aenderungen vorgenommen werden.
 * 
 * @see #recreateGrid(GridOperation)
 * @see GridOperation
 * @author Nicolas
 */
public class ExtendedGridPane {
	
	private GridPane grid;
	
	/**
	 * Erstellt eine neue Instanz mit einem leeren Grid.
	 */
	public ExtendedGridPane() {
		this(new GridPane());
	}
	
	/**
	 * Erstellt eine neue Instanz mit einem Grid, das genutzt werden soll. Wenn
	 * grid <code>null</code> ist, wird ein leeres {@link GridPane} erstellt.
	 * 
	 * @param grid
	 */
	public ExtendedGridPane(GridPane grid) {
		setGrid(grid);
	}
	
	/**
	 * Ersetzt das Gird, das genutzt werden soll. Wenn grid <code>null</code>
	 * ist, wird ein leeres {@link GridPane} erstellt.
	 * 
	 * @param grid
	 */
	public void setGrid(GridPane grid) {
		this.grid = grid == null ? new GridPane() : grid;
	}
	
	/**
	 * Aktuell genutztes Grid.
	 * 
	 * @return
	 */
	public GridPane getGrid() {
		return grid;
	}
	
	/**
	 * @return Alle {@link Node}s, die das aktuelle Grid benutzt
	 * @see #getGrid()
	 * @see GridPane#getChildren()
	 */
	public ObservableList<Node> getChildren() {
		return grid.getChildren();
	}
	
	/**
	 * @return Eine Kopie aller {@link Node}s, die das aktuelle Grid benutzt.
	 *         Kann zum Bearbeiten genutzt werden, ohne das Grid zu veraendern.
	 * @see #getGrid()
	 * @see GridPane#getChildren()
	 */
	public ObservableList<Node> getChildrenCopy() {
		return FXCollections.observableArrayList(grid.getChildren());
	}
	
	/**
	 * @return Liefert die Anzahl aller Rows, die das aktuelle Grid besitzt
	 * @see #getGrid()
	 */
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
	
	/**
	 * @return Liefert die Anzahl aller Columns, die das aktuelle Grid besitzt
	 * @see #getGrid()
	 */
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
	
	/**
	 * Fuegt dem aktuellen Grid eine Row hinzu
	 * 
	 * @param nodes
	 *            Die Elemente, die in einer Reihe sein sollen. Die Laenge muss
	 *            mit der von {@link #getColumnCount()} uebereinstimmen.
	 */
	public void addRow(final Node... nodes) {
		addRow(null, nodes);
	}
	
	/**
	 * Fuegt dem aktuellen Grid eine Row hinzu
	 * 
	 * @param customOperation
	 *            Eigene GridOperation, um weite Aenderungen vorzunehmen.
	 * @param nodes
	 *            Die Elemente, die in einer Reihe sein sollen. Die Laenge muss
	 *            mit der von {@link #getColumnCount()} uebereinstimmen.
	 */
	public void addRow(final GridOperation customOperation, final Node... nodes) {
		if (getColumnCount() > 0 && nodes.length != getColumnCount()) {
			throw new IllegalArgumentException(getColumnCount() + " != " + nodes.length);
		}
		
		recreateGrid(new GridOperation() {
			@Override
			public void onPreRecreate(ObservableList<Node> oldItems) {
				customOperation.onPreRecreate(oldItems);
			}
			
			@Override
			public void onPostRecreate() {
				grid.addRow(getRowCount(), nodes);
				customOperation.onPostRecreate();
			}
		}, nodes.length);
	}
	
	/**
	 * Loescht eine Row aus dem Grid.
	 * 
	 * @param rowIndex
	 */
	public void removeRow(int rowIndex) {
		removeRow(null, rowIndex);
	}
	
	/**
	 * Loescht eine Row aus dem Grid.
	 * 
	 * @param customOperation
	 *            Eigene GridOperation, um weite Aenderungen vorzunehmen.
	 * @param rowIndex
	 */
	public void removeRow(final GridOperation customOperation, final int rowIndex) {
		if (rowIndex >= 0 && rowIndex < getRowCount()) {
			recreateGrid(new GridOperation() {
				@Override
				public void onPreRecreate(ObservableList<Node> oldItems) {
					oldItems.remove(rowIndex * getColumnCount(), rowIndex * getColumnCount() + getColumnCount());
					customOperation.onPreRecreate(oldItems);
				}
				
				@Override
				public void onPostRecreate() {
					customOperation.onPostRecreate();
				}
			});
		}
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
