public class PlanetStuff {

	public static void main(String[] args) {
		System.out.println(calculateSustainablePopulation(47872, 1.4f, false));
		System.out.println(calculateGrowth(1471018, 49062, true, 1.4f, 0.12f));

	}

	public static int calculateSustainablePopulation(int diameter,
			float growthRateModifier, boolean hasLeisurePark) {
		Float sustainablePopulation = 0f;
		if (hasLeisurePark) {
			sustainablePopulation = 30 * diameter - 250 / growthRateModifier;
		} else if (!hasLeisurePark) {
			sustainablePopulation = 30 * diameter - 100 / growthRateModifier;
		}
		return sustainablePopulation.intValue();
	}

	public static int calculateGrowth(int currentPopulation, int diameter,
			boolean hasLeisurePark, float growthRateModifier, float taxRate) {
		Float expectedGrowth = 0.1f * (30 * diameter - currentPopulation)
				* growthRateModifier
				* (0.1f + 0.03f * (hasLeisurePark ? 1 : 0) - taxRate);
		return expectedGrowth.intValue();
	}
}
