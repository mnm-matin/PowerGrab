package uk.ac.ed.inf.powergrab;

import java.util.Comparator;
import java.util.List;

public class Station {
	//coordinates=[-3.191567256886182, 55.94471088693855]}, 
	//properties={
		//"id":"9b13-5eff-0d48-a549-733e-b0fe",
		//"coins":"-35.56574800298797",
		//"power":"-120.21221577379329",
		//"marker-symbol":"danger",
		//"marker-color":"#9c0000"}}
	
	public String id;
	
	public double longitude;
	public double latitude;
	
	public double coin;
	public double power;
	
	public double distance;
	
	public Direction getfrom;
	
	public boolean visited;
	
	
	public Station(String id, List<Double> p,double coins, double power) {
		this.id=id;
		this.coin=coins;
		this.power=power;
		
		//check if correct
		
		this.longitude=p.get(0);
		this.latitude=p.get(1);
		
		this.visited=false;
	}
	
	public boolean inRange(Position p) {
		return (p.latitude-latitude)<0.00025 & (p.longitude-longitude)<0.00025;
	}
	
	public void setDistance(Position p) {
		this.distance=Math.sqrt(Math.pow((this.latitude-p.latitude),2)+Math.pow((this.longitude-p.longitude),2));
		
	}
	
	public double getDistance() {
		return this.distance;
	}
	
	
	public String getRat() {
		return Double.toString(this.coin/this.power);
	}
	
	public String getDist() {
		return Double.toString(this.distance);
	}
	
	/*
	 static class StationSortingComparator implements Comparator<Station> { 
		  	
	        @Override
	        public int compare(Station station1, Station station2) { 
	            // for comparison 
	            int DistCompare = station1.getDist().compareTo(station2.getDist()); 
	            int RatCompare = station1.getRat().compareTo(station2.getRat()); 
	  
	            // 2-level comparison using if-else block 
	            if (DistCompare == 0) { 
	                return ((RatCompare == 0) ? DistCompare : RatCompare); 
	            } else { 
	                return DistCompare; 
	            } 
	        } 
	    }
	   */
	 
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
