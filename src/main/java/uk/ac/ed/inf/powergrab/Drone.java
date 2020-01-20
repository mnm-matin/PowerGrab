package uk.ac.ed.inf.powergrab;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class Drone {
	
	/*
	 * Base Strategy (given)
		1. Initialise the drone state (position, seed, type);
		2. Repeat
			2.1 Inspect the current state of the map, and current position;
			2.2 Calculate allowable moves of the drone;
			2.3 Decide in which direction to move;
			2.4 Move to your next position, update your position;
			2.5 Charge from the nearest changing station (if in range).
		Until 250 moves, or insufficient energy to move.
	 */
	

	public double power;
	public double coin;
	
	public Position Pos;
	
	protected java.util.Random rnd;
	
	public Drone (double initLat, double initLong, int seed) {
		this.power=250.0;
		this.coin=0.0;
		this.Pos=new Position(initLat,initLong);
		this.rnd = new Random(seed);
	}
	
	public Set<Direction> AllowableMoves(Position position) {
		// returns only the directions that would result in a legal move
		Set<Direction> directionSet= new HashSet<Direction>();
		
		for (Direction direction : Direction.values()) {
			if (position.nextPosition(direction).inPlayArea()) {
				directionSet.add(direction);
			}
		}
		return directionSet;
	}
	
	public void Charge() {
		//charging done after drone is moved
		setStationDist(this.Pos);
		Collections.sort(PowerGrabMap.stationList, new Station.StationDistSort());
		
		Station station = PowerGrabMap.stationList.get(0);
		
		if (station.distance<=0.00025) {
			this.power=this.power+station.power;
			this.coin=this.coin + station.coin;
			station.power=0;
			station.coin=0;
			}
		}
		
	
	public void Move(Direction directionToMove) {
		
		//checking if move is legal
		if (!Pos.nextPosition(directionToMove).inPlayArea()){
			System.out.println("Move Not In Play Area");
			directionToMove=getRandomDirection(AllowableMoves(Pos)); //moves in random direction if move not legal
		}
		
		//reduce power of drone by 1.25
		this.power=this.power-1.25;
		
		//updating Position
		this.Pos=Pos.nextPosition(directionToMove);
		
		//Charging from Stations
		Charge();
	}
	
	public void setStationDist(Position position) {
		// sets the distances of the Stations based on position
		for (Station s : PowerGrabMap.stationList) {
			s.setDistance(position);
		}
	}
	
	public Direction getRandomDirection(Set<Direction> directionSet){
		List<Direction> directionList = new Vector<Direction>(directionSet);
		
		int rndNumber =rnd.nextInt(directionList.size()); //uses random initialized with given seed
		
		Direction randomDirection = directionList.get(rndNumber);
		return randomDirection;
	}
	
	public List<Station> StationInRange (Position pos){
		List<Station> stationsInRange= new Vector<Station>();
		
		setStationDist(pos); //set distances for stations
		Collections.sort(PowerGrabMap.stationList, new Station.StationDistSort());
		
		for (Station s : PowerGrabMap.stationList) {
			if (s.distance<=0.00025) { //range defined as 0.00025
					stationsInRange.add(s);
					break;	//only adds the nearest station in range
			} else break; //stations sorted by distance in descending order
		}
		
		return stationsInRange;
	}
	
}



