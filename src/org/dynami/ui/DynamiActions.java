package org.dynami.ui;

import java.io.File;

import org.controlsfx.control.action.ActionMap;
import org.controlsfx.control.action.ActionProxy;
import org.dynami.runtime.config.StrategySettings;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.json.JSON;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.collectors.Strategies;

import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class DynamiActions {
	private File selectedFile = null;
	public DynamiActions(){
		ActionMap.register(this);
	}
	
	@ActionProxy(id="open", text="Open", accelerator="ctrl+o")
	public void open(ActionEvent e){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select *.dynami file");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Dynami file", "*.dynami"));
		File file = fileChooser.showOpenDialog(((MenuItem)e.getSource()).getParentPopup().getScene().getWindow());
		if(file != null && file.exists() && !file.isDirectory()){
			selectedFile = file;
			try {
				StrategySettings settings = JSON.Parser.deserialize(selectedFile, StrategySettings.class);
//				Strategies.Register.getSelectedValue().strategySettings = settings;
				
			} catch (Exception e1) {
				Execution.Manager.msg().async(Topics.ERRORS.topic, e1);
			}
		}
	}
	
	@ActionProxy(id="save", text="Save", accelerator="ctrl+s")
	public void save(ActionEvent e){
		if(selectedFile != null){
			try {
				JSON.Parser.serialize(selectedFile, Strategies.Register.getSelectedValue().strategySettings);
			} catch (Exception e1) {
				Execution.Manager.msg().async(Topics.ERRORS.topic, e1);
			}
		} else {
			saveAs(e);
		}
	}
	
	@ActionProxy(id="saveas", text="Save as",  accelerator="ctrl+shift+s")
	public void saveAs(ActionEvent e){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save *.dynami file as");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Dynami file", "*.dynami"));
		File file = fileChooser.showSaveDialog(((MenuItem)e.getSource()).getParentPopup().getScene().getWindow());
		if(file != null){
			selectedFile = file;
			try {
				JSON.Parser.serialize(selectedFile, Strategies.Register.getSelectedValue().strategySettings);
			} catch (Exception e1) {
				Execution.Manager.msg().async(Topics.ERRORS.topic, e1);
			}
		}
	}
}
