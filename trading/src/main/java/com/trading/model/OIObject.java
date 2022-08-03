package com.trading.model;

import java.io.Serializable;

public class OIObject implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String chnageInOiCE;
	String chnageInOiPE;
	String oiCE;
	String oiPE;
	String strike;
	String dateTime;
	public String getChnageInOiCE() {
		return chnageInOiCE;
	}
	public void setChnageInOiCE(String chnageInOiCE) {
		this.chnageInOiCE = chnageInOiCE;
	}
	public String getChnageInOiPE() {
		return chnageInOiPE;
	}
	public void setChnageInOiPE(String chnageInOiPE) {
		this.chnageInOiPE = chnageInOiPE;
	}
	public String getOiCE() {
		return oiCE;
	}
	public void setOiCE(String oiCE) {
		this.oiCE = oiCE;
	}
	public String getOiPE() {
		return oiPE;
	}
	public void setOiPE(String oiPE) {
		this.oiPE = oiPE;
	}
	public String getStrike() {
		return strike;
	}
	public void setStrike(String strike) {
		this.strike = strike;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	@Override
	public String toString() {
		return "OIObject [chnageInOiCE=" + chnageInOiCE + ", chnageInOiPE=" + chnageInOiPE + ", oiCE=" + oiCE
				+ ", oiPE=" + oiPE + ", strike=" + strike + ", dateTime=" + dateTime + "]";
	}
	

}
