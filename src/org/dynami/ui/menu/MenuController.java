/*
 * Copyright 2015 Alessandro Atria - a.atria@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dynami.ui.menu;

import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.action.ActionMap;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.models.StrategyComponents;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.collectors.Strategies;
import org.dynami.ui.prefs.PreferencesController;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MenuController implements Initializable {
	@FXML MenuBar menu;
	@FXML MenuItem openMenuItem;
	@FXML MenuItem saveMenuItem;
	@FXML MenuItem saveAsMenuItem;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		saveMenuItem.setDisable(true);
		saveAsMenuItem.setDisable(true);
		ActionMap.register(this);

		Strategies.Register.selectedProperty().addListener(new ChangeListener<StrategyComponents>() {
			@Override
			public void changed(ObservableValue<? extends StrategyComponents> observable, StrategyComponents oldValue, StrategyComponents newValue) {
				if(newValue != null){
					saveMenuItem.setDisable(false);
					saveAsMenuItem.setDisable(false);
				} else {
					saveMenuItem.setDisable(true);
					saveAsMenuItem.setDisable(true);
				}
			}
		});
	}

	@FXML
	public void open(ActionEvent e){
		ActionMap.action("open").handle(e);
	}

	@FXML
	public void save(ActionEvent e){
		ActionMap.action("save").handle(e);
	}

	@FXML
	public void saveAs(ActionEvent e){
		ActionMap.action("saveas").handle(e);
	}

	@FXML
	public void preferences(ActionEvent e){
		try {
			final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/dynami/ui/prefs/Preferences.fxml"));
			final MasterDetailPane content = (MasterDetailPane)fxmlLoader.load();
			final PreferencesController controller = fxmlLoader.getController();
			final Dialog<ButtonType> dialog = new Dialog<>();
			final DialogPane pane = new DialogPane();
			dialog.setTitle("Dynami - Preferences");
			dialog.setHeaderText("Set Dynami user preferences");
			dialog.setGraphic(new ImageView(new Image("/icons/_preferences.gif")));
			pane.setContent(content);
			dialog.setDialogPane(pane);
			dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.APPLY);
			dialog.showAndWait()
				.filter(response -> response.equals(ButtonType.APPLY))
				.ifPresent(response -> {
					controller.saveAll();
				});
		} catch (Exception e1) {
			Execution.Manager.msg().async(Topics.INTERNAL_ERRORS.topic, e1);
		}
	}

	@FXML
	public void close(ActionEvent e){
		Platform.exit();
	}
}
