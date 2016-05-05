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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import org.dynami.core.plot.Plot;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.plot.PlotData;
import org.dynami.runtime.plot.PlottableObject;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.DynamiApplication;
import org.dynami.ui.controls.chart.BarStickChart;
import org.dynami.ui.prefs.data.PrefsConstants;

import extfx.scene.chart.DateAxis;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

public class PriceChartController implements Initializable {
	@FXML VBox vbox;

	DateAxis xAxis;
	NumberAxis yAxis;
	
	final Map<String, XYChart.Series<Date, Number>> series = new HashMap<>();
	final Map<String, XYChart<Date, Number>> charts = new HashMap<>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		final int MAX_SAMPLES = Preferences.userRoot().node(PrefsConstants.PREFS_NODE).getInt(PrefsConstants.TIME_CHART.MAX_SAMPLE_SIZE, 50);
		xAxis = new DateAxis();
		yAxis = new NumberAxis();

		DynamiApplication.priceLowerBound.bind(yAxis.lowerBoundProperty());
		DynamiApplication.priceUpperBound.bind(yAxis.upperBoundProperty());
		DynamiApplication.priceTickUnit.bind(yAxis.tickUnitProperty());

		yAxis.setForceZeroInRange(false);
		yAxis.setAutoRanging(true);
		
		charts.put(Plot.MAIN_CHART, new BarStickChart(xAxis, yAxis, FXCollections.observableArrayList()));
		series.put(Plot.MAIN_CHART, new XYChart.Series<Date, Number>("Price", FXCollections.observableArrayList()));
		
		charts.get(Plot.MAIN_CHART).getData().add(series.get(Plot.MAIN_CHART));
		
		vbox.getChildren().add(charts.get(Plot.MAIN_CHART));
		
		Execution.Manager.msg().subscribe(Topics.CHART_SIGNAL.topic, (last, msg)->{
			final PlotData plotData = (PlotData)msg;
			System.out.println(plotData.data());
			DynamiApplication.timer().get("plotData", PlotData.class).push(plotData);
		});
		
		DynamiApplication.timer().get("plotData", PlotData.class).addConsumer(bars->{
			final List<XYChart.Data<Date,Number>> list = new ArrayList<>();
			final Map<String, List<XYChart.Data<Date,Number>>> seriesLists = new HashMap<>();
			
			bars.forEach(data->{
				list.add(new XYChart.Data<Date, Number>(
						new Date(data.bar.time),
						data.bar.high,
						data.bar
						));
				;
				data.data().forEach( i->{
					seriesLists.putIfAbsent(i.key, new ArrayList<XYChart.Data<Date,Number>>());
					System.out.println("STEP 2 "+i.key);
					seriesLists.get(i.key).add(new XYChart.Data<Date, Number>(new Date(data.bar.time),i.value));
				});
				
			});
			if(list.size()>0){
				System.out.println(series.keySet());
				Platform.runLater(()->{
					XYChart.Series<Date, Number> barSeries = series.get(Plot.MAIN_CHART);
					final int exeeding = Math.max(0, barSeries.getData().size()+list.size()-MAX_SAMPLES);
					if(exeeding  > 0){
						barSeries.getData().remove(0, exeeding-1);
					}
					barSeries.getData().addAll(list);
				});
			}
			if(seriesLists.size() > 0){
				seriesLists.forEach((k, v)->{
					System.out.println("STEP 1 "+k);
					Platform.runLater(()->{
						series.get(k).getData().addAll(seriesLists.get(k));
					});
				});
			}
		});

		Execution.Manager.msg().subscribe(DynamiApplication.RESET_TOPIC, (last, msg)->{
//			Platform.runLater(()->{
//				if(series.get(Plot.MAIN_CHART) == null) return; 
//				series.get(Plot.MAIN_CHART).getData().clear();
//			});
		});

		Execution.Manager.msg().subscribe(Topics.NEW_STAGE.topic, (last, msg)->{
			@SuppressWarnings("unchecked")
			List<PlottableObject> plottableObjects = (List<PlottableObject>)msg;
			final List<String> seriesNames = new ArrayList<>();
			plottableObjects.forEach(po->{
				seriesNames.addAll(po.keys());
			});
			
			List<String> chartPanes = plottableObjects.stream()
					.map(PlottableObject::on)
					.distinct()
					.collect(Collectors.toList());
			
			chartPanes.forEach(k->{
				if(!k.equals(Plot.MAIN_CHART)){
					Platform.runLater(()->{
						LineChart<Date, Number> chart = new LineChart<>(xAxis, new NumberAxis(), FXCollections.observableArrayList());
//						chart.legendVisibleProperty().set(false);
						charts.put(k, chart);
					});
				}
			});
			
			seriesNames.forEach(s->{
				System.out.println("Series Name "+s);
				charts.forEach((k, c)->{
					if(s.startsWith(k)){
						Platform.runLater(()->{
							final XYChart.Series<Date, Number> serie = new XYChart.Series<Date, Number>(s, FXCollections.observableArrayList());
							series.put(s, serie);
							charts.get(k).getData().add(serie);
						});
					}
				});
			});
			charts.forEach( (k, v)->{
				if(!k.equals(Plot.MAIN_CHART))
					Platform.runLater(()->{
						vbox.getChildren().add(v);
					});	
			});
		});
	}
}
