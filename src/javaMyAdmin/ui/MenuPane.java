package javaMyAdmin.ui;

import java.util.Locale;

import javaMyAdmin.util.ui.Lang;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCombination;

/**
 * Repraesentiert das Menue am oberen Bildschirmrand
 * 
 * @author Nicolas
 */
public class MenuPane extends MenuBar {
	
	public MenuPane() {
		getMenus().addAll(new File(), new Settings());
	}
	
	private class File extends Menu {
		
		public File() {
			super(Lang.getString("menu.file"));
			
			MenuItem exit = new MenuItem(Lang.getString("menu.file.exit"));
			exit.setAccelerator(KeyCombination.keyCombination("ALT+F4"));
			exit.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					Platform.exit();
					System.exit(0);
				}
			});
			
			getItems().addAll(exit);
		}
		
	}
	
	private class Settings extends Menu {
		
		public Settings() {
			super(Lang.getString("menu.settings"));
			
			Menu languages = new Menu(Lang.getString("menu.settings.language"));
			ToggleGroup languagesToggleGroup = new ToggleGroup();
			for (Locale locale : Lang.availableLanguages()) {
				RadioMenuItem item = new RadioMenuItem(locale.getDisplayLanguage(locale));
				item.setToggleGroup(languagesToggleGroup);
				
				if (Lang.getLocale().equals(locale)) {
					item.setSelected(true);
				}
				
				languages.getItems().add(item);
			}
			
			languagesToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
				
				@Override
				public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
					if (newValue != null) {
						RadioMenuItem item = (RadioMenuItem) newValue;
						for (Locale locale : Lang.availableLanguages()) {
							if (locale.getDisplayLanguage(locale).equals(item.getText())) {
								Lang.changeLocale(locale);
								
								Alert a = new Alert(AlertType.WARNING);
								a.setHeaderText(Lang.getString("menu.settings.language.header"));
								a.setContentText(Lang.getString("menu.settings.language.content"));
								a.showAndWait();
							}
						}
					}
				}
			});
			
			getItems().addAll(languages);
		}
	}
}
