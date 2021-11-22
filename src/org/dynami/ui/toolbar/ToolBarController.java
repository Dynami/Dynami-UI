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
package org.dynami.ui.toolbar;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ResourceBundle;

import org.dynami.runtime.IDataHandler;
import org.dynami.runtime.IExecutionManager;
import org.dynami.runtime.IService;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.models.StrategyComponents;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.DynamiApplication;
import org.dynami.ui.collectors.DataHandler;
import org.dynami.ui.collectors.Strategies;
import org.dynami.ui.controls.loading.Loading;
import org.dynami.ui.dialogs.ConfigDialog;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ToolBarController implements Initializable {
	private final ImageView loadIcon = new ImageView("icons/_load.gif");
	private final ImageView runIcon = new ImageView("icons/_run.gif");
	private final ImageView pauseIcon = new ImageView("icons/_pause.gif");
	private final ImageView resumeIcon = new ImageView("icons/_resume.gif");
	private final ImageView loadingIcon = new ImageView("icons/_loading.gif");
	private final String LOAD = "Load", RUN = "Run", RESUME = "Resume", PAUSE = "Pause";

	private boolean isStrategySelected = false;
	private boolean isDataHandlerSelected = false;

	private IDataHandler handler;
	private StrategyComponents strategyComponents;

	@FXML ToolBar toolbar;
	@FXML Button execButton, stopButton, confStratButton, confDataServiceButton;
	@FXML ComboBox<StrategyComponents> strategies;
	@FXML ComboBox<String> dataHandlers;
	@FXML Image execIcon, stopIcon;

	public void exec(ActionEvent e) throws Exception {
		if(LOAD.equals(execButton.getText())){
			final Loading loading = new Loading((Node)e.getSource());
			loading.centerOnScreen();
			loading.show();
			if(((IService)handler).isDisposed()){
				try {
					String handlerName = dataHandlers.selectionModelProperty().getValue().getSelectedItem();
					handler = DataHandler.Registry.getHandler(handlerName).getDeclaredConstructor().newInstance();
				} catch (InstantiationException | IllegalAccessException ex) {
					Execution.Manager.msg().async(Topics.INTERNAL_ERRORS.topic, ex);
				}
			}

			IService oldService = Execution.Manager.getServiceBus().registerService((IService)handler, 100);
			if(oldService != null){
				oldService.dispose();
				oldService = null;
			}

			Execution.Manager.msg().sync(DynamiApplication.RESET_TOPIC, null);

			final String strategyJarPath = strategies.getSelectionModel().getSelectedItem().jarName;

			boolean isOk = Execution.Manager.select(Strategies.Register.getSelectedValue().strategySettings, strategyJarPath);
			if(isOk){
				isOk = Execution.Manager.init(null);
			}
			if(isOk){
				isOk = Execution.Manager.load();
			}
			loading.close();
		} else if(RUN.equals(execButton.getText())){
			Execution.Manager.run();
		} else if(PAUSE.equals(execButton.getText())){
			Execution.Manager.pause();
		} else if(RESUME.equals(execButton.getText())){
			Execution.Manager.run();
		}
	}

	public void stop(ActionEvent e){
		if(Execution.Manager.stop()){
			Execution.Manager.dispose();
		}
	}


	private void checkSelection() {
		if(isStrategySelected && isDataHandlerSelected){
			execButton.setDisable(false);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		dataHandlers.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue != null && !newValue.equals("")){
					try {
						handler = DataHandler.Registry.getHandler(newValue).getDeclaredConstructor().newInstance();
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
						Execution.Manager.msg().async(Topics.INTERNAL_ERRORS.topic, e);
					}
				}
			}
		});

		strategies.valueProperty().addListener(new ChangeListener<StrategyComponents>() {
			@Override
			public void changed(ObservableValue<? extends StrategyComponents> observable, StrategyComponents oldValue, StrategyComponents newValue) {
				strategyComponents = newValue;
				Strategies.Register.setSelected(strategyComponents);
			}
		});

		execButton.setDisable(true);
		stopButton.setDisable(true);
		execButton.setGraphic(loadIcon);

		strategies.getItems().addAll(Strategies.Register.getStrategies());
		dataHandlers.getItems().addAll(DataHandler.Registry.dataHandlerNames());

		strategies.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
			isStrategySelected = (newValue != null && !newValue.equals(""));
			checkSelection();
		});
		dataHandlers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
			isDataHandlerSelected = (newValue != null && !newValue.equals(""));
			checkSelection();
		});

		Execution.Manager.addStateListener((oldState, newState)->{
			if(newState.equals(IExecutionManager.State.NonActive)){
				execButton.setGraphic(loadIcon);
				execButton.setText(LOAD);
				checkSelection();
				stopButton.setDisable(true);
				strategies.setDisable(false);
				dataHandlers.setDisable(false);
			} else if(newState.equals(IExecutionManager.State.Selected)){
				execButton.setGraphic(loadingIcon);
				execButton.setText(LOAD);
				execButton.setDisable(false);
				strategies.setDisable(false);
				dataHandlers.setDisable(false);
			} else if(newState.equals(IExecutionManager.State.Initialized)){
				// do nothing
			} else if(newState.equals(IExecutionManager.State.Loaded) ){
				execButton.setGraphic(runIcon);
				execButton.setText(RUN);
				execButton.setDisable(false);
				stopButton.setDisable(true);
				strategies.setDisable(true);
				dataHandlers.setDisable(true);
			} else if(newState.equals(IExecutionManager.State.Running)){
				execButton.setGraphic(pauseIcon);
				execButton.setText(PAUSE);
				execButton.setDisable(false);
				stopButton.setDisable(false);
				strategies.setDisable(true);
				dataHandlers.setDisable(true);
			} else if(newState.equals(IExecutionManager.State.Paused)){
				execButton.setGraphic(resumeIcon);
				execButton.setText(RESUME);
				execButton.setDisable(false);
				stopButton.setDisable(false);
				strategies.setDisable(true);
				dataHandlers.setDisable(true);
			} else if(newState.equals(IExecutionManager.State.Stopped)){
				execButton.setGraphic(loadIcon);
				execButton.setText(LOAD);
				execButton.setDisable(false);
				stopButton.setDisable(true);
				strategies.setDisable(false);
				dataHandlers.setDisable(false);
			}
		});
	}

	public void configStrategy(ActionEvent e) throws Exception {
		if(strategyComponents == null) return;
		
		ConfigDialog dialog = new ConfigDialog(strategyComponents);
		dialog.showAndWait();
	}

	public void configDataHandler(ActionEvent e){
		if(handler == null) return;
		try {
			ConfigDialog dialog = new ConfigDialog(handler);
			dialog.showAndWait();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}


