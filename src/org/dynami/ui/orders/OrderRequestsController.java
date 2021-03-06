package org.dynami.ui.orders;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import org.dynami.core.services.IOrderService.Status;
import org.dynami.core.utils.DUtils;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.DynamiApplication;
import org.dynami.ui.prefs.data.PrefsConstants;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class OrderRequestsController implements Initializable {
	private final ObservableList<OrderRequest> data = FXCollections.observableArrayList();
	private final FilteredList<OrderRequest> filteredData = new FilteredList<>(data, p->true);
	private int MAX_ROWS = 30;
	@FXML TextField filterText;
	@FXML TableView<OrderRequest> table;
	@FXML TableColumn<OrderRequest, Number> requestIdColumn;
//	@FXML TableColumn<OrderRequest, String> requestTypeColumn;
	@FXML TableColumn<OrderRequest, String> assetColumn;
	@FXML TableColumn<OrderRequest, Number> quantityColumn;
	@FXML TableColumn<OrderRequest, Number> entryPriceColumn;
	@FXML TableColumn<OrderRequest, Number> entryTimeColumn;
	@FXML TableColumn<OrderRequest, Number> executionTimeColumn;
	@FXML TableColumn<OrderRequest, String> notesColumn;
	@FXML TableColumn<OrderRequest, String> statusColumn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Preferences appPrefs = Preferences.userRoot().node(PrefsConstants.PREFS_NODE);
		MAX_ROWS = appPrefs.getInt(PrefsConstants.TRACES.MAX_ROWS, 50);

		Execution.Manager.msg().subscribe(DynamiApplication.RESET_TOPIC, (last, msg)->{
			Platform.runLater(()->data.clear());
		});

		Execution.Manager.msg().subscribe(Topics.ORDER_REQUESTS.topic, (last, msg)->{
			org.dynami.core.orders.OrderRequest request = (org.dynami.core.orders.OrderRequest)msg;
			DynamiApplication.timer().get("order_requests", OrderRequest.class).push(new OrderRequest(request));
		});

		DynamiApplication.timer().addClockedFunction(()->{
			Platform.runLater(()->{
				data.forEach(r->{
					if(!r.getStatus().equals(Status.Executed.name())
							&& !r.getStatus().equals(Status.Cancelled.name())
							&& !r.getStatus().equals(Status.Rejected.name())){
						org.dynami.core.orders.OrderRequest request = Execution.Manager.dynami().orders().getOrderById(r.getRequestID());
						Status status = request.getStatus();
						if(!status.name().equals(r.getStatus())){
							Platform.runLater(()->{
								r.setStatus(status.name());
								r.setExecutionTime(request.getExecutionTime());
							});
						}
					}
				});
			});
		});

		DynamiApplication.timer().get("order_requests", OrderRequest.class).addConsumer(list->{
			if(list != null && list.size() > 0 ){
				final List<OrderRequest> tmp = new ArrayList<>(list);
				Platform.runLater(()->{
					int diff = data.size()+tmp.size() - MAX_ROWS;
					if(diff > 0){
						data.remove(0, diff-1);
					}
					data.addAll(tmp);
				});
			}
		});

		table.setItems(filteredData);
		requestIdColumn.setCellValueFactory(new PropertyValueFactory<>("requestID"));
//		requestTypeColumn.setCellValueFactory(new PropertyValueFactory<>("requestType"));
		assetColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

		entryTimeColumn.setCellValueFactory(cell->cell.getValue().entryTime);
		entryTimeColumn.setCellFactory(col -> new TableCell<OrderRequest, Number>() {
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
		executionTimeColumn.setCellValueFactory(cell->cell.getValue().executionTime);
		executionTimeColumn.setCellFactory(col -> new TableCell<OrderRequest, Number>() {
	        @Override
	        public void updateItem(Number time, boolean empty) {
	            super.updateItem(time, empty);
	            if (empty) {
	                setText(null);
	            } else {
	            	if(time.longValue() > 0){
	            		setText(DUtils.LONG_DATE_FORMAT.format(time.longValue()));
	            	}
	            }
	        }
	    });
		entryPriceColumn.setCellValueFactory(cell->cell.getValue().entryPrice);
		entryPriceColumn.setCellFactory(col -> new TableCell<OrderRequest, Number>() {
	        @Override
	        public void updateItem(Number value, boolean empty) {
	            super.updateItem(value, empty);
	            if (empty) {
	                setText(null);
	            } else {

	                setText(String.format("%.2f", value.doubleValue()));
	            }
	        }
	    });
	}

	public void filter(ActionEvent e){

	}
}
