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
package org.dynami.ui.main;

import java.net.URL;
import java.util.ResourceBundle;

import org.dynami.ui.menu.MenuController;
import org.dynami.ui.status.StatusController;
import org.dynami.ui.toolbar.ToolBarController;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class MainWindowController implements Initializable {
	@FXML ToolBarController toolbarController;
	@FXML MenuController menuController;
	@FXML StatusController statusController;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
}
