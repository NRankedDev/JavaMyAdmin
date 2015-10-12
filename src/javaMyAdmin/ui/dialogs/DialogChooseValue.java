package javaMyAdmin.ui.dialogs;

import java.util.Arrays;
import java.util.List;

import javaMyAdmin.ui.util.OptionDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public abstract class DialogChooseValue extends OptionDialog {

	private final List<String> values;
	private final String inputDescr;

	protected ComboBox<String> comboBox;

	public DialogChooseValue(String title, String inputDescr, String... values) {
		this(title, inputDescr, Arrays.asList(values));
	}

	public DialogChooseValue(String title, String inputDescr, List<String> values) {
		super(title);
		this.inputDescr = inputDescr;
		this.values = values;
	}

	@Override
	protected void initGrid(GridPane grid) {
		comboBox = new ComboBox<String>();
		comboBox.getItems().addAll(values);
		comboBox.getSelectionModel().selectFirst();

		grid.addRow(0, new Label(inputDescr), comboBox);
	}

}
