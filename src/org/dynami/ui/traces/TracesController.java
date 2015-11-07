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
package org.dynami.ui.traces;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import org.dynami.core.services.ITraceService;
import org.dynami.core.utils.DUtils;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.DynamiApplication;
import org.dynami.ui.prefs.PrefsConstants;
import org.dynami.ui.timer.UITimer.ClockBuffer;

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
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class TracesController implements Initializable {
	@FXML TextField filterText;
	@FXML TableView<Trace> table;
	@FXML TableColumn<Trace, String> typeColumn;
	@FXML TableColumn<Trace, Number> timeColumn;
	@FXML TableColumn<Trace, String> stageColumn;
	@FXML TableColumn<Trace, String> lineColumn;
	@FXML ToggleButton infoFilter;
	@FXML ToggleButton debugFilter;
	@FXML ToggleButton warnFilter;
	@FXML ToggleButton errorFilter;
	
	private Background infoColor, debugColor, warnColor, errorColor;
	private int MAX_ROWS = 30;
	
	private final ObservableList<Trace> data = FXCollections.observableArrayList();
	private final FilteredList<Trace> filteredData = new FilteredList<>(data, p->true);
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Preferences appPrefs = Preferences.userRoot().node(DynamiApplication.class.getName());
		infoColor = new Background(new BackgroundFill(Color.web(appPrefs.get(PrefsConstants.TRACES.COLOR.INFO, Color.BEIGE.toString())), null, null ));
		debugColor = new Background(new BackgroundFill(Color.web(appPrefs.get(PrefsConstants.TRACES.COLOR.DEBUG, Color.BEIGE.toString())), null, null));
		warnColor = new Background(new BackgroundFill(Color.web(appPrefs.get(PrefsConstants.TRACES.COLOR.WARN, Color.BEIGE.toString())), null, null));
		errorColor = new Background(new BackgroundFill(Color.web(appPrefs.get(PrefsConstants.TRACES.COLOR.ERROR, Color.BEIGE.toString())), null, null));
		
		MAX_ROWS = appPrefs.getInt(PrefsConstants.TRACES.MAX_ROWS, 50);
		// load previous closed positions on start-up 
		if(Execution.Manager.isLoaded()){
//			List<ITraceService.Trace> _closed = Execution.Manager.dynami().trace();
//			count.set(_closed.size());
//			Platform.runLater(()->{
//				data.addAll(_closed.stream().map(Trace::new).collect(Collectors.toList()));
//			});
		}
		
		// if runtime is running
		Execution.Manager.msg().subscribe(Topics.TRACE.topic, (last, msg)->{
			ITraceService.Trace t = (ITraceService.Trace)msg;
			ClockBuffer<Trace> buffer = DynamiApplication.timer().get("traces", Trace.class);
			buffer.push(new Trace(t));
		});
		
		DynamiApplication.timer().get("traces", Trace.class).addConsumer(list->{
			if(list != null && list.size() > 0 ){
				final List<Trace> tmp = new ArrayList<>(list);
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
		table.setRowFactory(new Callback<TableView<Trace>, TableRow<Trace>>() {
			@Override
			public TableRow<Trace> call(TableView<Trace> param) {
				return new TableRow<Trace>(){
					@Override
					protected void updateItem(Trace item, boolean empty) {
						super.updateItem(item, empty);
						if(!empty){
							if(ITraceService.Trace.Type.Info.name().equals(item.getType())){
								setBackground(infoColor);
							} else if(ITraceService.Trace.Type.Debug.name().equals(item.getType())){
								setBackground(debugColor);
							} else if(ITraceService.Trace.Type.Warn.name().equals(item.getType())){
								setBackground(warnColor);
							} else if(ITraceService.Trace.Type.Error.name().equals(item.getType())){
								setBackground(errorColor);
							} else {
								
							}
						}
					}
				};
			}
		});

		typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
		stageColumn.setCellValueFactory(new PropertyValueFactory<>("stage"));
		lineColumn.setCellValueFactory(new PropertyValueFactory<>("line"));
		
		timeColumn.setCellValueFactory(cell->cell.getValue().timeProperty());
		timeColumn.setCellFactory(col -> new TableCell<Trace, Number>() {
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
	
	public void filterPositions(ActionEvent e){
		final String newValue = filterText.getText();
		final boolean textFilter = (newValue != null && !newValue.trim().equals(""));
		final boolean excludeInfo = infoFilter.isSelected();
		final boolean excludeDebug = debugFilter.isSelected();
		final boolean excludeWarn = warnFilter.isSelected();
		final boolean excludeError = errorFilter.isSelected();
		
		if(!textFilter && !excludeInfo && !excludeDebug && !excludeWarn && !excludeError){
			filteredData.setPredicate(p->true);
		} else {
			filteredData.setPredicate(c-> {
				if(excludeInfo && c.getType().equals(ITraceService.Trace.Type.Info.name())){
					return false;
				}
				if(excludeDebug && c.getType().equals(ITraceService.Trace.Type.Debug.name())){
					return false;
				}
				if(excludeWarn && c.getType().equals(ITraceService.Trace.Type.Warn.name())){
					return false;
				}
				if(excludeError && c.getType().equals(ITraceService.Trace.Type.Error.name())){
					return false;
				}
				if(textFilter && c.getLine().contains(newValue)){
					return true;
				} else {
					return false;
				}
			});
		}
	}
}
