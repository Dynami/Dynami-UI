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
package org.dynami.ui.orders;

import org.dynami.core.utils.DUtils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class OrderRequest {
	public LongProperty requestID = new SimpleLongProperty();
	public StringProperty requestType = new SimpleStringProperty();
	public StringProperty symbol = new SimpleStringProperty();
	public LongProperty quantity = new SimpleLongProperty();
	public DoubleProperty entryPrice = new SimpleDoubleProperty();
	public LongProperty entryTime = new SimpleLongProperty();
	public StringProperty notes = new SimpleStringProperty();
	public StringProperty status = new SimpleStringProperty();
	public LongProperty executionTime = new SimpleLongProperty();
	
	public OrderRequest() {}

	public OrderRequest(org.dynami.core.orders.OrderRequest wrapper){
		requestID.set(wrapper.id);
		requestType.set("Limit");
		symbol.set(wrapper.symbol);
		quantity.set(wrapper.quantity);
		entryPrice.set(wrapper.price);
		entryTime.set(wrapper.time);
		notes.set(wrapper.note);
		status.set(wrapper.getStatus().name());
		executionTime.set(wrapper.getExecutionTime());
	}

	public LongProperty requestID(){
		return requestID;
	}

	public StringProperty requestType(){
		return requestType;
	}

	public StringProperty symbol(){
		return symbol;
	}

	public LongProperty quantity(){
		return quantity;
	}

	public DoubleProperty entryPrice(){
		return entryPrice;
	}

	public LongProperty entryTime(){
		return entryTime;
	}

	public StringProperty notes(){
		return notes;
	}
	public StringProperty status(){
		return status;
	}

	public String getSymbol() {
		return symbol.get();
	}

	public void setSymbol(String symbol) {
		this.symbol.set(symbol);
	}

	public Long getQuantity() {
		return quantity.get();
	}

	public void setQuantity(Long quantity) {
		this.quantity.set(quantity);
	}

	public Double getEntryPrice() {
		return entryPrice.get();
	}

	public void setEntryPrice(Double entryPrice) {
		this.entryPrice.set(entryPrice);
	}

	public String getRequestType() {
		return requestType.get();
	}

	public void setRequestType(String requestType) {
		this.requestType.set(requestType);
	}

	public String getNotes() {
		return notes.get();
	}

	public void setNotes(String notes) {
		this.notes.set(notes);
	}

	public String getStatus() {
		return status.get();
	}

	public void setStatus(String status) {
		DUtils.threadSafe(()->this.status.set(status));
	}

	public Long getRequestID() {
		return requestID.get();
	}

	public void setRequestID(Integer requestID) {
		this.requestID.set(requestID);
	}

	public Long getEntryTime() {
		return entryTime.get();
	}

	public void setEntryTime(Long entryTime) {
		this.entryTime.set(entryTime);
	}
	
	public Long getExecutionTime(){
		return executionTime.get();
	}
	public void setExecutionTime(long time){
		executionTime.set(time);
	}
}
