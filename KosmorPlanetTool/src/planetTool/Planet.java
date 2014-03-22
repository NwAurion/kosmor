package planetTool;


import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Representation of a planet
 * @author Aurion
 * 
 */
@XmlRootElement
@XmlType(propOrder = { "name", "XPos", "YPos", "owner", "house", "color" })
public class Planet {


	String name;
	double xPos;
	double yPos;
	String color;
	String owner = "";
	String house = "unknown";
	String ships;
	int combatPower;
	String csvPlanetType;
	String type;
	int id;

	public static ArrayList<String> player_id_list = HTTPClientForKosmor.player_id_list;
	public static ArrayList<String> player_name_list =HTTPClientForKosmor.player_name_list;
	public static ArrayList<String> house_id_list = HTTPClientForKosmor.house_id_list;
	public static ArrayList<String> house_name_list = HTTPClientForKosmor.house_name_list;

	public Planet(String name, double x, double y, String color, Boolean ships, String type, String csvPlanetType) {
		this.name = name;
		xPos = x;
		yPos = y;
		this.color = color;
		if(ships == true){this.ships = "ships";}
		else {this.ships = "";}
		this.type = type;
		this.csvPlanetType = csvPlanetType;
	}

	public Planet() {
	}


	public String toHTMLTable(String link) {
		String output;
		output = "<tr>"+"\n";
		output += "<td><input type='radio' name='p1' value='"+name+"' onclick=\"DoDistance();\"/>" + "\n" +
				"<input type='radio' name='p2' value='"+name+"' onclick=\"DoDistance();\"/>" + "\n" +
				"<input type='radio' name='p3' value='"+name+"' onclick=\"DoDistance();\"/>" + "\n";
		output += "<td id='"+name+"'>"+link+"\n"+
				"<td id='"+name+"~x'>"+(int) xPos+"\n"+
				"<td id='"+name+"~y'>"+(int) yPos+"\n";
		if (Planet.player_name_list.contains(owner)) {
			output += "<td>"
					+ "<a href=\""
					+ Planet.player_id_list.get(Planet.player_name_list
							.indexOf(owner)) + "\">" + owner + "</a>"
							+ "\n";
		} else {
			output += "<td>" + owner + "\n";
		}
		if (Planet.house_name_list.contains(house)){
			output += "<td>"+"<a href=\""+Planet.house_id_list.get(Planet.house_name_list.indexOf(house))+"\">"+house+"</a>"+"\n";
		} else {
			output += "<td>"+house+"\n";
		}
		output += "<td>"+"<font color=#"+color+">"+type+"</font>"+"\n";
		if(combatPower > 0) {
			output += "<td>"+combatPower+"\n";
		} else {
			output += "<td>"+""+"\n";
		}

		output += "<td>"+"\n";
		output += "<td><input type='radio' name='dist' value='"+name+"' onchange=\"DistTool2();\" />" + "\n";
		output += "<td id='"+name+"~d'>" + "\n";
		output += "</tr>";
		return output;
	}

	public String toHTMLString() {
		return name + "	" + xPos + "	" + yPos + "	" + "<font color=#" + owner
				+ ">" + color + "</font>" + "	" + type + " " + "<font color=#"
				+ color + ">" + owner + "</font>" + " " + ships;
	}

	@Override
	public String toString(){
		String csvShips = "ns";
		if (ships == "ships"){
			csvShips = "s";
		}
		return csvPlanetType + "," + name + "," + xPos + "," + yPos + ","
		+ owner + "," + house + "," + color + "," + "na" + "," + "na"
		+ "," + csvShips;

	}

	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "xPos")
	public double getXPos(){
		return xPos;
	}

	public void setXPos(double xPos) {
		this.xPos = xPos;
	}

	@XmlElement(name = "yPos")
	public double getYPos(){
		return yPos;
	}

	public void setYPos(double yPos){
		this.yPos = yPos;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getHouse(){
		return house;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwner(){
		return owner;
	}

	public void setHouse(String house) {
		this.house = house;
	}

	public String getShips() {
		return ships;
	}

	public void setCombatPower(int combatPower) {
		this.combatPower = combatPower;

	}

	@XmlTransient
	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

}