package io.eiren.util;

import java.util.Arrays;

public class Aligner {

	public static final int FROM_LEFT =- 1;
	public static final int FROM_RIGHT = 2;


	public static String align(String str, char c, int maxLength, int side) {
		final StringBuilder sb = new StringBuilder();

		int diff;
		if (str.length() < maxLength) {
			diff = maxLength - str.length();
		} else return str;

		final char[] arr = new char[diff];
		Arrays.fill(arr, c);

		switch (side) {
			case FROM_LEFT:
				sb.append(arr).append(str);
				break;
			case FROM_RIGHT:
				sb.append(str).append(arr);
				break;
			default:
				throw new IllegalArgumentException();
		}
		return sb.toString();
	}
}
