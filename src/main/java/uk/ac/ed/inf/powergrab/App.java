package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class App 
{
	public static List<Position> posHistory = new Vector<Position>(); //including initial so size 251
	public static List<Direction> moveHistory = new Vector<Direction>();
	public static List<Double> coinHistory = new Vector<Double>();
	public static List<Double> powerHistory = new Vector<Double>();
	
    public static void main(String[] args) throws IOException {
    	    	
    	PowerGrabMap map_stored = new PowerGrabMap(args[2],args[1],args[0]); //initialize map
    	
    	if (args[6].equals("stateless")) {
    	
    		Stateless stateless= new Stateless(Double.parseDouble(args[3]),Double.parseDouble(args[4]),Integer.parseInt(args[5]));

    		posHistory.add(stateless.Pos);
    		
    		int movesLeft=250;
    		while (movesLeft>0 && stateless.power>=1.25) {
    			
    			Direction decidedDirection = stateless.decision();
    			moveHistory.add(decidedDirection);
    			
    			stateless.Move(stateless.decision()); // update Position & Charge from stations
    			
    			coinHistory.add(stateless.coin);
    			powerHistory.add(stateless.power);
    			posHistory.add(stateless.Pos);
    			
    			movesLeft=movesLeft-1; //decrease moves
    		}

        }
    	
    	else if (args[6].equals("stateful")) {
    		
    		Stateful stateful= new Stateful(Double.parseDouble(args[3]),Double.parseDouble(args[4]),Integer.parseInt(args[5]));

    		posHistory.add(stateful.Pos);
    		
    		int movesLeft=250;
    		while (movesLeft>0 && stateful.power>=1.25) {
    			
    			Direction decidedDirection = stateful.decision();
    			moveHistory.add(decidedDirection);
    			
    			stateful.Move(decidedDirection); // update Position & Charge from stations
    			
    			coinHistory.add(stateful.coin);
    			powerHistory.add(stateful.power);
    			posHistory.add(stateful.Pos);
    			
    			movesLeft=movesLeft-1; //decrease moves left
    			System.out.println(movesLeft);
    		}

    	} else
    	{
    		System.out.println("argument does not match");
    	}
    	
    	// Generate Files
    	
		FilesGenerator fg = new FilesGenerator();
		fg.toGeoJson(map_stored.writeMapString(posHistory),args[6],args[0],args[1],args[2]); //Generate GeoJson
		fg.totxt(posHistory,moveHistory,coinHistory,powerHistory,args[6],args[0],args[1],args[2]); // Generate txt file
}

}
