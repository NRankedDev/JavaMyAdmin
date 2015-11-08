package javaMyAdmin.ui.dialogs;

import java.sql.SQLException;

import javaMyAdmin.db.DBManager;
import javaMyAdmin.ui.dialogs.util.OptionDialog;
import javaMyAdmin.util.Config;
import javaMyAdmin.util.FXUtil;
import javaMyAdmin.util.Lang;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * LoginDialog, der benoetigt wird, um eine Verbindung mit dem Server und den
 * Datenbanken herzustellen
 * 
 * @author Nicolas
 */
public class DialogLogin extends OptionDialog {
	
	private TextField url;
	private TextField username;
	private PasswordField password;
	private CheckBox remember;
	
	public DialogLogin() {
		super(Lang.getString("dialog.connect.title", "Connect to server"), Lang.getString("dialog.connect", "Connect"));
		show();
	}
	
	@Override
	protected void initGrid(GridPane grid) {
		url = new TextField(Config.getInstance().getProperty("url", ""));
		username = new TextField(Config.getInstance().getProperty("username", ""));
		password = new PasswordField();
		password.setText(Config.getInstance().getProperty("password", ""));
		remember = new CheckBox();
		remember.setSelected(!url.getText().isEmpty());
		grid.addRow(0, new Label(Lang.getString("dialog.connect.url", "URL")), url);
		grid.addRow(1, new Label(Lang.getString("dialog.connect.username", "Username")), username);
		grid.addRow(2, new Label(Lang.getString("dialog.connect.password", "Password")), password);
		grid.addRow(3, new Label(Lang.getString("dialog.connect.remember", "Remember login")), remember);
	}
	
	@Override
	protected boolean handle() {
		return true;
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
					DBManager.getInstance().connect(url.getText(), username.getText(), password.getText());
					if (remember.isSelected()) {
						Config.getInstance().set("url", url.getText());
						Config.getInstance().set("username", username.getText());
						Config.getInstance().set("password", password.getText());
					} else {
						Config.getInstance().remove("url");
						Config.getInstance().remove("username");
						Config.getInstance().remove("password");
					}
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							try {
								DBManager.getInstance().getDB();
							} catch (SQLException e) {
								throw new RuntimeException(e);
							}
							hideDialog();
						}
					});
				} catch (final Exception e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							FXUtil.showErrorLog(e);
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
