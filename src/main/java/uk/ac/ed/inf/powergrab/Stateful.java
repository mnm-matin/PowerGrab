package uk.ac.ed.inf.powergrab;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class Stateful extends Drone {

	public Stateful(double initLat, double initLong, int seed) {
		super(initLat, initLong, seed);
		
	}
	
	/*
	 * Strategy:
	 * Similar to Stateless Strategy
	 * Where the Stateless gets a random direction
	 * The Stateless goes in the direction towards the closest green station
	 * However, the drone can get stuck i.e move back and forth at the same place
	 * If a stuck drone is detected then a random direction in non-red area is chosen
	 * However the drone can get stuck consecutively
	 * If this happens 5 times in a row then the drone is forced to go on a random walk
	 * A stuck drone is detected using a buffer of 6 moves
	 * The number of steps for a random walk are 10
	 */
	
	
	
	public int stuckCounter=0; //counts the number of times drone gets stuck
	public int randomWalkSteps=0; //number of steps to take for a random walk 
	
	public Direction decision() { 
		
		if (randomWalkSteps>0) {
			randomWalkSteps=randomWalkSteps-1;
			return getRandomDirection(getNoRedDirectionSet(Pos));
		}
		
		if (stuckCounter>=5) { //5 times stuck
			//forced random walk
			randomWalkSteps=10; //steps set to 10
			stuckCounter=0;
		}
		
		return directionBuffer(this.Pos);
	}
	
	public List<Direction> directionBuffer= new LinkedList<Direction>();
	
	public Direction directionBuffer(Position pos) {
		directionBuffer.clear();
		
		Position currentPos = pos; //storing current postion
		
		// size of direction buffer set to 6
		while (directionBuffer.size()<6) {
			// populating direction buffer
			Direction x = decisionAtPosition(pos);
			directionBuffer.add(x);
			pos=pos.nextPosition(x);
		}
		
		if (isStuck(directionBuffer)) {
			//checks if drone is stuck
			System.out.println("Stuck");
			stuckCounter++;
			return getRandomDirection(getNoRedDirectionSet(currentPos)); //returns random direction if stuck
		}
		
		return directionBuffer.get(0);
	
		}
	
	
	public Boolean isStuck(List<Direction> directionBuffer) {
		// checks for back and forth motion
		boolean stuck=false;
		int boolcounter = 0;
		for (int i=0; i<directionBuffer.size()-2; i++) {
			if (directionBuffer.get(i).equals(directionBuffer.get(i+2))) {
				//check even and odd positions
					boolcounter+=1;
			}
		}
		
		if (boolcounter==directionBuffer.size()-2) {
			if (!directionBuffer.get(0).equals(directionBuffer.get(1))) {
				//check if odd positions equal even positions
				stuck=true;
		}
		}
		
		return stuck;
	}
	
	
	
	public Direction decisionAtPosition(Position pos) {

		// Hash maps used that store a station and a set of directions that it can be reached from
		// divided into two based on red station or green station
		
		HashMap<Station,Set<Direction>>greenHM=new HashMap<Station,Set<Direction>>();
		HashMap<Station,Set<Direction>>redHM=new HashMap<Station,Set<Direction>>();
		
		Set<Direction> allowedDirections= AllowableMoves(pos);
		
		for (Direction direction : allowedDirections) {
			
			Position nextPos = pos.nextPosition(direction); //get next position
			
			for (Station station : StationInRange(nextPos)) {
				
					// populate green station Hash Map
					if (station.power>0) {
						Set<Direction> dlg = new HashSet<Direction>();
						dlg=greenHM.getOrDefault(station,dlg); // adding direction value to already existing set of direction
						dlg.add(direction);
						greenHM.put(station,dlg);
					}
				
					// populate red station Hash Map
					else if (station.power<0) {
						Set<Direction> dlr = new HashSet<Direction>();
						dlr=redHM.getOrDefault(station,dlr); // adding direction value to already existing set of direction
						dlr.add(direction);
						redHM.put(station,dlr);
					}
					
				}
			}
		
		// extracting list of stations from HashMap
		List<Station> GreenSinR = new Vector<Station>(greenHM.keySet());
		List<Station> RedSinR = new Vector<Station>(redHM.keySet());
		
		Set<Direction> directionRed = new HashSet<Direction>();
		
		for (Set<Direction> directionSet: redHM.values()) {
			for (Direction direction : directionSet) {
				directionRed.add(direction);
			}
		}

		
		if (GreenSinR.isEmpty()) {
			Direction thedirection;	
			
			if (directionRed.size() == allowedDirections.size() && allowedDirections.size()!=0) {
				//all possible directions result in red Station
				System.out.println("Rare All Red Case");
				//pick Red Direction that has least -ve power
				Collections.sort(RedSinR, new Station.StationPowerSort());
				return getRandomDirection(redHM.get(RedSinR.get(RedSinR.size()-1)));
				
			} else 	{
				Set<Direction> noRed = new HashSet<Direction>();
				noRed.addAll(allowedDirections);
				noRed.removeAll(directionRed); //complement sets
				thedirection = greedyDirection(pos, noRed); //return greedy position
			}
			return thedirection;
			
		} else if (GreenSinR.size()==1) {
			return getRandomDirection(greenHM.get(GreenSinR.get(0)));
			
			
		} else {
			Collections.sort(GreenSinR, new Station.StationPowerSort()); //return direction that results in highest power gain
			return getRandomDirection(greenHM.get(GreenSinR.get(0)));
		}			
		
	}
	
	
	
	
	public Direction greedyDirection(Position pos, Set<Direction> directionSet) {
		Station NS = NearestStation(pos);
		
		
		if (NS == null) {
			// check if no more green Stations exist (i.e power>0)
			
			// make sure no more stations with available coins exist
			Double maximumCoins = Double.NEGATIVE_INFINITY;
			for (Station station: PowerGrabMap.stationList) {
				if (station.coin>maximumCoins && station.coin>0 ) {
					maximumCoins = station.coin;
					NS=station;
				}
			}
			return getRandomDirection(directionSet);
		}
		
		double NSdist=NS.getDistanceFromPostion(pos);
		
		//get Direction that minimizes distance
		
		HashMap<Direction,Double>distances=new HashMap<Direction,Double>();
		
		for (Direction d : directionSet) {
			Position nextPos = pos.nextPosition(d);
			double newDist = NS.getDistanceFromPostion(nextPos);
			distances.put(d, newDist-NSdist); //the more-ve the better
		}
		
		Double minimum = Double.POSITIVE_INFINITY;
		Direction greedyDirection = null;
		for (Map.Entry<Direction, Double> entry: distances.entrySet())
		{
			Direction dir = entry.getKey();
			Double value = entry.getValue();
			if (value < minimum)
			{
				minimum = value;
				greedyDirection = dir;
			}
		}
		
		
		return greedyDirection;
	}
	
	
	public Station NearestStation (Position pos){
		// get nearest green station regardless of range
		Station NS=null;
		
		setStationDist(pos);
		Collections.sort(PowerGrabMap.stationList, new Station.StationDistSort());
		for (Station s : PowerGrabMap.stationList) {
			if (s.power>0) {
				NS=s;
				break;
			}
		}
		setStationDist(this.Pos);
		return NS;
	}
	
	
	public Set<Direction> getNoRedDirectionSet(Position pos) {
		
	Set<Direction> allowedDirections= AllowableMoves(pos); //set of legal moves
	
	Set<Direction> redDirection = new HashSet<Direction>();
	
	for (Direction d : allowedDirections) {
		//populate redDirection List
		Position nextPos = pos.nextPosition(d);
		for (Station s : StationInRange(nextPos)) {
				if (s.power<0) {
					redDirection.add(d);
				}
			}
		}

	allowedDirections.removeAll(redDirection); //remove red Directions from legal moves
	return allowedDirections;
	
	}
	
	
}
