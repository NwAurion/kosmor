package output;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;



public class XMLParserWriter {
	static LinkedList<PlanetLister> planetLists = new LinkedList<>();

	public static void main(String[] args) {
		File file = new File("planets.xml");
		XMLParserWriter.unmarshall(file);
	}

	public static void serialize(PlanetLister planetLister) {

		try {
			JAXBContext context = JAXBContext.newInstance(PlanetLister.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(planetLister, System.out);

			String fileName = "planets - " + planetLister.getKosmorDate()
					+ ".xml";
			Writer w = new FileWriter(fileName);
			m.marshal(planetLister, w);
		} catch (PropertyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void unmarshall() {
		File path = new File(".");
		String[] files = path.list(); // Get all files
		for (String file : files) { // Go through all files
			if (file.endsWith(".xml")) {
				XMLParserWriter.planetLists.add(XMLParserWriter.unmarshall(new File(file)));
			}
		}
	}

	public void compare() {
		PlanetLister planetLister = new PlanetLister();
		for (int i = 1; i < XMLParserWriter.planetLists.size(); i++) {
			String kosmorDateNew = XMLParserWriter.planetLists.get(i)
					.getKosmorDate();
			String kosmorDateOld = XMLParserWriter.planetLists.get(i - 1)
					.getKosmorDate();
			String[] kosmorDateNewParts = kosmorDateNew.split("-");
			String[] kosmorDateOldParts = kosmorDateOld.split("-");
			int yearNew = Integer.parseInt(kosmorDateNewParts[0]);
			int dayNew = Integer.parseInt(kosmorDateNewParts[1]);
			int yearOld = Integer.parseInt(kosmorDateOldParts[0]);
			int dayOld = Integer.parseInt(kosmorDateOldParts[1]);
			if (yearNew > yearOld || (yearNew == yearOld && dayNew > dayOld)) {
				planetLister = XMLParserWriter.planetLists.get(0);
			} else {
				planetLister = XMLParserWriter.planetLists.get(1);
			}
			XMLParserWriter.serialize(planetLister);
		}
	}

	public static PlanetLister unmarshall(File file) {
		PlanetLister planetLister = null;
		try {
			JAXBContext context = JAXBContext.newInstance(PlanetLister.class);
			Unmarshaller u = context.createUnmarshaller();
			planetLister = (PlanetLister) u.unmarshal(file);
		} catch (PropertyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return planetLister;
	}
}
