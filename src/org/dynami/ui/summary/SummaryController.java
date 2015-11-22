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
	@FXML Indicator roi;
	@FXML Indicator hvola;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		DynamiApplication.timer().addClockedFunction(()->{
			if(Execution.Manager.isLoaded()){
				final IPortfolioService portfolio = Execution.Manager.dynami().portfolio();
				double _realized = portfolio.realized();
				double _unrealized = portfolio.unrealized();
				double _initialBudget = portfolio.getInitialBudget();
				double _currentBudget = _initialBudget+_realized+_unrealized;
				double _roi = (_currentBudget/_initialBudget)-1;
				Platform.runLater(()->{
					realized.setValue(_realized);
					unrealized.setValue(_unrealized);
					initialBudget.setValue(_initialBudget);
					currentBudget.setValue(_currentBudget);
					roi.setValue(_roi);
				});
			}
		});
	}
}
