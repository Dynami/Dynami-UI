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
package org.dynami.ui.controls.config;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public abstract class FieldParam extends GridPane{
	@FXML GridPane paramPane;
	@FXML Label name;
	@FXML Label description;
	@FXML Tooltip tooltip;
	@FXML HBox controlsContainer;
	
	public FieldParam(String _name, String _description) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/dynami/ui/controls/config/fieldparam.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		name.setText(_name);
		description.setText(_description);
		tooltip.setText(_description);
//		controlsContainer.setPadding(new Insets(0, 20, 10, 20)); 
	}
}
