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
package org.dynami.ui.collectors;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.prefs.Preferences;

import org.dynami.ui.DynamiApplication;
import org.dynami.ui.prefs.PrefsConstants;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public enum Strategies {
	Register;
	private final String strategyDir;
	private final List<String> strategies = new CopyOnWriteArrayList<>();
	
	public void scanStrategyDirectory(){
		File dir = new File(strategyDir);
		String[] _strategies = dir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});
		
		strategies.addAll(Arrays.asList(_strategies));
	}
	
	public ObservableList<String> getStrategies(){
		return FXCollections.observableArrayList(strategies);
	}
	
	
	Strategies(){
		final Preferences prefs = Preferences.userRoot().node(DynamiApplication.class.getName());
		strategyDir = prefs.get(PrefsConstants.BASIC.STRATS_DIR, ".");
	}
}
