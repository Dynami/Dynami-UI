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
package org.dynami.ui.closed;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ClosedPosition {
	private StringProperty symbol = new SimpleStringProperty();
	private LongProperty quantity = new SimpleLongProperty();
	private DoubleProperty entryPrice = new SimpleDoubleProperty();
	private LongProperty entryTime = new SimpleLongProperty();
	private DoubleProperty exitPrice = new SimpleDoubleProperty();
	private LongProperty exitTime = new SimpleLongProperty();
	private DoubleProperty percReturn = new SimpleDoubleProperty();
	private DoubleProperty absReturn = new SimpleDoubleProperty();
	
	public ClosedPosition(org.dynami.core.portfolio.ClosedPosition cl) {
		setSymbol(cl.symbol);
		setQuantity(cl.quantity);
		setEntryPrice(cl.entryPrice);
		setEntryTime(cl.entryTime);
		setExitPrice(cl.entryPrice);
		setExitTime(cl.exitTime);
		setPercReturn(cl.percRoi());
		setAbsReturn(cl.roi());
	}
	
	public String getSymbol() {
		return symbol.get();
	}
	public void setSymbol(String symbol) {
		this.symbol.set(symbol);
	}
	public long getQuantity() {
		return quantity.get();
	}
	public void setQuantity(long quantity) {
		this.quantity.set(quantity);
	}
	public double getEntryPrice() {
		return entryPrice.get();
	}
	public void setEntryPrice(double entryPrice) {
		this.entryPrice.set(entryPrice);
	}
	public long getEntryTime() {
		return entryTime.get();
	}
	public void setEntryTime(long entryTime) {
		this.entryTime.set(entryTime);
	}
	public double getExitPrice() {
		return exitPrice.get();
	}
	public void setExitPrice(double exitPrice) {
		this.exitPrice.set(exitPrice);
	}
	public long getExitTime() {
		return exitTime.get();
	}
	public void setExitTime(long exitTime) {
		this.exitTime.set(exitTime);
	}
	public double getPercReturn() {
		return percReturn.get();
	}
	public void setPercReturn(double percReturn) {
		this.percReturn.set(percReturn);
	}
	public double getAbsReturn() {
		return absReturn.get();
	}
	public void setAbsReturn(double absReturn) {
		this.absReturn.set(absReturn);
	}
	
	public DoubleProperty entryPrice(){
		return entryPrice;
	}
	
	public DoubleProperty exitPrice(){
		return exitPrice;
	}
	
	public DoubleProperty percReturn(){
		return percReturn;
	}
	
	public DoubleProperty absReturn(){
		return absReturn;
	}
	
	public LongProperty exitTime(){
		return exitTime;
	}
	
	public LongProperty entryTime(){
		return entryTime;
	}
}
