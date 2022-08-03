package com.trading.model;

import java.io.Serializable;

public class OptionData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int strikePrice;
	Object lastPriceCe;
	Object lastPricePe;
	int changeInOiCe;
	int changeInOiPe;
	int openInterestCe;
	int openInterestPe;
	String dataTime;
	public int getStrikePrice() {
		return strikePrice;
	}
	public void setStrikePrice(int strikePrice) {
		this.strikePrice = strikePrice;
	}
	public Object getLastPriceCe() {
		return lastPriceCe;
	}
	public void setLastPriceCe(Object lastPriceCe) {
		this.lastPriceCe = lastPriceCe;
	}
	public Object getLastPricePe() {
		return lastPricePe;
	}
	public void setLastPricePe(Object lastPricePe) {
		this.lastPricePe = lastPricePe;
	}
	public int getChangeInOiCe() {
		return changeInOiCe;
	}
	public void setChangeInOiCe(int changeInOiCe) {
		this.changeInOiCe = changeInOiCe;
	}
	public int getChangeInOiPe() {
		return changeInOiPe;
	}
	public void setChangeInOiPe(int changeInOiPe) {
		this.changeInOiPe = changeInOiPe;
	}
	public int getOpenInterestCe() {
		return openInterestCe;
	}
	public void setOpenInterestCe(int openInterestCe) {
		this.openInterestCe = openInterestCe;
	}
	public int getOpenInterestPe() {
		return openInterestPe;
	}
	public void setOpenInterestPe(int openInterestPe) {
		this.openInterestPe = openInterestPe;
	}
	public String getDataTime() {
		return dataTime;
	}
	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}
	
	
	
}
