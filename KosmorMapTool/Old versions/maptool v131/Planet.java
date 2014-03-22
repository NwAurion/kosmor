package kosmor.maptool;

/**
 * Representation of a planet (and warplanet, so far) in Kosmor. Does not save
 * if it has ships on it yet.
 * 
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
	String owner;

	public Planet(String name, int x, int y, String color, Boolean wp) {
		this.name = name;
		this.xPos = x;
		this.yPos = y;
		this.color = color;
		if (wp == true){this.type = "Warplanet";}
		else if (wp == false){this.type = "Planet";}
	}



	public Planet() {
	}



	public String toString() {
		return name + "	" + xPos + "	" + yPos + "	" + color + "	" + type + " " + owner;

	}
	
	public String toHTMLString() {
		return name + "	" + xPos + "	" + yPos + "	"+"<font color=#"+color+">"+color+"</font>"+ "	" + type + " " + owner;

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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}


}