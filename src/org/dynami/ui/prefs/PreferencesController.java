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

import org.controlsfx.control.MasterDetailPane;
import org.dynami.ui.DynamiApplication;
import org.dynami.ui.prefs.data.Prefs;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

public class PreferencesController implements Initializable {
	private final DynamiPrefs[] prefsPages = DynamiPrefs.values();
	private Preferences storedPreferences;
	@FXML MasterDetailPane main;
	@FXML TreeView<PrefsItem> tree;
	@FXML BorderPane detailPane;
	@FXML TextField searchText;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		storedPreferences = Preferences.userRoot().node(DynamiApplication.class.getName());
		//DynamiPrefs.Basic.prefs().read(preferences);

		TreeItem<PrefsItem> root = new TreeItem<PrefsItem>(new PrefsItem(null, "General", ""));
		for(DynamiPrefs p:prefsPages){
			final Prefs prefsNode = p.prefs();
			prefsNode.read(storedPreferences);
			final Prefs.Panel panel = p.prefs().getClass().getAnnotation(Prefs.Panel.class);
			TreeItem<PrefsItem> child = new TreeItem<PrefsItem>(new PrefsItem(prefsNode, panel.name(), panel.description()));

			root.getChildren().add(child);
		}

		root.setExpanded(true);
		tree.setRoot(root);

		tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
			if(newValue.getValue().prefs != null){
				main.detailNodeProperty().set(new PrefsPage(newValue.getValue().prefs).getNode());
			}
		});
	}

	public void filter(ActionEvent e){
		final String newValue = searchText.getText();
		ObservableList<TreeItem<PrefsItem>> items = tree.getRoot().getChildren();
		if(newValue != null && !newValue.trim().equals("")){
			items.forEach(n->{
				if(newValue.contains(n.getValue().name)){



//					node.getGraphic().setVisible(true);
				} else {
//					node.getGraphic().setVisible(false);
				}
			});
		} else {
			items.forEach(node->{
//				node.getGraphic().setVisible(true);
			});
		}
	}

	public void saveAll(){
		for(DynamiPrefs p:prefsPages){
			p.prefs().write(storedPreferences);
		}
	}

	public static class PrefsItem {
		public final Prefs prefs;
		public final String name;
		public final String description;

		public PrefsItem(Prefs prefs, String name, String description) {
			this.prefs = prefs;
			this.name = name;
			this.description = description;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
