package maptool;

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
import java.util.TimeZone;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @updated 30.08.2011
 * @version v1.85
 * @author Aurion
 */
public class MapTool {
	
	final static double VERSION = 1.85;

	final static long MILLISECONDS_IN_A_DAY = 86400000;
	static long startTime = 0;
	static long endTime = 0;
	private static boolean writeWarplanets = false; // Write warplanets to the html
	private static boolean writePlanets = false; // Write planets with owners to the html
	private static boolean writeNeutral = false;// Write neutral planets to the html
	
	public static boolean fetchInfo = false;
	
	private static String checkForUpdate;
	private static boolean updateHTML = false;
	public static boolean GUI = false;
	private static boolean useCustomDate = false;
	
	public static String SVGFileName = "draw_map_svg.svg";
	
	static JFrame frame;
	static JTextArea textArea;
	static File settings = new File("settings.ini");
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	
	final static String STATUS_MESSAGE_STARTED = "Please do not close this window while it's working";
	final static String STATUS_MESSAGE_FINISHED = "Done. You can close this window now";

	
	/**
	 * Reads the kosmor svg map (which it downloads first if necessary) and creates a .html
	 * with the info read from the .svg. Parameters (either set in the settings file or attached as arguments in the startup) 
	 * determine what exactly will be written to the .html
	 * 
	 * @param args The startup parameters
	 * @throws ClientProtocolException 
	 * @throws UnsupportedEncodingException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException, ClientProtocolException, IOException {
		String argsString = Arrays.toString(args);
		String[] loginData = new String[2];
		// If the settings file does exist, use it
		if (settings.exists()){
			loginData = readSettings();
		}
		// If no settings file does exist, use the arguments supplied in the startup
		else loginData = readArgumentList(argsString);
		
		if(GUI)	initGUI(); // Initialize GUI only if wanted
		
		if (!checkForUpdate.equals("off")) { // Checking for updates
			UpdateHandler.checkForUpdate(checkForUpdate, VERSION);
		}
		String player_login = loginData[0];
		String player_password = loginData[1];
		Date date = new Date();
		
		if(updateHTML){
			File path = new File(".");
			String[] files = path.list(); // Get all files
			for (int i = 0; i < files.length; i++) { // Go through all files
				if (files[i].matches(".*[0-9][0-9][.][0-9][0-9].[0-9].*")) { // Matches to a date like "12.08.2011" Should probably add .svg extension?
					SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
					SVGFileName = files[i]; //  Set the current file
					try {
						date = df.parse(files[i].substring(9, 19)); // Parse the file name for the date
						Calendar cal = Calendar.getInstance();
						cal.setTime(date);
						cal.set(Calendar.HOUR_OF_DAY, 12);
						date = cal.getTime(); // Set the date to the date it got from the file name
						runMapTool(player_login, player_password, date); // Run the map tool with every file once
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if(!updateHTML) runMapTool(player_login, player_password, date); // If the user does not want to update, just run it normally

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(0);
		}

	public static void runMapTool(String player_login, String player_password,
			Date date) throws IOException, UnsupportedEncodingException,
			ClientProtocolException {
		File svgMap = new File(SVGFileName);
		
		if(GUI) textArea.setText(getTime()+": "+STATUS_MESSAGE_STARTED);
		else {
			System.out.println("You are running "+VERSION);
			System.out.println(getTime()+": "+STATUS_MESSAGE_STARTED);
		}
		startTime = System.currentTimeMillis();
		// If the map does not exists, try to download it
		
		if (!svgMap.exists()){	
			StatusMessageHandler.postStatusMessages(0);
			
			Object[] httpstuff = HTTPClientForKosmor.doConnect(player_login, player_password);
			DefaultHttpClient httpclient = (DefaultHttpClient) httpstuff[0];
			HttpEntity entity = (HttpEntity) httpstuff[1];
			player_login = (String) httpstuff[2];
			player_password = (String) httpstuff[3];
			
			StatusMessageHandler.postStatusMessages(1);
			
			HTTPClientForKosmor.downloadMap(httpclient, entity);
			
			StatusMessageHandler.postStatusMessages(2);
		}
		else {
			StatusMessageHandler.postStatusMessages(3);
		}
		/* If the -date parameter is used, the user has to input the date he wishes to use
		   in place of the current date */
		try {
		if (useCustomDate)
			if(GUI){
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH");
			date = df.parse(JOptionPane.showInputDialog(null,
					"Enter date",
					"Please enter the date", 
					JOptionPane.QUESTION_MESSAGE));
			date = dateToGMTDate(date);
		} else {
			System.out.println("Please set the date");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH");
				date = df.parse(reader.readLine());
				date = dateToGMTDate(date);
			}
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		HTMLBuilder.writeHTML(writeWarplanets, writePlanets, writeNeutral, date, player_login, player_password, fetchInfo);
		endTime = System.currentTimeMillis();
		long time = (endTime-startTime)/1000;
		
		if(GUI){StatusMessageHandler.setText(STATUS_MESSAGE_FINISHED);
		textArea.setText(textArea.getText()+"\n"+"It took "+time+" seconds");
		}
		else {
			StatusMessageHandler.setConsoleText(STATUS_MESSAGE_FINISHED);
			System.out.println("It took "+time+" seconds");
		}
	}

	public static String getTime(){
		return dateFormat.format(new Date());		
	}

	private static void initGUI() {
		frame = new JFrame();
		frame.setSize(new Dimension(400,500));
		frame.setTitle("Kosmor Map Tool "+VERSION);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		frame.add(scrollPane);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private static Date dateToGMTDate(Date date) {
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("dd.MM.yyyy HH");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

		//Local time zone   
		SimpleDateFormat dateFormatLocal = new SimpleDateFormat("dd.MM.yyyy HH");
		//Time in GMT
		try {
			date = dateFormatLocal.parse(dateFormatGmt.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	private static String[] readSettings(){
		String player_login = "";
		String player_password = "";
		
		String line;
		try {
			BufferedReader in = new BufferedReader(new FileReader(settings));
			while ((line = in.readLine()) != null) {
				if (line.startsWith("warplanets")) {
					writeWarplanets = Boolean.valueOf(line.substring(line.indexOf("=")+1));
				}
				if (line.startsWith("planets")) {
					writePlanets = Boolean.valueOf(line.substring(line.indexOf("=")+1));
				}
				if (line.startsWith("neutral")) {
					writeNeutral = Boolean.valueOf(line.substring(line.indexOf("=")+1));
				}
				if (line.startsWith("login")) {
					player_login = line.substring(line.indexOf("login")+6);
				}
				if (line.startsWith("password")) {
					player_password = line.substring(line.indexOf("password")+9);
				}
				if (line.startsWith("fetchinfo")) {
					fetchInfo = Boolean.valueOf(line.substring(line.indexOf("=")+1));
				}
				if (line.startsWith("updatehtml")) {
					updateHTML = Boolean.valueOf(line.substring(line.indexOf("=")+1));
				}
				if(line.startsWith("gui")) {
					GUI = Boolean.valueOf(line.substring(line.indexOf("=")+1));
				}
				if(line.startsWith("date")) {
					useCustomDate = Boolean.valueOf(line.substring(line.indexOf("=")+1));
				}
				if(line.startsWith("checkforupdate")) {
					checkForUpdate = line.substring(line.indexOf("checkforupdate")+15);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String[]{player_login, player_password};
	}
	
	
	/**
	 * Reads the startup parameters and sets the variables to the correct values
	 * @param argsString The startup parameters to evaluate
	 * @return 
	 */
	private static String[] readArgumentList(String argsString) {
		String player_login = "";
		String player_password = "";
		
			if(argsString.contains("-warplanets")){;
				writeWarplanets = true;
			}
			if(argsString.contains("-planets")){
				writePlanets = true;
			}
			if(argsString.contains("-neutral")){
				writeNeutral = true;
			}
			if (argsString.contains("-login")) {
				player_login = argsString.substring(argsString.indexOf("-login")+8, argsString.indexOf("-", argsString.indexOf("-login")+1));
				player_login = player_login.substring(0, player_login.length()-2);
			}
			if (argsString.contains("-password")) {
				player_password = argsString.substring(argsString.indexOf("-password")+11, argsString.indexOf("-", argsString.indexOf("-password")+1));
				player_password = player_password.substring(0, player_password.length()-2);
			}
			if(argsString.contains("-fetchinfo")){
				fetchInfo = true;
			}
			if(argsString.contains("-updatehtml")){
				updateHTML = true;
			}
			if(argsString.contains("-gui")){
				GUI = true;
			}
			if(argsString.contains("-date")) {
				useCustomDate = true;
			}
			if(argsString.contains("-checkforupdate")){
				checkForUpdate = argsString.substring(argsString.indexOf("-checkforupdate")+16);
			}
			
		return new String[]{player_login, player_password};
	}

	/**
	 * Creates a dateString, as in, the real and kosmorDate as String
	 * @param kosmorDate The kosmor date
	 * @param date  The real date
	 * @return dateString containing both the real and the kosmor date
	 */
	public static String dateStringBuilder(String kosmorDate, Date date) {

		String dayAsString;
		String monthAsString;
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);

		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH)+1;
		int year = calendar.get(Calendar.YEAR);

		dayAsString = String.valueOf(day);
		monthAsString = String.valueOf(month);

		if(dayAsString.length() <2){dayAsString = "0".concat(dayAsString);}
		if(monthAsString.length() <2){monthAsString = "0".concat(monthAsString);}

		String dateString = kosmorDate+"("+dayAsString+"."+monthAsString+"."+year+")";
		return dateString;
	}

	/**
	 * Calculates the current kosmorDate using the real date it started,
	 * the kosmorDate it started and the difference between now and then.
	 * @param date 
	 * @return the kosmorDate
	 */
	public static String getKosmorDate(Date currentDate){	

		GregorianCalendar startCalendar = new GregorianCalendar();
		startCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		startCalendar.set(Calendar.HOUR_OF_DAY, 8);
		startCalendar.set(Calendar.DAY_OF_MONTH, 2);
		startCalendar.set(Calendar.MONTH, 1);
		startCalendar.set(Calendar.YEAR, 2004);
		Date startDate = startCalendar.getTime();

		int kosmorDay = 7;
		int kosmorYear = 3500;

		while (startDate.getTime() < currentDate.getTime()- MILLISECONDS_IN_A_DAY) {
			startDate.setTime(startDate.getTime() + MILLISECONDS_IN_A_DAY);
			startCalendar.setTime(startDate);
			if (startCalendar.isLeapYear(startCalendar.get(Calendar.YEAR))) {
				if (kosmorDay > 366) {
					kosmorDay = 1;
					kosmorYear++;
				} else
					kosmorDay++;
			} else if (kosmorDay > 365) {
				kosmorDay = 1;
				kosmorYear++;
			} else
				kosmorDay++;
		}

		String kosmorDate = kosmorDay + "_" + kosmorYear;
		return kosmorDate;

	}

}
