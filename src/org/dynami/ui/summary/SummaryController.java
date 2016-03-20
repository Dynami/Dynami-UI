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
package org.dynami.ui.summary;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

import org.dynami.core.assets.Asset;
import org.dynami.core.services.IPortfolioService;
import org.dynami.runtime.impl.Execution;
import org.dynami.ui.DynamiApplication;
import org.dynami.ui.controls.indicator.Indicator;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class SummaryController implements Initializable {
	@FXML Indicator initialBudget;
	@FXML Indicator currentBudget;
	@FXML Indicator realized;
	@FXML Indicator unrealized;
	@FXML Indicator margin;
	@FXML Indicator maxMargin;
	@FXML Indicator roi;
	@FXML Indicator hvola;
	@FXML Indicator portfolioDelta, portfolioVega, portfolioTheta;

	private final AtomicReference<Double> _maxMargin = new AtomicReference<>(0.0);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		final AtomicReference<Double> delta = new AtomicReference<>(0.);
		final AtomicReference<Double> vega = new AtomicReference<>(0.);
		final AtomicReference<Double> theta = new AtomicReference<>(0.);
		Execution.Manager.msg().subscribe(DynamiApplication.RESET_TOPIC, (last, msg)->{
			Platform.runLater(()->{
				_maxMargin.set(0.);
				currentBudget.setValue(initialBudget.getValue());
				realized.setValue(0);
				unrealized.setValue(0);
				margin.setValue(0);
				maxMargin.setValue(0);
				roi.setValue(0);
//				hvola.setValue(0);
				portfolioDelta.setValue(0);
				portfolioVega.setValue(0);
				portfolioTheta.setValue(0);
			});
		});
		DynamiApplication.timer().addClockedFunction(()->{
			if(Execution.Manager.isLoaded()){
				final IPortfolioService portfolio = Execution.Manager.dynami().portfolio();
				double _margin = portfolio.requiredMargin();
				if(_margin < 0 && _margin < _maxMargin.get()){
					_maxMargin.set(_margin);
				}
				double _realized = portfolio.realised();
				double _unrealized = portfolio.unrealised();
				double _initialBudget = portfolio.getInitialBudget();
				double _currentBudget = _initialBudget+_realized+_unrealized;
				double _roi = (_currentBudget/_initialBudget)-1;

				delta.set(0.);
				vega.set(0.);
				theta.set(0.);
				portfolio.getOpenPositions().forEach(o->{
					if(o.asset.family.equals(Asset.Family.Option)){
						double _delta = ((Asset.Option)o.asset).greeks().delta() * o.quantity * o.asset.pointValue;
						delta.set(delta.get()+_delta);

						double _vega = ((Asset.Option)o.asset).greeks().vega() * o.quantity * o.asset.pointValue;
						vega.set(vega.get()+_vega);

						double _theta = ((Asset.Option)o.asset).greeks().theta() * o.quantity * o.asset.pointValue;
						theta.set(theta.get()+_theta);
					} else {
						delta.set(delta.get()+(o.quantity*o.asset.pointValue));
						vega.set(vega.get()+(o.quantity*o.asset.pointValue));
						theta.set(theta.get()+(o.quantity*o.asset.pointValue));
					}
				});

				Platform.runLater(()->{
					realized.setValue(_realized);
					unrealized.setValue(_unrealized);
					initialBudget.setValue(_initialBudget);
					currentBudget.setValue(_currentBudget);
					roi.setValue(_roi);

					margin.setValue(_margin);
					maxMargin.setValue(_maxMargin.get());
					portfolioDelta.setValue(delta.get());
					portfolioVega.setValue(vega.get());
					portfolioTheta.setValue(theta.get());
				});
			}
		});
	}
}
