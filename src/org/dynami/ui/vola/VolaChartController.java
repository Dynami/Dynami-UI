package org.dynami.ui.vola;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.dynami.core.Event;
import org.dynami.core.Event.Type;
import org.dynami.core.data.Bar;
import org.dynami.core.data.IVolatilityEngine;
import org.dynami.core.utils.DUtils;
import org.dynami.runtime.data.BarData;
import org.dynami.runtime.data.vola.CloseToCloseVolatilityEngine;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.DynamiApplication;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class VolaChartController implements Initializable {
	@FXML
	LineChart<Date, Number> chart;
	final BarData data = new BarData();
	final XYChart.Series<Date, Number> vola = new XYChart.Series<>();
	final IVolatilityEngine engine = new CloseToCloseVolatilityEngine();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		vola.setName("Historical Volatility");
		chart.setCreateSymbols(false);
		chart.getData().add(vola);
		chart.setAnimated(false);
		
		DynamiApplication.timer().get("_bars", Bar.class).addConsumer(bars->{
			final List<XYChart.Data<Date,Number>> list = new ArrayList<>();
			bars.forEach(bar->{
				data.append(bar);
				double lastVola = data.getVolatility(engine, 20)*Math.sqrt(DUtils.YEAR_WORKDAYS);
				list.add(new XYChart.Data<Date, Number>(new Date(bar.time), lastVola));
			});
			
			if(list.size()>0){
				vola.getData().addAll(list);
			}
		});
		
		Execution.Manager.msg().subscribe(Topics.STRATEGY_EVENT.topic, (last, msg)->{
			final Event e = (Event)msg;
			if(e.is(Type.OnBarClose)){
				DynamiApplication.timer().get("_bars", Bar.class).push(e.bar);
			}
		});
	}
}
