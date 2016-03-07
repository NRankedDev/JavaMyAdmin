package javaMyAdmin.ui.dialogs;

import java.sql.SQLException;

import javaMyAdmin.db.DBManager;
import javaMyAdmin.db.Database;
import javaMyAdmin.util.Config;
import javaMyAdmin.util.ui.FXUtil;
import javaMyAdmin.util.ui.Lang;
import javaMyAdmin.util.ui.OptionDialog;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;

/**
 * LoginDialog, der benoetigt wird, um eine Verbindung mit dem Server und den
 * Datenbanken herzustellen
 * 
 * @author Nicolas
 */
public class LoginDialog extends OptionDialog {
	
	private LoginDialogState state;
	private TextField url;
	private TextField username;
	private PasswordField password;
	private CheckBox remember;
	private boolean exit;
	
	public LoginDialog() {
		super("", Lang.getString("dialog.connect"), Lang.getString("dialog.connect.quit"));
		show();
	}
	
	public void setState(LoginDialogState state) {
		this.state = state;
		dialogStage.setTitle(state.getTitleText());
		
		if(state.isDisabled()) {
			url.setDisable(true);
			username.setDisable(true);
			password.setDisable(true);
			remember.setDisable(true);
			okButton.setDisable(true);
		} else {
			url.setDisable(false);
			username.setDisable(false);
			password.setDisable(false);
			remember.setDisable(false);
			okButton.setDisable(false);
		}
	}
	
	public LoginDialogState getState() {
		return state;
	}
	
	@Override
	protected void initGrid(GridPane grid) {
		url = new TextField(Config.getInstance().getProperty("url", ""));
		username = new TextField(Config.getInstance().getProperty("username", ""));
		password = new PasswordField();
		password.setText(Config.getInstance().getProperty("password", ""));
		remember = new CheckBox();
		remember.setSelected(!url.getText().isEmpty());
		grid.addRow(0, new Label(Lang.getString("dialog.connect.url")), url);
		grid.addRow(1, new Label(Lang.getString("dialog.connect.username")), username);
		grid.addRow(2, new Label(Lang.getString("dialog.connect.password")), password);
		grid.addRow(3, new Label(Lang.getString("dialog.connect.remember")), remember);
		
		setState(LoginDialogState.READY);
	}
	
	@Override
	protected boolean handle() {
		return false;
	}
	
	@Override
	protected void onOkButtonPressed(ActionEvent event) {
		setState(LoginDialogState.CONNECTING);
		
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
							setState(LoginDialogState.LOADING_DATABASES);
							
							/* Loading databases */
							try {
								for (Database db : DBManager.getInstance().getDB()) {
									try {
										db.getTable();
									} catch (SQLException e) {
										FXUtil.showErrorLog(e);
									}
								}
							} catch (SQLException e) {
								FXUtil.showErrorLog(e);
							}
							
							hideDialog();
						}
					});
				} catch(final Exception e) { 
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							setState(LoginDialogState.READY);
					
							if(e instanceof SQLException && ((SQLException) e).getErrorCode() == 1045) {
								Alert a = new Alert(AlertType.ERROR);
								a.setHeaderText(Lang.getString("dialog.connect.access_denied.header"));
								a.setContentText(Lang.getString("dialog.connect.access_denied.content"));
								a.showAndWait();
							} else {
								FXUtil.showErrorLog(e);
							}
						}
					});
				}
			}
		}.start();
	}
	
	@Override
	protected void onCancelButtonPressed(ActionEvent event) {
		super.onCancelButtonPressed(event);
		exit = true;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Platform.exit();
				System.exit(0);
			}
		});
	}
	
	public boolean getExit() {
		return exit;
	}
}
