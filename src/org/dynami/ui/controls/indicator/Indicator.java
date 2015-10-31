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
package org.dynami.ui.controls.indicator;

import java.io.IOException;
import java.text.NumberFormat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class Indicator extends BorderPane {
//	@FXML private ImageView _icon;
	@FXML private Label _name;
	@FXML private Label _value;
	
	private static final Background redBackground = new Background( new BackgroundFill(Color.RED, null, null));
	private static final Background greenBackground = new Background( new BackgroundFill(Color.LIGHTGREEN, null, null));
	
	private boolean percent = false;
	private double value = 0;
	
	public Indicator(){
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/dynami/ui/controls/indicator/indicator.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
	
	@FXML
	public void setPercent(boolean percent){
		this.percent = percent;
	}
	
	@FXML
	public boolean isPercent() {
		return percent;
	}
	
	@FXML
	public void setName(String name){
		_name.setText(name);
	}
	
	@FXML
	public String getName(){
		return _name.getText();
	}
	
	@FXML
	public void setDescription(String description){
		_name.setTooltip(new Tooltip(description));
	}
	
	@FXML
	public String getDescription(){
		return _name.getTooltip().getText();
	}

	@FXML
	public double getValue() {
		return value;
	}

	@FXML
	public void setValue(double value) {
		this.value = value;
		if(value == 0 ){
			setBackground(Background.EMPTY);
		} else if( value > 0){
			setBackground(greenBackground);
		} else {
			setBackground(redBackground);
		}
		
		if(percent){
			_value.setText(NumberFormat.getPercentInstance().format(value));
		} else {
			_value.setText(NumberFormat.getCurrencyInstance().format(value));
		}
	}
}