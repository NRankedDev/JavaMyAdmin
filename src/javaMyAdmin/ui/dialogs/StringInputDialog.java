package javaMyAdmin.ui.dialogs;

import javaMyAdmin.util.ui.OptionDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Dialog, bei dem man einen String eingeben kann.<br>
 * Verwendung fuer AddDatabase.
 * 
 * @author Nicolas
 */
public abstract class StringInputDialog extends OptionDialog {
	
	protected TextField input;
	protected String inputDescr;
	protected String defaultValue;
	
	public StringInputDialog(String frameTitle, String inputDescr) {
		this(frameTitle, inputDescr, null);
	}
	
	public StringInputDialog(String frameTitle, String inputDescr, String defaultValue) {
		super(frameTitle);
		this.inputDescr = inputDescr;
		this.defaultValue = defaultValue == null ? "" : defaultValue;
	}
	
	@Override
	protected void initGrid(GridPane grid) {
		this.input = new TextField(defaultValue);
		
		grid.addRow(0, new Label(inputDescr), input);
	}
	
}
