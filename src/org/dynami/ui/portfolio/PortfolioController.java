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
import java.text.NumberFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.dynami.core.assets.Asset;
import org.dynami.core.services.IAssetService;
import org.dynami.core.services.IPortfolioService;
import org.dynami.core.utils.DUtils;
import org.dynami.runtime.impl.Execution;
import org.dynami.ui.DynamiApplication;
import org.dynami.ui.UIUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class PortfolioController implements Initializable {
	private final ObservableList<OpenPosition> positions = FXCollections.observableArrayList();
	@FXML TableView<OpenPosition> table;

	@FXML TableColumn<OpenPosition, String> typeColumn;
	@FXML TableColumn<OpenPosition, String> symbolColumn;
	@FXML TableColumn<OpenPosition, Number> qtColumn;
	@FXML TableColumn<OpenPosition, Number> entryTimeColumn;
	@FXML TableColumn<OpenPosition, Number> entryPriceColumn;
	@FXML TableColumn<OpenPosition, Number> currentPriceColumn;
	@FXML TableColumn<OpenPosition, Number> percColumn;
	@FXML TableColumn<OpenPosition, Number> roiColumn;

	@FXML TableColumn<OpenPosition, Number> deltaColumn;
	@FXML TableColumn<OpenPosition, Number> gammaColumn;
	@FXML TableColumn<OpenPosition, Number> vegaColumn;
	@FXML TableColumn<OpenPosition, Number> thetaColumn;
	@FXML TableColumn<OpenPosition, Number> rhoColumn;

	@Override
	public void initialize(URL url, ResourceBundle resource) {
		table.setItems(positions);

		Execution.Manager.msg().subscribe(DynamiApplication.RESET_TOPIC, (last, msg)->{
			Platform.runLater(()-> positions.clear());
		});

		DynamiApplication.timer().addClockedFunction(()->{
			if(!Execution.Manager.isLoaded()) return;

			final IPortfolioService portfolio = Execution.Manager.dynami().portfolio();
			final List<OpenPosition> list = portfolio.getOpenPositions()
					.stream()
					.map(OpenPosition::new)
					.collect(Collectors.toList());
			Platform.runLater(()->{
				list.forEach(o->{
					// calculate roi and percent roi
					final double roi = portfolio.unrealised(o.getSymbol());
					final double percRoi = ((o.getCurrentPrice()/o.getEntryPrice())-1)*((o.getQuantity()>0)?1:-1);
					// check if Option and in this case add greeks
					o.setRoi(roi);
					o.setPercRoi(percRoi);
					if(Asset.Family.Option.name().equals(o.getAssetType())){
						final IAssetService assets = Execution.Manager.dynami().assets();
						Asset.Option opt = (Asset.Option)assets.getBySymbol(o.getSymbol());
						if(opt != null){
							o.setDelta(opt.greeks.delta()*o.getQuantity()*o.getPointValue());
							o.setGamma(opt.greeks.gamma()*o.getQuantity()*o.getPointValue());
							o.setVega(opt.greeks.vega()*o.getQuantity()*o.getPointValue());
							o.setTheta(opt.greeks.theta()*o.getQuantity()*o.getPointValue());
							o.setRho(opt.greeks.rho()*o.getQuantity()*o.getPointValue());
						}
					} else {
						o.setDelta(o.getQuantity()*o.getPointValue());
					}
				});
				positions.clear();
				positions.addAll(list);
			});
		});

		table.setRowFactory(new Callback<TableView<OpenPosition>, TableRow<OpenPosition>>() {
			@Override
			public TableRow<OpenPosition> call(TableView<OpenPosition> param) {
				return new TableRow<OpenPosition>(){
					@Override
					protected void updateItem(OpenPosition item, boolean empty) {
						super.updateItem(item, empty);
						if(!empty){
							if(item.getPercRoi() >= 0){
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

		typeColumn.setCellValueFactory(new PropertyValueFactory<>("assetType"));
		symbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
		qtColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		entryTimeColumn.setCellValueFactory(cell->cell.getValue().entryTime());
		entryTimeColumn.setCellFactory(col -> new TableCell<OpenPosition, Number>() {
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
		entryPriceColumn.setCellValueFactory(cell->cell.getValue().entryPrice);
		entryPriceColumn.setCellFactory(col -> new TableCell<OpenPosition, Number>() {
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

		currentPriceColumn.setCellValueFactory(cell->cell.getValue().currentPrice);
		currentPriceColumn.setCellFactory(col -> new TableCell<OpenPosition, Number>() {
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

		percColumn.setCellValueFactory(cell->cell.getValue().percRoi());
		percColumn.setCellFactory(col -> new TableCell<OpenPosition, Number>() {
	        @Override
	        public void updateItem(Number value, boolean empty) {
	            super.updateItem(value, empty);
	            if (empty) {
	                setText(null);
	            } else {
	            	setText(UIUtils.PERC_NUMBER_FORMAT.format(value.doubleValue()));
	            }
	        }
	    });

		roiColumn.setCellValueFactory(cell->cell.getValue().roi());
		roiColumn.setCellFactory(col -> new TableCell<OpenPosition, Number>() {
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

		deltaColumn.setCellValueFactory(cell->cell.getValue().delta());
		deltaColumn.setCellFactory(col -> new TableCell<OpenPosition, Number>() {
	        @Override
	        public void updateItem(Number value, boolean empty) {
	            super.updateItem(value, empty);
	            if (empty) {
	                setText(null);
	            } else {
	                setText(DUtils.NUMBER_FORMAT.format(value));
	            }
	        }
	    });

		gammaColumn.setCellValueFactory(cell->cell.getValue().gamma());
		gammaColumn.setCellFactory(col -> new TableCell<OpenPosition, Number>() {
	        @Override
	        public void updateItem(Number value, boolean empty) {
	            super.updateItem(value, empty);
	            if (empty) {
	                setText(null);
	            } else {
	                setText(DUtils.NUMBER_FORMAT.format(value));
	            }
	        }
	    });

		vegaColumn.setCellValueFactory(cell->cell.getValue().vega());
		vegaColumn.setCellFactory(col -> new TableCell<OpenPosition, Number>() {
	        @Override
	        public void updateItem(Number value, boolean empty) {
	            super.updateItem(value, empty);
	            if (empty) {
	                setText(null);
	            } else {
	                setText(DUtils.NUMBER_FORMAT.format(value));
	            }
	        }
	    });

		thetaColumn.setCellValueFactory(cell->cell.getValue().theta());
		thetaColumn.setCellFactory(col -> new TableCell<OpenPosition, Number>() {
	        @Override
	        public void updateItem(Number value, boolean empty) {
	            super.updateItem(value, empty);
	            if (empty) {
	                setText(null);
	            } else {
	                setText(DUtils.NUMBER_FORMAT.format(value));
	            }
	        }
	    });

		rhoColumn.setCellValueFactory(cell->cell.getValue().rho());
		rhoColumn.setCellFactory(col -> new TableCell<OpenPosition, Number>() {
	        @Override
	        public void updateItem(Number value, boolean empty) {
	            super.updateItem(value, empty);
	            if (empty) {
	                setText(null);
	            } else {
	                setText(DUtils.NUMBER_FORMAT.format(value));
	            }
	        }
	    });
	}
}
