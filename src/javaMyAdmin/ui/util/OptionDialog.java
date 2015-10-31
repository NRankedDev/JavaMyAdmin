package javaMyAdmin.ui.util;

import javaMyAdmin.ui.Frame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Abstrakte Klasse, die als Basis f�r alle Einstellungsdialoge gilt. Alle
 * Klassen, die von dieser erben, befinden sich im package 'javaMyAdmin.dialogs'
 * 
 * @author Nicolas
 */
public abstract class OptionDialog {

	protected static final Stage dialogStage = new DialogStage();

	private static final class DialogStage extends Stage {

		public DialogStage() {
			initModality(Modality.APPLICATION_MODAL);
			setResizable(false);
			getIcons().addAll(Frame.getIcons());
		}

	}

	private final String okButtonText;
	private final String cancelButtonText;

	protected Button okButton;
	protected Button cancelButton;

	/**
	 * 
	 * @param title
	 *            Der Fenstertitel
	 * 
	 * @see #show()
	 */
	public OptionDialog(String title) {
		this(title, Lang.getString("dialog.ok", "OK"));
	}

	/**
	 * 
	 * @param title
	 *            Der Fenstertitel
	 * @param okButtonText
	 *            Der Titel des 'OK'-Buttons, wenn anderer Titel ben�tigt
	 * 
	 * @see #show()
	 */
	public OptionDialog(String title, String okButtonText) {
		this(title, okButtonText, Lang.getString("dialog.cancel", "Cancel"));
	}

	/**
	 * 
	 * @param title
	 *            Der Fenstertitel
	 * @param okButtonText
	 *            Der Titel des 'OK'-Buttons, wenn anderer Titel ben�tigt
	 * @param cancelButtonText
	 *            Der Titel des 'Cancel'-Buttons, wenn anderer Titel ben�tigt
	 * 
	 * @see #show()
	 */
	public OptionDialog(String title, String okButtonText, String cancelButtonText) {
		this.okButtonText = okButtonText;
		this.cancelButtonText = cancelButtonText;
		if (dialogStage.isShowing()) {
			dialogStage.hide();
		}
		dialogStage.setTitle(title);
	}

	/**
	 * Zeigt das Fenster an
	 */
	public void show() {
		BorderPane root = new BorderPane();
		init(root);
		dialogStage.setScene(new Scene(root));
		dialogStage.sizeToScene();
		dialogStage.showAndWait();
		dialogStage.toFront();
	}

	protected void init(BorderPane root) {
		/*
		 * Initalisierung des Fensters. Wird erst ausgef�hrt, wenn das Fenster
		 * gezeigt werden soll (von der Methode show())
		 */
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10));
		grid.setHgap(10);
		grid.setVgap(10);

		initGrid(grid);

		HBox box = new HBox();
		box.setPadding(new Insets(10, 10, 10, 0));
		box.setAlignment(Pos.CENTER_RIGHT);

		okButton = new Button(okButtonText);
		okButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				onOkButtonPressed(event);
			}
		});
		cancelButton = new Button(cancelButtonText);
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				onCancelButtonPressed(event);
			}
		});
		box.getChildren().addAll(okButton, cancelButton);

		root.setTop(grid);
		root.setCenter(new Separator());
		root.setBottom(box);
		HBox.setMargin(okButton, new Insets(0, 10, 0, 0));
	}

	/**
	 * Initialisierung des Contents des Dialogs
	 * 
	 * @param grid
	 *            Das {@link GridPane}, auf dem sich alle Elemente befinden
	 *            m�ssen
	 */
	protected abstract void initGrid(GridPane grid);

	/**
	 * Wird ausgef�hrt, sobald der 'OK'-Button gedr�ckt wurde. Es wird
	 * <b>nicht</b> ausgef�hrt, wenn der 'Cancel'-Button gedr�ckt wurde. Hierzu
	 * kann die Methode {@link #onCancelButtonPressed(ActionEvent)}
	 * �berschrieben werden.
	 */
	protected abstract void handle();

	/**
	 * Wird ausgef�hrt, sobald der 'OK'-Button gedr�ckt wurde. Ruft
	 * {@link #handle()} und {@link #hideDialog()} auf.
	 * 
	 * @param event
	 */
	protected void onOkButtonPressed(ActionEvent event) {
		handle();
		hideDialog();
	}

	/**
	 * Wird ausgef�hrt, sobald der 'Cancel'-Button gedr�ckt wurde. Ruft
	 * {@link #hideDialog()} auf.
	 * 
	 * @param event
	 */
	protected void onCancelButtonPressed(ActionEvent event) {
		dialogStage.hide();
	}

	/**
	 * 
	 * @return <code>true</code> wenn der Dialog sichtbar ist
	 */
	public boolean isDialogShowing() {
		return dialogStage.isShowing();
	}

	/**
	 * Macht den Dialog unsichtbar
	 */
	public void hideDialog() {
		dialogStage.hide();
	}

	/**
	 * 
	 * @return
	 */
	public static Stage getDialogStage() {
		return dialogStage;
	}

}
