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
package org.dynami.ui.controls.config;

import java.lang.reflect.Field;

import org.dynami.runtime.config.ClassSettings;

public class PropertyParam<T> {
	private final String name;
	private final String description;
	private final Object parent;
//	private final Method getter, setter;
	private final Class<?> type;
	private final Field field;
	private final String fieldName;
	private final ClassSettings settings;
	
	public PropertyParam(String name, String description, ClassSettings settings, String fieldName) throws Exception {
		this.name = name;
		this.description = description;
		this.parent = null;
		this.type = settings.getParams().get(fieldName).getType();
		this.field = null;
		this.fieldName= fieldName;
//		this.field.setAccessible(true);
		this.settings = settings;
	}
	
	public PropertyParam(String name, String description, Object parent, Field f) throws Exception {
		this.name = name;
		this.description = description;
		this.parent = parent;
		this.type = f.getType();
		this.field = f;
		this.field.setAccessible(true);
		this.fieldName = null;
		this.settings = null;
//		getter = parent.getClass().getDeclaredMethod(getter(f.getName(), (f.getType().equals(boolean.class) ||  f.getType().equals(Boolean.class))));
//		setter = parent.getClass().getDeclaredMethod(setter(f.getName()), f.getType());
	}
	
	public Class<?> getType(){
		return type;
	}
	
	public void update(T t) {
		try {
			if(settings != null && settings.getParams().get(fieldName) != null){
				settings.getParams().get(fieldName).setValue(t);
			} else {
				field.set(parent, t);
			}
//			setter.invoke(parent, new Object[]{t});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public T get() {
		try {
			if(settings != null && settings.getParams().get(fieldName) != null){
				return (T)settings.getParams().get(fieldName).getValue();
			} else {
				return (T)field.get(parent);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
//	private static String getter(String fieldName, boolean isBoolean){
//		char[] cs =fieldName.toCharArray();
//		cs[0] = Character.toUpperCase(cs[0]);
//		return  ((isBoolean)?"is":"get")+ (new String(cs));
//	}
//
//	private static String setter(String input){
//		char[] tmp = input.toCharArray();
//		tmp[0] = Character.toUpperCase(tmp[0]);
//		return "set".concat(new String(tmp));
//	}
}
