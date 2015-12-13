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

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.dynami.core.config.Config;
import org.dynami.runtime.IDataHandler;
import org.dynami.runtime.IExecutionManager;
import org.dynami.runtime.IService;
import org.dynami.runtime.config.ClassSettings;
import org.dynami.runtime.config.ParamSettings;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.models.StrategyComponents;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.collectors.DataHandler;
import org.dynami.ui.collectors.Strategies;
import org.dynami.ui.controls.config.BooleanFieldParam;
import org.dynami.ui.controls.config.DoubleSpinnerFieldParam;
import org.dynami.ui.controls.config.FieldParam;
import org.dynami.ui.controls.config.FileFieldParam;
import org.dynami.ui.controls.config.IntegerSpinnerFieldParam;
import org.dynami.ui.controls.config.LongSpinnerFieldParam;
import org.dynami.ui.controls.config.PropertyParam;
import org.dynami.ui.controls.config.TextFieldParam;
import org.dynami.ui.controls.config.TimeFrameParam;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class ToolBarController implements Initializable {
	private final ImageView loadIcon = new ImageView("icons/_load.gif");
	private final ImageView runIcon = new ImageView("icons/_run.gif");
	private final ImageView pauseIcon = new ImageView("icons/_pause.gif");
	private final ImageView resumeIcon = new ImageView("icons/_resume.gif");
	private final String LOAD = "Load", RUN = "Run", RESUME = "Resume", PAUSE = "Pause";

	private boolean isStrategySelected = false;
	private boolean isDataHandlerSelected = false;

	private IDataHandler handler;
	private StrategyComponents strategyComponents;
	
	@FXML
	ToolBar toolbar;

	@FXML
	Button execButton, stopButton, confStratButton, confDataServiceButton;

	@FXML
	ComboBox<StrategyComponents> strategies;

	@FXML
	ComboBox<String>
	dataHandlers;

	@FXML
	TextField strategyName;

	@FXML
	Image execIcon, stopIcon;

	public void exec(ActionEvent e) throws Exception {
		if(LOAD.equals(execButton.getText())){
			Execution.Manager.getServiceBus().registerService((IService)handler, 100);
			
			final String strategyJarPath = strategies.getSelectionModel().getSelectedItem().jarName;

			boolean isOk = Execution.Manager.select(null, strategyJarPath);
			if(isOk){
				isOk = Execution.Manager.init(null);
			}
			if(isOk){
				isOk = Execution.Manager.load();
			}
		} else if(RUN.equals(execButton.getText())){
			Execution.Manager.run();
		} else if(PAUSE.equals(execButton.getText())){
			Execution.Manager.pause();
		} else if(RESUME.equals(execButton.getText())){
			Execution.Manager.run();
		}
	}

	public void stop(ActionEvent e){
		Execution.Manager.stop();
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
						handler = DataHandler.Registry.getHandler(newValue).newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						Execution.Manager.msg().async(Topics.ERRORS.topic, e);
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
				execButton.setDisable(true);
				stopButton.setDisable(true);
			} else if(newState.equals(IExecutionManager.State.Selected)){
				execButton.setGraphic(loadIcon);
				execButton.setText(LOAD);
				execButton.setDisable(false);
			} else if(newState.equals(IExecutionManager.State.Initialized)){

			} else if(newState.equals(IExecutionManager.State.Loaded)){
				execButton.setGraphic(runIcon);
				execButton.setText(RUN);
				execButton.setDisable(false);
				stopButton.setDisable(true);
			} else if(newState.equals(IExecutionManager.State.Running)){
				execButton.setGraphic(pauseIcon);
				execButton.setText(PAUSE);
				execButton.setDisable(false);
				stopButton.setDisable(false);
			} else if(newState.equals(IExecutionManager.State.Paused)){
				execButton.setGraphic(resumeIcon);
				execButton.setText(RESUME);
				execButton.setDisable(false);
				stopButton.setDisable(false);
			} else if(newState.equals(IExecutionManager.State.Stopped)){
				execButton.setGraphic(loadIcon);
				execButton.setText(LOAD);
				stopButton.setDisable(true);
				checkSelection();
			}
		});
	}

	public void configStrategy(ActionEvent e) throws Exception {
		if(strategyComponents == null) return;
		VBox vbox = new VBox();
		PopOver popOver = new PopOver(vbox);
		for(ClassSettings c: strategyComponents.strategySettings.getSettings().values()){
			VBox inner = new VBox();
			Label label = new Label(c.getName());
			label.getStyleClass().add("config-stage-title");
			label.prefWidthProperty().bind(vbox.widthProperty());
			inner.getChildren().add(label);
			for(ParamSettings ps : c.getParams().values()){
				Config.Param p = ps.getParam();
				if(p != null){
					try {
						FieldParam param;
						String name = !p.name().equals("")?p.name():ps.getName();
						String description = p.description();
						
						if(p.type().equals(Config.Type.TimeFrame)){
							param = new TimeFrameParam(new PropertyParam<Long>(name, description, c, ps.getFieldName()), (long)p.min(), (long)p.max(), (long)p.step());
						} else {
							if(ps.getType().equals(Double.class) || ps.getType().equals(double.class)){
								param = new DoubleSpinnerFieldParam(new PropertyParam<Double>(name, description, c, ps.getFieldName()), p.min(), p.max(), p.step());
							} else if(ps.getType().equals(Long.class) || ps.getType().equals(long.class)){
								param = new LongSpinnerFieldParam(new PropertyParam<Long>(name, description, c, ps.getFieldName()), (long)p.min(), (long)p.max(), (long)p.step());
							} else if(ps.getType().equals(Integer.class) || ps.getType().equals(int.class)){
								param = new IntegerSpinnerFieldParam(new PropertyParam<Integer>(name, description, c, ps.getFieldName()), (int)p.min(), (int)p.max(), (int)p.step());
							} else if(ps.getType().equals(Boolean.class) || ps.getType().equals(boolean.class)){
								param = new BooleanFieldParam(new PropertyParam<Boolean>(name, description, c, ps.getFieldName()));
							} else if(ps.getType().equals(File.class)){
								param = new FileFieldParam(new PropertyParam<File>(name, description, c, ps.getFieldName()));
							} else {
								param = new TextFieldParam(new PropertyParam<String>(name, description, c, ps.getFieldName()));
							} 
						}
						inner.getChildren().add(param);
					} catch (Exception e1) {
						Execution.Manager.msg().async(Topics.ERRORS.topic, e1);
					}
				}
			}
			vbox.getChildren().add(inner);
		}
		
		Button b = (Button)e.getSource();
		popOver.setArrowLocation(ArrowLocation.TOP_LEFT);
		popOver.show(b);
	}

	public void configDataHandler(ActionEvent e){
		if(handler == null) return;

		Field[] fields = handler.getClass().getDeclaredFields();
		VBox vbox = new VBox(5);
		for (Field f : fields) {
			Config.Param p = f.getAnnotation(Config.Param.class);
			if (p != null) {
				try {
					FieldParam param;
					String name = !p.name().equals("")?p.name():f.getName();
					String description = p.description();
					
					if(p.type().equals(Config.Type.TimeFrame)){
						param = new TimeFrameParam(new PropertyParam<Long>(name, description, handler, f), (long)p.min(), (long)p.max(), (long)p.step());
					} else {
						if(f.getType().equals(Double.class) || f.getType().equals(double.class)){
							param = new DoubleSpinnerFieldParam(new PropertyParam<Double>(name, description, handler, f), p.min(), p.max(), p.step());
						} else if(f.getType().equals(Long.class) || f.getType().equals(long.class)){
							param = new LongSpinnerFieldParam(new PropertyParam<Long>(name, description, handler, f), (long)p.min(), (long)p.max(), (long)p.step());
						} else if(f.getType().equals(Integer.class) || f.getType().equals(int.class)){
							param = new IntegerSpinnerFieldParam(new PropertyParam<Integer>(name, description, handler, f), (int)p.min(), (int)p.max(), (int)p.step());
						} else if(f.getType().equals(Boolean.class) || f.getType().equals(boolean.class)){
							param = new BooleanFieldParam(new PropertyParam<Boolean>(name, description, handler, f));
						} else if(f.getType().equals(File.class)){
							param = new FileFieldParam(new PropertyParam<File>(name, description, handler, f));
						} else {
							param = new TextFieldParam(new PropertyParam<String>(name, description, handler, f));
						} 
					}
					
					vbox.getChildren().add(param);
				} catch (Exception e1) {
					Execution.Manager.msg().async(Topics.ERRORS.topic, e1);
				}
			}
		}

		PopOver popOver = new PopOver(vbox);
		Button b = (Button)e.getSource();

		popOver.setArrowLocation(ArrowLocation.TOP_LEFT);
		popOver.show(b);
	}
}


