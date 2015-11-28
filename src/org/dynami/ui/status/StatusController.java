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
package org.dynami.ui.status;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.control.StatusBar;
import org.dynami.core.utils.DUtils;
import org.dynami.runtime.IServiceBus.ServiceStatus;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.topics.Topics;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StatusController implements Initializable {
	final String ERROR_PROMPT = "ERROR: ";
	final String INTERNAL_ERROR_PROMPT = "INT-ERROR: ";
	final int LENGHT = 10;
	final AtomicReferenceArray<ErrorInfo> errors = new AtomicReferenceArray<>(LENGHT);
	final AtomicInteger cursor = new AtomicInteger(-1);
	
	@FXML StatusBar statusBar;
	@FXML Label messageType;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		statusBar.setText("");
		Execution.Manager.msg().subscribe(Topics.SERVICE_STATUS.topic, (last, _msg)->{
			ServiceStatus s = (ServiceStatus)_msg;
			Platform.runLater(()->{
				messageType.setText(INTERNAL_ERROR_PROMPT);
				statusBar.setText(s.message);
			});
		});

		Execution.Manager.msg().subscribe(Topics.ERRORS.topic, (last, _msg)->{
			Throwable e = (Throwable)_msg;
			Platform.runLater(()->{
				errors.set(cursor.incrementAndGet()%LENGHT, new ErrorInfo(e));
				messageType.setText(ERROR_PROMPT);
				statusBar.setText(DUtils.getErrorMessage(e));
			});
			e.printStackTrace();
		});
		
		Execution.Manager.msg().subscribe(Topics.STRATEGY_ERRORS.topic, (last, _msg)->{
			Throwable e = (Throwable)_msg;
			Platform.runLater(()->{
				errors.set(cursor.incrementAndGet()%LENGHT, new ErrorInfo(e));
				messageType.setText(ERROR_PROMPT);
				statusBar.setText(DUtils.getErrorMessage(e));
			});
			e.printStackTrace();
		});
	}
	
	public void displayErrors(ActionEvent e){
		VBox vbox = new VBox();
		
		if(cursor.get() < 0){
			vbox.getChildren().add(new Label("No errors occurred"));
		} else {
			ErrorInfo info;
			for(int i = 0; i < Math.min(cursor.get()+1, LENGHT); i++){
				info = errors.get( (i+cursor.get())%LENGHT );
				VBox errorPane = new VBox(5);
				// caption
				Label errorMsg = new Label(DUtils.getErrorMessage(info.error));
				errorMsg.setAlignment(Pos.CENTER_LEFT);
				Label time = new Label(DUtils.LONG_DATE_FORMAT.format(info.time));
				time.setAlignment(Pos.CENTER_RIGHT);			
				HBox errorPaneCaption = new HBox();
				errorPaneCaption.getChildren().addAll(errorMsg, time);
				// trace error
				Label stackTrace = new Label(info.error.toString());
				
				errorPane.getChildren().addAll(errorPaneCaption, stackTrace);
				vbox.getChildren().add(errorPane);
			}
		}
		
		PopOver popOver = new PopOver(vbox);
		popOver.setArrowLocation(ArrowLocation.BOTTOM_RIGHT);
		Button b = (Button)e.getSource();
		popOver.show(b);
	}
	
	private static class ErrorInfo {
		public final Throwable error;
		public final long time = System.currentTimeMillis();
		public ErrorInfo(Throwable error){
			this.error = error;
		}
		
		
	}
}
