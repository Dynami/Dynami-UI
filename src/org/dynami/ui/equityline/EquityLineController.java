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
package org.dynami.ui.equityline;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.prefs.Preferences;

import org.dynami.core.Event;
import org.dynami.core.Event.Type;
import org.dynami.core.services.IPortfolioService;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.DynamiApplication;
import org.dynami.ui.prefs.data.PrefsConstants;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class EquityLineController implements Initializable {
	@FXML
	LineChart<Date, Number> chart;

	final XYChart.Series<Date, Number> realized = new XYChart.Series<>();
	final XYChart.Series<Date, Number> total = new XYChart.Series<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		final int MAX_SAMPLES = Preferences.userRoot().node(PrefsConstants.PREFS_NODE).getInt(PrefsConstants.TIME_CHART.MAX_SAMPLE_SIZE, 200);
		System.out.println("EquityLineController.initialize() MAX_SAMPLES "+MAX_SAMPLES);
		realized.setName("Realized");
		total.setName("Total");
		chart.setCreateSymbols(false);
		chart.getData().add(realized);
		chart.getData().add(total);
		chart.setAnimated(false);

		Execution.Manager.msg().subscribe(DynamiApplication.RESET_TOPIC, (last, msg)->{
			Platform.runLater(()->{
				realized.getData().clear();
				total.getData().clear();
			});
		});

		DynamiApplication.timer().get("equityLine", EquityLineData.class).addConsumer(bars->{
			final List<XYChart.Data<Date,Number>> listRealised = new CopyOnWriteArrayList<>();
			final List<XYChart.Data<Date,Number>> listTotal = new CopyOnWriteArrayList<>();
			bars.forEach(bar->{
				listRealised.add(new XYChart.Data<Date, Number>(new Date(bar.time), bar.realized));
				listTotal.add(new XYChart.Data<Date, Number>(new Date(bar.time), bar.realized+bar.unrealized));
			});

			if(listRealised.size()>0){
				int exeeding = Math.max(0, realized.getData().size()+listRealised.size()-MAX_SAMPLES);
				if(exeeding  > 0){
					realized.getData().remove(0, exeeding-1);
					total.getData().remove(0,  exeeding-1);
				}
				realized.getData().addAll(listRealised);
				total.getData().addAll(listTotal);
			}
		});
		Execution.Manager.msg().subscribe(Topics.STRATEGY_EVENT.topic, (last, msg)->{
			final Event e = (Event)msg;
			if(e.is(Type.OnBarClose)){
				final IPortfolioService portfolio = Execution.Manager.dynami().portfolio();
				double realized = portfolio.realized();
				double unrealized = portfolio.unrealized();

				DynamiApplication.timer().get("equityLine", EquityLineData.class).push(new EquityLineData(e.bar.time, realized, unrealized));
			}
		});
	}

	public static class EquityLineData {
		final public long time;
		final public double realized;
		final public double unrealized;

		public EquityLineData(long time, double realized, double unrealized) {
			this.time = time;
			this.realized = realized;
			this.unrealized = unrealized;
		}
	}
}
