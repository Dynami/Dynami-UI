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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class TextFieldParam extends FieldParam {
	private final TextField text = new TextField();
	private final PropertyParam<String> prop;
	public TextFieldParam(PropertyParam<String> _prop){
		super(_prop.getName(), _prop.getDescription());
		prop = _prop;
		controlsContainer.getChildren().add(text);
		
		text.setText(_prop.get());
		text.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				prop.update(newValue);
			}
		});
	}
	
	public PropertyParam<String> getPropertyParam() {
		return prop;
	}
}
