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
package org.dynami.ui.portfolio;

import org.dynami.core.utils.DUtils;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class OpenPosition {
	private StringProperty symbol = new SimpleStringProperty();
	private LongProperty quantity = new SimpleLongProperty();
	private StringProperty entryPrice = new SimpleStringProperty();
	private StringProperty entryTime = new SimpleStringProperty();
	private StringProperty currentTime = new SimpleStringProperty();
//	private final StringProperty perc = new SimpleStringProperty();
//	private final StringProperty roi = new SimpleStringProperty();
	
	public OpenPosition(org.dynami.core.portfolio.OpenPosition open) {
		symbol.set(open.symbol);
		quantity.set(open.quantity);
		entryPrice.set(DUtils.MONEY_FORMAT.format(open.entryPrice));
		entryTime.set(DUtils.LONG_DATE_FORMAT.format(open.entryTime));
		currentTime.set(DUtils.LONG_DATE_FORMAT.format(open.currentTime));
	}

	public String getSymbol() {
		return symbol.get();
	}

	public long getQuantity() {
		return quantity.get();
	}

	public String getEntryPrice() {
		return entryPrice.get();
	}

	public String getEntryTime() {
		return entryTime.get();
	}

	public String getCurrentTime() {
		return currentTime.get();
	}

	public void setSymbol(String symbol) {
		this.symbol.set(symbol);
	}

	public void setQuantity(long quantity) {
		this.quantity.set(quantity);
	}

	public void setEntryPrice(String entryPrice) {
		this.entryPrice.set(entryPrice);
	}

	public void setEntryTime(String entryTime) {
		this.entryTime.set(entryTime);
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime.set(currentTime);
	}
	
	
//	public StringProperty symbol(){
//		return symbol;
//	}
//	
//	public StringProperty entryTime(){
//		return entryTime;
//	}
//	
//	public StringProperty currentTime(){
//		return currentTime;
//	}
//	
//	public StringProperty quantity(){
//		return quantity;
//	}
//	
//	public StringProperty entryPrice(){
//		return entryPrice;
//	}
//	
//	public  StringProperty perc(){
//		return perc;
//	}
//	
//	public StringProperty roi(){
//		return roi;
//	}
}

