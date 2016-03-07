package javaMyAdmin.ui.dialogs;

import javaMyAdmin.util.ui.Lang;

public enum LoginDialogState {

	READY(false, Lang.getString("dialog.connect.title")),
	CONNECTING(true, Lang.getString("dialog.connect.connecting")),
	LOADING_DATABASES(true, Lang.getString("dialog.connect.loading"));
	
	private boolean disabled;
	private String titleText;
	
	private LoginDialogState(boolean disabled, String titleText) {
		this.disabled = disabled;
		this.titleText = titleText;
	}
	
	public String getTitleText() {
		return titleText;
	}
	
	public boolean isDisabled() {
		return disabled;
	}
	
}
