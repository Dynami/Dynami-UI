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
	@FXML
	ToolBar sideBar;
	
	
	
	@Override
	public void initialize(URL url, ResourceBundle resource) {
		
	}
	
	public void showClosedPositions(ActionEvent e){
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/org/dynami/ui/closed/ClosedPositions.fxml"));
			PopOver popOver = new PopOver(root);
			Button b = (Button)e.getSource();
			popOver.setArrowLocation(ArrowLocation.LEFT_TOP);
			popOver.show(b);
		} catch (IOException e1) {
			Execution.Manager.msg().async(Topics.ERRORS.topic, e1);
		}
	}
}
