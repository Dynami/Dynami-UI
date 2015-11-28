package org.dynami.ui.payoff;

import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.tools.Platform;
import org.dynami.ui.DynamiApplication;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;

public class PayoffChartController implements Initializable {
	@FXML AreaChart<Number, Number> chart;
	@FXML NumberAxis yAxis;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		DynamiApplication.priceUpperBound.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				yAxis.upperBoundProperty().setValue(newValue);
			}
		});
		
		DynamiApplication.priceLowerBound.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				yAxis.lowerBoundProperty().setValue(newValue);
			}
		});
		
		DynamiApplication.priceTickUnit.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				yAxis.tickUnitProperty().setValue(newValue);
			}
		});
		
		
		DynamiApplication.timer().addClockedFunction(()->{
			//List<OpenPosition> list = Execution.Manager.dynami().portfolio().getOpenPosition();
			
			//chart.requestLayout();
			
		});
	}
}
