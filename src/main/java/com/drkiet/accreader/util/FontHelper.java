package com.drkiet.accreader.util;

import com.drkiet.accreader.help.HelpPanel;

public class FontHelper {
	public static String updateFont(String text, int fontSize, String font) {
		String startFont = String.format(HelpPanel.FONT_SIZE_FACE, fontSize, font);

		for (int idx = 0; idx < HelpPanel.START_TAGS.length; idx++) {
			text = text.replaceAll(HelpPanel.START_TAGS[idx], HelpPanel.START_TAGS[idx] + startFont);
			text = text.replaceAll(HelpPanel.END_TAGS[idx], "</font>" + HelpPanel.END_TAGS[idx]);
		}

		return text;
	}

	public static String insertFont(String text) {
		String startFont = HelpPanel.FONT_SIZE_FACE;

		for (int idx = 0; idx < HelpPanel.START_TAGS.length; idx++) {
			text = text.replaceAll(HelpPanel.START_TAGS[idx], HelpPanel.START_TAGS[idx] + startFont);
			text = text.replaceAll(HelpPanel.END_TAGS[idx], "</font>" + HelpPanel.END_TAGS[idx]);
		}

		return text;
	}
}
