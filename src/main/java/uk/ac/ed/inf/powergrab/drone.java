package uk.ac.ed.inf.powergrab;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class drone {
	
	/*
		1. Initialise the drone state (position, seed, type);
		2. Repeat
			2.1 Inspect the current state of the map, and current position;
			2.2 Calculate allowable moves of the drone;
			2.3 Decide in which direction to move;
			2.4 Move to your next position, update your position;
			2.5 Charge from the nearest changing station (if in range).
		Until 250 moves, or insufficient energy to move.
	 */
	
	public double latitude;
	public double longitude;
	public double power;
	public double coin;
	
	public int moves=250;
	
	public Position Pos;
	
	private java.util.Random rnd;
	
	public drone (double initLat, double initLong, int seed) {
		this.latitude=initLat;
		this.longitude=initLong;
		this.power=250.0;
		this.coin=0.0;
		this.Pos=new Position(initLat,initLong);
		this.rnd = new Random(seed);
		Charge();
	}
	
	
	
	public List<Direction> AllowableMoves(Position p) {
		
		List<Direction> dl= new Vector<Direction>();
		
		for (Direction d : Direction.values()) {
			if (p.nextPosition(d).inPlayArea()) {
				dl.add(d);
			}
		}
		
		return dl;
	}
	
	public void Charge() {
		
		//input Stations in Range
		
		setStationDist(this.Pos);
		Collections.sort(Map.sl, new Station.StationDistSort());
		
		for (Station s : Map.sl) {
			if (s.distance<=0.00025) {
				if (s.visited == false) {
					this.power=this.power+s.power;
					this.coin=this.coin + s.coin;
					s.visited=true;
					break;
				}
			}
			else break;
		}
		
	}
	
	public void Move(Direction d) {
		
		this.power=this.power-1.25;
		
		if (!Pos.nextPosition(d).inPlayArea()){
			System.out.println("Move Not In Play Area");
		}
		
		if (this.moves==0) {
			System.out.println("No more moves left");
		}
		
		//updating Position
		this.Pos=Pos.nextPosition(d);
		this.moves -= 1;
		
		//Charging from Stations
		Charge();
	}
	
	public void setStationDist(Position p) {
		for (Station s : Map.sl) {
			s.setDistance(p);
		}
	}
	
	
	public Direction getRandomDirection(){
		
		int rndNumber =rnd.nextInt(AllowableMoves(Pos).size());
		Direction randomDirection = AllowableMoves(Pos).get(rndNumber);
		
		return randomDirection;
	}
	
	public List<Station> StationInRange (Position pos){
		List<Station> SinR= new Vector<Station>();
		
		setStationDist(pos);
		Collections.sort(Map.sl, new Station.StationDistSort());
		
		for (Station s : Map.sl) {
			if (s.distance<=0.00025) {
				if (s.visited==false) {
					SinR.add(s);
					break;	//only adds the nearest station in range
				}
			} else break;
		}
		
		System.out.println("Rare Case: 2 Stations equidis");
		
		return SinR;
	}
	
}


//distance 0.0003



//dronetype-DD-MM-YYYY.txt
//55.944425,-3.188396,SSE,55.944147836140246,-3.1882811949702905,0.0,248.75

//dronetype-DD-MM-YYYY.geojson
//with addition of line feature

//stateless only info of charging stations in range after next move