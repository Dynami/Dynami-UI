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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.DatePicker;

public class DateFieldParam extends FieldParam {
	private final DatePicker field = new DatePicker();
	private final PropertyParam<Date> prop;
	public DateFieldParam(PropertyParam<Date> _prop){
		super(_prop.getName(), _prop.getDescription());
		prop = _prop;
		controlsContainer.getChildren().add(field);
		
		field.setValue(_prop.get().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		field.valueProperty().addListener(new ChangeListener<LocalDate>() {
			@Override
			public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
				Date d = java.util.Date.from(newValue.atStartOfDay()
					      .atZone(ZoneId.systemDefault())
					      .toInstant());
				prop.update(d);
			}
		});
	}
	
	public PropertyParam<Date> getPropertyParam() {
		return prop;
	}
}
