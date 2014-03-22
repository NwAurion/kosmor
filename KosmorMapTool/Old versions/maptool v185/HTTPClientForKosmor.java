package maptool;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**
 * HTTPClient to connect to the kosmor website
 * @author Aurion
 *
 */
public class HTTPClientForKosmor {

	final static int[] COMBAT_POWER = new int[] { 9, 13, 19, 41, 65, 116, 188, 310, 528, 916, 3306 };


	/**
	 * Does connect to the given URL (kosmor.com/index...) and tries to login
	 * with the user name and password given. If no user name or password is
	 * given, it requests those.
	 * 
	 * @param player_login
	 *            The user name of the player
	 * @param player_password
	 *            The password used by the player
	 * @return 
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 */
	public static Object[] doConnect(String player_login, String player_password)
			throws IOException, UnsupportedEncodingException,ClientProtocolException {
	
		BufferedReader readLoginData = new BufferedReader(new InputStreamReader(System.in));
		// If no user name is given in the method call, it must be entered here
		if (player_login == "") {
			if(MapTool.GUI){
			  player_login = JOptionPane.showInputDialog(null,
					   "Missing username",
					   "Please enter your username",
					   JOptionPane.QUESTION_MESSAGE);
			}
			else {
				System.out.println("Please enter your username: ");
				player_login = readLoginData.readLine();
			}
			

		}
		// If no password is given in the method call, it must be entered here
		if (player_password == "") {
			if(MapTool.GUI){
			 player_password = JOptionPane.showInputDialog(null,
					   "Missing password",
					   "Pleae enter your password",
					   JOptionPane.QUESTION_MESSAGE);
			}
			else {
				System.out.println("Please enter your password ");
				player_password = readLoginData.readLine();
			}
		}

		DefaultHttpClient httpclient = new DefaultHttpClient();

		HttpGet httpget = new HttpGet("http://kosmor.com");

		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			InputStream input = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			while ((reader.readLine()) != null) {}
		}

		HttpPost httpost = new HttpPost("http://kosmor.com/index.php?action=1");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("player_login", player_login));
		nvps.add(new BasicNameValuePair("player_password", player_password));

		httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

		response = httpclient.execute(httpost);

		/*
		 * The first http post is needed to login, so it can not be removed. But
		 * once a call has been made, the whole response entity has to be
		 * consumed. The content is mostly irrelevant, except for wether the login actually worked
		 */
	
		
		String line;
		entity = response.getEntity();
		if (entity != null) {
			InputStream input = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			while ((line = reader.readLine()) != null) {
				if (line.contains("Login failed")) {
					if(MapTool.GUI){
					JOptionPane.showMessageDialog(
							null,
							"The username or password is invalid!",
							"Something went wrong", JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					}
					else System.out.println("The username or password is invalid! Exiting");
				}
			}
		}
		return new Object[]{httpclient, entity, player_login, player_password};
	}
	


	/**
	 * Does connect to the map location and downloads the map
	 * @param httpclient
	 * @param entity
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static void downloadMap(DefaultHttpClient httpclient, HttpEntity entity) throws IOException, ClientProtocolException {
		
		HttpPost httpost2 = new HttpPost("http://kosmor.com/draw_map_svg.svg");
		HttpResponse response = httpclient.execute(httpost2);

		HttpEntity entity2 = response.getEntity();

		if (entity != null) {
			InputStream input = entity2.getContent();
			FileWriter writer = new FileWriter("draw_map_svg.svg");

			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String ln = "";

			
			// Downloading the map and writing it to a file
			while ((ln = reader.readLine()) != null) {
				writer.write(ln);
				writer.write("\n");
			}
			writer.close();
			reader.close();
		}
		httpclient.getConnectionManager().shutdown();
	}


/**
 * Parses the html of a single planets location, looking for the planet to determine which one is correct when multiple are found.
 * @param httpclient The httpclient used for the connection
 * @param planetURL The url to the location of this planet
 * @param planetName The name of the planet to look for (needed if several planets are close together)
 * @return the house name of the planet
 * @throws ClientProtocolException
 * @throws IOException
 */
	public static Object[] parsePlanets(DefaultHttpClient httpclient, String planetURL, Planet planet, boolean isWarplanet) throws ClientProtocolException, IOException {
		HttpPost httpost3 = new HttpPost(planetURL);
		HttpResponse response = httpclient.execute(httpost3);
		String house = "";
		String owner = "";
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream input = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String ln = "";
			while ((ln = reader.readLine()) != null) {
				if (ln.contains(planet.getName())) { // If the planet is found..
					int i = 0;
					while ((ln = reader.readLine()) != null){ // ..then look in the following lines for the rest of the data
						i++;
						if (ln.contains("<td>House:</td>")) { // First planet does use "house"
							house = ln.substring(ln.indexOf("=NormalBold>") + 12,ln.indexOf("</span>"));
						} else if (ln.contains("<td>Haus:</td>")) { // Second (possibly any successive planets) do use "Haus" and another formatting
							house = ln.substring(ln.indexOf("<td>Haus:</td><td>") + 18, ln.indexOf("</td></tr>"));
						}
						if(ln.contains("<td>Owner:</td>")) { // Getting the owner, need to find the line containing it.
							try {
								owner = ln.substring(ln.indexOf("<span class=NormalBold>")+23, ln.indexOf("</span></td></tr>"));
							} catch (StringIndexOutOfBoundsException e) {
								owner = ln.substring(ln.indexOf("<b>")+3, ln.indexOf("</b></span>"));
							}				
						}
					if (i>5) break;	// Stop after reading all the relevant info
					}		
				}
			}
			reader.close();
		};
		if (isWarplanet) owner = planet.getName(); // The line with the owner has another formatting 
												   // for warplanets, much easier this way
		return new Object[]{house, owner};
	}



	public static int countShipsOnPlanet(DefaultHttpClient httpclient, String planetURL, Planet planet) throws IOException {
		HttpPost httpost3 = new HttpPost(planetURL);
		HttpResponse response = httpclient.execute(httpost3);
		int totalCombatPower = 0;
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream input = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String ln = "";
			while ((ln = reader.readLine()) != null) {
				if(ln.contains("Ships on<br>") && ln.contains(planet.getName())){ // If there are ships on the planet/warplanet it is looking for
					while ((ln = reader.readLine()) != null && ln.matches(".*(H[0-9]).*")){ // Go through all ships, stop when something
						if (ln.matches(".*\\(H1\\).*")) {									// not being a ship is found
							totalCombatPower  = totalCombatPower + COMBAT_POWER[0];			
						} else if (ln.matches(".*(H2).*")) {
							totalCombatPower  = totalCombatPower + COMBAT_POWER[1];
						} else if (ln.matches(".*(H3).*"))  {
							totalCombatPower  = totalCombatPower + COMBAT_POWER[2];
						} else if (ln.matches(".*(H4).*"))  {
							totalCombatPower  = totalCombatPower + COMBAT_POWER[3];			// Adding up the combat power
						} else if (ln.matches(".*(H5).*"))  {
							totalCombatPower  = totalCombatPower + COMBAT_POWER[4];
						} else if (ln.matches(".*(H6).*"))  {
							totalCombatPower  = totalCombatPower + COMBAT_POWER[5];
						} else if (ln.matches(".*(H7).*"))  {
							totalCombatPower  = totalCombatPower + COMBAT_POWER[6];
						} else if (ln.matches(".*(H8).*"))  {
							totalCombatPower  = totalCombatPower + COMBAT_POWER[7];
						} else if (ln.matches(".*(H9).*"))  {
							totalCombatPower  = totalCombatPower + COMBAT_POWER[8];
						} else if (ln.matches(".*(H10).*"))  {
							totalCombatPower  = totalCombatPower + COMBAT_POWER[9];
						} else if (ln.matches(".*(H11).*"))  {
							totalCombatPower  = totalCombatPower + COMBAT_POWER[10];
						}
						reader.readLine(); // Ships use 4 lines in total (including <table> tag etc.
						reader.readLine(); // Therefore, once a ship has been found, every fourth line
						reader.readLine(); // does contain the next ship, if there are still ships left
					}
				}
			}
		}
		return totalCombatPower;	

	}
}
