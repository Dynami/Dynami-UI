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
package org.dynami.ui.payoff;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.dynami.core.assets.Asset;
import org.dynami.core.assets.Asset.Family;
import org.dynami.core.assets.OptionChain;
import org.dynami.core.portfolio.OpenPosition;
import org.dynami.core.utils.DTime;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.utils.EuropeanBlackScholes;
import org.dynami.ui.DynamiApplication;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class PayoffChartController implements Initializable {
	@FXML AreaChart<Number, Number> chart;
	@FXML NumberAxis returnAxis;
	@FXML NumberAxis priceAxis;

	final AtomicReference<Number> upperBound = new AtomicReference<Number>(0);
	final AtomicReference<Number> lowerBound = new AtomicReference<Number>(0);
	final AtomicReference<Number> tickUnit = new AtomicReference<Number>(0);
	final AtomicBoolean showLegs = new AtomicBoolean(false);
	final AtomicBoolean showAtNow = new AtomicBoolean(false);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		DynamiApplication.priceUpperBound.addListener((obs, oldValue, newValue)->upperBound.set(newValue));
		DynamiApplication.priceLowerBound.addListener((obs, oldValue, newValue)->lowerBound.set(newValue));
		DynamiApplication.priceTickUnit.addListener((obs, oldValue, newValue)->tickUnit.set(newValue.doubleValue()));
		Execution.Manager.msg().subscribe(DynamiApplication.RESET_TOPIC, (last, msg)->{
			Platform.runLater(()-> chart.getData().clear());
		});
		DynamiApplication.timer().addClockedFunction(()->{
			if(!Execution.Manager.isLoaded()) return;
			final List<OpenPosition> list = Execution.Manager.dynami().portfolio().getOpenPositions();

			if(list.size() == 0){
				Platform.runLater(chart.getData()::clear);
				return;
			}

			final AtomicLong frontExpiration = new AtomicLong(Long.MAX_VALUE);
			final AtomicReference<Double> lastPrice = new AtomicReference<Double>(0.);

			list.forEach(o->{
				if(o.asset instanceof Asset.Option){
					Asset.Option deriv = (Asset.Option)o.asset;
					String symbol = deriv.underlyingAsset.symbol;
					final OptionChain chain = Execution.Manager.dynami().assets().getOptionChainFor(symbol);
					if(chain != null && chain.frontExpiration() < frontExpiration.get()){
						frontExpiration.set( chain.frontExpiration() );
					}
					lastPrice.set(deriv.underlyingAsset.asTradable().lastPrice());
				} else {
					lastPrice.set(o.asset.lastPrice());
				}
			});
//			System.out.println("PayoffChartController.initialize() "+lastPrice.get());
			final double upper = upperBound.get().doubleValue();
			final double lower = lowerBound.get().doubleValue();
			final double tick = tickUnit.get().doubleValue()/4;
			Platform.runLater(()->{
				priceAxis.setLowerBound(lower);
				priceAxis.setUpperBound(upper);
				priceAxis.setTickUnit(tick);
			});
			final int count = (int)((upper-lower)/tick);

			final List<XYChart.Series<Number, Number>> seriesCollection = new ArrayList<>();
			final Map<Number, Double> cumulativePosition = new TreeMap<>();
			final Map<Number, Double> atNowPosition = new TreeMap<>();

			final long now = DTime.Clock.getTime();
			list.forEach(o->{
				final XYChart.Series<Number, Number> series= new XYChart.Series<>();
				series.setName(o.asset.name);
//				long expiration = (o.asset.family.equals(Asset.Family.Option))?((Asset.Option)o.asset).expire:System.currentTimeMillis();
//				System.out.println("##################");
				for(int i = 0; i <= count; i++){
					double price = lower+(tick*i);
					double atExpirationAssetValue = 0; // o.asset.getValueAt(price, expiration, EuropeanBlackScholes.OptionPricingEngine);
					if(o.asset.is(Family.Option)){
						atExpirationAssetValue = ((Asset.Option)o.asset).getValueAtExpiration(price);
					} else {
						atExpirationAssetValue = price;
					}

					if(Double.isNaN(atExpirationAssetValue)) atExpirationAssetValue = 0;
					double atExpirationReturn = (atExpirationAssetValue-o.entryPrice)*o.quantity*o.asset.pointValue;
					if(showLegs.get()){
						series.getData().add(new XYChart.Data<Number, Number>(
								price,
								atExpirationReturn
								));
					} else {
						if(cumulativePosition.putIfAbsent(price, atExpirationReturn) != null){
							cumulativePosition.computeIfPresent(price, (p, v)->v+atExpirationReturn);
						}
					}
					if(showAtNow.get()){
						double atNowAssetValue = o.asset.getValueAt(price, now, EuropeanBlackScholes.OptionPricingEngine);
						if(Double.isNaN(atNowAssetValue)) atNowAssetValue = 0;
//						System.out.println("PayoffChartController.initialize("+o.asset.name+")  "+price+" @ "+(atNowAssetValue - o.entryPrice)*o.quantity*o.asset.pointValue);
						double atNowReturn = (atNowAssetValue-o.entryPrice)*o.quantity*o.asset.pointValue;
						if(atNowPosition.putIfAbsent(price, atNowReturn) != null){
							atNowPosition.computeIfPresent(price, (p, v)->v+atNowReturn);
						}
					}
				}
//				System.out.println("---------------");
				if(showLegs.get()){
					seriesCollection.add(series);
				}
			});

			final XYChart.Series<Number, Number> lastPriceSeries= new XYChart.Series<>();
			lastPriceSeries.setName("Current price");
			lastPriceSeries.getData().add(new XYChart.Data<Number, Number>(lastPrice.get()-tick/4., -tick));
			lastPriceSeries.getData().add(new XYChart.Data<Number, Number>(lastPrice.get(), 0));
			lastPriceSeries.getData().add(new XYChart.Data<Number, Number>(lastPrice.get()+tick/4., -tick));
			seriesCollection.add(lastPriceSeries);

			if(showAtNow.get()){
				final XYChart.Series<Number, Number> atNowPayoff= new XYChart.Series<>();
				atNowPayoff.setName("At now Payoff");
				atNowPosition.keySet().stream().forEach(k->{
					atNowPayoff.getData().add(new XYChart.Data<Number, Number>(k, atNowPosition.get(k)));
				});
				seriesCollection.add(atNowPayoff);
			}

			if(!showLegs.get()){
				final XYChart.Series<Number, Number> cumulativePayoff= new XYChart.Series<>();
				cumulativePayoff.setName("Cumulative Payoff");

				cumulativePosition.keySet().stream().forEach(k->{
					cumulativePayoff.getData().add(new XYChart.Data<Number, Number>(k, cumulativePosition.get(k)));
				});

				seriesCollection.add(cumulativePayoff);
			}

			Platform.runLater(()->{
				chart.getData().clear();
				chart.getData().addAll(seriesCollection);
			});
		});
	}

	public void showLegs(ActionEvent e){
		showLegs.set(!showLegs.get());
	}

	public void showAtNow(ActionEvent e){
		showAtNow.set(!showAtNow.get());
	}
}
