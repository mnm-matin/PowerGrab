package uk.ac.ed.inf.powergrab;

public class Position {
	public double latitude;
	public double longitude;
	
	public static double b=Math.PI/8; //Base Angle = 22.5 deg = pi/8 rad
	
	// creating a list of sin and cos values
	public static double sinVals[] = new double[16];
	public static double cosVals[] = new double[16];
	
	// computing sin and cos for all directions
	static {
		for (int i=0; i<16; i++) {
			sinVals[i]=Math.sin(i*b);
			cosVals[i]=Math.cos(i*b);
		}
	}
	
	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Position nextPosition(Direction direction) {
		int DirectionIndex = direction.ordinal(); //Gets index of the direction input
		// direction enum is arranged anti-clockwise starting from E
		// E would correspond to 0 , ENE to 1, NE to 2 ... 
		
		double r=0.0003; //rate of change
		
		double h=r*sinVals[DirectionIndex];
		double w=r*cosVals[DirectionIndex];
		
		double nextLatitude=this.latitude+h;
		double nextLongitude=this.longitude+w;
		
		return new Position (nextLatitude,nextLongitude);
	}

	
	public boolean inPlayArea() {
		return this.latitude < 55.946233 && this.latitude > 55.942617 && this.longitude < -3.184319 && this.longitude > -3.192473;
	}
}
