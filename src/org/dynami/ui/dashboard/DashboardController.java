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
package org.dynami.ui.dashboard;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

import org.dynami.core.services.IPortfolioService;
import org.dynami.runtime.impl.Execution;
import org.dynami.ui.DynamiApplication;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class DashboardController implements Initializable {
	@FXML HBox gauges;

	private final AtomicReference<Double> exposure = new AtomicReference<>(0.);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		GaugeBuilder<?> builder = GaugeBuilder.create().skinType(SkinType.SIMPLE);
		final Gauge gaugeFinExposure = builder
				.sections(new Section(0, 20, Color.GREEN),
	            			new Section(21, 40, Color.GREENYELLOW),
	            			new Section(41, 60, Color.YELLOW),
	            			new Section(61, 80, Color.ORANGE),
	            			new Section(81, 100, Color.ORANGERED))
               .title("Exposure")
               .threshold(50)
               .animated(true)
               .build();

		gaugeFinExposure.setTooltip(new Tooltip("Mesaures financial exposure, goes from 0 to 100 and is given by (margin/currentBudget)*100"));

		final Gauge gaugeGamma = builder
				.sections(new Section(-5., -3, Color.ORANGERED),
	            			new Section(-2.9, -1,Color.YELLOW),
	            			new Section(-0.9, 1, Color.GREENYELLOW),
	            			new Section(1.1, 3, Color.YELLOW),
	            			new Section(3.1, 5, Color.ORANGERED))
               .title("Gamma")
               .minValue(-5)
               .maxValue(5)
               .threshold(0)
               .animated(true)
               .build();

		gaugeGamma.setTooltip(new Tooltip("Portfolio Gamma for 1% movement of underlying asset"));

		gauges.getChildren().addAll(gaugeFinExposure, gaugeGamma);
		DynamiApplication.timer().addClockedFunction(()->{
			if(Execution.Manager.isLoaded()){
				final IPortfolioService portfolio = Execution.Manager.dynami().portfolio();
				double initialBudget = portfolio.getInitialBudget();
				double realized = portfolio.realized();
				double unrealized = portfolio.unrealized();
				double margin = -portfolio.requiredMargin();
				double currentExposure = (margin/(initialBudget+realized+unrealized))*100;
				if(currentExposure > exposure.get()){
					exposure.set(currentExposure);
					gaugeFinExposure.setValue(exposure.get());
				}
				IPortfolioService.Greeks greeks = portfolio.getPortfolioGreeks();
				double points = greeks.underlyingPrice*0.01;
				gaugeGamma.setValue(points*greeks.gamma);
			}
		});

		Execution.Manager.msg().subscribe(DynamiApplication.RESET_TOPIC, (last, msg)->{
			Platform.runLater(()-> exposure.set(0.));
		});
	}
}
