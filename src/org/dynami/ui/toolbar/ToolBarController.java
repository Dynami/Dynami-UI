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
package org.dynami.ui.toolbar;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.property.BeanProperty;
import org.controlsfx.property.editor.AbstractPropertyEditor;
import org.dynami.core.config.Config;
import org.dynami.runtime.IDataHandler;
import org.dynami.runtime.IExecutionManager;
import org.dynami.runtime.IService;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.DynamiApplication;
import org.dynami.ui.collectors.DataHandler;
import org.dynami.ui.collectors.Strategies;
import org.dynami.ui.prefs.PrefsConstants;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class ToolBarController implements Initializable {
	private final ImageView loadIcon = new ImageView("icons/_load.gif");
	private final ImageView runIcon = new ImageView("icons/_run.gif");
	private final ImageView pauseIcon = new ImageView("icons/_pause.gif");
	private final ImageView resumeIcon = new ImageView("icons/_resume.gif");
	private final String LOAD = "Load", RUN = "Run", RESUME = "Resume", PAUSE = "Pause";
	
	private boolean isStrategySelected = false;
	private boolean isDataHandlerSelected = false;
	
	private IDataHandler handler;
	
	@FXML
	ToolBar toolbar;
	
	@FXML
	Button execButton, stopButton, confStratButton, confDataServiceButton;
	
	@FXML
	ComboBox<String> strategies;
	
	@FXML
	ComboBox<String>
	dataHandlers;
	
	@FXML
	TextField strategyName;
	
	@FXML
	Image execIcon, stopIcon;
	
	public void exec(ActionEvent e) throws Exception {
		if(LOAD.equals(execButton.getText())){
			Execution.Manager.getServiceBus().registerService((IService)handler, 100);
			
			final String stratDir = Preferences.userRoot().node(DynamiApplication.class.getName()).get(PrefsConstants.BASIC.STRATS_DIR, ".");
			final String strategyJarPath = stratDir+"/"+strategies.getSelectionModel().getSelectedItem();
			
			boolean isOk = Execution.Manager.select(null, strategyJarPath);
			if(isOk){
				isOk = Execution.Manager.init(null);
			} 
			if(isOk){
				isOk = Execution.Manager.load();
			}
		} else if(RUN.equals(execButton.getText())){
			Execution.Manager.run();
		} else if(PAUSE.equals(execButton.getText())){
			Execution.Manager.pause();
		} else if(RESUME.equals(execButton.getText())){
			Execution.Manager.run();
		}
	}
	
	public void stop(ActionEvent e){
		Execution.Manager.stop();
	}
	
	
	private void checkSelection() {
		if(isStrategySelected && isDataHandlerSelected){
			execButton.setDisable(false);
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		dataHandlers.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue != null && !newValue.equals("")){
					try {
						handler = DataHandler.Registry.getHandler(newValue).newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						Execution.Manager.msg().async(Topics.ERRORS.topic, e);
					}
				}
			}
		});
		
		execButton.setDisable(true);
		stopButton.setDisable(true);
		
		execButton.setGraphic(loadIcon);
		
		strategies.getItems().addAll(Strategies.Register.getStrategies());
		dataHandlers.getItems().addAll(DataHandler.Registry.dataHandlerNames());
		
		strategies.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
			isStrategySelected = (newValue != null && !newValue.equals(""));
			checkSelection();
		});
		dataHandlers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
			isDataHandlerSelected = (newValue != null && !newValue.equals(""));
			checkSelection();
		});
		
		Execution.Manager.addStateListener((oldState, newState)->{
			if(newState.equals(IExecutionManager.State.NonActive)){
				execButton.setGraphic(loadIcon);
				execButton.setText(LOAD);
				execButton.setDisable(true);
				stopButton.setDisable(true);
			} else if(newState.equals(IExecutionManager.State.Selected)){
				execButton.setGraphic(loadIcon);
				execButton.setText(LOAD);
				execButton.setDisable(false);
			} else if(newState.equals(IExecutionManager.State.Initialized)){
				
			} else if(newState.equals(IExecutionManager.State.Loaded)){
				execButton.setGraphic(runIcon);
				execButton.setText(RUN);
				execButton.setDisable(false);
				stopButton.setDisable(true);
			} else if(newState.equals(IExecutionManager.State.Running)){
				execButton.setGraphic(pauseIcon);
				execButton.setText(PAUSE);
				execButton.setDisable(false);
				stopButton.setDisable(false);
			} else if(newState.equals(IExecutionManager.State.Paused)){
				execButton.setGraphic(resumeIcon);
				execButton.setText(RESUME);
				execButton.setDisable(false);
				stopButton.setDisable(false);
			} else if(newState.equals(IExecutionManager.State.Stopped)){
				execButton.setGraphic(loadIcon);
				execButton.setText(LOAD);
				stopButton.setDisable(true);
				checkSelection();
			}
		});
	}
	
	public void configStrategy(ActionEvent e){
		VBox vbox = new VBox();
		vbox.getChildren().addAll(new Label("Hello"), new Label("World!!!"));
		
		PopOver popOver = new PopOver(vbox);
		Button b = (Button)e.getSource();
		popOver.setArrowLocation(ArrowLocation.TOP_LEFT);
		popOver.show(b);
	}
	
	public void configDataHandler(ActionEvent e){
		if(handler == null) return;
		
		Field[] fields = handler.getClass().getDeclaredFields();
		ObservableList<PropertySheet.Item> items = FXCollections.observableArrayList();
		
		for (Field f : fields) {
			Config.Param p = f.getAnnotation(Config.Param.class);
			if (p != null) {
				try {
					Method setter = handler.getClass().getDeclaredMethod(setter(f.getName()), f.getType());
					Method getter = handler.getClass().getDeclaredMethod(getter(f.getName(), f.getType().equals(Boolean.TYPE)));
					
					PropertyDescriptor propertyDescriptor = new PropertyDescriptor(
							(!p.name().equals(""))?p.name():f.getName(), 
							getter, 
							setter);
					propertyDescriptor.setShortDescription(p.description());
					BeanProperty item = new BeanProperty(handler, propertyDescriptor);
					item.setEditable(true);
					items.add(item);
					
//					if(f.getType().equals(File.class)){
//						propertyDescriptor.setPropertyEditorClass(FilePropertyEditor.class);
//					}
					
				} catch (Exception e1) {
					Execution.Manager.msg().async(Topics.ERRORS.topic, e1);
				}
			}
		}
		
		PropertySheet sheet = new PropertySheet(items);
		PopOver popOver = new PopOver(sheet);
		Button b = (Button)e.getSource();
		popOver.setArrowLocation(ArrowLocation.TOP_LEFT);
		popOver.show(b);
	}
	
	private static String getter(String fieldName, boolean isBoolean){
		char[] cs =fieldName.toCharArray();
		cs[0] = Character.toUpperCase(cs[0]);
		return  ((isBoolean)?"is":"get")+ (new String(cs));
	}
	
	private static String setter(String input){
		char[] tmp = input.toCharArray();
		tmp[0] = Character.toUpperCase(tmp[0]);
		return "set".concat(new String(tmp));
	}
}

abstract class AbstractObjectField<T> extends HBox {
	private final CustomTextField textField = new CustomTextField();
	private static final Image image = new Image(AbstractObjectField.class.getResource("/org/controlsfx/control/open-editor.png").toExternalForm()); //$NON-NLS-1$
    private ObjectProperty<T> objectProperty = new SimpleObjectProperty<>();

    public AbstractObjectField() {
        super(1);
        textField.setEditable(false);
        textField.setFocusTraversable(false);

        StackPane button = new StackPane(new ImageView(image));
        button.setCursor(Cursor.DEFAULT);

        button.setOnMouseReleased(e -> {
            if ( MouseButton.PRIMARY == e.getButton() ) {
                final T result = edit(objectProperty.get());
                if (result != null) {
                    objectProperty.set(result);
                }
            }
        });

        textField.setRight(button);
        getChildren().add(textField);
        HBox.setHgrow(textField, Priority.ALWAYS);

        objectProperty.addListener((o, oldValue, newValue) -> textProperty().set(objectToString(newValue)));
    }

    protected StringProperty textProperty() {
        return textField.textProperty();
    }

    public ObjectProperty<T> getObjectProperty() {
        return objectProperty;
    }

    protected String objectToString(T object) {
        return object == null ? "" : object.toString(); //$NON-NLS-1$
    }

    protected abstract Class<T> getType();

    protected abstract T edit(T object);
}

class FileField extends AbstractObjectField<File> {

	@Override
	protected Class<File> getType() {
		return File.class;
	}

	@Override
	protected File edit(File object) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File");
		File selectedFile = fileChooser.showOpenDialog(DynamiApplication.getPrimaryStage());
		 if (selectedFile != null) {
		    return selectedFile;
		 }
		return null;
	}
	
	@Override 
	protected String objectToString(File file) {
        return file == null? "": String.format("%s", file.getName()); //$NON-NLS-1$ //$NON-NLS-2$
    }
	
}

class FilePropertyEditor extends AbstractPropertyEditor<File, FileField>{
	private ObjectProperty<File> property = new SimpleObjectProperty<>();
	
	public FilePropertyEditor(PropertySheet.Item item){
		super(item, new FileField());
	}
	
	public FilePropertyEditor(PropertySheet.Item item, FileField node) {
		super(item, node);
	}
	
	@Override
	public void setValue(File file) {
		property.set(file);
	}

	@Override
	protected ObservableValue<File> getObservableValue() {
		return property;
	}
}
