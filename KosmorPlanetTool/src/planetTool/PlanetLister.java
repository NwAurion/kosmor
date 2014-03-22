package planetTool;

import java.util.HashSet;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Planets")
public class PlanetLister {

	private HashSet<Planet> allPlanetsList = new HashSet<Planet>();


	@XmlElementWrapper
	@XmlElementRefs({ @XmlElementRef(type = Planet.class) })
	public HashSet<Planet> getPlanets() {
		return allPlanetsList;
	}

	public void setPlanets(HashSet<Planet> allPlanetsList) {
		this.allPlanetsList = allPlanetsList;
	}


}

