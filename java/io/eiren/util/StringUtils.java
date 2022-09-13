package io.eiren.util;

import java.text.DecimalFormatSymbols;
import java.util.Locale;


public class StringUtils {

	private static char DECIMAL_SEP;

	public static char getDecimalSeparator() {
		if (DECIMAL_SEP == '\u0000') {
			final Locale l = Locale.getDefault(Locale.Category.FORMAT);
			// Formatter.java always use "." in the Locale.US
			DECIMAL_SEP = (l == null || l.equals(Locale.US)
				? '.'
				: DecimalFormatSymbols.getInstance(l).getDecimalSeparator());
		}
		return DECIMAL_SEP;
	}

	public static String prettyNumber(float f) {
		return prettyNumber(f, 4);
	}

	public static String prettyNumber(float f, int numDigits) {
		String str = String.format("%." + numDigits + "f", f);
		if (numDigits != 0)
			str = org.apache.commons.lang3.StringUtils.stripEnd(str, "0");
		char lastChar = str.charAt(str.length() - 1);
		if (lastChar == getDecimalSeparator())
			str = str.substring(0, str.length() - 1);
		return str;
	}
}
