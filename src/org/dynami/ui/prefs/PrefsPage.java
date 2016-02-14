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

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.dynami.ui.DynamiApplication;
import org.dynami.ui.prefs.data.Prefs;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;

public class PrefsPage {
    private final Prefs data;
    private final VBox node = new VBox(5);
    private final List<ParameterField<?>> parameters = new ArrayList<>();

    public PrefsPage(Prefs data){
        this.data = data;
        Prefs.Panel panel = data.getClass().getAnnotation(Prefs.Panel.class);
        if(panel != null){
        	Label desc = new Label(panel.description());
        	desc.alignmentProperty().set(Pos.TOP_LEFT);
        	desc.wrapTextProperty().set(true);
        	desc.setStyle("-fx-font-weight:bold;");
        	desc.applyCss();
        	Separator sep = new Separator(Orientation.HORIZONTAL);
        	node.getChildren().addAll(desc, sep);
        }

        Field[] fields = data.getClass().getDeclaredFields();
        GridPane pane = new GridPane();
        pane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        ParameterField<?> field;
        int row = 0;
        for(final Field f : fields){
        	Prefs.Parameter parameter = f.getAnnotation(Prefs.Parameter.class);
        	if(parameter != null) {
        		Label label = new Label(parameter.name());
        		label.setTooltip(new Tooltip(parameter.description()));
        		GridPane.setHgrow(label, Priority.ALWAYS);

        		GridPane.setHalignment(label, HPos.LEFT);
        		pane.add(label, 0, row);
        		label.setPadding(new Insets(5, 5, 5, 5));
        		try {
	        		if(parameter.type().equals(Prefs.Type.Directory)){
						field = new ParameterDirectoryField(new ParameterProperty<File>(parameter, f, data));
	        		} else if(parameter.type().equals(Prefs.Type.Color)){
						field = new ParameterColorField(new ParameterProperty<Color>(parameter, f, data));
	        		} else if(parameter.type().equals(Prefs.Type.Integer)){
	        			field = new ParameterIntegerField(new ParameterProperty<Integer>(parameter, f, data));
	        		} else {
	        			field = new ParameterStringField(new ParameterProperty<String>(parameter, f, data));
	        		}
	        		GridPane.setVgrow(field.node(), Priority.SOMETIMES);
	        		GridPane.setHalignment(field.node(), HPos.RIGHT);
	        		parameters.add(field);
	        		pane.add(field.node(), 1, row);
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        		row++;
        	}
        }
        node.getChildren().add(pane);
        node.setPrefSize(800, 800);
    }

    public Node getNode() {
        return node;
    }

    private static abstract class ParameterField<T> {
    	final ParameterProperty<T> property;
    	final HBox container = new HBox(5);
    	public ParameterField(ParameterProperty<T> p){
    		this.property = p;
    	}

    	public Node node(){
    		return container;
    	}

    	public abstract void update();
    }

    private static class ParameterStringField extends ParameterField<String> {
    	final TextField field;
    	public ParameterStringField(ParameterProperty<String> property) {
    		super(property);
    		field = new TextField(String.valueOf(property.get()));
    		container.getChildren().add(field);
		}

    	@Override
    	public void update() {
    		property.update(field.getText());
    	}
    }

    private static class ParameterIntegerField extends ParameterField<Integer> {
    	final Spinner<Integer> field;
    	public ParameterIntegerField(ParameterProperty<Integer> property) {
    		super(property);
    		field = new Spinner<>();//TextField(String.valueOf(property.get()));
    		SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, property.get(), 1);
    		field.setValueFactory(factory);
    		field.valueProperty().addListener(new ChangeListener<Integer>() {
    			@Override
    			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
    				property.update(newValue);
    			}
    		});
    		container.getChildren().add(field);
		}

    	@Override
    	public void update() {
    		property.update(field.valueProperty().get());
    	}
    }

    private static class ParameterColorField extends ParameterField<Color> {
    	final ColorPicker field;

    	public ParameterColorField(ParameterProperty<Color> property) {
    		super(property);
    		Color color = property.get();
    		field = new ColorPicker(color);
    		container.getChildren().add(field);
		}

    	@Override
    	public void update() {
    		property.update(String.valueOf(field.getValue()));
    	}
    }

    private static class ParameterDirectoryField extends ParameterField<File> {
    	final TextField field;
    	public ParameterDirectoryField(ParameterProperty<File> property) {
    		super(property);
    		field = new TextField(String.valueOf(property.get()));
    		field.setEditable(false);
    		Button button = new Button("...");
    		button.setOnAction(e->{
    			DirectoryChooser chooser = new DirectoryChooser();
    			File dir = chooser.showDialog(DynamiApplication.getPrimaryStage());
    			if(dir != null){
    				field.setText(dir.getAbsolutePath());
    			}
    		});
    		container.getChildren().addAll(field, button);
		}

    	@Override
    	public void update() {
    		property.update(field.getText());
    	}
    }

    private static class ParameterProperty<T> {
    	private final Prefs.Parameter parameter;
    	private final Object parent;
    	private final Field field;
//    	private final Class<?> javaType;
    	private boolean dirty = false;

    	public ParameterProperty(Prefs.Parameter parameter, Field field, Object parent) throws Exception {
    		this.parent = parent;
    		this.field = field;
    		this.parameter = parameter;
    	}

    	public void update(Object t) {
    		try {
    			field.setAccessible(true);
    			field.set(parent, t);
    			dirty = true;
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}

    	@SuppressWarnings({ "unchecked"})
    	public T get() {
    		try {
    			return (T)field.get(parent);
    		} catch (Exception e) {
    			e.printStackTrace();
    			return null;
    		}
    	}

    	public void saved(){
    		dirty = false;
    	}

    	public boolean isDirty() {
			return dirty;
		}
    }
}
