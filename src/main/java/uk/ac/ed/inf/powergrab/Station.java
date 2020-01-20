package uk.ac.ed.inf.powergrab;

import java.util.Comparator;
import java.util.List;

public class Station {

	
	public String id;
	
	public double longitude;
	public double latitude;
	
	public double coin;
	public double power;
	
	public double distance; // distance from current position of drone

	public Station(String id, List<Double> pointList,double coins, double power) {
		this.id=id;
		this.coin=coins;
		this.power=power;
		this.longitude=pointList.get(0);
		this.latitude=pointList.get(1);
	}
		
	public void setDistance(Position position) {
		// using euclidean distance formula
		this.distance=Math.sqrt(Math.pow((this.latitude-position.latitude),2)+Math.pow((this.longitude-position.longitude),2));
	}
		
	public double getDistanceFromPostion(Position position) {
		// using euclidean distance formula
		return Math.sqrt(Math.pow((this.latitude-position.latitude),2)+Math.pow((this.longitude-position.longitude),2));
	}
			
	//Distance Sort in Ascending Order
	 static class StationDistSort implements Comparator<Station> { 
		  	
	        @Override
	        public int compare(Station station1, Station station2) { 
	            if ((station2.distance - station1.distance)<0) {
	            	return 1;
	            } else if ((station2.distance - station1.distance)>0){
	            	return -1;
	            } else {
	            	return 0;
	            }
	        } 
	    }
	 
	 //Power Sort in Descending Order
	 static class StationPowerSort implements Comparator<Station> { 
		  	
	        @Override
	        public int compare(Station station1, Station station2) { 
	            if ((station2.power - station1.power)<0) {
	            	return -1;
	            } else if ((station2.power - station1.power)>0){
	            	return +1;
	            } else {
	            	return 0;
	            }
	        } 
	    }
	
}
