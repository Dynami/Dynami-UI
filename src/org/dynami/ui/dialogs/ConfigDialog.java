/*
 * Copyright 2016 Alessandro Atria - a.atria@gmail.com
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
package org.dynami.ui.dialogs;

import java.io.File;
import java.lang.reflect.Field;

import org.dynami.core.config.Config;
import org.dynami.runtime.config.ClassSettings;
import org.dynami.runtime.config.ParamSettings;
import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.models.StrategyComponents;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.UIUtils;
import org.dynami.ui.controls.config.BooleanFieldParam;
import org.dynami.ui.controls.config.DoubleSpinnerFieldParam;
import org.dynami.ui.controls.config.FieldParam;
import org.dynami.ui.controls.config.FileFieldParam;
import org.dynami.ui.controls.config.IntegerSpinnerFieldParam;
import org.dynami.ui.controls.config.LongSpinnerFieldParam;
import org.dynami.ui.controls.config.PropertyParam;
import org.dynami.ui.controls.config.TextFieldParam;
import org.dynami.ui.controls.config.TimeFrameParam;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class ConfigDialog extends Dialog<Object> {
	private final Object handler;
	private final VBox vbox = new VBox();
	private final ScrollPane pane = new ScrollPane(vbox);
	public ConfigDialog(Object settings) throws Exception {
		this.handler = settings;
		pane.setHbarPolicy(ScrollBarPolicy.NEVER);
		pane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		pane.setPrefViewportHeight(vbox.getHeight());
		pane.setPrefHeight(400);
		pane.setFitToWidth(true);
		if(settings instanceof StrategyComponents){
			super.headerTextProperty().set("Strategy");
			
			applyClassSettings(vbox, ((StrategyComponents)settings).strategySettings.getStrategy());
			for(ClassSettings c: ((StrategyComponents)settings).strategySettings.getStagesSettings().values()){
				if(c != null && c.getName() != null && !c.getName().equals("")){
					applyClassSettings(vbox, c);
				}
			}
		} else {
			super.headerTextProperty().set("Data Handler");
			applyClass(vbox, settings);
		}
		ButtonType buttonTypeOk = new ButtonType("Close", ButtonData.OK_DONE);
		super.getDialogPane().getButtonTypes().add(buttonTypeOk);
		super.getDialogPane().setContent(pane);
		super.setResultConverter(new Callback<ButtonType, Object>() {
			@Override
			public Object call(ButtonType b) {
				if (b == buttonTypeOk) {
					return handler;
				}
				return null;
			}
		});
	}
	
	private static void applyClassSettings(final VBox vbox, final ClassSettings...cs) throws Exception {
		for(ClassSettings c:cs){
			VBox inner = new VBox();
			Label label = new Label(c.getName());
			label.getStyleClass().add("config-stage-title");
			label.setBackground(UIUtils.blackBackground);
			label.setTextFill(Color.WHITE);
			label.prefWidthProperty().bind(vbox.widthProperty());
			inner.getChildren().add(label);
			for(ParamSettings ps : c.getParams().values()){
				try {
					FieldParam param;
					String name = ps.getName();
					Class<?> type = ps.getParamValue().getType();
					String description = ps.getDescription();
					
					if(ps.getInnerType().equals(Config.Type.TimeFrame)){
						param = new TimeFrameParam(new PropertyParam<Long>(name, description, c, ps.getFieldName()), (long)ps.getMin(), (long)ps.getMax(), (long)ps.getStep());
					} else {
						if(type.equals(Double.class) || type.equals(double.class)){
							param = new DoubleSpinnerFieldParam(new PropertyParam<Double>(name, description, c, ps.getFieldName()), ps.getMin(), ps.getMax(), ps.getStep());
						} else if(type.equals(Long.class) || type.equals(long.class)){
							param = new LongSpinnerFieldParam(new PropertyParam<Long>(name, description, c, ps.getFieldName()), (long)ps.getMin(), (long)ps.getMax(), (long)ps.getStep());
						} else if(type.equals(Integer.class) || type.equals(int.class)){
							param = new IntegerSpinnerFieldParam(new PropertyParam<Integer>(name, description, c, ps.getFieldName()), (int)ps.getMin(), (int)ps.getMax(), (int)ps.getStep());
						} else if(type.equals(Boolean.class) || type.equals(boolean.class)){
							param = new BooleanFieldParam(new PropertyParam<Boolean>(name, description, c, ps.getFieldName()));
						} else if(type.equals(File.class)){
							param = new FileFieldParam(new PropertyParam<File>(name, description, c, ps.getFieldName()));
						} else {
							param = new TextFieldParam(new PropertyParam<String>(name, description, c, ps.getFieldName()));
						}
					}
					inner.getChildren().add(param);
				} catch (Exception e1) {
					Execution.Manager.msg().async(Topics.INTERNAL_ERRORS.topic, e1);
				}
			}
			vbox.getChildren().add(inner);
		}
	}

	private static void applyClass(final VBox vbox, final Object... cs) throws Exception {
		for (Object handler : cs) {
			Field[] fields = handler.getClass().getDeclaredFields();
			Label label = new Label(handler.getClass().getSimpleName());
			label.setBackground(UIUtils.blackBackground);
			label.setTextFill(Color.WHITE);
//			label.getStyleClass().add("config-stage-title");
			label.prefWidthProperty().bind(vbox.widthProperty());
			vbox.getChildren().add(label);
			for (Field f : fields) {
				Config.Param p = f.getAnnotation(Config.Param.class);
				if (p != null) {
					try {
						FieldParam param;
						String name = !p.name().equals("") ? p.name() : f.getName();
						String description = p.description();

						if (p.type().equals(Config.Type.TimeFrame)) {
							param = new TimeFrameParam(new PropertyParam<Long>(name, description, handler, f),
									(long) p.min(), (long) p.max(), (long) p.step());
						} else {
							if (f.getType().equals(Double.class) || f.getType().equals(double.class)) {
								param = new DoubleSpinnerFieldParam(
										new PropertyParam<Double>(name, description, handler, f), p.min(), p.max(),
										p.step());
							} else if (f.getType().equals(Long.class) || f.getType().equals(long.class)) {
								param = new LongSpinnerFieldParam(
										new PropertyParam<Long>(name, description, handler, f), (long) p.min(),
										(long) p.max(), (long) p.step());
							} else if (f.getType().equals(Integer.class) || f.getType().equals(int.class)) {
								param = new IntegerSpinnerFieldParam(
										new PropertyParam<Integer>(name, description, handler, f), (int) p.min(),
										(int) p.max(), (int) p.step());
							} else if (f.getType().equals(Boolean.class) || f.getType().equals(boolean.class)) {
								param = new BooleanFieldParam(
										new PropertyParam<Boolean>(name, description, handler, f));
							} else if (f.getType().equals(File.class)) {
								param = new FileFieldParam(new PropertyParam<File>(name, description, handler, f));
							} else {
								param = new TextFieldParam(new PropertyParam<String>(name, description, handler, f));
							}
						}

						vbox.getChildren().add(param);
					} catch (Exception e1) {
						Execution.Manager.msg().async(Topics.INTERNAL_ERRORS.topic, e1);
					}
				}
			}
		}
	}
}
