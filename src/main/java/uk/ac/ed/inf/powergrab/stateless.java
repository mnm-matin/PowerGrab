package uk.ac.ed.inf.powergrab;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class stateless extends drone {
	


	public stateless(double initLat, double initLong, int seed) {
		super(initLat, initLong, seed);
	}

	public Direction decision() {
		
		List<Station> GreenSinR= new Vector<Station>();
		List<Station> RedSinR= new Vector<Station>();
		
		List<Direction> directionGreen = new Vector<Direction>();
		List<Direction> directionRed = new Vector<Direction>();
		
		for (Direction d : AllowableMoves(this.Pos)) {
			
			Position nextPos = this.Pos.nextPosition(d);
			List<Station> SinR= StationInRange(nextPos);
			
			for (Station s : SinR) {
					if (s.power>=0) {
						GreenSinR.add(s); directionGreen.add(d); s.getfrom=d;
					}
					
					else {
						RedSinR.add(s); directionRed.add(d); s.getfrom=d;
					}
					
				}
			}
		
		if (GreenSinR.isEmpty()) {
			Direction randomDirection;	
			
			if (directionRed.equals(AllowableMoves(this.Pos))) {
				//all possible directions result in red Station
				System.out.println("Rare All Red Case");
				Collections.sort(GreenSinR, new Station.StationPowerSort());
				return RedSinR.get(RedSinR.size()-1).getfrom;
				
			} else do {
				randomDirection = getRandomDirection();
			} while (directionRed.contains(randomDirection));	//avoiding red Stations
			
			return randomDirection;
			 
			
		} else if (GreenSinR.size()==1) {
			return GreenSinR.get(0).getfrom;
			
			
		} else {
			Collections.sort(GreenSinR, new Station.StationPowerSort());
			return GreenSinR.get(0).getfrom;
		}			
		
	}

}

