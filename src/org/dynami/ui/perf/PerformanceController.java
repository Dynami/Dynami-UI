package org.dynami.ui.perf;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.prefs.Preferences;

import org.dynami.core.Event;
import org.dynami.core.Event.Type;
import org.dynami.core.portfolio.ClosedPosition;
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

public class PerformanceController implements Initializable {
	@FXML LineChart<Date, Number> chart;
	
	final XYChart.Series<Date, Number> buyAndHold = new XYChart.Series<>();
	final XYChart.Series<Date, Number> strategy = new XYChart.Series<>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		final int MAX_SAMPLES = Preferences.userRoot().node(PrefsConstants.PREFS_NODE).getInt(PrefsConstants.TIME_CHART.MAX_SAMPLE_SIZE, 200);
		buyAndHold.setName("Buy and hold");
		strategy.setName("Strategy");
		chart.setCreateSymbols(false);
		chart.getData().add(buyAndHold);
		chart.getData().add(strategy);
		chart.setAnimated(false);

		Execution.Manager.msg().subscribe(DynamiApplication.RESET_TOPIC, (last, msg)->{
			Platform.runLater(()->{
				buyAndHold.getData().clear();
				strategy.getData().clear();
			});
		});
		
		DynamiApplication.timer().get("performanceLine", PerformanceData.class).addConsumer(bars->{
			final List<XYChart.Data<Date,Number>> listBuyAndHold = new CopyOnWriteArrayList<>();
			final List<XYChart.Data<Date,Number>> listStrategy = new CopyOnWriteArrayList<>();
			bars.forEach(bar->{
				listBuyAndHold.add(new XYChart.Data<Date, Number>(new Date(bar.time), bar.buyAndHold));
				listStrategy.add(new XYChart.Data<Date, Number>(new Date(bar.time), bar.strategy));
			});

			if(listBuyAndHold.size()>0){
				int exeeding = Math.max(0, buyAndHold.getData().size()+listBuyAndHold.size()-MAX_SAMPLES);
				if(exeeding  > 0){
					buyAndHold.getData().remove(0, exeeding-1);
					strategy.getData().remove(0,  exeeding-1);
				}
				Platform.runLater(()->{
					buyAndHold.getData().addAll(listBuyAndHold);
					strategy.getData().addAll(listStrategy);
				});
			}
		});
		final AtomicReference<Double> buyAndHoldPerf = new AtomicReference<>(0.);
		final AtomicReference<Double> prevClose = new AtomicReference<>(Double.NaN);
		Execution.Manager.msg().subscribe(Topics.STRATEGY_EVENT.topic, (last, msg)->{
			final Event e = (Event)msg;
			if(e.is(Type.OnBarClose)){
				if(!Double.isNaN(prevClose.get())){
					buyAndHoldPerf.accumulateAndGet((e.bar.close/prevClose.get())-1., (a, b)-> a+b) ;
				}
				prevClose.set(e.bar.close);
				final IPortfolioService portfolio = Execution.Manager.dynami().portfolio();
				final double closedPositionPerf = portfolio.getClosedPositions()
						.stream()
						.mapToDouble(ClosedPosition::percRoi)
						.sum();
				final double openPositionPerf = 0; //portfolio.getOpenPositions().stream().mapToDouble(OpenPosition::performance).sum();
				DynamiApplication.timer().get("performanceLine", PerformanceData.class).push(
						new PerformanceData(
								e.bar.time, 
								buyAndHoldPerf.get(), 
								closedPositionPerf+openPositionPerf));
			}
		});
	}
	
	public static class PerformanceData {
		final public long time;
		final public double buyAndHold;
		final public double strategy;

		public PerformanceData(long time, double buyAndHold, double strategy) {
			this.time = time;
			this.buyAndHold = buyAndHold;
			this.strategy = strategy;
		}
	}
}
