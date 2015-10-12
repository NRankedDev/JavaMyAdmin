package javaMyAdmin.ui;

import java.sql.SQLException;

import javaMyAdmin.db.DBManager;
import javaMyAdmin.ui.util.Lang;
import javaMyAdmin.ui.util.OptionDialog;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * LoginDialog, der benötigt wird, um eine Verbindung mit dem Server und den
 * Datenbanken herzustellen
 * 
 * @author Nicolas
 */
public class LoginDialog extends OptionDialog {

	private TextField url;
	private TextField username;
	private PasswordField password;
	private CheckBox remember;

	public LoginDialog() {
		super(Lang.getString("dialog.connect.title", "Connect to server"), Lang.getString("dialog.connect", "Connect"));
		show();
	}

	@Override
	protected void initGrid(GridPane grid) {
		url = new TextField(Frame.CONFIG.getProperty("url", ""));
		username = new TextField(Frame.CONFIG.getProperty("username", ""));
		password = new PasswordField();
		password.setText(Frame.CONFIG.getProperty("password", ""));
		remember = new CheckBox();
		remember.setSelected(!url.getText().isEmpty());
		grid.addRow(0, new Label(Lang.getString("dialog.connect.url", "URL")), url);
		grid.addRow(1, new Label(Lang.getString("dialog.connect.username", "Username")), username);
		grid.addRow(2, new Label(Lang.getString("dialog.connect.password", "Password")), password);
		grid.addRow(3, new Label(Lang.getString("dialog.connect.remember", "Remember login")), remember);
	}

	@Override
	protected void handle() {
	}

	@Override
	protected void onOkButtonPressed(ActionEvent event) {
		url.setDisable(true);
		username.setDisable(true);
		password.setDisable(true);
		remember.setDisable(true);
		okButton.setDisable(true);
		dialogStage.setTitle(Lang.getString("dialog.connect.connecting", "Connecting..."));

		new Thread() {
			@Override
			public void run() {
				try {
					Frame.setDbManager(new DBManager(url.getText(), username.getText(), password.getText()));
					if (remember.isSelected()) {
						Frame.CONFIG.set("url", url.getText());
						Frame.CONFIG.set("username", username.getText());
						Frame.CONFIG.set("password", password.getText());
					} else {
						Frame.CONFIG.remove("url");
						Frame.CONFIG.remove("username");
						Frame.CONFIG.remove("password");
					}
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							try {
								Frame.getDbManager().getDB();
							} catch (SQLException e) {
								throw new RuntimeException(e);
							}
							hideDialog();
						}
					});
				} catch (Exception e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							e.printStackTrace();
							Alert a = new Alert(AlertType.ERROR);
							a.setHeaderText(Lang.getString("dialog.connect.error.header", "Couldn't connect to database."));
							a.setContentText(Lang.getString("dialog.connect.error.content", "Please check the connection to the database."));
							a.showAndWait();
							Platform.exit();
							System.exit(0);
						}
					});
				}
			}
		}.start();
	}

	@Override
	protected void onCancelButtonPressed(ActionEvent event) {
		super.onCancelButtonPressed(event);
		Platform.exit();
	}
}
