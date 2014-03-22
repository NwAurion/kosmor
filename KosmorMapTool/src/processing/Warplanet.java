package processing;

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
		this.xPosPrevious = xPrevious;
		this.yPosPrevious = yPrevious;
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

		String xPosAsString = formatter.format(this.xPos);
		String yPosAsString = formatter.format(this.yPos);
		String output;
		output = "<tr>"+"\n";
		output += "<td><input type='radio' name='p1' value='" + this.name + "'"
				+ " onclick=\"DoDistance();\"/>" + "\n"
				+ "<input type='radio' name='p2' value='" + this.name + "' "
				+ "onclick=\"DoDistance();\"/>" + "\n"
				+ "<input type='radio' name='p3' value='" + this.name + "' "
				+ "onclick=\"DoDistance();\"/>" + "\n";
		output += "<td id='"+this.name+"'>"+link+ "\n"+
				"<td id='"+this.name+"~x'>"+xPosAsString+"\n"+
				"<td id='"+this.name+"~y'>"+yPosAsString+"\n";

		if (MapTool.fetchInfo){
			if (Planet.player_name_list.contains(this.owner)){
				output += "<td>"+"<a href=\""+Planet.player_id_list.get(Planet.player_name_list.
						indexOf(this.owner))+"\">"+this.owner+"</a>"+ "\n";
			} else {
				output += "<td>"+this.owner+ "\n";
			}
			if (Planet.house_name_list.contains(this.house)){
				output += "<td>"+"<a href=\""+Planet.house_id_list.get(Planet.house_name_list.
						indexOf(this.house))+"\">"+this.house+"</a>"+ "\n";
			} else {
				output += "<td>"+this.house + "\n";
			}
			output += "<td>"+"<font color=#"+this.color+">"+this.type+"</font>"+ "\n";
			output += "<td>"+this.combatPower + "\n";
		}
		else {
			output +=
					"<td>"+"<font color=#"+this.color+">"+this.type+"</font>"+"\n"+
							"<td>"+this.ships+"\n";
		}
		output += "<td>"+this.distanceTraveled.intValue()+"\n";
		output += "<td><input type='radio' name='dist' value='" + this.name
				+ "' " + "onchange=\"DistTool2();\" />" + "\n";
		output += "<td id='"+this.name+"~d'>" + "\n";
		output += "</tr>" + "\n";
		return output;
	}

	public void setType(String type) {
		this.type = type;

	}

}