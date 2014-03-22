package planetTool;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import output.PlanetLister;
import output.XMLParserWriter;
import processing.Planet;

public class PlanetToolSimple extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1226425114537287641L;
	private final boolean DEBUG = false;

	public PlanetToolSimple() {
		super(new GridLayout(1,0));

		String[] columnNames = { "Name",
				"xPos",
				"yPos",
				"owner",
				"house",
		"color" };

		PlanetLister planetLister = XMLParserWriter.unmarshall(new File(
				"planets.xml"));

		LinkedList<Planet> allPlanets = new LinkedList<Planet>();
		allPlanets.addAll(planetLister.getPlanetList());
		allPlanets.addAll(planetLister.getNeutralPlanetList());

		int size = allPlanets.size();
		Object[][] data = new Object[size][6];

		for (int i = 0; i < size; i++) {
			data[i][0] = allPlanets.get(i).getName();
			data[i][1] = allPlanets.get(i).getXPos();
			data[i][2] = allPlanets.get(i).getYPos();
			data[i][3] = allPlanets.get(i).getOwner();
			data[i][4] = allPlanets.get(i).getHouse();
			data[i][5] = allPlanets.get(i).getColor();
		}

		final ETable table = new ETable(data, columnNames);
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);

		if (DEBUG) {
			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					printDebugData(table);
				}
			});
		}

		add(new JScrollPane(table));
	}

	private void printDebugData(JTable table) {
		int numRows = table.getRowCount();
		int numCols = table.getColumnCount();
		javax.swing.table.TableModel model = table.getModel();

		System.out.println("Value of data: ");
		for (int i=0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j=0; j < numCols; j++) {
				System.out.print("  " + model.getValueAt(i, j));
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("Kosmor Planet Tool");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		PlanetToolSimple newContentPane = new PlanetToolSimple();
		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setPreferredSize(new Dimension(500, 800));
		frame.setContentPane(newContentPane);

		//Display the window.
		frame.pack();
		frame.setVisible(true);

	}

	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				PlanetToolSimple.createAndShowGUI();
			}
		});
	}

}
