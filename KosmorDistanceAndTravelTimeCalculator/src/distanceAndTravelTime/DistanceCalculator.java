package distanceAndTravelTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DistanceCalculator {
	public static void main(String[] args) throws IOException {
		System.out
		.println("Please enter x and y of the first planet like \"-735 201\"");
		double[] coords1 = DistanceCalculator.input();
		System.out
		.println("Please enter x and y of the second planet the same way");
		double[] coords2 = DistanceCalculator.input();
		double distance = DistanceCalculator.calculateDistance(coords1, coords2);
		System.out.println("The distance between the two objects is: ");
		System.out.println(distance);
	}

	private static double calculateDistance(double[] coords1, double[] coords2) {
		double a = (coords1[0] - coords2[0]);
		double b = (coords1[1] - coords2[1]);
		double c = Math.pow(a, 2) + Math.pow(b, 2);

		System.out.println(coords1[0]+" "+coords2[0]+" "+a);
		System.out.println(coords1[1]+" "+coords2[1]+" "+b);
		// Pythagorean theorem, a² + b² = c² ..

		return Math.sqrt(c);

	}

	private static double[] input() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String s = br.readLine();

		double[] in = new double[2];
		String[] splitStrings = s.split(" ");

		in[0] = Double.parseDouble(splitStrings[0]);
		in[1] = Double.parseDouble(splitStrings[1]);
		return in;
	}
}
