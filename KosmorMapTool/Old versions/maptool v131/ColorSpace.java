package kosmor.maptool;


public class ColorSpace {

	private static float[] hexToRGB(String hex) {
		float red = Integer.parseInt(hex.substring(0, 2), 16);
		float green = Integer.parseInt(hex.substring(2, 4), 16);
		float blue = Integer.parseInt(hex.substring(4, 6), 16);
		return new float[] { red, green, blue };
	}

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

	public static float[] hexToCMYK(String hex) {
		float[] rgbclor = hexToRGB(hex);
		float red = rgbclor[0];
		float green = rgbclor[1];
		float blue = rgbclor[2];
		float[] cmykColor = rgbToCMYK(red, green, blue);
		return cmykColor;
	}

	public static float[] calculateFirstDarkerColor(float[] cmykColor) {
		float cyan = cmykColor[0];
		float magenta = cmykColor[1];
		float yellow = cmykColor[2];
		float key = cmykColor[3];
		key = cmykColor[3] + 35; // Add 35 to the key, making the color darker
		float[] firstCMYKColor = new float[] { cyan, magenta, yellow, key };
		return firstCMYKColor;
	}

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
