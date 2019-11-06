package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class App 
{
	public static List<Position> posHistory = new Vector<Position>(); //including initial pos so size 251
	
	
    public static void main( String[] args ) throws IOException
    {
    	Map test = new Map("2019","01","01");
    	
    	if ("stateless"=="stateless") {
    	
    		stateless testStateless= new stateless((55.946233+55.942617)/2,(-3.184319-3.192473)/2,5678);
    		posHistory.add(testStateless.Pos);
    		
    		while (testStateless.moves>0 && testStateless.power>=1.25) {
    			testStateless.Move(testStateless.decision()); // update Position & Charge from stations & decrease moves
    			posHistory.add(testStateless.Pos);
    		}

    	System.out.println(test.writeMapString(test.buildCoorString(posHistory)));

    	
    	}
    }
    
    
}


