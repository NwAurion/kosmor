

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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

	/**
	 * Does connect to the given URL (kosmor.com/index...) and tries to login
	 * with the user name and password given. If no user name or password is
	 * given, it requests those.
	 * 
	 * @param player_login
	 *            The user name of the player
	 * @param player_password
	 *            The password used by the player
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 */
	public static void doConnect(String player_login, String player_password)
			throws IOException, UnsupportedEncodingException,ClientProtocolException {
	
		BufferedReader readLoginData = new BufferedReader(new InputStreamReader(System.in));
		
		// If no user name is given in the method call, it must be entered here
		if (player_login == "") {
			System.out.println("Please enter your username: ");
			player_login = readLoginData.readLine();
		}
		// If no password is given in the method call, it must be entered here
		if (player_password == "") {
			System.out.println("Please enter your password ");
			player_password = readLoginData.readLine();
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
		 * consumed. The content is - in this case - irrelevant however.
		 */
		entity = response.getEntity();
		if (entity != null) {
			InputStream input = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			
			while ((reader.readLine()) != null) {}
		}

		// Connecting to the map location
		HttpPost httpost2 = new HttpPost("http://kosmor.com/draw_map_svg.svg");
		response = httpclient.execute(httpost2);

		HttpEntity entity2 = response.getEntity();

		if (entity != null) {
			InputStream input = entity2.getContent();
			FileWriter writer = new FileWriter("draw_map_svg.svg");

			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String ln = "";

			// Dwonloading the map and writing it to a file
			while ((ln = reader.readLine()) != null) {
				writer.write(ln);
				writer.write("\n");
			}
			writer.close();
			reader.close();
		}
		httpclient.getConnectionManager().shutdown();
	}
}
