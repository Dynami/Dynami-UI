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
import java.io.FilenameFilter;

import org.dynami.runtime.utils.ClassPathHack;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DynamiPreloader extends Preloader {
	ProgressBar bar;
	Stage stage;

	private Scene createPreloaderScene() {
		bar = new ProgressBar();
		BorderPane p = new BorderPane();
		p.setStyle("-fx-background-color: rgb(90,90,90);");
		p.setCenter(bar);
		Scene scene = new Scene(p, 300, 150, Color.ORANGE);

		return scene;
	}

	public void start(Stage stage) throws Exception {
		this.stage = stage;
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setScene(createPreloaderScene());
		stage.show();
		final File ext_libs = new File("./ext-libs");
		if(ext_libs.exists() && ext_libs.isDirectory()){
			File[] jars = ext_libs.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".jar");
				}
			});
			for(File jar:jars){
				ClassPathHack.addFile(jar);
			}
		}
	}

	@Override
	public void handleProgressNotification(ProgressNotification pn) {
		bar.setProgress(pn.getProgress());
	}

	@Override
	public void handleStateChangeNotification(StateChangeNotification evt) {
		if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
			stage.hide();
		}
	}
}