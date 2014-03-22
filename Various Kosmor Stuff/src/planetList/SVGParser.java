
import java.io.File;
import java.util.Date;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SVGParser {

	/**
	 * Parses a given .svg for nodes
	 * @return <b>bothNodeLists</b> an Array with two NodeLists
	 */
	public static NodeList[] parseSVG(Date date) {
	
		/* As it has to return two things, it can not return them 
		 * directly but has to encapsulate it in another object.
		 */
		NodeList[] bothNodeLists = new NodeList[2];
		;
		try {
		
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			
			String SVGFileName = "./draw_map_svg.svg";
			File svgMap = new File(SVGFileName);
		
			System.out.println("Parsing the svg");	// Status message #1 Parsing	
			System.out.println("This should not longer than 30 seconds");	// Status message #2 Parsing
			
			Document doc = docBuilder.parse(SVGFileName);

			// "text" is the actual planet/warplanet
			NodeList listOfPlanetsAsXMLNode = doc.getElementsByTagName("text");
			
			// "line" is the line drawn by a warplanet. Used for calculation of warplanet coordinates.
			NodeList listOfLinesAsXMLNode = doc.getElementsByTagName("line");
			bothNodeLists[0] = listOfPlanetsAsXMLNode;
			bothNodeLists[1] = listOfLinesAsXMLNode;
		
			
			// normalize text representation
			doc.getDocumentElement().normalize();
	
			// Build the fileName the .svg should be renamed to, using the dateStringBuilder
			String newSVGFileName = MapTool.dateStringBuilder(MapTool.getKosmorDate(date), date);
			newSVGFileName = newSVGFileName.concat(" - Map - kosmor.com - .svg");
			
			// Making sure the directory does exist, if not, it is created
			File svgdir = new File("svg");
			if(!svgdir.exists() || !svgdir.isDirectory()){
				svgdir.mkdir();
			}
			
			svgMap.renameTo(new File(svgdir+"\\"+newSVGFileName));
			
			System.out.println("Done parsing"); // Status message #3 Parsing
	
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