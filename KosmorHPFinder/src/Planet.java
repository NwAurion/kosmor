public class Planet {

	double x;
	double y;
	String name;

	public Planet(String name, double x, double y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
}
