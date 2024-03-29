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
package org.dynami.ui;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

public class UIUtils {
	public static final Background redBackground = new Background( new BackgroundFill(Color.RED, null, null));
	public static final Background greenBackground = new Background( new BackgroundFill(Color.LIGHTGREEN, null, null));
	public static final Background blackBackground = new Background( new BackgroundFill(Color.BLACK, null, null));
	public static final Background defaultBackground = new Background( new BackgroundFill(Color.WHITE, null, null));
	
	public static Color randomColor(){
		int red = (int)Math.random()*255;
		int green = (int)Math.random()*255;
		int blue = (int)Math.random()*255;
		return Color.rgb(red, green, blue);
	};
	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	public static NumberFormat PERC_NUMBER_FORMAT = NumberFormat.getPercentInstance();
	static {
		PERC_NUMBER_FORMAT.setMinimumFractionDigits(2);
	}
}
