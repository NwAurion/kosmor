package distanceAndTravelTime;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class DistanceAndTravelTimeCalculator extends JFrame implements
DocumentListener, ItemListener {

	static JCheckBox jumpCheckBox = new JCheckBox();
	static JCheckBox independenceCheckBox = new JCheckBox();
	static JTextField distanceField = new JTextField();
	static JTextField travelTimeField = new JTextField();
	static JTextField inAttackOnDay = new JTextField();
	boolean doJump = false;
	boolean isIndependent = false;

	JTextField xCoordFirstObject;
	JTextField yCoordFirstObject;
	JTextField xCoordSecondObject;
	JTextField yCoordSecondObject;
	List<Double> values = new ArrayList<Double>();

	Document x1Document;
	Document y1Document;
	Document x2Document;
	Document y2Document;

	double xCoordFirst;
	double yCoordFirst;
	double xCoordSecond;
	double yCoordSecond;

	private static final long serialVersionUID = 2604335183116640378L;

	public static void main(String[] args) {
		new DistanceAndTravelTimeCalculator();

	}

	private static double calculateDistance(Double[] values) {

		double a = (values[0] - values[2]);
		double b = (values[1] - values[3]);

		// Pythagorean theorem, a² + b² = c² ..
		double c = Math.pow(a, 2) + Math.pow(b, 2);

		double distance = (double) Math.round(Math.sqrt(c) * 100) / 100;
		DistanceAndTravelTimeCalculator.distanceField.setText(String
				.valueOf(distance));

		return distance;

	}

	private static double calculateTravelTime(double distance,	boolean isIndependent, boolean doJump) {
		double travelTime = 0;
		int jump = 0;

		// String travelTimeOutput =
		// "With jump on the day before in attack range at day ";
		if (doJump) {
			if (!isIndependent) {
				{
					while (distance > 0) {
						if (distance > 120 && jump == 0) {
							distance = distance - 150.0;
							jump = 4;
							travelTime++;
						} else if (distance <= 120 || jump != 0) {
							distance = distance - 60.0;
							jump--;
							travelTime++;
						}
						distance = (double) Math.round(distance * 1000) / 1000;
					}
					DistanceAndTravelTimeCalculator.travelTimeField
					.setText(String.valueOf(travelTime));
					return travelTime;
				}
			} else {
				while (distance > 0) {
					/*
					 * if (distance >= 540 && distance <= 660) {
					 * System.out.println(travelTimeOutput+ (int) (travelTime +
					 * 1)+ ", Remaining distance: " + distance);
					 * DistanceAndTravelTimeCalculator
					 * .inAttackOnDay.setText("Days: " + (int) (travelTime + 1)+
					 * " Distance: "+ (Math.round(distance * 100) / 100.0d)); }
					 */
					/*
					 * if (distance >= 240 && distance <= 360) {
					 * System.out.println(travelTimeOutput+ (int) (travelTime +
					 * 1)+ ", Remaining distance: " + distance);
					 * DistanceAndTravelTimeCalculator
					 * .inAttackOnDay.setText("Days: " + (int) (travelTime + 1)+
					 * " Distance: " +(Math.round(distance * 100) / 100.0d)); }
					 */
					/*
					 * if (distance >= 90 && distance <= 210) {
					 * System.out.println(travelTimeOutput+ (int) (travelTime +
					 * 1) + ", Remaining distance: " + distance);
					 * DistanceAndTravelTimeCalculator
					 * .inAttackOnDay.setText("Days: " + (int) (travelTime + 1)
					 * + " Distance: "+ (Math.round(distance * 100) / 100.0d));
					 * }
					 */
					if (distance >= 480 && jump == 0) {
						distance = distance - 600.0;
						jump = 4;
						travelTime++;
					} else if (distance >= 240 && jump == 0) {
						distance = distance - 300.0;
						jump = 4;
						travelTime++;
					} else if (distance > 120 && jump == 0) {
						distance = distance - 150.0;
						jump = 4;
						travelTime++;
					} else if (distance <= 120 || jump != 0) {
						distance = distance - 60.0;
						jump--;
						travelTime++;
					}
					distance = (double) Math.round(distance * 1000) / 1000;
				}
			}

		} else if (!doJump) {
			while (distance > 0) {
				distance = distance - 60;
				travelTime++;
			}

		}

		DistanceAndTravelTimeCalculator.travelTimeField.setText(String.valueOf(travelTime));
		return jump;
	}

	DistanceAndTravelTimeCalculator() {

		this.setSize(new Dimension(180, 250));
		setResizable(false);
		JPanel panel = new JPanel();

		// Fields for the first object
		JTextField xCoordFirstObject;
		JTextField yCoordFirstObject;
		JTextField xCoordSecondObject;
		JTextField yCoordSecondObject;

		xCoordFirstObject = new JTextField();
		xCoordFirstObject.setPreferredSize(new Dimension(75, 25));
		xCoordFirstObject.setToolTipText("XPos of the first object");
		xCoordFirstObject.getDocument().addDocumentListener(this);
		x1Document = xCoordFirstObject.getDocument();
		x1Document.addDocumentListener(this);

		yCoordFirstObject = new JTextField();
		yCoordFirstObject.setPreferredSize(new Dimension(75, 25));
		yCoordFirstObject.setToolTipText("YPos of the first object");
		yCoordFirstObject.getDocument().addDocumentListener(this);
		y1Document = yCoordFirstObject.getDocument();
		y1Document.addDocumentListener(this);

		// Fields for the second object
		xCoordSecondObject = new JTextField();
		xCoordSecondObject.setPreferredSize(new Dimension(75, 25));
		xCoordSecondObject.setToolTipText("XPos of the second object");
		xCoordSecondObject.getDocument().addDocumentListener(this);
		x2Document = xCoordSecondObject.getDocument();
		x2Document.addDocumentListener(this);

		yCoordSecondObject = new JTextField();
		yCoordSecondObject.setPreferredSize(new Dimension(75, 25));
		yCoordSecondObject.setToolTipText("YPos of the second object");
		y2Document = yCoordSecondObject.getDocument();
		y2Document.addDocumentListener(this);

		DistanceAndTravelTimeCalculator.distanceField
		.setPreferredSize(new Dimension(155, 25));
		DistanceAndTravelTimeCalculator.travelTimeField.setPreferredSize(new Dimension(85, 25));

		DistanceAndTravelTimeCalculator.jumpCheckBox.setText("Toggle jump");
		DistanceAndTravelTimeCalculator.jumpCheckBox.addItemListener(this);

		DistanceAndTravelTimeCalculator.independenceCheckBox.setText("Toggle independence");
		DistanceAndTravelTimeCalculator.independenceCheckBox.addItemListener(this);

		//DistanceAndTravelTimeCalculator.inAttackOnDay.setPreferredSize(new Dimension(200, 25));

		// Adding everything to the panel
		panel.add(xCoordFirstObject);
		panel.add(yCoordFirstObject);
		panel.add(xCoordSecondObject);
		panel.add(yCoordSecondObject);
		panel.add(DistanceAndTravelTimeCalculator.distanceField);
		panel.add(DistanceAndTravelTimeCalculator.travelTimeField);
		panel.add(DistanceAndTravelTimeCalculator.jumpCheckBox);
		panel.add(DistanceAndTravelTimeCalculator.independenceCheckBox);
		// panel.add(DistanceAndTravelTimeCalculator.inAttackOnDay);

		this.add(panel);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		try {
			double distance = DistanceAndTravelTimeCalculator.calculateDistance(getValues());
			DistanceAndTravelTimeCalculator.calculateTravelTime(distance, isIndependent, doJump);
		} catch (NumberFormatException e1) {
			DistanceAndTravelTimeCalculator.distanceField.setText("NaN");
		} catch (BadLocationException e1) {
			DistanceAndTravelTimeCalculator.distanceField.setText("NaN");
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		try {
			double distance = DistanceAndTravelTimeCalculator.calculateDistance(getValues());
			DistanceAndTravelTimeCalculator.calculateTravelTime(distance, isIndependent, doJump);
		} catch (NumberFormatException e1) {
			DistanceAndTravelTimeCalculator.distanceField.setText("NaN");
			DistanceAndTravelTimeCalculator.travelTimeField.setText("NaN");
		} catch (BadLocationException e1) {
			DistanceAndTravelTimeCalculator.distanceField.setText("NaN");
			DistanceAndTravelTimeCalculator.travelTimeField.setText("NaN");
		}

	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		try {
			double distance = DistanceAndTravelTimeCalculator.calculateDistance(getValues());
			DistanceAndTravelTimeCalculator.calculateTravelTime(distance, isIndependent, doJump);
		} catch (NumberFormatException e1) {
			DistanceAndTravelTimeCalculator.distanceField.setText("NaN");
			DistanceAndTravelTimeCalculator.travelTimeField.setText("NaN");
		} catch (BadLocationException e1) {
			DistanceAndTravelTimeCalculator.distanceField.setText("NaN");
			DistanceAndTravelTimeCalculator.travelTimeField.setText("NaN");
		}

	}

	/**
	 * @throws NumberFormatException
	 * @throws BadLocationException
	 */
	private Double[] getValues() throws NumberFormatException,
	BadLocationException {
		xCoordFirst = Double.parseDouble(x1Document.getText(0,
				x1Document.getLength()));
		yCoordFirst = Double.parseDouble(y1Document.getText(0,
				y1Document.getLength()));
		xCoordSecond = Double.parseDouble(x2Document.getText(0,
				x2Document.getLength()));
		yCoordSecond = Double.parseDouble(y2Document.getText(0,
				y2Document.getLength()));
		Double[] returnValues = new Double[] { xCoordFirst, yCoordFirst,
				xCoordSecond, yCoordSecond };
		return returnValues;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		if (source == DistanceAndTravelTimeCalculator.jumpCheckBox) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				doJump = true;
			} else {
				doJump = false;
			}
			DistanceAndTravelTimeCalculator.independenceCheckBox.setSelected(false);
		} else if (source == DistanceAndTravelTimeCalculator.independenceCheckBox) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				DistanceAndTravelTimeCalculator.jumpCheckBox.setSelected(true);
				DistanceAndTravelTimeCalculator.independenceCheckBox.setSelected(true);
				isIndependent = true;
			} else {
				isIndependent = false;
			}
		}
		try {
			Double[] coordValues = getValues();
			DistanceAndTravelTimeCalculator.calculateTravelTime(DistanceAndTravelTimeCalculator.calculateDistance(coordValues), isIndependent,
					doJump);
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}

	}
}