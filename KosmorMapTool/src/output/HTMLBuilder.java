package output;


import input.HTTPClientForKosmor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;

import processing.MapTool;
import processing.Planet;
import processing.PlanetListBuilder;
import processing.StatusMessageHandler;
import processing.Warplanet;

/**
 * This class writes the planets to a html file.
 * 
 * @author Aurion
 * 
 */
public class HTMLBuilder {

	final static long MILLISECONDS_IN_A_DAY = 86400000; // 24*60*60*1000
	// Hours*Minutes*Seconds*Milliseconds

	static LinkedList<Planet> planetList;
	static LinkedList<Planet> neutralPlanetList;
	static LinkedList<Warplanet> warplanetList;
	/**
	 * Writes all planets as table to a HTML file
	 * 
	 * @param writeWarplanets
	 *            determines if the tool does write out the warplanets
	 * @param writePlanets
	 *            determines if the tool writes out planets (with owners)
	 * @param writeNeutral
	 *            determines if the tool writes out neutral planets
	 * @param date
	 *            the date to use
	 * @param player_login
	 *            the username of the player
	 * @param player_password
	 *            the password of the player
	 * @param fetchInfo
	 *            Determines if the tool should look up the houses in Kosmor
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void writeHTML(Boolean writeWarplanets, Boolean writePlanets,
			Boolean writeNeutral, GregorianCalendar date, String player_login,
			String player_password, boolean fetchInfo) {
		MapTool.setKosmorDate(date);
		String kosmorDate = MapTool.getKosmorDate(); // Gets the kosmorDate

		LinkedList<Object> warplanetAndPlanetList = PlanetListBuilder
				.buildPlanetList(kosmorDate, date, fetchInfo);
		HTMLBuilder.planetList = (LinkedList<Planet>) warplanetAndPlanetList.get(0);
		HTMLBuilder.warplanetList = (LinkedList<Warplanet>) warplanetAndPlanetList
				.get(1);
		HTMLBuilder.neutralPlanetList = (LinkedList<Planet>) warplanetAndPlanetList.get(2);
		String dateString = MapTool.dateStringBuilder(kosmorDate, date);
		// Uses the kosmorDate to build the dateString

		StatusMessageHandler.postStatusMessages(10);

		// Making sure the directory exists, if not, is is created
		File htmldir = new File("html");
		if (!htmldir.exists() || !htmldir.isDirectory()) {
			htmldir.mkdir();
		}

		String fileName = htmldir + "/" + dateString
				+ (" - Planet & WP List - kosmor.com - .html");
		// Uses the dateString to create the fileName

		try {

			FileWriter fileOut = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fileOut);

			StatusMessageHandler.postStatusMessages(11);

			HTMLBuilder.initializeHTML(dateString, out);

			DefaultHttpClient httpclient = (DefaultHttpClient) HTTPClientForKosmor
					.doConnect(player_login, player_password)[0];

			out.write("<tbody>");
			out.newLine();
			if (writeWarplanets) {
				HTMLBuilder.writeWarplanets(fetchInfo, HTMLBuilder.warplanetList, out, httpclient);
			}
			if (writePlanets) {
				HTMLBuilder.writePlanets(fetchInfo, HTMLBuilder.planetList, out, httpclient);
			}
			/*
			 * if (writeNeutral) { HTMLBuilder.writeNeutral(fetchInfo,
			 * HTMLBuilder.neutralPlanetList, out, httpclient); }
			 */
			out.write("</tbody>");
			out.newLine();
			httpclient.getConnectionManager().shutdown();
			out.write("</table>");
			out.newLine();
			HTMLBuilder.importJavaScriptFile(out);
			HTMLBuilder.writeScriptInfo(out);
			out.write("</form>");
			out.newLine();
			out.write("</body>");
			out.newLine();
			out.write("</html>");

			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		StatusMessageHandler.postStatusMessages(15);

		MapTool.setWarplanetList(HTMLBuilder.warplanetList);
		MapTool.setPlanetList(HTMLBuilder.planetList);
		MapTool.setNeutralPlanetList(HTMLBuilder.neutralPlanetList);
	}

	/**
	 * Initializes the html file, writes out the header, imports the css and
	 * script etc.
	 * 
	 * @param dateString
	 * @param out
	 * @throws IOException
	 */
	public static void initializeHTML(String dateString, BufferedWriter out)
			throws IOException {
		out.write("<!doctype html>");
		out.newLine();
		out.write("<html>");
		out.newLine();
		out.write("<head>");
		HTMLBuilder.importCSSFile(out);
		out.write("</head>");
		out.newLine();
		out.write("<body>");
		out.newLine();
		out.write("<form name=\"form\">");
		out.newLine();
		out.write(dateString);
		out.newLine();
		out.write("<br>");
		out.newLine();
		out.write("<br>");
		out.newLine();
		out.write("<span id='distance12'> </span><br />");
		out.newLine();
		out.write("<span id='distance13'> </span><br />");
		out.newLine();
		out.write("<span id='distance23'> </span>");
		out.newLine();
		out.write("<br>");
		out.newLine();
		out.write("<br>");
		out.newLine();
		out.write("<table id=\"table1\" class=\"sortable\">");
		out.newLine();
		out.write("<thead>");
		out.newLine();
		out.write("<tr>");
		out.newLine();
		out.write("<th></th>");
		out.newLine();
		out.write("<th>Name</th>");
		out.newLine();
		out.write("<th>XCoord</th>");
		out.newLine();
		out.write("<th>YCoord</th>");
		out.newLine();
		if (MapTool.fetchInfo) {
			out.write("<th>Player</a></th>");
			out.newLine();
			out.write("<th>House</a></th>");
			out.newLine();
		}
		out.write("<th>Type</th>");
		out.newLine();
		if (MapTool.fetchInfo) {
			out.write("<th>Combat power</th>");
		} else {
			out.write("<th>Ships</th>");
		}
		out.newLine();
		out.write("<th>Moved</th>");
		out.newLine();
		out.write("<!--NEW-->"); // Defining the new area for ease of comparison
		// in case of errors, remove later
		out.newLine();
		out.write("<th id='selectedname'>a=</th>");
		out.newLine();
		out.write("<th>Dist from a=</th>");
		out.newLine();
		out.write("<!--ENDNEW-->");
		out.newLine(); // End of the new area, remove later
		out.write("</tr>");
		out.newLine();
		out.write("</thead>");
		out.newLine();
	}

	/**
	 * Writes the import command for the css
	 * 
	 * @param out
	 * @throws IOException
	 */
	private static void importCSSFile(BufferedWriter out) throws IOException {
		out.newLine();
		out.write("<style type=\"text/css\" media=\"screen\">");
		out.newLine();
		out.write("@import \"http://dl.dropbox.com/u/1398050/Kosmor/scripts/maptool.css\"");
		out.newLine();
		out.write("</style>");
		out.newLine();
		out.newLine();
	}

	/**
	 * Writes the import command for the javascript file
	 * 
	 * @param out
	 * @throws IOException
	 */
	public static void importJavaScriptFile(BufferedWriter out)
			throws IOException {
		out.newLine();
		out.write("<script type='text/javascript' src='http://dl.dropbox.com/u/1398050/Kosmor/scripts/maptool.js'></script>");
		out.newLine();
		out.write("<script type='text/javascript' src='http://dl.dropbox.com/u/1398050/Kosmor/scripts/sorting.js'></script>");
		out.newLine();
		out.write("<script type='text/javascript' src='http://dl.dropbox.com/u/1398050/Kosmor/scripts/distance.js'></script>");
		out.newLine();
		out.write("<script type='text/javascript' src='http://dl.dropbox.com/u/1398050/Kosmor/scripts/dist2.js'></script>");
		out.newLine();
	}

	/**
	 * Writes the script info. Adjusts which colums have a dropdown menu, sets
	 * autocomplete etc.
	 * 
	 * @param out
	 * @throws IOException
	 */
	private static void writeScriptInfo(BufferedWriter out) throws IOException {
		out.newLine();
		out.write("<script language=\"javascript\" type=\"text/javascript\">");
		out.newLine();
		out.write("var table1_Props = { ");
		out.newLine();
		out.write("			bind_script:{ name:\"autocomplete\", target_fn: setAutoComplete },");
		out.newLine();
		out.write("			col_0: \"none\",");
		out.newLine();
		if (MapTool.fetchInfo) {
			out.write("			col_6: \"select\",");
			out.newLine();
		} else {
			out.write("			col_4: \"select\",");
			out.newLine();
			out.write("			col_5: \"select\",");
			out.newLine();
		}
		out.newLine();
		out.write("			rows_counter: true, ");
		out.newLine();
		out.write("			display_all_text: \"Display all\"");
		out.newLine();
		out.write("		};");
		out.newLine();
		out.write("setFilterGrid( \"table1\",table1_Props );");
		out.newLine();
		out.write("setAlternateRows( \"table1\",table1_Props );");
		out.newLine();
		out.write("</script>");
		out.newLine();
	}

	/**
	 * Writes the warplanets to the .html file
	 * 
	 * @param fetchInfo
	 * @param warplanetList
	 * @param out
	 * @param httpclient
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static void writeWarplanets(boolean fetchInfo,
			LinkedList<Warplanet> warplanetList, BufferedWriter out,
			DefaultHttpClient httpclient) throws ClientProtocolException,
			IOException {
		String house;
		String player;
		int combatPower;

		double warplanetListSize = warplanetList.size();
		String warplanetListSizeString = String.format("%.0f",
				warplanetListSize);
		if (fetchInfo) {
			StatusMessageHandler.postStatusMessages(12);
		}
		double value = 0;
		String percentage;

		String text = "";
		String textNew = "";
		if (MapTool.GUI) {
			text = MapTool.getTextArea().getText();
			textNew = text + "\n" + MapTool.getTime() + ": ";
		}
		for (int i = 0; i < warplanetListSize; i++) {
			Warplanet warplanet = warplanetList.get(i);
			String url = HTMLBuilder.createURL(warplanet);
			if (fetchInfo) {
				value = (i / warplanetListSize) * 100;
				percentage = String.format("%.2f", value);

				if (MapTool.GUI) {
					MapTool.getTextArea().setText(textNew
							+ "Fetching info of warplanet " + i + "/"
							+ warplanetListSizeString + " = " + percentage
							+ "%");
				} else {
					StatusMessageHandler
					.setConsoleText("Fetching info of warplanet " + i
							+ "/" + warplanetListSizeString + " = "
							+ percentage + "%");
				}

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
			url = HTMLBuilder.createURL(warplanet);
			String link = "<a href=\"" + url + "\">" + warplanet.getName()
					+ "</a>";
			String output = warplanet.toHTMLTable(link, fetchInfo);
			out.write(output);
			out.newLine();
			out.newLine();
		}
		out.newLine();
		if (MapTool.GUI) {
			MapTool.getTextArea().setText(text);
		}

	}

	/**
	 * Writes the neutral planets to the .html file
	 * 
	 * @param fetchHouse
	 * @param neutralPlanetList
	 * @param out
	 * @throws IOException
	 */
	public static void writeNeutral(boolean fetchHouse,
			LinkedList<Planet> neutralPlanetList, BufferedWriter out,
			DefaultHttpClient httpclient) throws IOException {
		for (int i = 0; i < neutralPlanetList.size(); i++) {
			Planet planet = neutralPlanetList.get(i);
			String url = HTMLBuilder.createURL(planet);
			if (fetchHouse) {
				HTTPClientForKosmor
				.parseNeutralPlanets(httpclient, url, planet);
				planet.setHouse("Neutral");
			}
			String link = "<a href=\"" + url + "\">" + planet.getName()
					+ "</a>";
			String output = planet.toHTMLTable(link);
			out.write(output);
			out.newLine();
		}
	}

	/**
	 * Writes the planets with owners to the .html file
	 * 
	 * @param fetchHouse
	 * @param planetList
	 * @param out
	 * @param httpclient
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static void writePlanets(boolean fetchHouse,
			LinkedList<Planet> planetList, BufferedWriter out,
			DefaultHttpClient httpclient) throws ClientProtocolException,
			IOException {
		String house;
		String owner;
		int combatPower;
		double planetListSize = planetList.size();
		String planetListSizeString = String.format("%.0f", planetListSize);
		if (fetchHouse) {
			StatusMessageHandler.postStatusMessages(13);
			StatusMessageHandler.postStatusMessages(14);
		}

		String text = "";
		String textNew = "";

		if (MapTool.GUI) {
			text = MapTool.getTextArea().getText();
			textNew = text + "\n" + MapTool.getTime() + ": ";
		}

		double value = 0;
		String percentage;
		for (int i = 0; i < planetListSize; i++) {
			Planet planet = planetList.get(i);
			String url = HTMLBuilder.createURL(planet);
			if (fetchHouse) {
				value = (i / planetListSize) * 100;
				percentage = String.format("%.2f", value);
				if (MapTool.GUI) {
					MapTool.getTextArea().setText(textNew
							+ " Fetching info of planet " + i + "/"
							+ planetListSizeString + " = " + percentage + "%");
				} else {
					StatusMessageHandler
					.setConsoleText("Fetching info of planet " + i
							+ "/" + planetListSizeString + " = "
							+ percentage + "%");
				}

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
			url = HTMLBuilder.createURL(planet);
			String link = "<a href=\"" + url + "\">" + planet.getName()
					+ "</a>";
			String output = planet.toHTMLTable(link);
			out.write(output);
			out.newLine();
			out.newLine();
		}
		if (MapTool.GUI) {
			MapTool.getTextArea().setText(text);
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
