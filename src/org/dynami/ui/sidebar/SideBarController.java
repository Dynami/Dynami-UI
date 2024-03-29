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
package org.dynami.ui.sidebar;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.topics.Topics;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;

public class SideBarController implements Initializable {
	private Parent closedPositionsPane;
	private Parent tracesPane;
	private PopOver closedPositionsPopOver;
	private PopOver tracesPopOver;
	
	@FXML
	ToolBar sideBar;
	
	@Override
	public void initialize(URL url, ResourceBundle resource) {
		
	}
	
	public void showClosedPositions(ActionEvent e){
		try {
			Button b = (Button)e.getSource();
			if(closedPositionsPane == null){
				closedPositionsPane = FXMLLoader.load(getClass().getResource("/org/dynami/ui/closed/ClosedPositions.fxml"));
				closedPositionsPopOver = new PopOver(closedPositionsPane);
				closedPositionsPopOver.setArrowLocation(ArrowLocation.LEFT_TOP);
			}
			closedPositionsPopOver.show(b);
		} catch (IOException e1) {
			Execution.Manager.msg().async(Topics.INTERNAL_ERRORS.topic, e1);
		}
	}
	
	public void showTraces(ActionEvent e){
		try {
			Button b = (Button)e.getSource();
			if(tracesPane == null){
				tracesPane = FXMLLoader.load(getClass().getResource("/org/dynami/ui/traces/Traces.fxml"));
				tracesPopOver = new PopOver(tracesPane);
				tracesPopOver.setArrowLocation(ArrowLocation.LEFT_TOP);
			}
			tracesPopOver.show(b);
		} catch (IOException e1) {
			Execution.Manager.msg().async(Topics.INTERNAL_ERRORS.topic, e1);
		}
	}
}
