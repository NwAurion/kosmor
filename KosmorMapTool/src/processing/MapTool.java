package processing;


import input.HTTPClientForKosmor;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.TimeZone;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;

import output.CSVCreator;
import output.HTMLBuilder;
import output.PlanetLister;
import output.XMLParserWriter;

/**
 * @updated 31.07.2012
 * @version v2.00
 * @author Aurion
 */
public class MapTool {

	final static double VERSION = 2.00;

	final static long MILLISECONDS_IN_A_DAY = 86400000;

	static long startTime = 0;
	static long endTime = 0;

	// Write warplanets to the html
	public static boolean writeWarplanets = false;
	// Write planets with owners to the html
	public static boolean writePlanets = false;
	// Write neutral planets to the html
	public static boolean writeNeutral = false;

	public static boolean fetchInfo = false;

	private static String checkForUpdate = "off";
	private static boolean updateHTML = false;
	public static boolean GUI = false;
	private static boolean useCustomDate = false;
	private static Date date;
	private static boolean writeCSV = false;

	public static String SVGFileName = "draw_map_svg.svg";

	static JFrame frame;
	private static JTextArea textArea;
	static File settings = new File("settings.ini");
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	static String kosmorDate;

	private static LinkedList<Warplanet> warplanetList;
	private static LinkedList<Planet> planetList;
	private static LinkedList<Planet> neutralPlanetList;

	static PlanetLister planetLister;

	final static String STATUS_MESSAGE_STARTED = "Please do not close this window while it's working";
	final static String STATUS_MESSAGE_FINISHED = "Done. You can close this window now";

	/**
	 * Reads the kosmor svg map (which it downloads first if necessary) and
	 * creates a .html with the info read from the .svg. Parameters (either set
	 * in the settings file or attached as arguments in the startup) determine
	 * what exactly will be written to the .html
	 * 
	 * @param args
	 *            The startup parameters
	 * @throws ClientProtocolException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException,
	ClientProtocolException, IOException {
		String argsString = Arrays.toString(args);
		String[] loginData = new String[2];
		// If the settings file does exist, use it
		if (MapTool.settings.exists()) {
			loginData = MapTool.readSettings();
		} else {
			loginData = MapTool.readArgumentList(argsString);
		}

		if (MapTool.GUI) {
			MapTool.initGUI(); // Initialize GUI only if wanted
		}

		if (!MapTool.checkForUpdate.equals("off")) { // Checking for updates
			UpdateHandler.checkForUpdate(MapTool.checkForUpdate, MapTool.VERSION);
		}
		String player_login = loginData[0];
		String player_password = loginData[1];
		Date date = new Date();
		GregorianCalendar cal = new GregorianCalendar();

		if (MapTool.fetchInfo) {
			HTTPClientForKosmor.readPlayersFromKosmorOnlineTools();
			HTTPClientForKosmor.readHousesFromKosmorOnlineTools(0);
		}

		if (MapTool.updateHTML) {
			File path = new File(".");
			String[] files = path.list(); // Get all files
			for (String file : files) { // Go through all files
				if (file.matches(".*[0-9][0-9][.][0-9][0-9].[0-9].*")) {
					// Matches to date like "12.08.2011" Should probably add
					// .svg extension?
					SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
					MapTool.SVGFileName = file; // Set the current file
					try {
						date = df.parse(file.substring(9, 19));
						// Parse the file name for the date
						cal.setTime(date);
						cal.set(Calendar.HOUR_OF_DAY, 12);
						date = cal.getTime(); // Set the date to the date it got
						// from the file name
						MapTool.runMapTool(player_login, player_password, cal);
						// Run the map tool with every file once
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (!MapTool.updateHTML) {
			MapTool.runMapTool(player_login, player_password, cal);
			// If the user does not want to update, just run it normally
		}

		/*
		 * BufferedReader br = new BufferedReader(new
		 * InputStreamReader(System.in));
		 * 
		 * System.out.println("Please write \"exit\" to close"); String s =
		 * br.readLine(); if (s == "exit") { System.exit(0); }
		 */
		/*
		 * try { Thread.sleep(5000); } catch (InterruptedException e) {
		 * e.printStackTrace(); } System.exit(0);
		 */
	}

	public static void runMapTool(String player_login, String player_password,
			GregorianCalendar cal) throws IOException,
			UnsupportedEncodingException, ClientProtocolException {
		File svgMap = new File(MapTool.SVGFileName);

		if (MapTool.GUI) {
			MapTool.getTextArea().setText(MapTool.getTime() + ": " + MapTool.STATUS_MESSAGE_STARTED);
		} else {
			System.out.println("You are running " + MapTool.VERSION);
			System.out.println(MapTool.getTime() + ": " + MapTool.STATUS_MESSAGE_STARTED);
		}
		MapTool.startTime = System.currentTimeMillis();
		// If the map does not exists, try to download it

		if (!svgMap.exists()) {
			StatusMessageHandler.postStatusMessages(0);

			Object[] httpstuff = HTTPClientForKosmor.doConnect(player_login,
					player_password);
			DefaultHttpClient httpclient = (DefaultHttpClient) httpstuff[0];
			HttpEntity entity = (HttpEntity) httpstuff[1];
			player_login = (String) httpstuff[2];
			player_password = (String) httpstuff[3];

			StatusMessageHandler.postStatusMessages(1);

			HTTPClientForKosmor.downloadMap(httpclient, entity);

			StatusMessageHandler.postStatusMessages(2);
		} else {
			StatusMessageHandler.postStatusMessages(3);
		}
		/*
		 * If the -date parameter is used, the user has to input the date he
		 * wishes to use in place of the current date
		 */
		try {
			if (MapTool.useCustomDate) {
				if (MapTool.GUI) {
					SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH");
					MapTool.date = df.parse(JOptionPane.showInputDialog(null,
							"Enter date", "Please enter the date",
							JOptionPane.QUESTION_MESSAGE));
					cal = MapTool.dateToGMTDate(MapTool.date);
				} else {
					System.out.println("Please set the date");
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(System.in));
					SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH");
					MapTool.date = df.parse(reader.readLine());
					cal = MapTool.dateToGMTDate(MapTool.date);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		MapTool.planetLister = new PlanetLister(MapTool.date,
				MapTool.getKosmorDate());
		HTMLBuilder.writeHTML(MapTool.writeWarplanets, MapTool.writePlanets,
				MapTool.writeNeutral, cal,
				player_login, player_password, MapTool.fetchInfo);

		if (MapTool.writeCSV) {
			CSVCreator csvCreator = new CSVCreator();
			csvCreator.writeCSV(cal, MapTool.getWarplanetList(),
					MapTool.planetList);
		}

		XMLParserWriter.serialize(MapTool.planetLister);

		// boolean database = false;
		// if (database) {
		// String url = "jdbc:mysql://localhost:3306/";
		// String dbName = "kosmor";
		// String driver = "com.mysql.jdbc.Driver";
		// String userName = "root";
		// String password = "root";
		// DatabaseHandler databaseHandler = new DatabaseHandler(url, dbName,
		// driver, userName, password);
		// databaseHandler.connect();
		// databaseHandler.write(MapTool.planetList);
		// databaseHandler.disconnect();
		// }

		MapTool.endTime = System.currentTimeMillis();
		long time = (MapTool.endTime - MapTool.startTime) / 1000;

		if (MapTool.GUI) {
			StatusMessageHandler.setText(MapTool.STATUS_MESSAGE_FINISHED);
			MapTool.getTextArea().setText(MapTool.getTextArea().getText() + "\n" + "It took " + time
					+ " seconds");
		} else {
			StatusMessageHandler.setConsoleText(MapTool.STATUS_MESSAGE_FINISHED);
			System.out.println("It took " + time + " seconds");
		}
	}

	public static String getTime() {
		return MapTool.dateFormat.format(new Date());
	}

	private static void initGUI() {
		MapTool.frame = new JFrame();
		MapTool.frame.setSize(new Dimension(400, 500));
		MapTool.frame.setTitle("Kosmor Map Tool " + MapTool.VERSION);

		MapTool.setTextArea(new JTextArea());
		MapTool.getTextArea().setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(MapTool.getTextArea());
		scrollPane
		.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		MapTool.frame.add(scrollPane);
		MapTool.frame.setVisible(true);
		MapTool.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private static GregorianCalendar dateToGMTDate(Date date) {
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("dd.MM.yyyy HH");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

		// Local time zone
		SimpleDateFormat dateFormatLocal = new SimpleDateFormat("dd.MM.yyyy HH");
		// Time in GMT
		try {
			date = dateFormatLocal.parse(dateFormatGmt.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		return cal;
	}

	private static String[] readSettings() {
		String player_login = "";
		String player_password = "";

		String line;
		try {
			BufferedReader in = new BufferedReader(new FileReader(MapTool.settings));
			while ((line = in.readLine()) != null) {
				if (line.startsWith("warplanets")) {
					MapTool.writeWarplanets = Boolean.valueOf(line.substring(line
							.indexOf("=") + 1));
				}
				if (line.startsWith("planets")) {
					MapTool.writePlanets = Boolean.valueOf(line.substring(line
							.indexOf("=") + 1));
				}
				if (line.startsWith("neutral")) {
					MapTool.writeNeutral = Boolean.valueOf(line.substring(line
							.indexOf("=") + 1));
				}
				if (line.startsWith("login")) {
					player_login = line.substring(line.indexOf("login") + 6);
				}
				if (line.startsWith("password")) {
					player_password = line
							.substring(line.indexOf("password") + 9);
				}
				if (line.startsWith("fetchinfo")) {
					MapTool.fetchInfo = Boolean.valueOf(line.substring(line
							.indexOf("=") + 1));
				}
				if (line.startsWith("updatehtml")) {
					MapTool.updateHTML = Boolean.valueOf(line.substring(line
							.indexOf("=") + 1));
				}
				if (line.startsWith("gui")) {
					MapTool.GUI = Boolean
							.valueOf(line.substring(line.indexOf("=") + 1));
				}
				if (line.startsWith("date")) {
					MapTool.useCustomDate = Boolean.valueOf(line.substring(line
							.indexOf("=") + 1));
				}
				if (line.startsWith("checkforupdate")) {
					MapTool.checkForUpdate = line.substring(line
							.indexOf("checkforupdate") + 15);
				}
				if (line.startsWith("csv")) {
					MapTool.writeCSV = Boolean
							.valueOf(line.substring(line.indexOf("=") + 1));
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String[] { player_login, player_password };
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
			;
			MapTool.writeWarplanets = true;
		}
		if (argsString.contains("-planets")) {
			MapTool.writePlanets = true;
		}
		if (argsString.contains("-neutral")) {
			MapTool.writeNeutral = true;
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
		if (argsString.contains("-fetchinfo")) {
			MapTool.fetchInfo = true;
		}
		if (argsString.contains("-updatehtml")) {
			MapTool.updateHTML = true;
		}
		if (argsString.contains("-gui")) {
			MapTool.GUI = true;
		}
		if (argsString.contains("-date")) {
			MapTool.useCustomDate = true;
		}
		if (argsString.contains("-checkforupdate")) {
			MapTool.checkForUpdate = argsString.substring(argsString
					.indexOf("-checkforupdate") + 16);
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
	public static String dateStringBuilder(String kosmorDate, Calendar date) {

		String dayAsString;
		String monthAsString;
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date.getTime());

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
	 * @param currentDate
	 * @return the kosmorDate
	 */
	public static void setKosmorDate(GregorianCalendar currentDate) {

		GregorianCalendar startCalendar = new GregorianCalendar();
		startCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		startCalendar.set(Calendar.HOUR_OF_DAY, 8);
		startCalendar.set(Calendar.DAY_OF_MONTH, 2);
		startCalendar.set(Calendar.MONTH, 1);
		startCalendar.set(Calendar.YEAR, 2004);
		Date startDate = startCalendar.getTime();

		int kosmorDay = 7;
		int kosmorYear = 3500;

		while (startDate.getTime() < currentDate.getTimeInMillis()
				- MapTool.MILLISECONDS_IN_A_DAY) {
			startDate.setTime(startDate.getTime() + MapTool.MILLISECONDS_IN_A_DAY);
			startCalendar.setTime(startDate);
			if (currentDate.isLeapYear(currentDate.get(Calendar.YEAR) - 1)) {
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

		String formattedKosmorDay = String.format("%03d", kosmorDay);
		String kosmorDate = kosmorYear + "_" + formattedKosmorDay;
		MapTool.kosmorDate = kosmorDate;

	}

	public static String getKosmorDate() {
		if (MapTool.kosmorDate == null) {
			MapTool.setKosmorDate(new GregorianCalendar());
		}
		return MapTool.kosmorDate;
	}

	public static LinkedList<Planet> getPlanetList() {
		return MapTool.planetList;
	}

	public static void setPlanetList(LinkedList<Planet> planetList) {
		MapTool.planetList = planetList;
	}

	public static LinkedList<Warplanet> getWarplanetList() {
		return MapTool.warplanetList;
	}

	public static void setWarplanetList(LinkedList<Warplanet> warplanetList) {
		MapTool.warplanetList = warplanetList;
	}

	public static LinkedList<Planet> getNeutralPlanetList() {
		return MapTool.neutralPlanetList;
	}

	public static void setNeutralPlanetList(LinkedList<Planet> neutralPlanetList) {
		MapTool.neutralPlanetList = neutralPlanetList;

	}

	public static JTextArea getTextArea() {
		return MapTool.textArea;
	}

	public static void setTextArea(JTextArea textArea) {
		MapTool.textArea = textArea;
	}

	public static PlanetLister getPlanetLister() {
		return MapTool.planetLister;
	}

}
