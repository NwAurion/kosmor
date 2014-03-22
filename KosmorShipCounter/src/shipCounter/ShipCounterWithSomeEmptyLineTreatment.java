package shipCounter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

public class ShipCounterWithSomeEmptyLineTreatment extends JPanel {

	/**
	 * 
	 */
	private final static long serialVersionUID = 1L;
	final static int[] COMBAT_POWER = new int[] { 9, 13, 19, 41, 65, 116, 188,
			310, 528, 916, 3306 };

	int[] totalCombatPower = new int[12];
	String[] text = new String[11];
	int[] count = new int[11];
	int[] total = new int[11];

	static String owner;

	JTextArea postShipListHere = new JTextArea();
	JTextArea postResultHere = new JTextArea();
	String[] shipString;

	public String outputBuilder(String owner) {
		text[0] = " probes (H1) with ";
		text[1] = " orbital) gliders (H2) with ";
		text[2] = " space fighters (H3) with ";
		text[3] = " interceptors (H4) with ";
		text[4] = " frigates (H5) with ";
		text[5] = " heavy frigates (H6) with ";
		text[6] = " cruisers (H7) with ";
		text[7] = " heavy cruisers (H8) with ";
		text[8] = " destroyers (H9) with ";
		text[9] = " goliaths (H10) with ";
		text[10] = " leviathan (H11) with ";
		for (int i = 0; i < 11; i++) {
			totalCombatPower[i] = count[i] * COMBAT_POWER[i];
			totalCombatPower[11] = totalCombatPower[11] + totalCombatPower[i];
		}
		String outputString = "";
		for (int i = 0; i < 11; i++) {
			if (count[i] > 0) {
				outputString = outputString + count[i] + text[i]+totalCombatPower[i]+" cp \n";
			}
		}
		outputString = owner + " has " + "\n" + outputString + "Total: "
				+ totalCombatPower[11] + " cp " + "\n\n";
		return outputString;
	}

	public ShipCounterWithSomeEmptyLineTreatment() {
		setLayout(new BorderLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.gridwidth = GridBagConstraints.REMAINDER; // last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;

		postShipListHere.setLineWrap(true);

		JScrollPane areaScrollPane = new JScrollPane(postShipListHere);
		areaScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		areaScrollPane.setPreferredSize(new Dimension(500, 500));

		postResultHere.setLineWrap(true);

		JScrollPane areaScrollPane2 = new JScrollPane(postResultHere);
		areaScrollPane2
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		areaScrollPane2.setPreferredSize(new Dimension(500, 500));

		JPanel leftPane = new JPanel();
		leftPane.add(areaScrollPane);

		JPanel rightPane = new JPanel();
		rightPane.add(areaScrollPane2);

		JButton buttonStart = new JButton();
		buttonStart.setPreferredSize(new Dimension(50, 50));
		buttonStart.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				postResultHere.setText("");
				for (int i = 0; i < 11; i++) {
					count[i] = 0;
					total[i] = 0;
					totalCombatPower[11] = 0;
				}
				int wasEmpty = 0;
				String text = postShipListHere.getText();
				int totalLines = postShipListHere.getLineCount();
				String[][] shipString = new String[totalLines][3];
				int end = 0;
				for (int i = 0; i < totalLines; i++) {
					try {
						int start = postShipListHere.getLineStartOffset(i);
						end = postShipListHere.getLineEndOffset(i);
						String line = text.substring(start, end);
						line = line.trim();
						if (line.isEmpty()) {
							text = text.substring(0, start)+text.substring(end);
							postShipListHere.setText(text);	
							totalLines = postShipListHere.getLineCount();
							i--;
							wasEmpty++;
							//if(text.substring(end, text.length()).length() ==0) {totalLines--; break;}
							if(!text.substring(end, text.length()).matches("[.]+")){System.out.println("hallo"); wasEmpty++;  break;}
							continue;
						}
						
						System.out.println(text.substring(end, text.length()).length());
						System.out.println("line is: "+line+" "+line.isEmpty());
						byte[] bytes = line.getBytes();
						System.out.println("From bytes "+Arrays.toString(bytes));
						System.out.println("text is"+text+" textende");
						shipString[i] = line.split(" ");
						
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				ArrayList<String> owners = new ArrayList<String>();
				for (int i = 0; i < totalLines-wasEmpty; i++) {
					if (shipString[i].length == 7) {
						String[][] newShipString = new String[1][6];
						newShipString[0][0] = shipString[i][0]+shipString[i][1];
						shipString[i][0] = newShipString[0][0];
						for (int j = 1; j < 5; j++) {
							shipString[i][j] = shipString[i][j + 1];
						}
					}
					System.out.println(Arrays.toString(shipString[i]));
					shipString[i][3] = shipString[i][3].substring(0,shipString[i][3].length() - 1);

					if (!owners.contains(shipString[i][3])) {
						owners.add(shipString[i][3]);
					}
				}

				for (int ownerIterator = 0; ownerIterator < owners.size(); ownerIterator++) {
					for (int shipIterator = 0; shipIterator < shipString.length-wasEmpty; shipIterator++) {					
						if (shipString[shipIterator][3].equals(owners.get(ownerIterator))) {
							if (shipString[shipIterator][0].contains("(H1)")) {
								count[0]++;
							} else if (shipString[shipIterator][0].contains("(H2)")) {
								count[1]++;
							} else if (shipString[shipIterator][0].contains("(H3)")) {
								count[2]++;
							} else if (shipString[shipIterator][0].contains("(H4)")) {
								count[3]++;
							} else if (shipString[shipIterator][0].contains("(H5)")) {
								count[4]++;
							} else if (shipString[shipIterator][0].contains("(H6)")) {
								count[5]++;
							} else if (shipString[shipIterator][0].contains("(H7)")) {
								count[6]++;
							} else if (shipString[shipIterator][0].contains("(H8)")) {
								count[7]++;
							} else if (shipString[shipIterator][0].contains("(H9)")) {
								count[8]++;
							} else if (shipString[shipIterator][0].contains("(H10)")) {
								count[9]++;
							} else if (shipString[shipIterator][0].contains("(H11)")) {
								count[10]++;
							}
						}
					}
					postResultHere.setText(postResultHere.getText()+outputBuilder(owners.get(ownerIterator)));
					for (int j = 0; j < 11; j++) {
						total[j] = total[j] + count[j];
						count[j] = 0;
						totalCombatPower[11] = 0;
					}

				}
				for (int i = 0; i < 11; i++) {
					count[i] = total[i];
				}
				postResultHere.setText(postResultHere.getText()
						+ outputBuilder("The whole house"));
			}
		});

		add(buttonStart, BorderLayout.CENTER);
		add(leftPane, BorderLayout.LINE_START);
		add(rightPane, BorderLayout.LINE_END);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Shipcounter");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new ShipCounterWithSomeEmptyLineTreatment());
		frame.pack();
		frame.setVisible(true);
	}

}
