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
package org.dynami.ui.timer;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class UITimer {
	private final long interval;
	private final Timer timer = new Timer("UITimer", true);
	private final Map<String, ClockBuffer<?>> buffers = new ConcurrentHashMap<>();
	private final List<Runnable> clockedFunctions = new CopyOnWriteArrayList<>();
	
	public UITimer(long interval){
		this.interval = interval;
	}
	
	public void addClockedFunction(Runnable runner){
		clockedFunctions.add(runner);
	}
	
	@SuppressWarnings("unchecked")
	public <T> ClockBuffer<T> get(String key, Class<T> type){
		buffers.putIfAbsent(key, new ClockBuffer<T>());
		return (ClockBuffer<T>)buffers.get(key);
	}
	
	public void start(){
		timer.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run() {
				buffers.values().forEach(ClockBuffer::flush);
				clockedFunctions.forEach(Runnable::run);
			}
		}, interval, interval);
	}
	
	public void dispose(){
		timer.cancel();
		buffers.values().forEach(ClockBuffer::dispose);
		buffers.clear();
		clockedFunctions.clear();
	}
	
	public static class ClockBuffer<T> {
		private final List<T> tempData = new CopyOnWriteArrayList<>();
		private final List<Consumer<List<T>>> consumers = new CopyOnWriteArrayList<>();
		
		public void push(T t){
			tempData.add(t);
		}
		
		public void add(Consumer<List<T>> consumer){
			this.consumers.add(consumer);
		}
		
		public void flush(){
			if(tempData.size() > 0){
				consumers.forEach(c->c.accept(tempData));
			}
			tempData.clear();
		}
		
		public void dispose(){
			consumers.clear();
			tempData.clear();
		}
	}
}
