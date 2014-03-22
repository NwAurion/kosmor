import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class HPFinder {

	static double meanX;
	static double meanY;
	static File planetLostFile = new File("d:/downloads/planetslost.txt");
	static File planetFullFile = new File("d:/downloads/planetsfull.txt");
	static String line;
	static ArrayList<Planet> planetsLost = new ArrayList<Planet>();
	static ArrayList<Planet> planetsFull = new ArrayList<Planet>();
	static HashMap<String, Boolean> possibleHPs = new HashMap<String, Boolean>();

	public static void main(String[] args) throws IOException {
		// calculateMean(Heval, Riarneti);
		readPlanetsLost();
		readPlanetsFull();
		test();
		Iterator<String> i = possibleHPs.keySet().iterator();
		while (i.hasNext()) {
			System.out.println(i.next());
		}

	}

	public static void test() {
		// Berechne für jeden Planeten die Distanz zu einem bestimmten anderen
		// Planeten
		// Speichere die Distanz und schaue ob sie größer wird.
		// Tue das ganze für ALLE Planeten die in Frage kommen.
		// Planet possibleHP = new Planet("Paraku", 6058.0, -4029.0);
		// Planet possibleHP = new Planet("Sorskara", -5360.0, -3676.0);
		boolean test = true;
		for (int j = 0; j < planetsFull.size(); j++) {
			double previousDist = 0;
			double currentDist = 0;
			test = true;
			for (int i = 0; i < planetsLost.size(); i++) {
				previousDist = currentDist;
				currentDist = calculateDistance(planetsLost.get(i),planetsFull.get(j));
				// System.out.println(currentDist + " "+previousDist);
				if (currentDist < previousDist) {
					test = false;
				}
			}
			if (test){
				possibleHPs.put(planetsFull.get(j).getName(), true);
			}
		}
	}

	private static double calculateDistance(Planet first, Planet second) {
		double a = (first.getX() - second.getX());
		double b = (first.getY() - second.getY());
		double c = Math.pow(a, 2) + Math.pow(b, 2);

		// Pythagorean theorem, a² + b² = c² ..

		return Math.sqrt(c);

	}

	public static void readPlanetsLost() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(
				planetLostFile));
		while ((line = reader.readLine()) != null) {
			Planet planet;
			if (line.startsWith("p,")) {
				line = line.substring(line.indexOf(",") + 1);
				String name = line.substring(0, line.indexOf(","));
				line = line.substring(line.indexOf(",") + 1);
				double x = Double.parseDouble(line.substring(0,
						line.indexOf(",")));
				line = line.substring(line.indexOf(",") + 1);
				double y = Double.parseDouble(line.substring(0,
						line.indexOf(",")));
				planet = new Planet(name, x, y);
				planetsLost.add(planet);
			}
		}
	}

	public static void readPlanetsFull() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(
				planetFullFile));
		while ((line = reader.readLine()) != null) {
			Planet planet;
			if (line.startsWith("p,")) {
				line = line.substring(line.indexOf(",") + 1);
				String name = line.substring(0, line.indexOf(","));
				line = line.substring(line.indexOf(",") + 1);
				double x = Double.parseDouble(line.substring(0,
						line.indexOf(",")));
				line = line.substring(line.indexOf(",") + 1);
				double y = Double.parseDouble(line.substring(0,
						line.indexOf(",")));
				planet = new Planet(name, x, y);
				planetsFull.add(planet);
			}
		}
	}

	public static void calculateMean(Planet first, Planet second) {
		meanX = (first.getX() + second.getX()) / 2;
		meanY = (first.getY() + second.getY()) / 2;
	}
}
