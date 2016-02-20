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
package org.dynami.ui.errors;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ErrorInfo {
	private StringProperty type = new SimpleStringProperty();
	private LongProperty time = new SimpleLongProperty();
	private StringProperty msg = new SimpleStringProperty();

	public static enum Type {Strategy, Internal, UserInterface, Unkwon};

	public final Throwable _error;
	public final long _time;
	public final Type _type;
	public ErrorInfo(Throwable _error, Type _type){
		_time = System.currentTimeMillis();
		this._error = _error;
		this._type = _type;

		setTime(_time);
		setMsg(_error.getMessage());
		setType(_type.name());
	}

	public String getType() {
		return type.get();
	}
	public void setType(String type) {
		this.type.set(type);
	}
	public long getTime() {
		return time.get();
	}
	public void setTime(long time) {
		this.time.set(time);
	}
	public String getMsg() {
		return msg.get();
	}
	public void setMsg(String msg) {
		this.msg.set(msg);
	}

	public StringProperty type(){
		return type;
	}

	public StringProperty msg(){
		return msg;
	}

	public LongProperty time(){
		return time;
	}
}
