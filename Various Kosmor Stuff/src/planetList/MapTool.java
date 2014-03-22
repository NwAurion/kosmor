
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.http.client.ClientProtocolException;

/**
 * @updated 24.06.2011
 * @version v1.4
 * @author Aurion
 * 
 */
public class MapTool {

	final static long MILLISECONDS_IN_A_DAY = 86400000;
	static long startTime = 0;
	static long endTime = 0;
	private static Boolean writeWarplanets;
	private static Boolean writePlanets;
	private static Boolean writeNeutral;

	/**
	 * Reads the kosmor svg map (which it downloads first if necessary) and
	 * creates a .html with the info read from the .svg. Parameters determine
	 * what exactly will be written to the .html
	 * 
	 * @param args
	 *            The startup parameters
	 * @throws ClientProtocolException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws UnsupportedEncodingException,
			ClientProtocolException, IOException {
		// Reading the String[] args parameter lists, to find out which ones
		// where used
		String argsString = Arrays.toString(args);
		String[] loginData = readArgumentList(argsString);

		String player_login = loginData[0];
		String player_password = loginData[1];

		String SVGFileName = "draw_map_svg.svg";
		File svgMap = new File(SVGFileName);

		// If the map does not exists, try to download it
		if (!svgMap.exists()) {
			System.out.println("Connecting to Kosmor.com to download the map"); // Status
																				// message
																				// #1
																				// Downloading
			HTTPClientForKosmor.doConnect(player_login, player_password);
			System.out.println("Succesfully downloaded the map"); // Status
																	// message
																	// #2
																	// Downloading
		}

		System.out.println("Please stand by while it's working."); // Status
																	// message
																	// #1 Map
																	// Tool
		System.out.println("It should not take longer then a minute"); // Status
																		// message
																		// #2
																		// Map
																		// Tool

		/*
		 * If the -date parameter is used, the user has to input the date he
		 * wishes to use in place of the current date
		 */
		if (argsString.contains("-date")) {
			System.out.println("Please set date");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			try {
				SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
				Date date = df.parse(reader.readLine());
				date.setHours(15);
				startTime = System.currentTimeMillis();
				// HTMLBuilder.writeHTML(writeWarplanets, writePlanets,
				// writeNeutral, date);
				endTime = System.currentTimeMillis();
			} catch (IOException e) {
				e.getCause();
			} catch (ParseException e) {
				e.getCause();
			}
		} else { // If no -date parameter was used, the current system date will
					// be used
			startTime = System.currentTimeMillis();
			// HTMLBuilder.writeHTML(writeWarplanets, writePlanets,
			// writeNeutral, new Date());
			endTime = System.currentTimeMillis();
		}
		System.out.println("Finished. It took " + (endTime - startTime) / 1000
				+ " seconds"); // Status message #3 Map Tool
	}

	/**
	 * Reads the startup parameters and sets the variables to the correct values
	 * 
	 * @param argsString
	 *            The startup parameters to evaluate
	 * @return
	 */
	private static String[] readArgumentList(String argsString) {
		String player_login = "";
		String player_password = "";

		if (argsString.contains("-warplanets")) {
			writeWarplanets = true;
		}
		if (argsString.contains("-planets")) {
			writePlanets = true;
		}
		if (argsString.contains("-neutral")) {
			writeNeutral = true;
		}
		if (argsString.contains("-login")) {
			player_login = argsString.substring(
					argsString.indexOf("-login") + 8,
					argsString.indexOf("-", argsString.indexOf("-login") + 1));
			player_login = player_login.substring(0, player_login.length() - 2);
		}
		if (argsString.contains("-password")) {
			player_password = argsString.substring(argsString
					.indexOf("-password") + 11, argsString.indexOf("-",
					argsString.indexOf("-password") + 1));
			player_password = player_password.substring(0,
					player_password.length() - 2);
		}
		return new String[] { player_login, player_password };
	}

	/**
	 * Creates a dateString, as in, the real and kosmorDate as String
	 * 
	 * @param kosmorDate
	 *            The kosmor date
	 * @param date
	 *            The real date
	 * @return dateString containing both the real and the kosmor date
	 */
	public static String dateStringBuilder(String kosmorDate, Date date) {

		String dayAsString;
		String monthAsString;
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);

		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);

		dayAsString = String.valueOf(day);
		monthAsString = String.valueOf(month);

		if (dayAsString.length() < 2) {
			dayAsString = "0".concat(dayAsString);
		}
		if (monthAsString.length() < 2) {
			monthAsString = "0".concat(monthAsString);
		}

		String dateString = kosmorDate + "(" + dayAsString + "."
				+ monthAsString + "." + year + ")";
		return dateString;
	}

	/**
	 * Calculates the current kosmorDate using the real date it started, the
	 * kosmorDate it started and the difference between now and then.
	 * 
	 * @param date
	 * @return the kosmorDate
	 */
	public static String getKosmorDate(Date currentDate) {
		GregorianCalendar currentDateCalendar = new GregorianCalendar();

		GregorianCalendar startCalendar = new GregorianCalendar();
		startCalendar.set(Calendar.HOUR_OF_DAY, 10);
		startCalendar.set(Calendar.DAY_OF_MONTH, 2);
		startCalendar.set(Calendar.MONTH, 1);
		startCalendar.set(Calendar.YEAR, 2004);
		Date startDate = startCalendar.getTime();

		int kosmorDay = 6;
		int kosmorYear = 3500;

		if (currentDateCalendar.get(Calendar.HOUR_OF_DAY) > 10) {
			kosmorDay++;
		}

		while (startDate.getTime() < currentDate.getTime()
				- MILLISECONDS_IN_A_DAY) {
			startDate.setTime(startDate.getTime() + MILLISECONDS_IN_A_DAY);
			startCalendar.setTime(startDate);
			if (startCalendar.isLeapYear(startCalendar.get(Calendar.YEAR))) {
				if (kosmorDay > 366) {
					kosmorDay = 1;
					kosmorYear++;
				} else {
					kosmorDay++;
				}
			} else if (kosmorDay > 365) {
				kosmorDay = 1;
				kosmorYear++;
			} else {
				kosmorDay++;
			}
		}

		String kosmorDate = kosmorDay + "_" + kosmorYear;
		return kosmorDate;

	}

}
