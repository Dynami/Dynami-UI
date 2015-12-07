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

import org.dynami.core.data.IData.TimeUnit;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;

public class TimeFrameParam extends FieldParam {
	private final Spinner<Long> spinner;
	private final ComboBox<TimeUnit> combo = new ComboBox<>(FXCollections.observableArrayList(
				TimeUnit.values()
			));
	
	public TimeFrameParam(PropertyParam<Long> prop, long min, long max, long step) {
		super(prop.getName(), prop.getDescription());
		spinner = new Spinner<>(new LongSpinnerValueFactory(min, max));
		long compression = prop.get();
		TimeUnit tu = TimeUnit.getTimeUnit(compression);
		long units = TimeUnit.getUnits(compression);
		spinner.getValueFactory().setValue(units);
		combo.getSelectionModel().select(tu);
		
		spinner.valueProperty().addListener(new ChangeListener<Long>() {
			@Override
			public void changed(ObservableValue<? extends Long> observable, Long oldValue, Long newValue) {
				long compression = combo.getSelectionModel().getSelectedItem().millis();
				prop.update(newValue*compression);
			}
		});
		
		combo.valueProperty().addListener(new ChangeListener<TimeUnit>() {
			@Override
			public void changed(ObservableValue<? extends TimeUnit> observable, TimeUnit oldValue, TimeUnit newValue) {
				if(newValue.equals(TimeUnit.Tick)){
					spinner.setDisable(true);
				} else {
					spinner.setDisable(false);
				}
				long units = spinner.getValue();
				prop.update(newValue.millis()*units);
			}
		});
		spinner.setPrefWidth(80);
		combo.setPrefWidth(120);
		controlsContainer.setPrefWidth(200);
		controlsContainer.getChildren().addAll(combo, spinner);
	}
}
