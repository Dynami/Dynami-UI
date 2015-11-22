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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class DoubleSpinnerFieldParam extends FieldParam {
	private final Spinner<Double> spinner = new Spinner<>();
	
	public DoubleSpinnerFieldParam(PropertyParam<Double> _prop, double min, double max, double step) {
		super(_prop.getName(), _prop.getDescription());
		
		SpinnerValueFactory<Double> factory = new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, _prop.get(), step);
		spinner.setValueFactory(factory);
		factory.setValue(_prop.get());
		spinner.valueProperty().addListener(new ChangeListener<Double>() {
			@Override
			public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
				_prop.update(newValue);
			}
		});
		
		controlsContainer.getChildren().add(spinner);
	}
	
	public Spinner<Double> getSpinner() {
		return spinner;
	}
}
