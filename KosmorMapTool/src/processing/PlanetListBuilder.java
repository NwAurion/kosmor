package processing;

import input.SVGParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import org.w3c.dom.NodeList;

/**
 * Creates a list of all planets
 * @author Aurion
 *
 */
public class PlanetListBuilder {
	static int XCOORD_CORRECTION; // Correction factor for the x coordinate of planets, see actual calculation
	static int YCOORD_CORRECTION; // Correction factor for the y coordinate of planets, see actual calculation
	static int XCOORD_CORRECTION_WARPLANET; // Correction factor for the x coordinate of warplanets, see actual calculation
	static int YCOORD_CORRECTION_WARPLANET; // Correction factor for the y coordinate of warplanets, see actual calculation
	static Boolean is_warplanet;
	static Boolean didJump;

	static int xPos1, xPos2, yPos1, yPos2;

	static String xCoord1AsString, xCoord2AsString, yCoord1AsString, yCoord2AsString;
	private static NodeList listOfPlanetsAsXMLNode;
	private static NodeList listOfLinesAsXMLNode;
	private static int totalPlanets;
	private static int totalLines;

	static LinkedList<Warplanet> warplanetList = new LinkedList<Warplanet>();
	static LinkedList<Planet> neutralPlanetList = new LinkedList<Planet>();
	static LinkedList<Object> allPlanetLists = new LinkedList<Object>();
	static LinkedList<Planet> planetList = new LinkedList<Planet>();

	/**
	 * Builds the list of planets. Parses the svg and calls generateAllPlanets
	 * which builds the actual list
	 * 
	 * @return <b>allPlanets</b> a list containing all generated planets
	 */
	public static LinkedList<Object> buildPlanetList(String kosmorDate, Calendar date, Boolean fetchInfo) {
		NodeList[] returnedNodeLists = SVGParser.parseSVG(kosmorDate, date);

		PlanetListBuilder.listOfPlanetsAsXMLNode = returnedNodeLists[0];
		PlanetListBuilder.totalPlanets = PlanetListBuilder.listOfPlanetsAsXMLNode.getLength() - 10;
		PlanetListBuilder.listOfLinesAsXMLNode = returnedNodeLists[1];
		PlanetListBuilder.totalLines = PlanetListBuilder.listOfLinesAsXMLNode.getLength() - 10;


		StatusMessageHandler.postStatusMessages(8);
		LinkedList<Object> warplanetAndPlanetList = PlanetListBuilder.generateAllPlanets(fetchInfo);
		StatusMessageHandler.postStatusMessages(9);

		return warplanetAndPlanetList;
	}


	/**
	 * Initializes the planet coordinate correction factors with respect to a single, fixed planet
	 * This is important because the coordinates in the .svg change depending on what one does see etc.
	 */
	private static void initializePlanet() {

		int currentPosition = 0;
		String tempName = PlanetListBuilder.listOfPlanetsAsXMLNode.item(currentPosition).getTextContent();
		String name = tempName.substring(1,PlanetListBuilder.listOfPlanetsAsXMLNode.item(currentPosition).getTextContent().length() - 1);


		// If Lymgat is found, use it to calculate the coordinates. If not, ask for another planet
		if (name.equals("Lymgat")) {
			PlanetListBuilder.calculateCoordinates(currentPosition);
		}
		else {
			String somePlanetName = "";
			if(MapTool.GUI){
				somePlanetName  = JOptionPane.showInputDialog(null,
						"Need a planet to calculate coordinates",
						"Please enter the coordinates of a planet you see",
						JOptionPane.QUESTION_MESSAGE);
			}
			else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("Please enter your username: ");
				try {
					somePlanetName = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			for (int i = 0; i<PlanetListBuilder.listOfPlanetsAsXMLNode.getLength(); i++){
				tempName = PlanetListBuilder.listOfPlanetsAsXMLNode.item(i).getTextContent();
				name = tempName.substring(1,PlanetListBuilder.listOfPlanetsAsXMLNode.item(i).getTextContent().length() - 1);
				if (name.equals(somePlanetName)){
					PlanetListBuilder.calculateCoordinates(i);
				}
			}
		}
	}


	private static void calculateCoordinates(int currentPosition) {
		String tempXCoord = PlanetListBuilder.listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(2).toString();
		String tempYCoord = PlanetListBuilder.listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(3).toString();

		PlanetListBuilder.xCoord2AsString = tempXCoord.substring(3, tempXCoord.length() - 1);
		PlanetListBuilder.yCoord2AsString = tempYCoord.substring(3, tempYCoord.length() - 1);

		/*
		 * The coordinates in the svg are not the actual coordinates, as the
		 * .svg uses it's own coordinate system of size 10000*10000, with
		 * 0/0 being the top left. Therefore, it has to be adjusted.
		 */

		PlanetListBuilder.xPos2 = Integer.parseInt(PlanetListBuilder.xCoord2AsString);
		PlanetListBuilder.yPos2 = Integer.parseInt(PlanetListBuilder.yCoord2AsString);

		PlanetListBuilder.XCOORD_CORRECTION = PlanetListBuilder.xPos2 - 8;
		PlanetListBuilder.YCOORD_CORRECTION = PlanetListBuilder.yPos2 + 1;

		PlanetListBuilder.XCOORD_CORRECTION_WARPLANET = PlanetListBuilder.XCOORD_CORRECTION - 1;
		PlanetListBuilder.YCOORD_CORRECTION_WARPLANET = PlanetListBuilder.YCOORD_CORRECTION + 1;
	}

	/**
	 * Creates a single warplanet, using the list of planets in form of XMlNOdes
	 * Does calculate the real coordinates, as the svg coordinates are not correct
	 * 
	 * @param currentPosition
	 * @param is_warplanet
	 * @param wp
	 * @return <b>planet</b> a single warplanet
	 */
	private static Warplanet generateSingleWarplanet(int currentPosition,
			String ships) {
		Boolean hasShips = false;
		PlanetListBuilder.didJump = false;

		String tempColor = PlanetListBuilder.listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(1).toString();
		String color = tempColor.substring(13, tempColor.length() - 2);

		String tempName = PlanetListBuilder.listOfPlanetsAsXMLNode.item(currentPosition).getTextContent();
		String name = tempName.substring(1,PlanetListBuilder.listOfPlanetsAsXMLNode.item(currentPosition).getTextContent().length() - 1);

		String tempXCoord1 = PlanetListBuilder.listOfLinesAsXMLNode.item(currentPosition - (PlanetListBuilder.totalPlanets - PlanetListBuilder.totalLines)).getAttributes().item(1).toString();
		String tempXCoord2 = PlanetListBuilder.listOfLinesAsXMLNode.item(currentPosition - (PlanetListBuilder.totalPlanets - PlanetListBuilder.totalLines)).getAttributes().item(2).toString();

		String tempYCoord1 = PlanetListBuilder.listOfLinesAsXMLNode.item(currentPosition - (PlanetListBuilder.totalPlanets - PlanetListBuilder.totalLines)).getAttributes().item(3).toString();
		String tempYCoord2 = PlanetListBuilder.listOfLinesAsXMLNode.item(currentPosition - (PlanetListBuilder.totalPlanets - PlanetListBuilder.totalLines)).getAttributes().item(4).toString();

		PlanetListBuilder.xCoord1AsString = tempXCoord1.substring(4, tempXCoord1.length() - 1);
		PlanetListBuilder.yCoord1AsString = tempYCoord1.substring(4, tempYCoord1.length() - 1);

		PlanetListBuilder.xCoord2AsString = tempXCoord2.substring(4, tempXCoord2.length() - 1);
		PlanetListBuilder.yCoord2AsString = tempYCoord2.substring(4, tempYCoord2.length() - 1);

		PlanetListBuilder.xPos1 = Integer.parseInt(PlanetListBuilder.xCoord1AsString);
		PlanetListBuilder.yPos1 = Integer.parseInt(PlanetListBuilder.yCoord1AsString);

		PlanetListBuilder.xPos2 = Integer.parseInt(PlanetListBuilder.xCoord2AsString);
		PlanetListBuilder.yPos2 = Integer.parseInt(PlanetListBuilder.yCoord2AsString);

		PlanetListBuilder.xPos1 = PlanetListBuilder.xPos1 - PlanetListBuilder.XCOORD_CORRECTION_WARPLANET;
		PlanetListBuilder.yPos1 = PlanetListBuilder.yPos1 - PlanetListBuilder.YCOORD_CORRECTION_WARPLANET;

		PlanetListBuilder.xPos2 = PlanetListBuilder.xPos2 - PlanetListBuilder.XCOORD_CORRECTION_WARPLANET;
		PlanetListBuilder.yPos2 = PlanetListBuilder.yPos2 - PlanetListBuilder.YCOORD_CORRECTION_WARPLANET;


		double a = (PlanetListBuilder.xPos1-PlanetListBuilder.xPos2);
		double b = (PlanetListBuilder.yPos1-PlanetListBuilder.yPos2);
		double distanceTraveled = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));

		if (distanceTraveled > 61) {
			PlanetListBuilder.didJump = true;
		}

		if(ships.contains("ships")){
			hasShips = true;
		}

		String type = "Warplanet";
		String csvPlanetType = "w";
		Warplanet warplanet = new Warplanet(name, PlanetListBuilder.xPos2, PlanetListBuilder.yPos2, color, hasShips, PlanetListBuilder.didJump, distanceTraveled, PlanetListBuilder.xPos1, PlanetListBuilder.yPos1, type, csvPlanetType);
		return warplanet;
	}

	/**
	 * Creates a single planet, using the list of planets in form of XMlNOdes
	 * Does calculate the real coordinates, as the svg coordinates are not correct
	 * 
	 * @param currentPosition
	 * @param is_warplanet
	 * @return <b>planet</b> a single planet
	 */
	private static Planet generateSinglePlanet(int currentPosition, String ships) {
		Boolean hasShips = false;

		String tempColor = PlanetListBuilder.listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(1).toString();
		String color = tempColor.substring(13, tempColor.length() - 2);

		String tempName = PlanetListBuilder.listOfPlanetsAsXMLNode.item(currentPosition).getTextContent();
		String name = tempName.substring(1,PlanetListBuilder.listOfPlanetsAsXMLNode.item(currentPosition).getTextContent().length() - 1);

		String tempXCoord = PlanetListBuilder.listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(2).toString();
		String tempYCoord = PlanetListBuilder.listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(3).toString();

		PlanetListBuilder.xCoord2AsString = tempXCoord.substring(3, tempXCoord.length() - 1);
		PlanetListBuilder.yCoord2AsString = tempYCoord.substring(3, tempYCoord.length() - 1);

		PlanetListBuilder.xPos2 = Integer.parseInt(PlanetListBuilder.xCoord2AsString) - PlanetListBuilder.XCOORD_CORRECTION;
		PlanetListBuilder.yPos2 = Integer.parseInt(PlanetListBuilder.yCoord2AsString) - PlanetListBuilder.YCOORD_CORRECTION;

		if(ships.contains("ships")){
			hasShips = true;
		}

		String type = "Planet";
		String csvPlanetType = "p";
		Planet planet = new Planet(name, PlanetListBuilder.xPos2, PlanetListBuilder.yPos2, color, hasShips, type, csvPlanetType);
		return planet;
	}


	/**
	 * Builds the actual list of planets using generateSinglePlanet which does
	 * the real work
	 * 
	 * @return <b>planetList</b> a list containing all planets
	 */
	private static LinkedList<Object> generateAllPlanets(Boolean fetchHouse) {
		PlanetListBuilder.initializePlanet();

		int planetCount = 0;

		for (int currentPosition= 0; currentPosition <=PlanetListBuilder.totalPlanets; currentPosition++){
			String tempWP = PlanetListBuilder.listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(0).toString();
			String wp = tempWP.substring(7, tempWP.length() - 1);


			if (wp.contains("wpname")){
				planetCount--;
			}
			if (wp.contains("name")){
				planetCount++;
			}
		}

		for (int currentPosition = planetCount; currentPosition <= PlanetListBuilder.totalPlanets; currentPosition++) {
			String tempWP = PlanetListBuilder.listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(0).toString();
			String ships = tempWP.substring(7, tempWP.length() - 1);
			PlanetListBuilder.is_warplanet = true;
			Warplanet warplanet = PlanetListBuilder.generateSingleWarplanet(currentPosition, ships);
			PlanetListBuilder.warplanetList.add(warplanet);
		}

		for (int currentPosition = 0; currentPosition < planetCount; currentPosition++) {
			String tempPlanet = PlanetListBuilder.listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(0).toString();
			String ships = tempPlanet.substring(7, tempPlanet.length() - 1);
			PlanetListBuilder.is_warplanet = false;
			Planet planet = PlanetListBuilder.generateSinglePlanet(currentPosition, ships);

			if (planet.getColor().equals("b4b4b4") || planet.getColor().equals("dcdcdc") || planet.getColor().equals("787878")){
				planet.setHouse("neutral");
				PlanetListBuilder.neutralPlanetList.add(planet);
			} else {
				PlanetListBuilder.planetList.add(planet);
			}
		}

		PlanetListBuilder.allPlanetLists.add(PlanetListBuilder.planetList);
		PlanetListBuilder.allPlanetLists.add(PlanetListBuilder.warplanetList);
		PlanetListBuilder.allPlanetLists.add(PlanetListBuilder.neutralPlanetList);
		MapTool.getPlanetLister().setPlanetList(PlanetListBuilder.planetList);
		return PlanetListBuilder.allPlanetLists;

	}


	/**
	 * Sets the owner of a planet according to it's color
	 * More or less outdated now that the map tool can parse Kosmor
	 * for each planet to fetch the house
	 * 
	 * @param planetList
	 * @param warplanetList
	 */
	@SuppressWarnings("unused")
	private static void setHouse(Planet planet, LinkedList<Planet> warplanetList) {
		String planetColor = planet.getColor();

		float[] planetColorCMYK = ColorSpace.hexToCMYK(planetColor);

		for (int j = 0; j < warplanetList.size(); j++) {

			Planet warplanet = warplanetList.get(j);
			float[] warplanetColor = ColorSpace.hexToCMYK(warplanet.getColor());
			float[] warplanetFirstDarkerColor = ColorSpace.calculateFirstDarkerColor(warplanetColor);
			float[] warplanetSecondDarkerColor = ColorSpace.calculateSecondDarkerColor(warplanetColor);
			String warplanetHouse = warplanet.getHouse();

			if (Arrays.toString(planetColorCMYK).equals(Arrays.toString(warplanetColor))) {
				planet.setHouse(warplanetHouse + "'s house");
			}

			/*
			 *  Does work correct, as in, if the planet is actually of the "first darker color", it is set.
			 *  Does not ensure the right owner, as different houses can have the same color
			 */
			else if (Arrays.toString(planetColorCMYK).equals(Arrays.toString(warplanetFirstDarkerColor))) {
				planet.setHouse(warplanetHouse + "'s house");
			}

			// See above
			else if (Arrays.toString(planetColorCMYK).equals(Arrays.toString(warplanetSecondDarkerColor))) {
				planet.setHouse(warplanetHouse + "'s house");
			}

			else if
			(ColorSpace.compareCMYKColors(planetColorCMYK, warplanetFirstDarkerColor)
					|| ColorSpace.compareCMYKColors(planetColorCMYK, warplanetSecondDarkerColor) )
			{planet.setHouse(warplanetHouse);}
			else if (planet.getHouse() == null) {
				planet.setHouse("unknown");
			}
		}
	}
}


