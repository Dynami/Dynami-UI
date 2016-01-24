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
package org.dynami.ui.pricechart;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import org.dynami.core.Event;
import org.dynami.core.Event.Type;
import org.dynami.core.data.Bar;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.DynamiApplication;
import org.dynami.ui.controls.chart.BarStickChart;
import org.dynami.ui.prefs.PrefsConstants;

import extfx.scene.chart.DateAxis;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

public class CandleChartController implements Initializable {
	@FXML VBox vbox;

	BarStickChart chart;
	DateAxis xAxis;
	NumberAxis yAxis;
	XYChart.Series<Date, Number> series = new XYChart.Series<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		final int MAX_SAMPLES = Preferences.userRoot().node(DynamiApplication.class.getName()).getInt(PrefsConstants.TIME_CHART.MAX_SAMPLE_SIZE, 50);
		xAxis = new DateAxis();
//		xAxis.labelProperty().set("Time");
		yAxis = new NumberAxis();
//		yAxis.labelProperty().set("Price");
		chart = new BarStickChart(xAxis, yAxis, FXCollections.observableArrayList());
		vbox.getChildren().add(chart);

		DynamiApplication.priceLowerBound.bind(yAxis.lowerBoundProperty());
		DynamiApplication.priceUpperBound.bind(yAxis.upperBoundProperty());
		DynamiApplication.priceTickUnit.bind(yAxis.tickUnitProperty());

		series.setName("Price");
		chart.getData().add(series);
		yAxis.setForceZeroInRange(false);
		yAxis.setAutoRanging(true);

		DynamiApplication.timer().get("bars", Bar.class).addConsumer(bars->{
			final List<XYChart.Data<Date,Number>> list = new ArrayList<>();
			bars.forEach(bar->{
				list.add(new XYChart.Data<Date, Number>(
						new Date(bar.time),
						bar.high,
						bar
						));
				;
			});
			if(list.size()>0){
				Platform.runLater(()->{
					final int exeeding = Math.max(0, series.getData().size()+list.size()-MAX_SAMPLES);
					if(exeeding  > 0){
						series.getData().remove(0, exeeding-1);
					}
					series.getData().addAll(list);
				});
			}
		});

		Execution.Manager.msg().subscribe(Topics.STRATEGY_EVENT.topic, (last, msg)->{
			final Event e = (Event)msg;
			if(e.is(Type.OnBarClose)){
				DynamiApplication.timer().get("bars", Bar.class).push(e.bar);
			}
		});
	}
}
