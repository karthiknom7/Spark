package com.spark.streaming.model;

import java.io.Serializable;

public class FlightDetails implements Serializable{

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String flightId; 
	   private long temp; 
	   private boolean landed; 
	private double temperature;
	
	public String getFlightId() {
		return flightId;
	}
	public double getTemp() {
		return temp;
	}
	public boolean isLanded() {
		return landed;
	}
	public double getTemperature() {
		return temperature;
	}
	
	@Override
	public String toString() {
		return "FlightDetails [flightId=" + flightId + ", temp=" + temp + ", landed=" + landed + ", temperature="
				+ temperature + "]";
	} 
	
	
}
