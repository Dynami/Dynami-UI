package org.dynami.ui.vola;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import org.dynami.core.Event;
import org.dynami.core.Event.Type;
import org.dynami.core.assets.Market;
import org.dynami.core.data.Bar;
import org.dynami.core.data.IVolatilityEngine;
import org.dynami.core.data.vola.RogersSatchellVolatilityEngine;
import org.dynami.runtime.data.BarData;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.DynamiApplication;
import org.dynami.ui.prefs.data.PrefsConstants;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class VolaChartController implements Initializable {
	@FXML
	LineChart<Date, Number> chart;
	final BarData data = new BarData();
	final XYChart.Series<Date, Number> histVola = new XYChart.Series<>();
//	final XYChart.Series<Date, Number> implVola = new XYChart.Series<>();
	final IVolatilityEngine engine = new RogersSatchellVolatilityEngine();
	Market market;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		final int MAX_SAMPLES = Preferences.userRoot().node(PrefsConstants.PREFS_NODE).getInt(PrefsConstants.TIME_CHART.MAX_SAMPLE_SIZE, 200);
		histVola.setName("Historical Volatility");
//		implVola.setName("Implied Volatility");
		chart.setCreateSymbols(false);
		chart.getData().add(histVola);
		chart.setAnimated(false);

		Execution.Manager.msg().subscribe(DynamiApplication.RESET_TOPIC, (last, msg)->{
			Platform.runLater(()-> histVola.getData().clear());
		});

		DynamiApplication.timer().get("_bars", Bar.class).addConsumer(bars->{
			final List<XYChart.Data<Date,Number>> tmpHistVola = new ArrayList<>();
//			final List<XYChart.Data<Date,Number>> tmpImplVola = new ArrayList<>();
			bars.forEach(bar->{
				data.append(bar);
				if(market == null){
					market = Execution.Manager.dynami().assets().getMarketBySymbol(bar.symbol);
				}
				double lastVola = 0;
				if(data.setAutoCompressionRate()){
					lastVola = data.getVolatility(engine, 20)*engine.annualizationFactor(data.getCompression(), 20, market);
				}
				//double lastVola = data.getVolatility(engine, 20)*engine.annualizationFactor(data.getCompression(), 20, market);
				tmpHistVola.add(new XYChart.Data<Date, Number>(new Date(bar.time), lastVola));
			});

			if(tmpHistVola.size()>0){
				int exeeding = Math.max(0, histVola.getData().size()+tmpHistVola.size()-MAX_SAMPLES);
				if(exeeding  > 0){
					histVola.getData().remove(0, exeeding-1);
				}
				histVola.getData().addAll(tmpHistVola);
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
