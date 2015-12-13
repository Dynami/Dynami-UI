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

import org.controlsfx.control.action.ActionMap;
import org.dynami.runtime.models.StrategyComponents;
import org.dynami.ui.collectors.Strategies;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

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
		
	}
	
	@FXML
	public void close(ActionEvent e){
		Platform.exit();
	}
}
