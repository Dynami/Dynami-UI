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
package org.dynami.ui.portfolio;

import java.net.URL;
import java.util.ResourceBundle;

import org.dynami.runtime.topics.Topics;
import org.dynami.ui.DynamiApplication;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PortfolioController implements Initializable {
	@FXML TableView<OpenPosition> openPositions;
	
	@FXML TableColumn<OpenPosition, String> entryTimeColumn;
	@FXML TableColumn<OpenPosition, String> symbolColumn;
	@FXML TableColumn<OpenPosition, String> qtColumn;
	@FXML TableColumn<OpenPosition, String> priceColumn;
	@FXML TableColumn<OpenPosition, String> percColumn;
	@FXML TableColumn<OpenPosition, String> returnColumn;
	
	@Override
	public void initialize(URL url, ResourceBundle resource) {
		entryTimeColumn.setCellValueFactory(new PropertyValueFactory<>("entryTime"));
		symbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
		qtColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		
		DynamiApplication.timer().get(Topics.EXECUTED_ORDER.topic, org.dynami.core.portfolio.OpenPosition.class).add((list)->{
			final ObservableList<OpenPosition> pos = FXCollections.observableArrayList();
			list.forEach(o->{
				pos.add(new OpenPosition(o));
			});
			Platform.runLater(()->{
				openPositions.getItems().clear();
				openPositions.getItems().addAll(pos);
			});
		});
	}
}
