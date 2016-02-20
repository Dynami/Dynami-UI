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
package org.dynami.ui.errors;

import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.dialog.ExceptionDialog;
import org.dynami.core.utils.DUtils;
import org.dynami.runtime.impl.Execution;
import org.dynami.ui.DynamiApplication;

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
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;

public class ErrorsController implements Initializable {
	@FXML ToggleButton strategyFilter, internalFilter, userInterfaceFilter;
	@FXML TableView<ErrorInfo> table;
	@FXML TableColumn<ErrorInfo, Number> timeColumn;
	@FXML TableColumn<ErrorInfo, String> typeColumn, msgColumn;

	private final ObservableList<ErrorInfo> data = FXCollections.observableArrayList();
	private final FilteredList<ErrorInfo> filteredData = new FilteredList<>(data, p->true);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Execution.Manager.msg().subscribe(DynamiApplication.RESET_TOPIC, (last, msg)->{
			Platform.runLater(()->data.clear());
		});


		DynamiApplication.getStartUpErrors().forEach(e->data.add(new ErrorInfo(e, ErrorInfo.Type.UserInterface)));

		table.setItems(filteredData);

		table.setRowFactory( tv -> {
		    TableRow<ErrorInfo> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
		        	new ExceptionDialog(row.getItem()._error).showAndWait();
		        }
		    });
		    return row ;
		});

		typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
		msgColumn.setCellValueFactory(new PropertyValueFactory<>("msg"));
		timeColumn.setCellValueFactory(cell->cell.getValue().time());
		timeColumn.setCellFactory(col -> new TableCell<ErrorInfo, Number>() {
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
	}

	public void addErrorInfo(final ErrorInfo errorInfo){
		data.add(errorInfo);
	}

	public void filter(ActionEvent e){
		final boolean excludeStrategyErrors = strategyFilter.isSelected();
		final boolean excludeInternalErrors = internalFilter.isSelected();
		final boolean excludeUIErrors = userInterfaceFilter.isSelected();
		filteredData.setPredicate(c->{
			if( (!excludeStrategyErrors && c.getType().equals(ErrorInfo.Type.Strategy.name()))
				|| (!excludeInternalErrors && c.getType().equals(ErrorInfo.Type.Internal.name()))
				|| (!excludeUIErrors && c.getType().equals(ErrorInfo.Type.UserInterface.name()))){
				return true;
			} else {
				return false;
			}
		});
	}
}
