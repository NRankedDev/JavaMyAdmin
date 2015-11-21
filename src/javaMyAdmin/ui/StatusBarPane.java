package javaMyAdmin.ui;

import java.awt.Toolkit;

import javaMyAdmin.db.DBManager;
import javaMyAdmin.db.Table;
import javaMyAdmin.ui.TableContentPane.TableRecord;
import javaMyAdmin.util.ui.Lang;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class StatusBarPane extends BorderPane {
	
	private int searchIndex;
	private Label tableInfo;
	private Label readOnly;
	
	public StatusBarPane() {
		HBox left = new HBox();
		
		left.getChildren().addAll(new Label(String.format(Lang.getString("status.connected"), DBManager.getInstance().getUrl())));
		
		HBox right = new HBox();
		
		final TextField search = new TextField();
		search.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				searchIndex = 0;
			}
		});
		
		final Button searchButton = new Button(Lang.getString("status.table.search"));
		searchButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				TableContentPane pane = Frame.getInstance().getTableContentPane();
				ObservableList<TableRecord> rows = pane.getItems();
				
				if (rows.size() == 0) {
					Toolkit.getDefaultToolkit().beep();
					return;
				}
				
				int indexFound = 0;
				
				for (int i = 0; i < rows.size(); i++) {
					for (SimpleStringProperty ssp : rows.get(i).getData().values()) {
						if (ssp.get().toLowerCase().contains(search.getText().toLowerCase())) {
							if (searchIndex == indexFound++) {
								searchIndex++;
								pane.scrollTo(i);
								pane.getSelectionModel().select(i);
								return;
							}
						}
					}
				}
				
				pane.getSelectionModel().clearSelection();
				Toolkit.getDefaultToolkit().beep();
				searchIndex = 0;
			}
		});
		
		right.getChildren().addAll(search, searchButton, tableInfo = new Label(), readOnly = new Label());
		HBox.setMargin(searchButton, new Insets(0, 20, 0, 10));
		readOnly.setTextFill(Color.RED);
		
		setLeft(left);
		setRight(right);
		BorderPane.setMargin(left, new Insets(5));
		BorderPane.setMargin(right, new Insets(5));
		
		setTableInfo(null);
	}
	
	public void setTableInfo(Table table) {
		readOnly.setText("");
		
		if (table == null) {
			tableInfo.setText(Lang.getString("status.table.none"));
			readOnly.setVisible(false);
		} else {
			tableInfo.setText(String.format(Lang.getString("status.table.selected"), table.getName()));
			
			if (table.getAbstract()) {
				readOnly.setText(" " + Lang.getString("status.table.read_only"));
				readOnly.setVisible(true);
			} else {
				readOnly.setVisible(false);
			}
		}
	}
}
