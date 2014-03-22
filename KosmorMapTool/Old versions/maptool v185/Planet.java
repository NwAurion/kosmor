package maptool;

/**
 * Representation of a planet or warplanet in Kosmor. * 
 * @author Aurion
 * 
 */
public class Planet {
	String name;
	int xPos;
	int yPos;
	String type;
	String color;
	boolean wp;
	String player;
	String house;
	String ships;
	int combatPower;

	public Planet(String name, int x, int y, String color, Boolean wp, Boolean ships) {
		this.name = name;
		this.xPos = x;
		this.yPos = y;
		this.color = color;
		if (wp == true){this.type = "Warplanet";}
		else if (wp == false){this.type = "Planet";}
		if(ships == true){this.ships = "ships";}
		else {this.ships = "";}
	}

	public String toString() {
		return name + "	" + xPos + "	" + yPos + "	" + color + "	" + type + " " + player + " " + ships;

	}

	public String toHTMLTable(String link) {
		String output;
		if (MapTool.fetchInfo){ output = "<tr>"+"\n"+
			"<td>"+link+"\n"+
			"<td>"+xPos+"\n"+
			"<td>"+yPos+"\n"+
			"<td>"+player+"\n"+
			"<td>"+house+"\n"+
			"<td>"+"<font color=#"+color+">"+type+"</font>"+"\n";
		if(combatPower > 0)output += "<td>"+combatPower+"\n";
		else output += "<td>"+""+"\n";
		output += "</tr>";
		}
		else { output = "<tr>"+"\n"+
			"<td>"+link+"</td>"+"\n"+
			"<td>"+xPos+"</td>"+"\n"+
			"<td>"+yPos+"</td>"+"\n"+
			"<td>"+"<font color=#"+color+">"+type+"</font>"+"</td>"+"\n"+
			"<td>"+ships+"</td>"+"\n"+
			"</tr>";
		}
		return output;
	}
	
	public String toHTMLString() {
		return name + "	" + xPos + "	" + yPos + "	"+"<font color=#"+player+">"+color+"</font>"+ "	" + type + " " +"<font color=#"+color+">"+player+"</font>"+ " " +ships;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
	
	public void setPlayer(String player) {
		this.player = player;
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


}