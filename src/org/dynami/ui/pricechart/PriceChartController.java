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

import org.dynami.core.Event;
import org.dynami.core.Event.Type;
import org.dynami.core.data.Bar;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.DynamiApplication;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class PriceChartController implements Initializable {
	@FXML
	LineChart<Date,Number> chart;
	
	@FXML
	NumberAxis yAxis;
	
	XYChart.Series<Date, Number> series = new XYChart.Series<>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		series.setName("Price");
		chart.setCreateSymbols(false);
		chart.getData().add(series);
		chart.setAnimated(false);
		yAxis.setForceZeroInRange(false);
		yAxis.setAutoRanging(true);
		
		DynamiApplication.timer().get("bars", Bar.class).addConsumer(bars->{
			final List<XYChart.Data<Date,Number>> list = new ArrayList<>();
			bars.forEach(bar->{
				list.add(new XYChart.Data<Date, Number>(new Date(bar.time), bar.close));
			});
			if(list.size()>0){
				series.getData().addAll(list);
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
