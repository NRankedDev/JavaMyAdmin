package javaMyAdmin.ui.dialogs.abstr;

import java.util.Optional;

import javaMyAdmin.util.ui.Images;
import javaMyAdmin.util.ui.Lang;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

/**
 * Dialog zum Löschen verschiedener Dinge
 * 
 * @author Nicolas
 */
public abstract class ConfirmRemoveDialog extends TextInputDialog {
	
	/**
	 * 
	 * @param object
	 *            Database oder Table
	 * @param objectName
	 *            Name der Database oder Table
	 */
	public ConfirmRemoveDialog(String object, String objectName) {
		setTitle(Lang.getString("dialog.remove.title"));
		setHeaderText(String.format(Lang.getString("dialog.remove.header"), object, objectName) + "\n" + String.format(Lang.getString("dialog.remove.header_2"), object));
		setContentText(Lang.getString("dialog.remove.name"));
		((Stage) getDialogPane().getScene().getWindow()).getIcons().addAll(Images.ICONS);
		
		Optional<String> result = showAndWait();
		if (result.isPresent()) {
			if (result.get().equals(objectName)) {
				handle();
			} else {
				Alert a = new Alert(AlertType.INFORMATION);
				a.setHeaderText(Lang.getString("dialog.remove.cancel.header"));
				a.setContentText(String.format(Lang.getString("dialog.remove.cancel.content"), object));
				((Stage) a.getDialogPane().getScene().getWindow()).getIcons().addAll(Images.ICONS);
				a.show();
			}
		}
	}
	
	protected abstract void handle();
}
