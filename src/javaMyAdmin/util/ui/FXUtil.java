package javaMyAdmin.util.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * Haeufig benutzte Methoden fuer JavaFX (GUI).
 * 
 * @author Nicolas
 */
public class FXUtil {
	
	/**
	 * Prueft, ob eine TreeItem das RootItem eines {@link TreeView} ist.
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
	
	/**
	 * Zeigt einen Fehler in einem Fenster an. Dieser Fehler wird außerdem
	 * automatisch in der Konsole ausgegeben.
	 * 
	 * @param t
	 *            Der Stacktrace des Fehlers
	 */
	public static void showErrorLog(Throwable t) {
		t.printStackTrace();
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText(Lang.getString("error.header"));
		if (!(t instanceof SQLException)) {
			alert.setContentText(Lang.getString("error.unknown"));
		} else {
			SQLException e = (SQLException) t;
			alert.setContentText(e.getErrorCode() + ": " + e.getLocalizedMessage());
		}
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String exceptionText = sw.toString();
		
		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);
		
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		
		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.addRow(1, textArea);
		
		alert.getDialogPane().setExpandableContent(expContent);
		alert.getDialogPane().setMinWidth(500);
		((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(Images.ICONS);
		alert.showAndWait();
	}
	
}
