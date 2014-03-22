package output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import processing.MapTool;
import processing.Planet;
import processing.Warplanet;


public class CSVCreator {
	static LinkedList<Planet> planetList;
	static LinkedList<Warplanet> warplanetList;
	static BufferedWriter writer;

	public void writeCSV(GregorianCalendar date,
			LinkedList<Warplanet> warplanetList, LinkedList<Planet> planetList) {
		String kosmorDate = MapTool.getKosmorDate();
		String dateString = MapTool.dateStringBuilder(kosmorDate, date);
		// Uses the kosmorDate to build the dateString
		File csvDir = new File("csv");
		if (!csvDir.exists() || !csvDir.isDirectory()) {
			csvDir.mkdir();
		}
		String fileName = csvDir + "/" + dateString + (" - csv -.csv");
		// Uses the dateString to create the fileName
		kosmorDate = kosmorDate.replace("_", "-");
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			writer.write("# csv for kosmor com day =" + kosmorDate + "\n");
			for (Warplanet warplanet : warplanetList) {
				writer.write(warplanet.toString());
				writer.write("\n");
			}
			for (Planet planet : planetList) {
				writer.write(planet.toString());
				writer.write("\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
