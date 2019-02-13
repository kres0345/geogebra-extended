package org.geogebra.keyboard.scientific.model;

import org.geogebra.keyboard.base.Accents;
import org.geogebra.keyboard.base.model.impl.factory.LetterKeyboardFactory;

import java.util.Arrays;

public class ScientificLettersKeyboardFactory extends LetterKeyboardFactory {

	private static final String DEFAULT_CONTROL_ROW = "=,'";
	private StringBuilder builder = new StringBuilder();

	@Override
	public void setKeyboardDefinition(String topRow, String middleRow, String bottomRow, String
			controlRow, Integer bottomActionLeft, Integer controlActionLeft) {
		String[] rows = { topRow, middleRow, bottomRow };
		String[][] possibleAccents = new String[rows.length][2];
		int[] accentsLength = new int[rows.length];
		int allAccentsLength = 0;
		for (int i = 0; i < rows.length; i++) {
			possibleAccents[i][0] = getAccents(rows[i]);
			possibleAccents[i][1] = reverse(getAccents(reverse(rows[i])));
			accentsLength[i] = possibleAccents[i][0].length() + possibleAccents[i][1].length();
			allAccentsLength += accentsLength[i];
		}

		builder.setLength(0);
		for (int i = 0; i < rows.length; i++) {
			String row = rows[i];
			builder.append(row.substring(
					possibleAccents[i][0].length(),
					row.length() - possibleAccents[i][1].length()));
		}

		String allButtons = builder.toString();
		char[] characters = allButtons.toCharArray();
		Arrays.sort(characters);
		int length = characters.length + allAccentsLength;
		int[] lengths = new int[rows.length + 1];
		int rowLength = (int) Math.ceil(length / 3.0f);
		for (int i = 0; i < rows.length; i++) {
			lengths[i + 1] = rowLength - accentsLength[i];
		}
		if (length % 3 == 2) {
			lengths[3] -= 1;
		} else if (length % 3 == 1) {
			lengths[1] -= 1;
			lengths[3] -= 1;
		}

		String[] newRows = new String[rows.length];
		for (int i = 0; i < rows.length; i++) {
			lengths[i + 1] += lengths[i];
			newRows[i] = possibleAccents[i][0] + subrangeToString(characters, lengths[i],
					lengths[i + 1]) + possibleAccents[i][1];
		}
		super.setKeyboardDefinition(newRows[0], newRows[1], newRows[2], DEFAULT_CONTROL_ROW,
				null, ACTION_SHIFT);
	}

	private String getAccents(String string) {
		builder.setLength(0);
		for (int i = 0; i < string.length() && i < 2; i++) {
			String c = String.valueOf(string.charAt(i));
			if (Accents.isAccent(c)) {
				builder.append(c);
			} else {
				break;
			}
		}
		return builder.toString();
	}

	private String reverse(String string) {
		return new StringBuilder(string).reverse().toString();
	}

	private String subrangeToString(char[] chars, int from, int to) {
		return String.valueOf(Arrays.copyOfRange(chars, from, to));
	}
}
