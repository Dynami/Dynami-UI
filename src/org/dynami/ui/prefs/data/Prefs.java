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
package org.dynami.ui.prefs.data;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.dynami.runtime.impl.Execution;
import org.dynami.runtime.topics.Topics;
import org.dynami.ui.UIUtils;

import javafx.scene.paint.Color;

public abstract class Prefs {
	public void read(Preferences preferences){
		Field[] fields = getClass().getDeclaredFields();
		try {
			for(Field f:fields){
				Parameter parameter = f.getAnnotation(Parameter.class);
				if(parameter != null){
					f.setAccessible(true);
					if(Type.Boolean.equals(parameter.type())){
						f.set(this, preferences.getBoolean(parameter.config(), Boolean.parseBoolean(parameter.defaultValue())));
					} else if(Type.Integer.equals(parameter.type())){
						f.set(this, preferences.getInt(parameter.config(), Integer.parseInt(parameter.defaultValue())));
					} else if(Type.Long.equals(parameter.type())){
						f.set(this, preferences.getLong(parameter.config(), Long.parseLong(parameter.defaultValue())));
					} else if(Type.Float.equals(parameter.type())){
						f.set(this, preferences.getFloat(parameter.config(), Float.parseFloat(parameter.defaultValue())));
					} else if(Type.Double.equals(parameter.type())){
						f.set(this, preferences.getDouble(parameter.config(), Double.parseDouble(parameter.defaultValue())));
					} else if(Type.Directory.equals(parameter.type())){
						f.set(this, new File(preferences.get(parameter.config(), parameter.defaultValue())));
					} else if(Type.Color.equals(parameter.type())){
						f.set(this, Color.web(preferences.get(parameter.config(), parameter.defaultValue())));
					} else if(Type.Date.equals(parameter.type())){
						f.set(this, UIUtils.DATE_FORMAT.parse(preferences.get(parameter.config(), parameter.defaultValue())));
					} else if(Type.File.equals(parameter.type())){
						f.set(this, new File(preferences.get(parameter.config(), parameter.defaultValue())));
					} else if(Type.String.equals(parameter.type())){
						f.set(this, preferences.get(parameter.config(), parameter.defaultValue()));
					} else {
						f.set(this, preferences.get(parameter.config(), parameter.defaultValue()));
					}
				}
			}
		} catch (Exception e) {
			Execution.Manager.msg().async(Topics.INTERNAL_ERRORS.topic, e);
		}
	}

	public void write(Preferences preferences){
		Field[] fields = getClass().getDeclaredFields();
		try {
			for(Field f:fields){
				Parameter parameter = f.getAnnotation(Parameter.class);
				if(parameter != null){
					f.setAccessible(true);
					if(Type.Boolean.equals(parameter.type())){
						if(f.getType().equals(boolean.class)){
							preferences.putBoolean(parameter.config(), f.getBoolean(this));
						} else if(f.getType().equals(boolean.class)){
							Boolean input = (Boolean)f.get(this);
							preferences.putBoolean(parameter.config(), input.booleanValue());
						} else {
							throw new RuntimeException("Not valid java type for "+Type.Boolean+" ["+f.getType()+"]");
						}
					} else if(Type.Integer.equals(parameter.type())){
						if(f.getType().equals(int.class)){
							preferences.putInt(parameter.config(), f.getInt(this));
						} else if(f.getType().equals(Integer.class)){
							Integer input = (Integer)f.get(this);
							preferences.putInt(parameter.config(), input.intValue());
						} else {
							throw new RuntimeException("Not valid java type for "+Type.Boolean+" ["+f.getType()+"]");
						}
					} else if(Type.Long.equals(parameter.type())){
						if(f.getType().equals(long.class)){
							preferences.putLong(parameter.config(), f.getLong(this));
						} else if(f.getType().equals(Long.class)){
							Long input = (Long)f.get(this);
							preferences.putLong(parameter.config(), input.longValue());
						} else {
							throw new RuntimeException("Not valid java type for "+Type.Long+" ["+f.getType()+"]");
						}
					} else if(Type.Float.equals(parameter.type())){
						if(f.getType().equals(float.class)){
							preferences.putFloat(parameter.config(), f.getFloat(this));
						} else if(f.getType().equals(Float.class)){
							Float input = (Float)f.get(this);
							preferences.putFloat(parameter.config(), input.floatValue());
						} else {
							throw new RuntimeException("Not valid java type for "+Type.Float+" ["+f.getType()+"]");
						}
					} else if(Type.Double.equals(parameter.type())){
						if(f.getType().equals(double.class)){
							preferences.putDouble(parameter.config(), f.getDouble(this));
						} else if(f.getType().equals(Double.class)){
							Double input = (Double)f.get(this);
							preferences.putDouble(parameter.config(), input.doubleValue());
						} else {
							throw new RuntimeException("Not valid java type for "+Type.Double+" ["+f.getType()+"]");
						}
					} else if(Type.Directory.equals(parameter.type())){
						preferences.put(parameter.config(), ((File)f.get(this)).getAbsolutePath());
					} else if(Type.File.equals(parameter.type())){
						preferences.put(parameter.config(), ((File)f.get(this)).getAbsolutePath());
					} else if(Type.Date.equals(parameter.type())){
						preferences.put(parameter.config(), UIUtils.DATE_FORMAT.format(f.get(this)));
					} else if(Type.Color.equals(parameter.type())){
						preferences.put(parameter.config(), String.valueOf(f.get(this)));
					} else if(Type.String.equals(parameter.type())){
						preferences.put(parameter.config(), String.valueOf(f.get(this)));
					} else {
						preferences.put(parameter.config(), String.valueOf(f.get(this)));
					}
				}
			}
		} catch (Exception e) {
			Execution.Manager.msg().async(Topics.INTERNAL_ERRORS.topic, e);
		} finally {
			try {
				preferences.flush();
				preferences.sync();
			} catch (BackingStoreException e) {
				Execution.Manager.msg().async(Topics.INTERNAL_ERRORS.topic, e);
			}
		}
	}

	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Panel {
		String name();
		String description() default "";
	}

	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Parameter {
		String name();
		String config();
		Type type();
		String defaultValue();
		String description() default "";
	}

	public static enum Type {
		Integer,
		Long,
		Double,
		Date,
		Float,
		String,
		Boolean,
		Color,
		Directory,
		File
	};
}
