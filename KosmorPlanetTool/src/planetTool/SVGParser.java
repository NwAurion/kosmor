package planetTool;

import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SVGParser {

	/**
	 * Parses a given .svg for nodes
	 * @return <b>bothNodeLists</b> an Array with two NodeLists
	 */
	public static NodeList[] parseSVG(String kosmorDate, Calendar date) {

		/* As it has to return two things, it can not return them
		 * directly but has to encapsulate it in another object.
		 */
		NodeList[] bothNodeLists = new NodeList[2];

		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			String SVGFileName = "draw_map_svg.svg";

			Document doc = docBuilder.parse(SVGFileName);

			// "text" is the actual planet/warplanet
			NodeList listOfPlanetsAsXMLNode = doc.getElementsByTagName("text");

			// "line" is the line drawn by a warplanet. Used for calculation of warplanet coordinates.
			NodeList listOfLinesAsXMLNode = doc.getElementsByTagName("line");
			bothNodeLists[0] = listOfPlanetsAsXMLNode;
			bothNodeLists[1] = listOfLinesAsXMLNode;

			// normalize text representation
			doc.getDocumentElement().normalize();

		} catch (SAXParseException err) {
			System.out.println("** Parsing error" + ", line "
					+ err.getLineNumber() + ", uri " + err.getSystemId());
			System.out.println(" " + err.getMessage());

		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();

		} catch (Throwable t) {
			t.printStackTrace();
		}



		return bothNodeLists;

	}
}