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
	}
	
	public Class<?> getType(){
		return type;
	}
	
//	private static Class<?> wrapPrimitive(String clazz) throws Exception {
//		if("int".equals(clazz)){
//			return Class.forName("java.lang.Integer");
//		} else if("long".equals(clazz)){
//			return Class.forName("java.lang.Long");
//		} else if("float".equals(clazz)){
//			return Class.forName("java.lang.Float");
//		} else if("double".equals(clazz)){
//			return Class.forName("java.lang.Double");
//		} else if("short".equals(clazz)){
//			return Class.forName("java.lang.Short");
//		} else if("boolean".equals(clazz)){
//			return Class.forName("java.lang.Boolean");
//		} else {
//			return Class.forName(clazz);
//		}
//	}
	
	public void update(T t) {
		try {
			if(settings != null && settings.getParams().get(fieldName) != null){
				settings.getParams().get(fieldName).setValue(t);
			} else {
				field.set(parent, t);
			}
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
}
