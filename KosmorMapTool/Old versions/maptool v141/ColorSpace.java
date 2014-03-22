package kosmor.maptool;

/**
 * ColorSpace can be used to convert colors from RGB (used in the .svg, saved as hex value) 
 * to CMYK (to find out the darker colors, used for planets size 1 and 2) and to
 * compare to CMYK colors to find out of they are the same
 * @author Aurion
 */
public class ColorSpace {
	
/**
 * Converts from hex code to RGB color
 * @param hex the color as hex code #AFB3F0
 * @return the color as RGB values
 */
	private static float[] hexToRGB(String hex) {
		float red = Integer.parseInt(hex.substring(0, 2), 16);
		float green = Integer.parseInt(hex.substring(2, 4), 16);
		float blue = Integer.parseInt(hex.substring(4, 6), 16);
		return new float[] { red, green, blue };
	}
	
/**
 * Converts from a RGB to a CMYK color model
 * @param red the red value of the RGB color
 * @param green the green value of the RGB color
 * @param blue The blue value of the RGB color
 * @return the color as CMYK values
 */
	private static float[] rgbToCMYK(float red, float green, float blue) {

		if (red == 0 && green == 0 && blue == 0) {
			return new float[] { 0, 0, 0, 100 };
		}

		float key;
		float cyan = 1 - (red / 255);
		float magenta = 1 - (green / 255);
		float yellow = 1 - (blue / 255);
		float mincyanMY = Math.min(cyan, Math.min(magenta, yellow));

		cyan = Math.round((cyan - mincyanMY) / (1 - mincyanMY) * 100);
		magenta = Math.round((magenta - mincyanMY) / (1 - mincyanMY) * 100);
		yellow = Math.round((yellow - mincyanMY) / (1 - mincyanMY) * 100);
		key = Math.round(mincyanMY * 100);

		return new float[] { cyan, magenta, yellow, key };
	}

	/**
	 * Converts from hex code to a CMYK color model, using the hexToRGB
	 * and RGB to CMYK methods
	 * @param hex the color as hex code #AFB3F0
	 * @return
	 */
	public static float[] hexToCMYK(String hex) {
		float[] rgbclor = hexToRGB(hex);
		float red = rgbclor[0];
		float green = rgbclor[1];
		float blue = rgbclor[2];
		float[] cmykColor = rgbToCMYK(red, green, blue);
		return cmykColor;
	}

	/**
	 * Determines the "first darker color" by adding 35 to the k or black value of CMYK
	 * @param cmykColor the color as CMYK values
	 * @return firstDarkerColor, the color used for planets size 2
	 */
	public static float[] calculateFirstDarkerColor(float[] cmykColor) {
		float cyan = cmykColor[0];
		float magenta = cmykColor[1];
		float yellow = cmykColor[2];
		float key = cmykColor[3];
		key = cmykColor[3] + 35; // Add 35 to the key, making the color darker
		float[] firstDarkerColor = new float[] { cyan, magenta, yellow, key };
		return firstDarkerColor;
	}

	/**
	 * Determines the "second darker color" by adding 35 to the k or black value of CMYK
	 * @param cmykColor the color as CMYK values
	 * @return secondDarkerColor, the color used for planets size 1
	 */
	public static float[] calculateSecondDarkerColor(float[] cmykColor) {
		float cyan = cmykColor[0];
		float magenta = cmykColor[1];
		float yellow = cmykColor[2];
		float key = cmykColor[3];
		key = cmykColor[3] + 70; // Add 70 to the key, making the color darker
		float[] secondDarkerCMYKColor = new float[] { cyan, magenta, yellow,
				key };
		return secondDarkerCMYKColor;
	}

	/**
	 * 
	 * @param cmykColor1 The first color to compare
	 * @param cmykColor2 The second color to compare
	 * @return whether the colors are the same (with tolerance)
	 */
	public static boolean compareCMYKColors(float[] cmykColor1,
			float[] cmykColor2) {
		float cyan1 = cmykColor1[0];
		float magenta1 = cmykColor1[1];
		float yellow1 = cmykColor1[2];
		float key1 = cmykColor1[3];
		float cyan2 = cmykColor2[0];
		float magenta2 = cmykColor2[1];
		float yellow2 = cmykColor2[2];
		float key2 = cmykColor2[3];

		if ((key1 == key2) && (cyan1 < cyan2 + 2 && cyan1 > cyan2 - 2)
				&& (magenta1 < magenta2 + 2 && magenta1 > magenta2 - 2)
				&& (yellow1 < yellow2 + 2 && yellow1 > yellow2 - 2)) {
			return true;
		} 
		else return false;

	}
}
