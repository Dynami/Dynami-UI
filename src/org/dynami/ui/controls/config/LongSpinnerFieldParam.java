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

public class LongSpinnerFieldParam extends FieldParam {
	private final Spinner<Long> spinner = new Spinner<>();
	private final PropertyParam<Long> prop;
	
	public LongSpinnerFieldParam(PropertyParam<Long> _prop, long min, long max, long step) {
		super(_prop.getName(), _prop.getDescription());
		this.prop = _prop;
		
		SpinnerValueFactory<Long> factory = new LongSpinnerValueFactory(min, max, prop.get(), step);
		spinner.setValueFactory(factory);
		spinner.setEditable(true);
		
		spinner.valueProperty().addListener(new ChangeListener<Long>() {
			@Override
			public void changed(ObservableValue<? extends Long> observable, Long oldValue, Long newValue) {
				System.out.println( "LongSpinnerFieldParam.changed_2("+newValue+")");
				prop.update(newValue);
			}
		});
		controlsContainer.getChildren().add(spinner);
	}
	
	public Spinner<Long> getSpinner() {
		return spinner;
	}
}
