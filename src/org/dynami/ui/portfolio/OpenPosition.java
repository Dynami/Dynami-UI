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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class OpenPosition {
	public StringProperty assetType = new SimpleStringProperty();
	public StringProperty symbol = new SimpleStringProperty();
	public LongProperty quantity = new SimpleLongProperty(); 
	public DoubleProperty entryPrice = new SimpleDoubleProperty();
	public LongProperty entryTime = new SimpleLongProperty();
	public DoubleProperty pointValue = new SimpleDoubleProperty();
	public LongProperty currentTime = new SimpleLongProperty();
	public DoubleProperty roi = new SimpleDoubleProperty();
	public DoubleProperty percRoi = new SimpleDoubleProperty();
	public DoubleProperty delta = new SimpleDoubleProperty(1);
	public DoubleProperty gamma = new SimpleDoubleProperty(0);
	public DoubleProperty vega = new SimpleDoubleProperty(0);
	public DoubleProperty theta = new SimpleDoubleProperty(0);
	public DoubleProperty rho = new SimpleDoubleProperty(0);
	
	
	public OpenPosition( org.dynami.core.portfolio.OpenPosition op){
		setAssetType(op.family.name());
		setSymbol(op.symbol);
		setQuantity(op.quantity);
		setEntryPrice(op.entryPrice);
		setEntryTime(op.entryTime);
		setPointValue(op.pointValue);
		setCurrentTime(op.currentTime);
	}
	
	public StringProperty assetType(){
		return assetType;
	}

	public StringProperty symbol() {
		return symbol;
	}

	public LongProperty quantity() {
		return quantity;
	}

	public DoubleProperty entryPrice() {
		return entryPrice;
	}

	public LongProperty entryTime() {
		return entryTime;
	}

	public DoubleProperty pointValue() {
		return pointValue;
	}

	public LongProperty currentTime() {
		return currentTime;
	}
	
	public void setAssetType(String assetType) {
		this.assetType.set( assetType );
	}
	
	public String getAssetType() {
		return assetType.get();
	}

	public String getSymbol() {
		return symbol.get();
	}

	public Long getQuantity() {
		return quantity.get();
	}

	public Double getEntryPrice() {
		return entryPrice.get();
	}

	public Long getEntryTime() {
		return entryTime.get();
	}

	public Double getPointValue() {
		return pointValue.get();
	}

	public Long getCurrentTime() {
		return currentTime.get();
	}
	
	public void setSymbol(String symbol) {
		this.symbol.set(symbol);
	}

	public void setQuantity(Long quantity) {
		this.quantity.set(quantity);
	}

	public void setEntryPrice(Double entryPrice) {
		this.entryPrice.set(entryPrice);
	}

	public void setEntryTime(Long entryTime) {
		this.entryTime.set(entryTime);
	}

	public void setPointValue(Double pointValue) {
		this.pointValue.set( pointValue );
	}

	public void setCurrentTime(Long currentTime) {
		this.currentTime.set(currentTime);
	}

	public Double getRoi() {
		return roi.get();
	}

	public void setRoi(Double roi) {
		this.roi.set(roi);
	}

	public Double getPercRoi() {
		return percRoi.get();
	}

	public void setPercRoi(Double percRoi) {
		this.percRoi.set(percRoi);
	}

	public Double getDelta() {
		return delta.get();
	}

	public void setDelta(Double delta) {
		this.delta.set(delta);
	}

	public Double getGamma() {
		return gamma.get();
	}

	public void setGamma(Double gamma) {
		this.gamma.set( gamma );
	}

	public Double getVega() {
		return vega.get();
	}

	public void setVega(Double vega) {
		this.vega.set( vega );
	}

	public Double getTheta() {
		return theta.get();
	}

	public void setTheta(Double theta) {
		this.theta.set( theta );
	}

	public Double getRho() {
		return rho.get();
	}

	public void setRho(Double rho) {
		this.rho.set( rho );
	}
	
	public DoubleProperty roi(){
		return roi;
	}
	
	public DoubleProperty percRoi(){
		return percRoi;
	}
	
	public DoubleProperty delta(){
		return delta;
	}
	
	public DoubleProperty gamma(){
		return gamma;
	}
	
	public DoubleProperty vega(){
		return vega;
	}
	
	public DoubleProperty theta(){
		return theta;
	}
	
	public DoubleProperty rho(){
		return rho;
	}
}

