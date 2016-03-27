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

import org.dynami.core.utils.DUtils;
import org.dynami.runtime.impl.Execution;
import org.dynami.ui.DynamiApplication;
import org.dynami.ui.UIUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
	@FXML TableView<ClosedPosition> table;
	@FXML TableColumn<ClosedPosition, String> assetColumn;
	@FXML TableColumn<ClosedPosition, Long> quantityColumn;
	@FXML TableColumn<ClosedPosition, Number> entryPriceColumn;
	@FXML TableColumn<ClosedPosition, Number> entryTimeColumn;
	@FXML TableColumn<ClosedPosition, Number> exitPriceColumn;
	@FXML TableColumn<ClosedPosition, Number> exitTimeColumn;
	@FXML TableColumn<ClosedPosition, Number> percReturnColumn;
	@FXML TableColumn<ClosedPosition, Number> returnColumn;
	private final ObservableList<ClosedPosition> data = FXCollections.observableArrayList();
	private final FilteredList<ClosedPosition> filteredData = new FilteredList<>(data, p->true);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Execution.Manager.msg().subscribe(DynamiApplication.RESET_TOPIC, (last, msg)->{
			Platform.runLater(()->data.clear());
			Platform.runLater(()-> filteredData.clear());
		});

		DynamiApplication.timer().addClockedFunction(()->{
			if(Execution.Manager.isLoaded()){
				final List<org.dynami.core.portfolio.ClosedPosition> _closed = Execution.Manager.dynami().portfolio().getClosedPositions();
				final List<ClosedPosition> tmp = new ArrayList<>();
				if(data.size() < _closed.size()){
					for(int i = data.size(); i < _closed.size(); i++){
						tmp.add(new ClosedPosition(_closed.get(i)));
					}
					Platform.runLater(()->{
						data.addAll(tmp);
					});
				}
			}
		});

		table.setItems(filteredData);
		table.setRowFactory(new Callback<TableView<ClosedPosition>, TableRow<ClosedPosition>>() {
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
						} else {
							setBackground(UIUtils.defaultBackground);
						}
					}
				};
			}
		});

		assetColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
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
	}

	public void filterPositions(ActionEvent e){
		final String newValue = filterText.getText();
		if(newValue == null || newValue.trim().equals("")){
			filteredData.setPredicate(p->true);
		} else {
			filteredData.setPredicate(c-> {
				return c.getSymbol().startsWith(newValue);
			});
		}
	}
}
