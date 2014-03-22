package maptool;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;

/**
 * This class writes the planets to a html file.
 * @author Aurion
 *
 */
public class HTMLBuilder {

	final static long MILLISECONDS_IN_A_DAY = 86400000; // 24*60*60*1000 
														// Hours*Minutes*Seconds*Milliseconds

	/**
	 * Writes all planets as links to a html file.
	 * @param args 
	 * @param date 
	 * @throws IOException
	 */
	public static void writeHTML(Boolean writeWarplanets, Boolean writePlanets, Boolean writeNeutral, Date date) {
	
		LinkedList<Planet>[] warplanetAndPlanetList = PlanetListBuilder.buildPlanetList(date);
		LinkedList<Planet> planetList = warplanetAndPlanetList[0];
		LinkedList<Planet> warplanetList = warplanetAndPlanetList[1];
		LinkedList<Planet> neutralPlanetList = warplanetAndPlanetList[2];
		
		String kosmorDate  = MapTool.getKosmorDate(date); // Gets the kosmorDate
		String dateString = MapTool.dateStringBuilder(kosmorDate, date); // Uses the kosmorDate to build the dateString
		
		// Making sure the directory exists, if not, is is created
		File htmldir = new File("html");
		if(!htmldir.exists() || !htmldir.isDirectory()){
			htmldir.mkdir();
		}
		
		String fileName = htmldir+"/"+dateString +(" - Planet & WP List - kosmor.com - .html"); // Uses the dateString to create the fileName
		
		try {
		System.out.println("Writing the html file..."); // Status message #1 Writing HTML
		System.out.println("This will only take a few seconds"); // Status #2 Writing the HTML
		
		FileWriter fileOut = new FileWriter(fileName);
		BufferedWriter out = new BufferedWriter(fileOut);

		out.write("<!doctype html>");
		out.newLine();
		out.write("<html>");
		out.newLine();
		out.write("<body>");
		out.write(dateString);
		out.write("<br>");
		out.newLine();
		
		if(writeWarplanets){
			for (int i = 0; i < warplanetList.size(); i++) {
				Planet planet = warplanetList.get(i);
				out.write("<br>");
				String output = "<a href=\""+createWarPlanetURL(warplanetList, i)+"\">"+planet.toHTMLString()+"</a>";
				out.write(output);
				out.newLine();
			}
		}
		
		if(writePlanets){
			for (int i = 0; i < planetList.size(); i++) {
				Planet planet = planetList.get(i);
				out.write("<br>");
				String output = "<a href=\""+createPlanetURL(planetList, i)+"\">"+planet.toHTMLString()+"</a>";
				out.write(output);
				out.newLine();
			}
		}
		
		if(writeNeutral){
			for (int i = 0; i < neutralPlanetList.size(); i++) {
				Planet planet = neutralPlanetList.get(i);
				out.write("<br>");
				String output = "<a href=\""+createPlanetURL(neutralPlanetList, i)+"\">"+planet.toHTMLString()+"</a>";
				out.write(output);
				out.newLine();
			}
		}
		
		out.write("</body>");
		out.write("</html>");
		
		out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done creating the html"); // Status message #3 Writing HTML
	}

	/**
	 * Builds the actual URL of a planet.
	 * @param planetList The list with all planets
	 * @param currentPos Which planet to use
	 * @return <b>planetsAsUrl</b> The url later used as link to the planet
	 */
	private static String createPlanetURL(LinkedList<Planet> planetList, int currentPos) {
		
		String planetAsURL = null;
		
		// Procudes a string like http:kosmor.com/index.php?action=3000&xpos=1115&ypos=1075&t_typ=1&t_id=3814
		Planet planet = planetList.get(currentPos);
		planetAsURL = "http://www.kosmor.com/index.php?action=3000&xpos="+planet.xPos + "&ypos=" + planet.yPos;
		
		return planetAsURL;

	}
	
	private static String createWarPlanetURL(LinkedList<Planet> warplanetList, int currentPos) {
		
		String warplanetAsURL = null;
		
		// Procudes a string like http:kosmor.com/index.php?action=3000&xpos=1115&ypos=1075&t_typ=1&t_id=3814
		Planet planet = warplanetList.get(currentPos);
		warplanetAsURL = "http://www.kosmor.com/index.php?action=3000&xpos="+planet.xPos + "&ypos=" + planet.yPos;
		
		return warplanetAsURL;

	}
}
