package output;

import java.util.Date;
import java.util.LinkedList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import processing.Planet;

@XmlRootElement(name = "Planets")
public class PlanetLister {


	private LinkedList<Planet> planetList = new LinkedList<Planet>();
	private LinkedList<Planet> neutralPlanetList = new LinkedList<Planet>();

	@XmlAttribute
	String kosmorDate;
	@XmlAttribute
	Date date;

	public PlanetLister() {
	};

	public PlanetLister(Date date, String kosmorDate) {
		this.date = date;
		this.kosmorDate = kosmorDate;
	}

	@XmlElementWrapper
	@XmlElementRefs({ @XmlElementRef(type = Planet.class) })
	public LinkedList<Planet> getPlanetList() {
		return planetList;
	}

	public void setPlanetList(LinkedList<Planet> planetList) {
		this.planetList = planetList;
	}

	@XmlElementWrapper
	@XmlElementRefs({ @XmlElementRef(type = Planet.class) })
	public LinkedList<Planet> getNeutralPlanetList() {
		return neutralPlanetList;
	}

	public void setNeutralPlanetList(LinkedList<Planet> neutralPlanetList) {
		this.neutralPlanetList = neutralPlanetList;
	}

	public Date getDate() {
		return date;
	}

	public String getKosmorDate() {
		return kosmorDate;
	}
}

