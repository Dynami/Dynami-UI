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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.dynami.runtime.IDataHandler;

public enum DataHandler {
	Registry;
	private final List<Class<? extends IDataHandler>> handlers = new ArrayList<>();

	public void register(Class<? extends IDataHandler> handlerClass){
		handlers.add(handlerClass);
	}
	
	public Collection<Class<? extends IDataHandler>> dataHandlers(){
		return Collections.unmodifiableCollection(handlers);
	}
	
	public Collection<String> dataHandlerNames(){
		return handlers.stream().map(Class::getSimpleName).collect(Collectors.toList());
	}
	
	public Class<? extends IDataHandler> getHandler(final String name){
		return handlers.stream()
				.filter(c->c.getSimpleName().equals(name))
				.findFirst().get();
	}
}
