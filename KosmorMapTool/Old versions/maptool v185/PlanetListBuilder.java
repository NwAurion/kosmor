package maptool;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
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
	static int xPos, yPos;

	static String xCoordAsString, yCoordAsString;
	private static NodeList listOfPlanetsAsXMLNode;
	private static NodeList listOfLinesAsXMLNode;
	private static int totalPlanets;
	private static int totalLines;

	/**
	 * Builds the list of planets. Parses the svg and calls generateAllPlanets
	 * which builds the actual list
	 * 
	 * @return <b>allPlanets</b> a list containing all generated planets
	 */
	public static LinkedList<Planet>[] buildPlanetList(Date date, Boolean fetchInfo) {
		NodeList[] returnedNodeLists = SVGParser.parseSVG(date);
		
		listOfPlanetsAsXMLNode = returnedNodeLists[0];
		totalPlanets = listOfPlanetsAsXMLNode.getLength() - 10;
		listOfLinesAsXMLNode = returnedNodeLists[1];
		totalLines = listOfLinesAsXMLNode.getLength() - 10;
		
		StatusMessageHandler.postStatusMessages(8);
		LinkedList<Planet>[] warplanetAndPlanetList = generateAllPlanets(fetchInfo);
		StatusMessageHandler.postStatusMessages(9);

		return warplanetAndPlanetList;
	}

	
	/**
	 * Initializes the planet coordinate correction factors with respect to a single, fixed planet
	 * This is important because the coordinates in the .svg change depending on what one does see etc.
	 */
	public static void initializePlanet() {
	
		int currentPosition = 0;
		String tempName = listOfPlanetsAsXMLNode.item(currentPosition).getTextContent();
		String name = tempName.substring(1,listOfPlanetsAsXMLNode.item(currentPosition).getTextContent().length() - 1);

		
		// If Lymgat is found, use it to calculate the coordinates. If not, ask for another planet
		if (name.equals("Lymgat")) {
			calculateCoordinates(currentPosition);
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

			for (int i = 0; i<listOfPlanetsAsXMLNode.getLength(); i++){
				tempName = listOfPlanetsAsXMLNode.item(i).getTextContent();
				name = tempName.substring(1,listOfPlanetsAsXMLNode.item(i).getTextContent().length() - 1);
				if (name.equals(somePlanetName)){
					calculateCoordinates(i);
				}
			}
		}
	}


	public static void calculateCoordinates(int currentPosition) {
		String tempXCoord = listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(2).toString();
		String tempYCoord = listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(3).toString();
		
		xCoordAsString = tempXCoord.substring(3, tempXCoord.length() - 1);
		yCoordAsString = tempYCoord.substring(3, tempYCoord.length() - 1);

		/*
		 * The coordinates in the svg are not the actual coordinates, as the
		 * .svg uses it's own coordinate system of size 10000*10000, with
		 * 0/0 being the top left. Therefore, it has to be adjusted.
		 */
		
		xPos = Integer.parseInt(xCoordAsString);
		yPos = Integer.parseInt(yCoordAsString);

		XCOORD_CORRECTION = xPos - 8;
		YCOORD_CORRECTION = yPos + 1;
		
		XCOORD_CORRECTION_WARPLANET = XCOORD_CORRECTION - 1;
		YCOORD_CORRECTION_WARPLANET = YCOORD_CORRECTION + 1;
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
	public static Planet generateSingleWarplanet(int currentPosition, boolean is_warplanet, String ships) {
		Boolean hasShips = false;
		
		String tempColor = listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(1).toString();
		String color = tempColor.substring(13, tempColor.length() - 2);

		String tempName = listOfPlanetsAsXMLNode.item(currentPosition).getTextContent();
		String name = tempName.substring(1,listOfPlanetsAsXMLNode.item(currentPosition).getTextContent().length() - 1);

		String tempXCoord = listOfLinesAsXMLNode.item(currentPosition - (totalPlanets - totalLines)).getAttributes().item(2).toString();
		String tempYCoord = listOfLinesAsXMLNode.item(currentPosition - (totalPlanets - totalLines)).getAttributes().item(4).toString();
			
	
		xCoordAsString = tempXCoord.substring(4, tempXCoord.length() - 1);
		yCoordAsString = tempYCoord.substring(4, tempYCoord.length() - 1);

		xPos = Integer.parseInt(xCoordAsString)- XCOORD_CORRECTION_WARPLANET;
		yPos = Integer.parseInt(yCoordAsString)- YCOORD_CORRECTION_WARPLANET;	

		if(ships.contains("ships")){
			hasShips = true;
		}
		
		Planet warplanet = new Planet(name, xPos, yPos, color, is_warplanet, hasShips);
		warplanet.setHouse(warplanet.getName());
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
	public static Planet generateSinglePlanet(int currentPosition, boolean is_warplanet, String ships) {
		Boolean hasShips = false;
		
		String tempColor = listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(1).toString();
		String color = tempColor.substring(13, tempColor.length() - 2);

		String tempName = listOfPlanetsAsXMLNode.item(currentPosition).getTextContent();
		String name = tempName.substring(1,listOfPlanetsAsXMLNode.item(currentPosition).getTextContent().length() - 1);

		String tempXCoord = listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(2).toString();
		String tempYCoord = listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(3).toString();

		xCoordAsString = tempXCoord.substring(3, tempXCoord.length() - 1);
		yCoordAsString = tempYCoord.substring(3, tempYCoord.length() - 1);

		xPos = Integer.parseInt(xCoordAsString) - XCOORD_CORRECTION;
		yPos = Integer.parseInt(yCoordAsString) - YCOORD_CORRECTION;

		if(ships.contains("ships")){
			hasShips = true;
		}
		
		
		Planet planet = new Planet(name, xPos, yPos, color, is_warplanet, hasShips);
		return planet;
	}
	
	
	/**
	 * Builds the actual list of planets using generateSinglePlanet which does
	 * the real work
	 * 
	 * @return <b>planetList</b> a list containing all planets
	 */
	@SuppressWarnings("unchecked")
	public static LinkedList<Planet>[] generateAllPlanets(Boolean fetchHouse) {
		LinkedList<Planet> planetList = new LinkedList<Planet>();
		LinkedList<Planet> warplanetList = new LinkedList<Planet>();
		LinkedList<Planet> neutralPlanetList = new LinkedList<Planet>();
		LinkedList<Planet>[] allPlanetLists = new LinkedList[3];
		
		initializePlanet();
		
		int warplanetCount = 0;
		int planetCount = 0;

		for (int currentPosition= 0; currentPosition <=totalPlanets; currentPosition++){
			String tempWP = listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(0).toString();
			String wp = tempWP.substring(7, tempWP.length() - 1);
			
			if (wp.contains("wpname")){
				warplanetCount++;
			}
			else if (wp.contains("name")){
				planetCount++;
			}
		}
		
		for (int currentPosition = planetCount; currentPosition <= totalPlanets; currentPosition++) {
			String tempWP = listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(0).toString();
			String ships = tempWP.substring(7, tempWP.length() - 1);
			is_warplanet = true;
			Planet warplanet = generateSingleWarplanet(currentPosition, is_warplanet, ships);
			warplanetList.add(warplanet);
		}

		for (int currentPosition = 0; currentPosition < planetCount; currentPosition++) {
			String tempPlanet = listOfPlanetsAsXMLNode.item(currentPosition).getAttributes().item(0).toString();
			String ships = tempPlanet.substring(7, tempPlanet.length() - 1);
			is_warplanet = false;
			Planet planet = generateSinglePlanet(currentPosition, is_warplanet, ships);

			if (planet.getColor().equals("b4b4b4") || planet.getColor().equals("dcdcdc") || planet.getColor().equals("787878")){
				planet.setHouse("neutral");
				neutralPlanetList.add(planet);
			}
			else planetList.add(planet);		
		}

		allPlanetLists[0] = planetList;
		allPlanetLists[1] = warplanetList;
		allPlanetLists[2] = neutralPlanetList;
		return allPlanetLists;

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
			else if (planet.getHouse() == null) 
				planet.setHouse("unknown");
		}
	}
}


