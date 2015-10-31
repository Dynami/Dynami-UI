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

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MenuController implements Initializable {
	@FXML
	MenuBar menu;
	
	@FXML
	MenuItem openMenuItem;
	
	@FXML
	MenuItem saveMenuItem;
	
	@FXML
	public void open(ActionEvent e){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select dynami file");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Dynami file", "*.dynami"));
		File file = fileChooser.showOpenDialog(menu.getScene().getWindow());
		if(file != null && file.exists() && !file.isDirectory()){
			
		}
	}
	
	@FXML
	public void save(ActionEvent e){
		
	}
	
	@FXML
	public void saveAs(ActionEvent e){
		
	}
	
	@FXML
	public void preferences(ActionEvent e){
		
	}
	
	@FXML
	public void close(ActionEvent e){
		Platform.exit();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		saveMenuItem.setDisable(true);
	}
}
