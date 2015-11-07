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
package org.dynami.ui.traces;

import org.dynami.core.services.ITraceService;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Trace {
	private StringProperty type = new SimpleStringProperty();
	private LongProperty time = new SimpleLongProperty();
	private StringProperty stage = new SimpleStringProperty();
	private StringProperty line = new SimpleStringProperty();
	
	public Trace(ITraceService.Trace t) {
		setType(t.type.name());
		setTime(t.time);
		setStage(t.stage);
		setLine(t.line);
	}

	public String getType() {
		return type.get();
	}

	public void setType(String type) {
		this.type.set(type);
	}

	public Long getTime() {
		return time.get();
	}

	public void setTime(Long time) {
		this.time.set(time);
	}

	public String getStage() {
		return stage.get();
	}

	public void setStage(String stage) {
		this.stage.set(stage);
	}

	public String getLine() {
		return line.get();
	}

	public void setLine(String line) {
		this.line.set(line);
	}
	
	public StringProperty typeProperty(){
		return type;
	}
	
	public LongProperty timeProperty(){
		return time;
	}
	
	public StringProperty stageProperty(){
		return stage;
	}
	public StringProperty lineProperty(){
		return line;
	}
}
