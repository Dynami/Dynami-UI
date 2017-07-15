/*
 * Copyright 2017 Alessandro Atria - a.atria@gmail.com
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

import java.util.Date;

import extfx.scene.control.DatePicker;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class DateFieldParam extends FieldParam {
	private final DatePicker field = new DatePicker();
	private final PropertyParam<Date> prop;
	public DateFieldParam(PropertyParam<Date> _prop){
		super(_prop.getName(), _prop.getDescription());
		prop = _prop;
		controlsContainer.getChildren().add(field);
		
		field.setValue(_prop.get());
		field.valueProperty().addListener(new ChangeListener<Date>() {
			@Override
			public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {
				prop.update(newValue);
			}
		});
	}
	
	public PropertyParam<Date> getPropertyParam() {
		return prop;
	}
}
