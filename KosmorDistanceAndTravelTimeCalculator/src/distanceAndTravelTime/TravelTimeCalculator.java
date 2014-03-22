package distanceAndTravelTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TravelTimeCalculator {

	public static void main(String[] args) throws IOException {
		System.out
		.println("Please insert \"doc\" if you want to read the documentation. Type anything else to proceed without.");
		if (TravelTimeCalculator.docInput().equals("doc")) {
			TravelTimeCalculator.printDoc();
			System.out.println();
			System.out.println("Press any key to proceed");
			TravelTimeCalculator.docInput();
		}
		System.out.println("Please insert the distance:");
		double distance = TravelTimeCalculator.input();
		System.out.println("Is the owner of the wp independent?");
		System.out.println("Please insert 0 for false, 1 for true");
		boolean independence = TravelTimeCalculator.independent();
		System.out.println("Please insert time since last jump");
		int jump = 4 - (int) TravelTimeCalculator.input();
		if (jump < 0) {
			jump = 0;
		}
		int travelTime = (int) TravelTimeCalculator.calculateTravelTime(
				distance, independence, jump);
		System.out.println("ETA: " + travelTime + " days");
	}

	private static void printDoc() {
		String docString = "This tool calculates the time it takes a wp to travel a given distance. "
				+ "\n"
				+ "It also tells you at which days the wp would be in attack range (+/- 60 LY for H11) if it jumped the day before."
				+ "\n"
				+ "It does NOT tell you if the wp would be capable of jumping, as that depends on wether it jumped before etc."
				+ "\n"
				+ "The ETA does not necessarily change with jumps on different days, thus it's not feasible "
				+ "\n"
				+ "to determine wether a wp could jump on a given day. This tool assumes a jump as soon as possible. It is however possible to "
				+ "\n"
				+ "It is however possible to set the time since the last jump, to delay the initial jump";
		System.out.println(docString);
	}

	private static double calculateTravelTime(double distance,
			boolean independence, int jump) {
		double travelTime = 0;
		String traveltimeOutput = "With jump on the day before in attack range at day ";
		if (independence == false) {
			{
				while (distance > 0) {
					if (jump == 0) {
						if (distance >= 90 && distance <= 210) {
							System.out.println(traveltimeOutput
									+ (int) (travelTime + 1)
									+ ", Remaining distance: " + distance);
						}
						distance = distance - 150.0;
						jump = 4;
						travelTime++;
					}
					if (jump > 0) {
						distance -= 60.0;
						jump--;
						travelTime++;
					}
				}
				return travelTime;
			}
		} else {
			while (distance > 0) {
				System.out.println(distance + " " + jump + " " + travelTime);
				if (distance >= 540 && distance <= 660) {
					System.out.println(traveltimeOutput
							+ (travelTime + 1) + ", Remaining distance: "
							+ distance);
				}
				if (distance >= 240 && distance <= 360) {
					System.out.println(traveltimeOutput
							+ (int) (travelTime + 1) + ", Remaining distance: "
							+ distance);
				}
				if (distance >= 90 && distance <= 210) {
					System.out.println(traveltimeOutput
							+ (int) (travelTime + 1) + ", Remaining distance: "
							+ distance);
				}
				if (distance > 600 && jump == 0) {
					distance = distance - 600.0;
					jump = 4;
					travelTime++;
				}
				else if (distance > 300 && jump == 0){
					distance = distance - 300.0;
					jump = 4;
					travelTime++;
				}
				else if (distance > 120 && jump == 0) {
					distance = distance - 150.0;
					jump = 4;
					travelTime++;
				}
				else if (distance <= 120 || jump != 0) {
					distance = distance - 60.0;
					jump--;
					travelTime++;
				}
				distance = (double) Math.round(distance * 1000) / 1000;
			}
			return travelTime;
		}
	}

	private static boolean independent() throws NumberFormatException,
	IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		if (Integer.parseInt(br.readLine()) == 1) {
			return true;
		} else {
			return false;
		}

	}

	private static double input() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		double input = Double.parseDouble(br.readLine());
		return input;
	}

	private static String docInput() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		return br.readLine();
	}

}
