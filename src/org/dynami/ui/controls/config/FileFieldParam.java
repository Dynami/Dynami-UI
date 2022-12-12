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

import java.io.File;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class FileFieldParam extends FieldParam {
	private final TextField text =new TextField();
	private final Button button = new Button("...");
	public FileFieldParam(PropertyParam<File> _prop) {
		super(_prop.getName(), _prop.getDescription());
		final File f = _prop.get();

		text.setEditable(false);
		if(f != null && f.exists()){
			text.setText(f.getAbsolutePath());
		}
		button.setOnAction((event)->{
			FileChooser fileChooser = new FileChooser();
			if(f != null && f.exists()){
				fileChooser.setInitialFileName(f.getAbsolutePath());
				fileChooser.setInitialDirectory(f.getParentFile());
			}
			File ff = fileChooser.showOpenDialog(paramPane.getScene().getWindow());
			if(ff != null){
				_prop.update(ff);
				text.setText(ff.getAbsolutePath());
			}
		});

		controlsContainer.getChildren().addAll(text, button);
	}
}
