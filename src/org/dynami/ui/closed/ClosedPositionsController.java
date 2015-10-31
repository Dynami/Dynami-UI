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
package org.dynami.ui.closed;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.dynami.core.utils.DUtils;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.DynamiApplication;
import org.dynami.ui.UIUtils;
import org.dynami.ui.timer.UITimer.ClockBuffer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class ClosedPositionsController implements Initializable {
	@FXML TextField filterText;
	@FXML TableView<ClosedPosition> closedPositionsTable;
	@FXML TableColumn<ClosedPosition, String> assetColumn;
	@FXML TableColumn<ClosedPosition, Long> quantityColumn;
	@FXML TableColumn<ClosedPosition, Number> entryPriceColumn;
	@FXML TableColumn<ClosedPosition, Number> entryTimeColumn;
	@FXML TableColumn<ClosedPosition, Number> exitPriceColumn;
	@FXML TableColumn<ClosedPosition, Number> exitTimeColumn;
	@FXML TableColumn<ClosedPosition, Number> percReturnColumn;
	@FXML TableColumn<ClosedPosition, Number> returnColumn;
	
	private final AtomicInteger count = new AtomicInteger(0);
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		closedPositionsTable.setRowFactory(new Callback<TableView<ClosedPosition>, TableRow<ClosedPosition>>() {
			@Override
			public TableRow<ClosedPosition> call(TableView<ClosedPosition> param) {
				return new TableRow<ClosedPosition>(){
					@Override
					protected void updateItem(ClosedPosition item, boolean empty) {
						super.updateItem(item, empty);
						if(!empty){
							if(item.getPercReturn()>= 0){
								setBackground(UIUtils.greenBackground);
							} else {
								setBackground(UIUtils.redBackground);
							}
						}
					}
				};
			}
		});
		
		assetColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));;
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		
		entryPriceColumn.setCellValueFactory(cell->cell.getValue().entryPrice());
		entryPriceColumn.setCellFactory(col -> new TableCell<ClosedPosition, Number>() {
	        @Override 
	        public void updateItem(Number price, boolean empty) {
	            super.updateItem(price, empty);
	            if (empty) {
	                setText(null);
	            } else {
	            	setText(String.format("%.2f", price.doubleValue()));
	            }
	        }
	    });
		
		entryTimeColumn.setCellValueFactory(cell->cell.getValue().entryTime());
		entryTimeColumn.setCellFactory(col -> new TableCell<ClosedPosition, Number>() {
	        @Override 
	        public void updateItem(Number time, boolean empty) {
	            super.updateItem(time, empty);
	            if (empty) {
	                setText(null);
	            } else {
	                setText(DUtils.LONG_DATE_FORMAT.format(time.longValue()));
	            }
	        }
	    });
		
		exitPriceColumn.setCellValueFactory(cell->cell.getValue().exitPrice());
		exitPriceColumn.setCellFactory(col -> new TableCell<ClosedPosition, Number>() {
	        @Override 
	        public void updateItem(Number price, boolean empty) {
	            super.updateItem(price, empty);
	            if (empty) {
	                setText(null);
	            } else {
	            	
	                setText(String.format("%.2f", price.doubleValue()));
	            }
	        }
	    });
		exitTimeColumn.setCellValueFactory(cell->cell.getValue().exitTime());
		exitTimeColumn.setCellFactory(col -> new TableCell<ClosedPosition, Number>() {
	        @Override 
	        public void updateItem(Number time, boolean empty) {
	            super.updateItem(time, empty);
	            if (empty) {
	                setText(null);
	            } else {
	                setText(DUtils.LONG_DATE_FORMAT.format(time.longValue()));
	            }
	        }
	    });
		
		percReturnColumn.setCellValueFactory(cell->cell.getValue().percReturn());
		percReturnColumn.setCellFactory(col -> new TableCell<ClosedPosition, Number>() {
	        @Override 
	        public void updateItem(Number price, boolean empty) {
	            super.updateItem(price, empty);
	            if (empty) {
	                setText(null);
	            } else {
	            	setText(UIUtils.PERC_NUMBER_FORMAT.format(price.doubleValue()));
	            }
	        }
	    });
		
		returnColumn.setCellValueFactory(cell->cell.getValue().absReturn());
		returnColumn.setCellFactory(col -> new TableCell<ClosedPosition, Number>() {
	        @Override 
	        public void updateItem(Number price, boolean empty) {
	            super.updateItem(price, empty);
	            if (empty) {
	                setText(null);
	            } else {
	                setText(NumberFormat.getCurrencyInstance().format(price.doubleValue()));
	            }
	        }
	    });
		
		// load previous closed positions on start-up 
		if(Execution.Manager.isLoaded()){
			List<org.dynami.core.portfolio.ClosedPosition> _closed = Execution.Manager.dynami().portfolio().getClosedPosition();
			count.set(_closed.size());
			Platform.runLater(()->{
				closedPositionsTable.getItems().addAll(_closed.stream().map(ClosedPosition::new).collect(Collectors.toList()));
			});
		}
		
		// if runtime is running
		Execution.Manager.msg().subscribe(Topics.EXECUTED_ORDER.topic, (last, msg)->{
			List<org.dynami.core.portfolio.ClosedPosition> _closed = Execution.Manager.dynami().portfolio().getClosedPosition();
			int diff = _closed.size() - count.get();
			if(_closed.size() > 0 && diff > 0){
				ClockBuffer<ClosedPosition> buffer = DynamiApplication.timer().get("closed", ClosedPosition.class);
				for(int i = _closed.size()-diff; i < _closed.size(); i++){
					buffer.push(new ClosedPosition(_closed.get(i)));
					count.incrementAndGet();
				}
			}
		});
		
		DynamiApplication.timer().get("closed", ClosedPosition.class).addConsumer(list->{
			if(list != null && list.size() > 0 ){
				final List<ClosedPosition> tmp = new ArrayList<>(list);
				Platform.runLater(()->{
					closedPositionsTable.getItems().addAll(tmp);
				});
			}
		});
	}
	
	public void filterPositions(ActionEvent e){
		
	}
}
