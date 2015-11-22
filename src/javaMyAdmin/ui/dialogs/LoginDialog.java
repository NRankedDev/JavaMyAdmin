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
public class LoginDialog extends OptionDialog {
	
	private TextField url;
	private TextField username;
	private PasswordField password;
	private CheckBox remember;
	private boolean exit;
	
	public LoginDialog() {
		super(Lang.getString("dialog.connect.title"), Lang.getString("dialog.connect"), Lang.getString("dialog.connect.quit"));
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
		grid.addRow(0, new Label(Lang.getString("dialog.connect.url")), url);
		grid.addRow(1, new Label(Lang.getString("dialog.connect.username")), username);
		grid.addRow(2, new Label(Lang.getString("dialog.connect.password")), password);
		grid.addRow(3, new Label(Lang.getString("dialog.connect.remember")), remember);
	}
	
	@Override
	protected boolean handle() {
		return false;
	}
	
	@Override
	protected void onOkButtonPressed(ActionEvent event) {
		url.setDisable(true);
		username.setDisable(true);
		password.setDisable(true);
		remember.setDisable(true);
		okButton.setDisable(true);
		dialogStage.setTitle(Lang.getString("dialog.connect.connecting"));
		
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
							dialogStage.setTitle(Lang.getString("dialog.connect.loading"));
							
							/* Loading databases */
							try {
								for (Database db : DBManager.getInstance().getDB()) {
									try {
										db.loadTables();
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
