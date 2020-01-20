package uk.ac.ed.inf.powergrab;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class Stateless extends Drone {
	
	public Stateless(double initLat, double initLong, int seed) {
		super(initLat, initLong, seed);
	}

	public Direction decision() {

		// Hash maps used that store a station and a set of directions that it can be reached from
		// divided into two based on red station or green station
		HashMap<Station,Set<Direction>>greenHM=new HashMap<Station,Set<Direction>>();
		HashMap<Station,Set<Direction>>redHM=new HashMap<Station,Set<Direction>>();
		
		Set<Direction> allowedDirections= AllowableMoves(this.Pos);
		
		// populate greenHM & redHM
		for (Direction d : allowedDirections) {
			
			Position nextPos = this.Pos.nextPosition(d); //get next position
			
			for (Station s : StationInRange(nextPos)) {
				
					// populate green station Hash Map
					if (s.power>0) {
						Set<Direction> dlg = new HashSet<Direction>();
						dlg=greenHM.getOrDefault(s,dlg); // adding direction value to already existing set of direction
						dlg.add(d);
						greenHM.put(s,dlg);
					}
				
					// populate red station Hash Map
					else if (s.power<0) {
						Set<Direction> dlr = new HashSet<Direction>();
						dlr=redHM.getOrDefault(s,dlr); // adding direction value to already existing set of direction
						dlr.add(d);
						redHM.put(s,dlr);
					}
					
				}
			}
		
		// extracting list of stations from HashMap
		List<Station> GreenSinR = new Vector<Station>(greenHM.keySet());
		List<Station> RedSinR = new Vector<Station>(redHM.keySet());
		
		Set<Direction> directionRed = new HashSet<Direction>();
		
		for (Set<Direction> ds: redHM.values()) {
			for (Direction d : ds) {
				directionRed.add(d);
			}
		}

		
		if (greenHM.isEmpty()) {
			Direction randomDirection;	
			
			if (directionRed.containsAll(allowedDirections)) { 
				//all possible directions result in red Station
				//pick Red Direction that has least -ve power
				Collections.sort(RedSinR, new Station.StationPowerSort());
				return getRandomDirection(redHM.get(RedSinR.get(RedSinR.size()-1))); //pick last station with least -ve power
				
			} else do {
				// generates random direction that does not result in charging from red station
				randomDirection = getRandomDirection(allowedDirections); 
			} while (directionRed.contains(randomDirection));
			return randomDirection;
			 
			
		} else if (GreenSinR.size()==1) {
			return getRandomDirection(greenHM.get(GreenSinR.get(0)));

		} else {
			Collections.sort(GreenSinR, new Station.StationPowerSort()); //return direction that results in highest power gain
			return getRandomDirection(greenHM.get(GreenSinR.get(0)));
		}			
		
	}
	
	
	
}

