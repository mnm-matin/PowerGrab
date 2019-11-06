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
import com.mapbox.geojson.Point;

public class Map {

	public static String MapString;
	
	public static List<Station> sl= new Vector<Station>();
	
	public Map(String year,String month, String day) throws IOException {
		Map.MapString = getMap(year,month,day);
		Map.sl=getStationList();
		
	}
	
	public String getMap(String year,String month, String day) throws IOException {
		
		String mapString=("http://homepages.inf.ed.ac.uk/stg/powergrab/"+year+"/"+month+"/"+day+"/powergrabmap.geojson");
		
		//System.out.println(mapString);
		
		URL mapUrl = new URL(mapString);
		
		URLConnection urlconn =mapUrl.openConnection();
		HttpURLConnection conn=(HttpURLConnection) urlconn;
		conn.setReadTimeout(1000);
		conn.setConnectTimeout(15000);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.connect();
		
		InputStream mapSourceIn = conn.getInputStream();
		
//***** Convert Input Stream to String 
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(mapSourceIn));
		String read;
		while ((read=br.readLine()) != null) {
		    
			sb.append(read);
		    sb.append("\n");
		}
		br.close();
	
		String mapSource = sb.toString();
//*****
		return mapSource;
	}
	
	
	public List<Feature> getFeatures(String mapSource){
		FeatureCollection fc = FeatureCollection.fromJson(mapSource);
		List<Feature> fl = fc.features();
		
		return fl;
	}
	
	public List<Station> getStationList() {
		
		List<Station> sl= new Vector<Station>();
		
		for (Feature f : getFeatures(Map.MapString)) {
			
			Geometry g = f.geometry();
			List<Double> p=((Point) g).coordinates();
			
			JsonElement id = f.getProperty("id");
			JsonElement coins = f.getProperty("coins");
			JsonElement power = f.getProperty("power");
			
			sl.add(new Station(id.getAsString(),p,coins.getAsDouble(),power.getAsDouble()));
		}
		return sl;
	}
	
	
	public String buildCoorString (List<Position> posHistory) {
		String coorString = "";
    	
		
		for (Position pos : posHistory.subList(0, posHistory.size()-2)) {
			coorString += "["+ pos.longitude + ", " + pos.latitude+"],";
	    	coorString +="\n"+"          ";
		}
		
		coorString += "["+ posHistory.get(posHistory.size()-1).longitude + ", " + posHistory.get(posHistory.size()-1).latitude+"]";
		
    	
    	return coorString;
	}
	
	
	public String writeMapString(String coorString) {
		
		String editMap="";
		
		editMap +=Map.MapString.substring(0,Map.MapString.length()-26);
		editMap +=","+"\n"+"\n";
		
		String line = ""
			+ "    {"+"\n"
				+ "      \"type\": \"Feature\","+"\n"
				+ "      \"geometry\": {"+"\n"
					+ "      \"type\": \"LineString\","+"\n"
					+ "      \"coordinates\": ["+"\n"
					+"          "+coorString+"\n"
					+ "      ]"+"\n"
				+ "    },"+"\n"
				+ "      \"properties\": {}"+"\n"
			+ "    }";
		
		editMap += line;
		editMap += Map.MapString.substring(Map.MapString.length()-26,Map.MapString.length());
		
		return editMap;
	}
	
	
}
