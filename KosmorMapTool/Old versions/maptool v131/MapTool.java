package kosmor.maptool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @updated 21.06.2011
 * @version v1.31
 * @author Aurion
 *
 */
public class MapTool {
	

	final static long MILLISECONDS_IN_A_DAY = 86400000;
	static long startTime = 0;
	static long endTime = 0;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
			System.out.println("Please stand by while it's working.");
			System.out.println("It should not take longer then a minute");
			
			if(Arrays.toString(args).contains("-date")){
				System.out.println("Please set date");
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				try {
					SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
					Date date = df.parse(reader.readLine());
					date.setHours(15);
					startTime = System.currentTimeMillis();
					HTMLBuilder.writeHTML(args, date); 
					endTime = System.currentTimeMillis();
				} catch (IOException e) {
					e.getCause();
				} catch (ParseException e) {
					e.getCause();
				}
			}
			else {	
				startTime = System.currentTimeMillis();
				HTMLBuilder.writeHTML(args, new Date()); 
				endTime = System.currentTimeMillis();
			}
			System.out.println("Finished. It took "+(endTime-startTime)/1000+" seconds");
	}
	
	/**
	 * Creates a dateString, as in, the real and kosmorDate as String
	 * @param kosmorDate
	 * @param date 
	 * @return dateString containing both the real and the kosmordate
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
