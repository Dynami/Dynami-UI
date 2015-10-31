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
