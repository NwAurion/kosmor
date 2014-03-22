/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package planetTool;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TimeZone;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.http.HttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class PlanetTool extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5941208233926058614L;
	private Object[][] data;
	private ETable table;
	private JTextField filterText;
	private TableRowSorter<MyTableModel> sorter;
	private static PlanetLister planetLister = new PlanetLister();
	private final static long MILLISECONDS_IN_A_DAY = 86400000;
	private static String kosmorDate;

	private static LinkedList<Planet> warplanetList;
	private static LinkedList<Planet> planetList;
	private static LinkedList<Planet> neutralPlanetList;
	private HashSet<Planet> allPlanets = new HashSet<Planet>();

	File planetsFile = new File("planets.xml");
	DefaultHttpClient httpclient;
	File svgFile = new File("draw_map_svg.svg");

	public PlanetTool() {
		super();

		String userName = JOptionPane.showInputDialog(null,
				"Enter your user name : ", "Planet Tool", 1);
		String password = JOptionPane.showInputDialog(null,
				"Enter your password : ", "Planet Tool", 1);

		init();

		try {
			Object[] httpResponse = HTTPClientForKosmor.doConnect(userName,
					password);
			httpclient = (DefaultHttpClient) httpResponse[0];
			HTTPClientForKosmor.downloadMap(httpclient,
					(HttpEntity) httpResponse[1]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (svgFile.exists()) {
			updateInfo(userName, password, httpclient);
			PlanetLister planetLister = new PlanetLister();
			planetLister.setPlanets(allPlanets);
			XMLParserWriter.serialize(planetLister);
			svgFile.delete();
		}
	}

	public void updateInfo(String userName, String password,
			DefaultHttpClient httpClient) {
		LinkedList<Planet> newAllPlanets = InfoFetcher.fetchInfo(userName,
				password, httpClient);

		for (Planet planet : newAllPlanets) {
			allPlanets.add(planet);
		}
		fillTable();
	}


	private void init() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		//Create a table with a sorter.
		MyTableModel model = new MyTableModel();
		sorter = new TableRowSorter<MyTableModel>(model);
		table = new ETable(model);
		table.setRowSorter(sorter);
		table.setPreferredScrollableViewportSize(new Dimension(500, 800));
		table.setFillsViewportHeight(true);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);

		//Add the scroll pane to this panel.
		add(scrollPane);

		//Create a separate form for filterText and statusText
		JPanel form = new JPanel(new SpringLayout());
		JLabel l1 = new JLabel("Filter Text:", SwingConstants.TRAILING);
		form.add(l1);
		filterText = new JTextField();
		//Whenever filterText changes, invoke newFilter.
		filterText.getDocument().addDocumentListener(
				new DocumentListener() {
					@Override
					public void changedUpdate(DocumentEvent e) {
						newFilter();
					}
					@Override
					public void insertUpdate(DocumentEvent e) {
						newFilter();
					}
					@Override
					public void removeUpdate(DocumentEvent e) {
						newFilter();
					}
				});
		l1.setLabelFor(filterText);
		form.add(filterText);
		SpringUtilities.makeCompactGrid(form, 1, 2, 6, 6, 6, 6);
		add(form);
	}

	/**
	 * Update the row filter regular expression from the expression in
	 * the text box.
	 */
	private void newFilter() {
		RowFilter<MyTableModel, Object> rf = null;
		//If current expression doesn't parse, don't update.
		try {
			rf = RowFilter.regexFilter("(?i)" + filterText.getText(), 0);
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}
		sorter.setRowFilter(rf);
	}

	public void initTable() {

		allPlanets = XMLParserWriter.unmarshall(planetsFile).getPlanets();


		fillTable();
		/*
		 * for (int i = 0; i < size; i++) { data[i][0] = allPlanets..getName();
		 * data[i][1] = allPlanets.get(i).getXPos(); data[i][2] =
		 * allPlanets.get(i).getYPos(); data[i][3] =
		 * allPlanets.get(i).getOwner(); data[i][4] =
		 * allPlanets.get(i).getHouse(); data[i][5] =
		 * allPlanets.get(i).getColor();
		 * 
		 * }
		 */
	}

	private void fillTable() {
		data = new Object[allPlanets.size()][6];
		Iterator<Planet> it = allPlanets.iterator();
		int i = 0;
		while (it.hasNext()) {
			Planet planet = it.next();
			data[i][0] = planet.getName();
			data[i][1] = planet.getXPos();
			data[i][2] = planet.getYPos();
			data[i][3] = planet.getOwner();
			data[i][4] = planet.getHouse();
			data[i][5] = planet.getColor();
			i++;
		}
	}

	class MyTableModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4200531628774200506L;

		String[] columnNames = { "Name", "xPos", "yPos", "owner", "house",
		"color" };

		public MyTableModel() {
			if (planetsFile.exists()) {
				initTable();
			}
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		@Override
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}


	}

	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("Kosmor Planet Tool");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		PlanetTool newContentPane = new PlanetTool();
		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void setKosmorDate(GregorianCalendar currentDate) {

		GregorianCalendar startCalendar = new GregorianCalendar();
		startCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		startCalendar.set(Calendar.HOUR_OF_DAY, 8);
		startCalendar.set(Calendar.DAY_OF_MONTH, 2);
		startCalendar.set(Calendar.MONTH, 1);
		startCalendar.set(Calendar.YEAR, 2004);
		Date startDate = startCalendar.getTime();

		int kosmorDay = 7;
		int kosmorYear = 3500;

		while (startDate.getTime() < currentDate.getTimeInMillis()
				- PlanetTool.MILLISECONDS_IN_A_DAY) {
			startDate.setTime(startDate.getTime()
					+ PlanetTool.MILLISECONDS_IN_A_DAY);
			startCalendar.setTime(startDate);
			if (currentDate.isLeapYear(currentDate.get(Calendar.YEAR) - 1)) {
				if (kosmorDay > 366) {
					kosmorDay = 1;
					kosmorYear++;
				} else {
					kosmorDay++;
				}
			} else if (kosmorDay > 365) {
				kosmorDay = 1;
				kosmorYear++;
			} else {
				kosmorDay++;
			}
		}

		String formattedKosmorDay = String.format("%03d", kosmorDay);
		String kosmorDate = kosmorYear + "_" + formattedKosmorDay;
		PlanetTool.kosmorDate = kosmorDate;

	}

	public static void main(String[] args) {
		PlanetTool.createAndShowGUI();

	}

	public static PlanetLister getPlanetLister() {
		return PlanetTool.planetLister;
	}

	public static LinkedList<Planet> getPlanetList() {
		return PlanetTool.planetList;
	}

	public static void setPlanetList(LinkedList<Planet> planetList) {
		PlanetTool.planetList = planetList;
	}

	public static LinkedList<Planet> getWarplanetList() {
		return PlanetTool.warplanetList;
	}

	public static void setWarplanetList(LinkedList<Planet> warplanetList) {
		PlanetTool.warplanetList = warplanetList;
	}

	public static LinkedList<Planet> getNeutralPlanetList() {
		return PlanetTool.neutralPlanetList;
	}

	public static void setNeutralPlanetList(LinkedList<Planet> neutralPlanetList) {
		PlanetTool.neutralPlanetList = neutralPlanetList;

	}

	public static String getKosmorDate() {
		return PlanetTool.kosmorDate;
	}
}