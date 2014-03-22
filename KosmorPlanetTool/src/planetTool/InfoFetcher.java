package planetTool;



import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;


/**
 * This class fetches the info for all planets
 * 
 * @author Aurion
 * 
 */
public class InfoFetcher {

	final static long MILLISECONDS_IN_A_DAY = 86400000;
	// 24*60*60*1000 || Hours*Minutes*Seconds*Milliseconds

	private static LinkedList<Planet> planetList;
	private static LinkedList<Planet> neutralPlanetList;
	private static LinkedList<Planet> warplanetList;
	private static LinkedList<Planet> allPlanetsList;

	/**
	 * Fetches the info for all non-neutral planets and warplanets
	 * 
	 * @param date
	 *            the date to use
	 * @param player_login
	 *            the username of the player
	 * @param player_password
	 *            the password of the player
	 * @param fetchInfo
	 *            Determines if the tool should look up the houses in Kosmor
	 * @return
	 * 
	 * @throws IOException
	 */
	public static LinkedList<Planet> fetchInfo(String player_login,
			String player_password, DefaultHttpClient httpClient) {
		PlanetTool.setKosmorDate(new GregorianCalendar());

		LinkedList<LinkedList<Planet>> warplanetAndPlanetList = PlanetListBuilder
				.buildPlanetList(PlanetTool.getKosmorDate(),
						new GregorianCalendar());
		InfoFetcher.planetList = warplanetAndPlanetList.get(0);
		InfoFetcher.warplanetList = warplanetAndPlanetList
				.get(1);
		InfoFetcher.neutralPlanetList = warplanetAndPlanetList.get(2);

		try {


			InfoFetcher.fetchWarplanets(InfoFetcher.warplanetList, httpClient);

			InfoFetcher.fetchPlanets(InfoFetcher.planetList, httpClient);


		} catch (Exception e) {
			e.printStackTrace();
		}

		InfoFetcher.allPlanetsList = new LinkedList<Planet>();
		InfoFetcher.allPlanetsList.addAll(InfoFetcher.planetList);
		InfoFetcher.allPlanetsList.addAll(InfoFetcher.neutralPlanetList);
		InfoFetcher.allPlanetsList.addAll(InfoFetcher.warplanetList);
		return InfoFetcher.allPlanetsList;

	}

	/**
	 * Writes the warplanets to the .html file
	 * 
	 * @param warplanetList2
	 * @param out
	 * @param httpclient
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static void fetchWarplanets(LinkedList<Planet> warplanetList2,
			DefaultHttpClient httpclient)
					throws ClientProtocolException,
					IOException {
		String house;
		String player;
		int combatPower;

		double warplanetListSize = warplanetList2.size();
		for (int i = 0; i < warplanetListSize; i++) {
			Warplanet warplanet = (Warplanet) warplanetList2.get(i);
			String url = InfoFetcher.createURL(warplanet);
			String[] info = HTTPClientForKosmor.parsePlanets(httpclient,
					url, warplanet, true);
			house = info[0];
			player = info[1];
			combatPower = 0;
			if (warplanet.getShips().contains("ships")) {
				combatPower = HTTPClientForKosmor.countShipsOnPlanet(
						httpclient, url, warplanet);
			}
			warplanet.setHouse(house);
			warplanet.setOwner(player);
			warplanet.setCombatPower(combatPower);

		}


	}

	/**
	 * Writes the planets with owners to the .html file
	 * 
	 * @param planetList
	 * @param httpclient
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static void fetchPlanets(LinkedList<Planet> planetList,
			DefaultHttpClient httpclient)
					throws ClientProtocolException,
					IOException {
		String house;
		String owner;
		int combatPower;
		double planetListSize = planetList.size();

		for (int i = 0; i < planetListSize; i++) {
			Planet planet = planetList.get(i);
			String url = InfoFetcher.createURL(planet);

			Object[] info = HTTPClientForKosmor.parsePlanets(httpclient,
					url, planet, false);
			house = (String) info[0];
			owner = (String) info[1];
			combatPower = 0;
			if (planet.getShips().contains("ships")) {
				combatPower = HTTPClientForKosmor.countShipsOnPlanet(
						httpclient, url, planet);
			}
			planet.setHouse(house);
			planet.setOwner(owner);
			planet.setCombatPower(combatPower);

		}
	}

	/**
	 * Builds the actual URL of a planet.
	 * 
	 * @return the URLlater used as link to the planet
	 */
	private static String createURL(Planet planet) {
		// Creates a string in the format
		// http:kosmor.com/index.php?action=3000&xpos=1115&ypos=1075&t_typ=1&t_id=3814
		return "http://www.kosmor.com/index.php?action=3000&xpos="
		+ planet.getXPos() + "&ypos=" + planet.getYPos();

	}

}
