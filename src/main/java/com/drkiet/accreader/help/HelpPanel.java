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
	public static final String FONT_SIZE_FACE = "<font size=\"%d\" face=\"%s\">";

	private static final long serialVersionUID = -8548706825616512644L;

	public static final String[] START_TAGS = { "<h1", "<h2", "<h3", "<p", "<li", "<a" },
			END_TAGS = { "</h1>", "</h2>", "</h3>", "</p>", "</li>", "</a>" };

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
		String startFont = String.format(FONT_SIZE_FACE, textPaneFontSize, textPaneFont);

		for (int idx = 0; idx < START_TAGS.length; idx++) {
			faqs = faqs.replaceAll(START_TAGS[idx], START_TAGS[idx] + startFont);
			faqs = faqs.replaceAll(END_TAGS[idx], "</font>" + END_TAGS[idx]);
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
