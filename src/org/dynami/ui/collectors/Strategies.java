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
import java.io.FileFilter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.prefs.Preferences;

import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.impl.StrategyClassLoader;
import org.dynami.runtime.models.StrategyComponents;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.DynamiApplication;
import org.dynami.ui.prefs.data.PrefsConstants;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public enum Strategies {
	Register;
	private final String strategyDir;
	private final List<StrategyComponents> strategies = new CopyOnWriteArrayList<>();
//	private final ObservableValue<StrategyComponents> selected = new ObservableValue<>(null);
	private final SimpleObjectProperty<StrategyComponents> selected = new SimpleObjectProperty<>();

	public void scanStrategyDirectory(){
		File dir = new File(strategyDir);
		File[] _strategies = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".jar");
			}
		});
		String[] _names = new String[_strategies.length];
		int i = 0;
		for(File s:_strategies){
			_names[i++] = s.getName();
			StrategyClassLoader loader = null;
			try {
				loader = new StrategyClassLoader(s.getAbsolutePath(), this.getClass().getClassLoader());
				this.strategies.add(loader.getStrategyComponents());
			} catch (Exception e) {
				Execution.Manager.msg().async(Topics.UI_ERRORS.topic, e);
				e.printStackTrace();
			} finally {
				try { if(loader!=null)loader.close();} catch (Exception e2) {}
			}
		}
	}

	public void setSelected(StrategyComponents selectedItem){
		selected.set(selectedItem);
	}


	public ObservableValue<StrategyComponents> selectedProperty(){
		return selected;
	}

	public StrategyComponents getSelectedValue(){
		return selected.get();
	}

	public ObservableList<StrategyComponents> getStrategies(){
		return FXCollections.observableArrayList(strategies);
	}

	Strategies(){
		final Preferences prefs = Preferences.userRoot().node(DynamiApplication.class.getName());
		strategyDir = prefs.get(PrefsConstants.BASIC.STRATS_DIR, ".");
	}
}
