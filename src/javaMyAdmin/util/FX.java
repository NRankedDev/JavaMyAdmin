package javaMyAdmin.util;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class FX {
	
	/**
	 * Prüft, ob eine TreeItem das RootItem eines {@link TreeView} ist.
	 * 
	 * @param item
	 * @return
	 */
	public static boolean isRoot(TreeItem<?> item) {
		return getLayer(item) == 0;
	}
	
	/**
	 * Liefert die Schicht eines TreeItem innerhalb eines {@link TreeView}. Die
	 * Schicht beginnt beim Root Item mit 0.
	 * 
	 * @param item
	 * @return
	 */
	public static int getLayer(TreeItem<?> item) {
		if (item == null) {
			throw new IllegalArgumentException("item is null");
		}
		
		int layer = 0;
		
		while ((item = item.getParent()) != null) {
			layer++;
		}
		
		return layer;
	}
	
}
