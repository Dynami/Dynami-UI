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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;

public class BooleanFieldParam extends FieldParam {
	private final CheckBox checkbox;
	
	public BooleanFieldParam(PropertyParam<Boolean> prop){
		super(prop.getName(), prop.getDescription());
		checkbox = new CheckBox();
		checkbox.tooltipProperty().set(new Tooltip(prop.getDescription()));
		checkbox.selectedProperty().set(prop.get());
		checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				prop.update(newValue);
			}
		});
		
		controlsContainer.getChildren().add(checkbox);
	}
}
