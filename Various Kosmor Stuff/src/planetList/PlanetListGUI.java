import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 * Looks up a planet from a list of all planets by typing in a name If a planet
 * name contains the char combination written, it is listed, together with all
 * other planets fulfilling the same condition
 * 
 * @author Aurion
 * 
 */
public class PlanetListGUI extends JFrame implements DocumentListener,
		ItemListener {
	static LinkedList<String> planetList = new LinkedList<String>();
	static LinkedList<String> foundPlanets;
	static JTextField searchField = new JTextField();
	static JTextArea planetListing = new JTextArea();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PlanetListGUI();
	}

	/**
	 * For easy manipulation of the planetList, to list every planet in a new
	 * line etc.
	 * 
	 * @param list
	 * @return
	 */
	public static String[] toString(LinkedList<String> list) {
		String planet = new String();
		String[] toString = new String[100];
		for (String s : list) {
			planet = planet.concat(s);
			planet = planet.trim();
			toString = planet.split(" ");
		}
		return toString;
	}

	PlanetListGUI() {
		setSize(new Dimension(500, 500));
		JPanel mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(500, 500));
		searchField.setPreferredSize(new Dimension(200, 25));
		planetListing.setPreferredSize(new Dimension(475, 400));
		Document searchFieldDocument = searchField.getDocument();
		searchFieldDocument.addDocumentListener(this);
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"smallplanetList.txt"));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				planetList.add(line);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		mainPanel.add(searchField);
		mainPanel.add(planetListing);
		add(mainPanel);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		setPlanetListingText();
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		setPlanetListingText();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		setPlanetListingText();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		setPlanetListingText();
	}

	/**
	 * Sets the text in the planetListing TextArea
	 */
	private void setPlanetListingText() {
		foundPlanets = new LinkedList<String>();
		for (String s : planetList) {
			if (s.toLowerCase().contains(searchField.getText().toLowerCase())) {
				foundPlanets.add(s);
			}
		}
		planetListing.setText(null);
		for (String s : foundPlanets) {
			planetListing.setText(planetListing.getText() + s);
			planetListing.setText(planetListing.getText().trim() + "\n");
		}
	}

}
