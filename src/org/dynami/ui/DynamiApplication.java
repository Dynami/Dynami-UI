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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.controlsfx.dialog.ExceptionDialog;
import org.dynami.runtime.handlers.TextFileDataHandler;
import org.dynami.runtime.impl.Execution;
import org.dynami.ui.collectors.DataHandler;
import org.dynami.ui.collectors.Strategies;
import org.dynami.ui.prefs.data.PrefsConstants;
import org.dynami.ui.timer.UITimer;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
/* VM args: -Djavafx.verbose=true -Dprism.verbose=true
 * For Mac Users on run configurations Arguments tab deselect -XstartOnFirstThead check. 
 */
public class DynamiApplication extends Application {
	private static final UITimer _timer = new UITimer(250);
	public static final String RESET_TOPIC = "RESET_UI";
	private static final List<Throwable> startUpErrors = new ArrayList<>();
	private static Stage _primaryStage;
	public static DoubleProperty priceLowerBound = new SimpleDoubleProperty();
	public static DoubleProperty priceUpperBound = new SimpleDoubleProperty();
	public static DoubleProperty priceTickUnit = new SimpleDoubleProperty();

	@SuppressWarnings("unused")
	private final DynamiActions actions;

	public DynamiApplication(){
		System.out.println("DynamiApplication.DynamiApplication()");
		actions = new DynamiActions();
	}

	@Override
	public void init() throws Exception {
		super.init();
		try { Strategies.Register.scanStrategyDirectory(); } catch (Throwable e) { startUpErrors.add(e); }
		try { DataHandler.Registry.register(TextFileDataHandler.class);} catch (Throwable e) { startUpErrors.add(e); }
		try { Execution.Manager.getServiceBus().registerDefaultServices();} catch (Throwable e) { startUpErrors.add(e); }
	}

	public static UITimer timer(){
		return _timer;
	}

	@Override
	public void start(Stage primaryStage) {
		System.out.println("DynamiApplication.start()");
		try {
			moveToPreloader();
			setUserAgentStylesheet(STYLESHEET_CASPIAN);
			_timer.start();
			_primaryStage = primaryStage;
			
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("/org/dynami/ui/main/MainWindow.fxml"));
			Scene scene = new Scene(root,1024,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			_primaryStage.getIcons().add(new Image("/icons/_dynami.png"));
			_primaryStage.setTitle("Dynami");
			_primaryStage.setScene(scene);
			_primaryStage.sizeToScene();
			_primaryStage.show();
		} catch(Throwable e) {
			e.printStackTrace();
			new ExceptionDialog(e).showAndWait();
		}
	}

	private void moveToPreloader() throws Exception {
 		final File preferencesFile = new File(PrefsConstants.PREFS_FILE_PATH);
		if(preferencesFile.exists()){
			Preferences.importPreferences(new FileInputStream(preferencesFile));
		}
	}

	public static Stage getPrimaryStage() {
		return _primaryStage;
	}


	@Override
	public void stop() throws Exception {
		_timer.dispose();
		Execution.Manager.stop();
		Execution.Manager.dispose();
		Execution.Manager.msg().dispose();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	public static List<Throwable> getStartUpErrors(){
		try {
			return startUpErrors;
		} finally {
			//startUpErrors.clear();
		}
	}

	private static NumberAxis priceAxis;
	public static NumberAxis getSingletonAxis(){
		if(priceAxis == null){
			priceAxis = new NumberAxis();
		}
		return priceAxis;
	}
}
