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
package org.dynami.ui.prefs;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import org.dynami.ui.DynamiApplication;

import javafx.fxml.Initializable;

public class PreferencesController implements Initializable {
	private Preferences prefs;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		prefs = Preferences.userRoot().node(DynamiApplication.class.getName());
		prefs.put(PrefsConstants.BASIC.STRATS_DIR, "D:/git/Dynami-Sample-Strategy/resources/");
		
	}

}
