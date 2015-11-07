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
package org.dynami.ui;
	
import java.util.prefs.Preferences;

import org.dynami.runtime.handlers.TextFileDataHandler;
import org.dynami.runtime.impl.Execution;
import org.dynami.ui.collectors.DataHandler;
import org.dynami.ui.collectors.Strategies;
import org.dynami.ui.prefs.PrefsConstants;
import org.dynami.ui.timer.UITimer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DynamiApplication extends Application {
	private static final UITimer _timer = new UITimer(1000);
	private static Stage _primaryStage;
	
	@Override
	public void init() throws Exception {
		super.init();
		Preferences appPrefs = Preferences.userRoot().node(DynamiApplication.class.getName());
		appPrefs.put(PrefsConstants.BASIC.STRATS_DIR, "D:/dynami-repo/Dynami-Sample-Strategy/resources/");
		
		appPrefs.putInt(PrefsConstants.TRACES.MAX_ROWS, 100);
		appPrefs.put(PrefsConstants.TRACES.COLOR.INFO, Color.LIGHTSKYBLUE.toString());
		appPrefs.put(PrefsConstants.TRACES.COLOR.DEBUG, Color.LIGHTGRAY.toString());
		appPrefs.put(PrefsConstants.TRACES.COLOR.WARN, Color.ORANGE.toString());
		appPrefs.put(PrefsConstants.TRACES.COLOR.ERROR, Color.ORANGERED.toString());
		
		Strategies.Register.scanStrategyDirectory();
		DataHandler.Registry.register(TextFileDataHandler.class);
		Execution.Manager.getServiceBus().registerDefaultServices();
	}
	
	
	public static UITimer timer(){
		return _timer;
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			_timer.start();
			_primaryStage = primaryStage;
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("main/MainWindow.fxml"));
			Scene scene = new Scene(root,1024,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.getIcons().add(new Image("icons/_dynami.png"));
			primaryStage.setTitle("Dynami");
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Stage getPrimaryStage() {
		return _primaryStage;
	}
	
	
	@Override
	public void stop() throws Exception {
		_timer.dispose();
		Execution.Manager.msg().dispose();
		Execution.Manager.dispose();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
