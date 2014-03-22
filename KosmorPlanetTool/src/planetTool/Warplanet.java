package planetTool;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Representation of a warplanet
 * Treated as special kind of planet
 * @author Aurion
 * 
 */
public class Warplanet extends Planet {

	Boolean didJump;
	Double distanceTraveled;
	double xPosPrevious;
	double yPosPrevious;


	public Warplanet(String name, int x, int y, String color, Boolean ships,
			Boolean didJump, double distanceTraveled, int xPrevious,
			int yPrevious, String type, String csvPlanetType) {
		super(name, x, y, color, ships, type, csvPlanetType);
		this.didJump = didJump;
		this.distanceTraveled = distanceTraveled;
		xPosPrevious = xPrevious;
		yPosPrevious = yPrevious;
	}

	public String toHTMLTable(String link, boolean fetchInfo) {
		DecimalFormat formatter;
		if (fetchInfo) {
			formatter = new DecimalFormat("#0.0"); // Using one decimal place
		}
		else {
			formatter = new DecimalFormat("#0"); // SVG coords are integer to begin with, no point in displaying decimal places
		}

		// Ensuring the separator is a dot (31.092 instead of 31,092)
		DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols(Locale.getDefault());
		decimalSymbol.setDecimalSeparator('.');
		formatter.setDecimalFormatSymbols(decimalSymbol);

		String xPosAsString = formatter.format(xPos);
		String yPosAsString = formatter.format(yPos);
		String output;
		output = "<tr>"+"\n";
		output += "<td><input type='radio' name='p1' value='" + name + "'"
				+ " onclick=\"DoDistance();\"/>" + "\n"
				+ "<input type='radio' name='p2' value='" + name + "' "
				+ "onclick=\"DoDistance();\"/>" + "\n"
				+ "<input type='radio' name='p3' value='" + name + "' "
				+ "onclick=\"DoDistance();\"/>" + "\n";
		output += "<td id='"+name+"'>"+link+ "\n"+
				"<td id='"+name+"~x'>"+xPosAsString+"\n"+
				"<td id='"+name+"~y'>"+yPosAsString+"\n";


		if (Planet.player_name_list.contains(owner)){
			output += "<td>"+"<a href=\""+Planet.player_id_list.get(Planet.player_name_list.
					indexOf(owner))+"\">"+owner+"</a>"+ "\n";
		} else {
			output += "<td>"+owner+ "\n";
		}
		if (Planet.house_name_list.contains(house)){
			output += "<td>"+"<a href=\""+Planet.house_id_list.get(Planet.house_name_list.
					indexOf(house))+"\">"+house+"</a>"+ "\n";
		} else {
			output += "<td>"+house + "\n";
		}
		output += "<td>"+"<font color=#"+color+">"+type+"</font>"+ "\n";
		output += "<td>"+combatPower + "\n";

		output += "<td>"+distanceTraveled.intValue()+"\n";
		output += "<td><input type='radio' name='dist' value='" + name
				+ "' " + "onchange=\"DistTool2();\" />" + "\n";
		output += "<td id='"+name+"~d'>" + "\n";
		output += "</tr>" + "\n";
		return output;
	}

	public void setType(String type) {
		this.type = type;

	}

}