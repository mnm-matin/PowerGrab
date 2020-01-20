package uk.ac.ed.inf.powergrab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Vector;

import com.google.gson.JsonElement;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

public class PowerGrabMap {

	public static String MapString;
	
	public static List<Station> stationList= new Vector<Station>();
	
	public PowerGrabMap(String year,String month, String day) throws IOException {
		PowerGrabMap.MapString = getMap(year,month,day);
		PowerGrabMap.stationList=getStationList();
		
	}
	
	public String getMap(String year,String month, String day) throws IOException {
		
		String mapString=("http://homepages.inf.ed.ac.uk/stg/powergrab/"+year+"/"+month+"/"+day+"/powergrabmap.geojson");
				
		URL mapUrl = new URL(mapString);
		
		URLConnection urlconn =mapUrl.openConnection();
		HttpURLConnection conn=(HttpURLConnection) urlconn;
		conn.setReadTimeout(1000); //1000
		conn.setConnectTimeout(15000); //15000
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.connect();
		
		InputStream mapSourceIn = conn.getInputStream();
		
		//Convert Input Stream to String 
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(mapSourceIn));
		String read;
		while ((read=br.readLine()) != null) {
		    
			sb.append(read);
		    sb.append("\n"); //for readability
		}
		br.close();
	
		String mapSource = sb.toString();
		
		return mapSource;
	}
	
	
	public List<Feature> getFeatureList(String mapSource){
		FeatureCollection featureCollection = FeatureCollection.fromJson(mapSource); //parse geojson string
		List<Feature> featureList = featureCollection.features();
		
		return featureList;
	}
	
	public List<Station> getStationList() {
		// returns a list of Station objects
		List<Station> stationList= new Vector<Station>();
		
		for (Feature f : getFeatureList(PowerGrabMap.MapString)) {
			
			Geometry g = f.geometry();
			
			List<Double> p=((Point) g).coordinates();
			JsonElement id = f.getProperty("id");
			JsonElement coins = f.getProperty("coins");
			JsonElement power = f.getProperty("power");
			
			//only essential information added to Station object
			stationList.add(new Station(id.getAsString(),p,coins.getAsDouble(),power.getAsDouble())); //create Station objects
		}
		return stationList;
	}
	
	
	
	public String writeMapString(List<Position> posHistory) {
		
		List<Point> pointHistory = new Vector<Point>();
		
		// convert position type list to point type list
		for (Position pos : posHistory) {
			Point p = Point.fromLngLat(pos.longitude, pos.latitude);
			pointHistory.add(p);
		}
		
		LineString lineString = LineString.fromLngLats(pointHistory); //create line string from list of points
		Feature lineStringFeature = Feature.fromGeometry(lineString); //convert LineString to Feature
		
		List<Feature> featureList = getFeatureList(PowerGrabMap.MapString); //store original feature list without line string
		featureList.add(lineStringFeature); //add line string to original feature list
		FeatureCollection featureListWithLS = FeatureCollection.fromFeatures(featureList); //convert feature list to feature collection
		
		return featureListWithLS.toJson(); //convert Feature Collection to geojson string
	}
	
	
}
