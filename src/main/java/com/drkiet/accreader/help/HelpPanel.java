package com.drkiet.accreader.help;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.Border;

import com.drkiet.accreader.reference.ReferencesPanel;
import com.drkiet.accreader.util.FileHelper;

public class HelpPanel extends JPanel {
	private static final String fonSizeFace = "<font size=\"%d\" face=\"%s\">";

	private static final long serialVersionUID = -8548706825616512644L;

	public static final String[] startTags = { "<h1>", "<h2>", "<h3>", "<p>", "<li>" },
			endTags = { "</h1>", "</h2>", "</h3>", "</p>", "</li>" };

	private JTextPane textPane;
	private String text = null;
	private Integer textPaneFontSize = 4;
	private String textPaneFont = "Candara";

	public HelpPanel() {
		textPane = new JTextPane();
		textPane.setCaretColor(Color.WHITE);
		textPane.setContentType("text/html");
		text = FileHelper.loadFaqsFile();
		textPane.setText(updateFont(text));

		textPane.setCaretPosition(0);
		textPane.setForeground(Color.BLUE);
		setLayout(new BorderLayout());
		add(new JScrollPane(textPane), BorderLayout.CENTER);
		setBorder();
	}

	private String updateFont(String faqs) {
		String startFont = String.format(fonSizeFace, textPaneFontSize, textPaneFont);

		for (int idx = 0; idx < startTags.length; idx++) {
			faqs = faqs.replaceAll(startTags[idx], startTags[idx] + startFont);
			faqs = faqs.replaceAll(endTags[idx], "</font>" + endTags[idx]);
		}

		return faqs;
	}

	private void setBorder() {
		Border innerBorder = BorderFactory.createTitledBorder("FAQs");
		Border outterBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outterBorder, innerBorder));
	}

	public void setSmallerText() {
		if (textPaneFontSize > ReferencesPanel.SMALLEST_TEXT_AREA_FONT_SIZE) {
			this.textPaneFontSize--;
		}
		textPane.setText(updateFont(text));
		textPane.setCaretPosition(0);
		repaint();
	}

	public void setLargerText() {
		if (textPaneFontSize < ReferencesPanel.LARGEST_TEXT_AREA_FONT_SIZE) {
			this.textPaneFontSize++;
		}
		textPane.setText(updateFont(text));
		textPane.setCaretPosition(0);
		repaint();
	}
}
