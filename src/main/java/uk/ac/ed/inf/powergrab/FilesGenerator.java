package uk.ac.ed.inf.powergrab;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

public class FilesGenerator {
	
	public void toGeoJson (String geojson_string, String dronetype, String day, String month, String year) {
		
		try (Writer geojson_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dronetype+"-"+day+"-"+month+"-"+year+".geojson"), "utf-8"))) {
			geojson_writer.write(geojson_string);
			} 
		
		catch (IOException e) {
				e.printStackTrace();
				}		
		}

	
	public void totxt (List<Position> posHistory, List<Direction> moveHistory, List<Double> coinHistory, List<Double> powerHistory, String dronetype, String day, String month, String year) {
		
		try (Writer txt_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dronetype+"-"+day+"-"+month+"-"+year+".txt"), "utf-8"))) {
			
			for (int i=0;i<posHistory.size()-1;i++) {
			txt_writer.write(posHistory.get(i).latitude+", "+posHistory.get(i).longitude
					+", "+moveHistory.get(i)+", "
					+posHistory.get(i+1).latitude+", "+posHistory.get(i+1).longitude
					+", "+coinHistory.get(i)+", "+powerHistory.get(i)+"\n");
			}
			} 
		
		catch (IOException e) {
				e.printStackTrace();
				}		
		}
	}


//dronetype-DD-MM-YYYY.txt
//55.944425,-3.188396,SSE,55.944147836140246,-3.1882811949702905,0.0,248.75

//dronetype-DD-MM-YYYY.geojson
//with addition of line feature