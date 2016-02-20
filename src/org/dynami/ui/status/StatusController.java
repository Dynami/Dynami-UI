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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.control.StatusBar;
import org.controlsfx.dialog.ExceptionDialog;
import org.dynami.core.utils.DUtils;
import org.dynami.runtime.IServiceBus.ServiceStatus;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.errors.ErrorInfo;
import org.dynami.ui.errors.ErrorsController;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class StatusController implements Initializable {
	final String ERROR_PROMPT = "ERROR: ";
	final String INTERNAL_ERROR_PROMPT = "INT-ERROR: ";
	final int LENGHT = 10;
//	final AtomicReferenceArray<ErrorInfo> errors = new AtomicReferenceArray<>(LENGHT);
//	final AtomicInteger cursor = new AtomicInteger(-1);

	@FXML StatusBar statusBar;
	@FXML Label messageType;

	private VBox errorsPane;
	private ErrorsController errorsController;

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

		Execution.Manager.msg().subscribe(Topics.INTERNAL_ERRORS.topic, (last, _msg)->{
			Throwable e = (Throwable)_msg;
			Platform.runLater(()->{
				errorsController.addErrorInfo(new ErrorInfo(e, ErrorInfo.Type.Internal));
//				errors.set(cursor.incrementAndGet()%LENGHT, new ErrorInfo(e, ErrorInfo.Type.Unkwon));
				messageType.setText(ERROR_PROMPT);
				statusBar.setText(DUtils.getErrorMessage(e));
			});
			e.printStackTrace();
			new ExceptionDialog(e).showAndWait();
//			Dialogs.create().showException(e);
		});

		Execution.Manager.msg().subscribe(Topics.STRATEGY_ERRORS.topic, (last, _msg)->{
			Throwable e = (Throwable)_msg;
			Platform.runLater(()->{
				errorsController.addErrorInfo(new ErrorInfo(e, ErrorInfo.Type.Strategy));

//				errors.set(cursor.incrementAndGet()%LENGHT, new ErrorInfo(e, ErrorInfo.Type.Strategy));
				messageType.setText(ERROR_PROMPT);
				statusBar.setText(DUtils.getErrorMessage(e));
			});
			e.printStackTrace();
		});

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/dynami/ui/errors/Errors.fxml"));
		try {
			errorsPane = loader.load();
			errorsController = loader.getController();
		} catch (IOException e1) {
			e1.printStackTrace();
			Execution.Manager.msg().async(Topics.UI_ERRORS.topic, e1);
		}
	}

	public void displayErrors(ActionEvent e){
		PopOver popOver = new PopOver(errorsPane);
		popOver.setArrowLocation(ArrowLocation.BOTTOM_RIGHT);
		Button b = (Button)e.getSource();
		popOver.show(b);
	}
}
