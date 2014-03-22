package kosmor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.*;

public class KosmorStarRedistributionTax {
	static BigDecimal c;
	static int n;
	static int i;
	static final int MAX_NUMBER = 315;
	static BigDecimal taxIncome = BigDecimal.ZERO;
	private static BigDecimal sumSRT = BigDecimal.ZERO;
	private static BigDecimal totalTaxIncome = BigDecimal.ZERO;
	/**
	 * @param args
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static void main(String[] args) throws NumberFormatException,
			IOException {
			for (i = 1; i < MAX_NUMBER; i++) {
			//taxIncome = taxIncome.add(calculateTaxIncome(i));
			taxIncome = calculateTaxIncome(i);
			totalTaxIncome = totalTaxIncome.add(taxIncome);
			System.out.println("Total income before SRT is now " + totalTaxIncome);
			sumSRT = sumSRT.add(calculateTaxIncomeAfterStarRedistributionTax(i, taxIncome));
			System.out.println("Total income after SRT is now " + sumSRT);
			System.out.println(\u000D);
			
		}
	}

	private static String input() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String in = br.readLine();
		return in;
	}

	/**
	 * 
	 * @param n int The taxrank of the planet in question
	 * @return c BigDecimal The % of star redistribution tax for planet n
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static BigDecimal calculateStarRedistributionTax(int n)
			throws NumberFormatException, IOException {
		double a = 6;
		double b = Math.exp(0.05 * n);
		c = BigDecimal.valueOf(a).multiply(BigDecimal.valueOf(b));
		if (c.compareTo(BigDecimal.valueOf(95)) > 0) {
			c = BigDecimal.valueOf(95);
		}
		System.out.println("Tax Rank " + n + " " + c + " %");

		return c;
	}

	/**
	 * 
	 * @param n int The taxrank of the planet in question
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static BigDecimal calculateTaxIncomeAfterStarRedistributionTax(int n, BigDecimal taxIncome)
			throws NumberFormatException, IOException {

		BigDecimal starRedistributionTaxPercent = calculateStarRedistributionTax(n);
		//BigDecimal taxIncome = calculateTaxIncome(n);
		
		BigDecimal starTax = taxIncome.multiply(starRedistributionTaxPercent
				.divide(BigDecimal.valueOf(100)));
		starTax = starTax.setScale(0, BigDecimal.ROUND_DOWN);
		System.out.println("starRedistributionTax= " + starTax);

		BigDecimal income = taxIncome.subtract(starTax);

		return income;
	}

	private static BigDecimal calculateTaxIncome(int n) {
		int planetsize = 960000;
		
		BigDecimal planetSize = BigDecimal.valueOf(planetsize);
		BigDecimal taxPercent = BigDecimal.valueOf(0.109594);
		// Derzeitiger Durchschnittssteuersatz: ~0.1067
	
		//System.out.println("TaxPercent = " + taxPercent);

		BigDecimal taxIncome = planetSize.multiply(taxPercent);
		taxIncome = taxIncome.setScale(0, BigDecimal.ROUND_DOWN);
		//System.out.println("Tax income = " + taxIncome +" credits");
		return taxIncome;
	}

}