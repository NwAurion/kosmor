package shipCounter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ShipCounter extends JPanel {

	/**
	 * 
	 */
	private final static long serialVersionUID = 1L;
	// Constants for the combat power of each ship type
	final static int[] COMBAT_POWER = new int[] { 9, 13, 19, 41, 65, 116, 188, 310, 528, 916, 3306 };
	private boolean fromBattleReport =	false;

	int[] totalCombatPower = new int[12];
	String[] text = new String[11];
	int[] count = new int[12];
	int[] total = new int[11];

	static String owner;
	static String house;

	JTextArea postShipListHere = new JTextArea();
	JTextPane postResultHere = new JTextPane();
	String[] shipString;
	JCheckBox fromBattleReportCheckBox;

	public static String ensureLength(String s, int length) {
		while(s.length() <length){
			s = " "+s;
		}
		System.out.println(s);
		return s;
	    
	}
	
	public String outputBuilder(String owner) {
		/*text[0] = "	probes (H1) with	";
		text[1] = "	orbital gliders (H2) with	";
		text[2] = "	space fighters (H3) with	";
		text[3] = "	interceptors (H4) with	";
		text[4] = "	frigates (H5) with	";
		text[5] = "	heavy frigates (H6) with	";
		text[6] = "	cruisers (H7) with	";
		text[7] = "	heavy cruisers (H8) with	";
		text[8] = "	destroyers (H9) with	";
		text[9] = "	goliaths (H10) with	";
		text[10] = "	leviathan (H11) with	";*/
		text[0] = "	H1	";
		text[1] = "	H2	";
		text[2] = "	H3	";
		text[3] = "	H4	";
		text[4] = "	H5	";
		text[5] = "	H6	";
		text[6] = "	H7	";
		text[7] = "	H8	";
		text[8] = "	H9	";
		text[9] = "	H10	";
		text[10] = "	H11 ";
		for (int i = 0; i < 11; i++) {
			totalCombatPower[i] = count[i] * COMBAT_POWER[i];
			totalCombatPower[11] = totalCombatPower[11] + totalCombatPower[i];
			count[11] = count[11]+count[i];
		}
		
		String outputString = "";
		for (int i = 0; i < 11; i++) {
			if (count[i] > 0) {
				outputString = outputString + ensureLength(String.valueOf(count[i]),5) + text[i]+ensureLength(String.valueOf(totalCombatPower[i]), 5)+"\n";
			}
		}
		outputString = owner + " has " + "\n" + outputString + ensureLength(String.valueOf(count[11]),5)+"	Ships with"+ ensureLength(String.valueOf(totalCombatPower[11]), 10) + " cp " + "\n\n";
		return outputString;
	}

	public ShipCounter() {
		setLayout(new BorderLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.gridwidth = GridBagConstraints.REMAINDER; // last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;

		postShipListHere.setLineWrap(true);
		JScrollPane areaScrollPane = new JScrollPane(postShipListHere);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		areaScrollPane.setPreferredSize(new Dimension(500, 500));
	//	postResultHere.setLineWrap(true);

		JScrollPane areaScrollPane2 = new JScrollPane(postResultHere);
		areaScrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		areaScrollPane2.setPreferredSize(new Dimension(500, 500));
	//	postResultHere.setTabSize(6);
		JPanel leftPane = new JPanel();
		leftPane.add(areaScrollPane);

		JPanel rightPane = new JPanel();
		rightPane.add(areaScrollPane2);
	
		SimpleAttributeSet as = new SimpleAttributeSet();
	    StyleConstants.setAlignment( as, StyleConstants.ALIGN_JUSTIFIED );
	    StyledDocument doc = postResultHere.getStyledDocument();
	    Style style = doc.addStyle( "ALIGN_RIGHT", null );
	    style.addAttributes( as );
	    doc.setLogicalStyle( 0, style );
		
	    fromBattleReportCheckBox = new JCheckBox();	
		fromBattleReportCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED){
					fromBattleReport = true;
				}
				else fromBattleReport = false;
				
			}
		});
		JButton buttonStart = new JButton();
		buttonStart.setPreferredSize(new Dimension(50, 50));
		buttonStart.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				// Emptying the result in the beginning, allowing for a fresh start
				postResultHere.setText("");
				
				/* Setting all counters to zero in the beginning, to allow for a fresh start, 
				   without previous calculations influencing the current one */
				for (int i = 0; i < 11; i++) {
					count[i] = 0;
					total[i] = 0;
					totalCombatPower[11] = 0;
				}
				
				int wasNoShip = 0; // Counts the empty lines
				String text = postShipListHere.getText(); // Getting the posted ship list
				int totalLines = postShipListHere.getLineCount(); // Getting the number of lines
				String[][] shipString = new String[totalLines][3];
				wasNoShip = gettingShipInfo(wasNoShip, text, totalLines, shipString);

				ArrayList<String> owners = new ArrayList<String>();
				addShipsToOwners(wasNoShip, totalLines, shipString, owners);
				
				int shipType;
				if (fromBattleReport == false) shipType = 0;
				else shipType = 1;
			
				
				// Counting the ships for the respective owners and in total
				countShipsAndCombatPower(wasNoShip, totalLines, shipString,	owners, shipType);
				// Transferring the totals back to count in preparation of the final output
				for (int i = 0; i < 11; i++) {
					count[i] = total[i];
				}
				// Final output for the whole house. Replace string with actual house name later
				postResultHere.setText(postResultHere.getText()+outputBuilder("The house "+house));
			}

			/**
			 * Adds the ships to their respective owners
			 * @param wasNoShipCounter Counts lines not containing ships
			 * @param totalLines Total number of lines in the text
			 * @param shipInfo All info a ship line contains
			 * @param owners The owners of the ships
			 */
			private void addShipsToOwners(int wasNoShipCounter, int totalLines, String[][] shipInfo, ArrayList<String> owners) {
				for (int i = 0; i < totalLines-wasNoShipCounter; i++) {
					// Some ship type, like "orbital glider" consist of two words. Here, they get put together into one field
					if (shipInfo[i].length == 7) {
						String[][] newShipString = new String[1][6];
						newShipString[0][0] = shipInfo[i][0]+shipInfo[i][1];
						shipInfo[i][0] = newShipString[0][0];
						
						// Moving the other info one field forward, to match ship types with a single word, like "goliath"
						for (int j = 1; j < 5; j++) {
							shipInfo[i][j] = shipInfo[i][j + 1];
						}
					}
					
					// Removing the trailing comma
					if(!fromBattleReport)
					shipInfo[i][3] = shipInfo[i][3].substring(0,shipInfo[i][3].length() - 1);
					
					// Adding the ships to their respective owners.
					if (!owners.contains(shipInfo[i][3])) {
						owners.add(shipInfo[i][3]);
					}
				}
			}

			/**
			 * Counts the ships for their respective owner, in total, and the combat power
			 * @param wasNoShipCounter The number of lines not containing ships
			 * @param totalLines The total number of lines
			 * @param shipInfo Saves all info a line with a ship contains
			 * @param owners The owners of the ships - the player
			 * @param shipType The type of the ship, e.g H1, H2, H10..
			 */
			private void countShipsAndCombatPower(int wasNoShipCounter, int totalLines, String[][] shipInfo, ArrayList<String> owners, int shipType) {
				for (int ownerIterator = 0; ownerIterator < owners.size(); ownerIterator++) {
					for (int shipIterator = 0; shipIterator < totalLines-wasNoShipCounter; shipIterator++) {
						if (shipInfo[shipIterator][3].equals(owners.get(ownerIterator))) {
							if (shipInfo[shipIterator][shipType].contains("(H1)")) {
								count[0]++;
							} else if (shipInfo[shipIterator][shipType].matches(".*(H2).*")) {
								count[1]++;
							} else if (shipInfo[shipIterator][shipType].matches(".*(H3).*"))  {
								count[2]++;
							} else if (shipInfo[shipIterator][shipType].matches(".*(H4).*"))  {
								count[3]++;
							} else if (shipInfo[shipIterator][shipType].matches(".*(H5).*"))  {
								count[4]++;
							} else if (shipInfo[shipIterator][shipType].matches(".*(H6).*"))  {
								count[5]++;
							} else if (shipInfo[shipIterator][shipType].matches(".*(H7).*"))  {
								count[6]++;
							} else if (shipInfo[shipIterator][shipType].matches(".*(H8).*"))  {
								count[7]++;
							} else if (shipInfo[shipIterator][shipType].matches(".*(H9).*"))  {
								count[8]++;
							} else if (shipInfo[shipIterator][shipType].matches(".*(H10).*"))  {
								count[9]++;
							} else if (shipInfo[shipIterator][shipType].matches(".*(H11).*"))  {
								count[10]++;
							}
						}
					}
					// Creating the output and setting the text on the right side to it
					postResultHere.setText(postResultHere.getText()+outputBuilder(owners.get(ownerIterator)));
					
					// Adding up the totals and resetting the other counters
					for (int j = 0; j < 11; j++) {
						total[j] = total[j] + count[j];
						count[j] = 0;
						totalCombatPower[11] = 0;
						count[11] = 0;
					}

				}
			}

			/**
			 * Parses the text for ships. "Skips" lines not containing ships
			 * @param wasNoShipCount the counter for lines not containing ships
			 * @param text The text in the postShipListHere field
			 * @param totalLines The total number of lines in the text
			 * @param shipInfo All info a single line with ship contains
			 * @return wasNoShipCount new amount of lines containing no ships
			 */
			private int gettingShipInfo(int wasNoShipCount, String text, int totalLines, String[][] shipInfo) {
				for (int i = 0; i < totalLines; i++) { // Parsing every line
					try {
						int start = postShipListHere.getLineStartOffset(i); 
						int end = postShipListHere.getLineEndOffset(i);
						
						// Getting a single line  form the text
						String line = text.substring(start, end); 
						line = line.trim();

						// Counting the lines that do not contain ships
						if (!line.matches(".*(H[0-9]).*")) {
							wasNoShipCount++; // Increase empty line counter
							continue; // Next loop
						}

						// Splitting the line to get all the info
						shipInfo[i - wasNoShipCount] = line.split(" ");
						if (!fromBattleReport) {
							house = shipInfo[i - wasNoShipCount][5];
						}
						else house = "";
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return wasNoShipCount;
			}
		});
		
		JPanel southPanel = new JPanel();
		southPanel.setPreferredSize(new Dimension(100,25));
		add(southPanel,BorderLayout.SOUTH);
		southPanel.add(fromBattleReportCheckBox);
		add(buttonStart, BorderLayout.CENTER);
		add(leftPane, BorderLayout.LINE_START);
		add(rightPane, BorderLayout.LINE_END);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Shipcounter");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new ShipCounter());
		frame.pack();
		frame.setVisible(true);
	}

}
