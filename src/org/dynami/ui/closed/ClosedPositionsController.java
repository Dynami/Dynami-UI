package org.dynami.ui.closed;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.dynami.core.utils.DUtils;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.DynamiApplication;
import org.dynami.ui.timer.UITimer.ClockBuffer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

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
	            	setText(String.format("%.3f", price.doubleValue()*100.));
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
	                setText(String.format("%.2f", price.doubleValue()));
	            }
	        }
	    });
		
		// if Runtime is not running
		if(Execution.Manager.isLoaded()){
			List<org.dynami.core.portfolio.ClosedPosition> __closed = Execution.Manager.dynami().portfolio().getClosedPosition();
			if(__closed != null && __closed.size() > 0){
				closedPositionsTable.getItems().addAll(__closed.stream().map(ClosedPosition::new).collect(Collectors.toList()));
			}
		}
		
		// if runtime is running
		Execution.Manager.msg().subscribe(Topics.EXECUTED_ORDER.topic, (last, msg)->{
			List<org.dynami.core.portfolio.ClosedPosition> _closed = Execution.Manager.dynami().portfolio().getClosedPosition();
			int diff = _closed.size() - count.get();
			if(_closed.size() > 0 && diff > 0){
				ClockBuffer<org.dynami.core.portfolio.ClosedPosition> buffer = DynamiApplication.timer().get("closed", org.dynami.core.portfolio.ClosedPosition.class);
				for(int i = _closed.size()-diff; i < _closed.size(); i++){
					buffer.push(_closed.get(i));
					count.incrementAndGet();
				}
			}
		});
		
		DynamiApplication.timer().get("closed", org.dynami.core.portfolio.ClosedPosition.class).add(list->{
			if(list != null && list.size() > 0 ){
				Platform.runLater(()->{
					for(org.dynami.core.portfolio.ClosedPosition cl : list){
						closedPositionsTable.getItems().add(new ClosedPosition(cl));
					}
					//closedPositionsTable.getItems().addAll(list.stream().map(ClosedPosition::new).collect(Collectors.toList()));
				});
			}
		});
	}
	
	public void filterPositions(ActionEvent e){
		
	}
	
	@Override
	protected void finalize() throws Throwable {
		System.out.println("ClosedPositionsController.finalize()");
	}
}
