package planetLister;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import maptool.Planet;

public class PlanetLister {

	public static void listPlanets(LinkedList<Planet> planetList,
			LinkedList<Planet> neutralPlanetList, String kosmorDate)
			throws IOException {
		File file = new File("planetList.txt");

		// HashMap<Integer, Planet> planetMap = new HashMap<Integer, Planet>();
		HashMap<String, Planet> planets = new HashMap<String, Planet>();
		int maxPastOwnerCount = 0;

		BufferedReader in = new BufferedReader(new FileReader(file));
		String line;

		for (Planet planet : neutralPlanetList) {
			// planetMap.put(planet.getId(), planet);
			planets.put(planet.getName(), planet);
			if (planets.containsValue(planet)
					&& planets.get(planet.getName()).getId() != planet.getId()) {
				maxPastOwnerCount = Math.max(maxPastOwnerCount, planet
						.getPastOwners().values().size());
			}
		}
		for (Planet planet : planetList) {
			// planetMap.put(planet.getId(), planet);
			planets.put(planet.getName(), planet);
			if (planets.containsValue(planet)
					&& planets.get(planet.getName()).getId() != planet.getId()) {
				maxPastOwnerCount = Math.max(maxPastOwnerCount, planet
						.getPastOwners().values().size());
			}
		}

		while ((line = in.readLine()) != null) {
			String[] lineParts = line.split("\\s+");
			System.out.println(Arrays.toString(lineParts));
			if (planets.containsKey(lineParts[0])) {
				Planet planet = planets.get(lineParts[0]);
				int size = planet.getPastOwners().values().size();
				if (planet.getPastOwners().isEmpty()
						|| !planet.getPastOwners().values().toArray()[size - 1]
								.equals(lineParts[3])) {
					planet.getPastOwners().put(kosmorDate, lineParts[3]);
				}
			}
			in.close();

			Iterator<Entry<String, Planet>> it = planets.entrySet().iterator();

			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write("Name");
			out.write("\t");
			out.write("XPos");
			out.write("\t");
			out.write("YPos");
			out.write("\t");
			out.write("Owner");
			out.write("\t");

			for (int i = 0; i < maxPastOwnerCount; i++) {
				out.write("date:owner");
			}

			out.newLine();

			while (it.hasNext()) {
				Planet planet = it.next().getValue();
				out.write(planet.getName());
				out.write("\t");
				out.write(planet.getXPos() + "");
				out.write("\t");
				out.write(planet.getYPos() + "");
				out.write("\t");
				out.write(planet.getPlayer());
				out.write("\t");

				out.write(planet.getPastOwners().toString());
				out.newLine();
			}
			out.flush();
			out.close();

		}

	}
}
