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
	
	public StringInputDialog(String frameTitle, String inputDescr) {
		super(frameTitle);
		this.inputDescr = inputDescr;
	}
	
	@Override
	protected void initGrid(GridPane grid) {
		this.input = new TextField();
		
		grid.addRow(0, new Label(inputDescr), input);
	}
	
}
