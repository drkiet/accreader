package com.drkiet.accreader.reference;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.Border;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drkiet.accreader.definition.DefinitionFrame;
import com.drkiet.accreader.definition.DefinitionPanel;
import com.drkiet.accreader.reference.page.ReferencePageFrame;
import com.drkiet.accreader.util.FileHelper;
import com.drkiet.accreader.util.WebHelper;
import com.drkiet.search.DocumentSearch;

public class ReferencesPanel extends JPanel {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReferencesFrame.class);

	private static final long serialVersionUID = -8244136736985618463L;
	public static final int SMALLEST_TEXT_AREA_FONT_SIZE = 3;
	public static final int LARGEST_TEXT_AREA_FONT_SIZE = 7;
	private JTextPane referencesPane;
	private ReferencePageFrame referencePageFrame = null;
	private int textPaneFontSize = 4;
	private String textPaneFont = "Candara";
	private String referenceText = null;
	private DefinitionFrame definitionFrame;
	private String accDefinition = null;
	private DocumentSearch ds;
	private String searchText;
	private ReferencesFrame referencesFrame;
	private String refName = "";

	public ReferencesPanel() {
//		loadRefsDocument();
		referencesPane = new JTextPane();
		referencesPane.setCaretPosition(0);
		referencesPane.setCaretColor(Color.WHITE);
		referencesPane.setContentType("text/html");

		setLayout(new BorderLayout());
		add(new JScrollPane(referencesPane), BorderLayout.CENTER);
		referencesPane.addMouseListener(getMouseListner());
		setBorder();
	}

	private MouseListener getMouseListner() {
		return new MouseListener() {

			private String highlightedText;

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (referencesPane.getSelectedText() != null) { // See if they selected something
					highlightedText = referencesPane.getSelectedText().trim();
					if (referencePageFrame == null) {
						referencePageFrame = new ReferencePageFrame(refName);
						referencePageFrame.setReferencesFrame(referencesFrame);
						referencePageFrame.setDefinitionFrame(definitionFrame);
					}
					referencePageFrame.setPageNumber(Integer.valueOf(highlightedText), searchText);
					LOGGER.info("highlighted page: {} for {}", highlightedText, searchText);
				}
			}

		};
	}

	private void loadRefsDocument() {
		ds = DocumentSearch.getInstance(FileHelper.getFQRefencesFileName(refName), FileHelper.getWorkspaceFolder());
	}

	public void setText(String text) {
		if (text.toLowerCase().startsWith("http")) {
			displayHttpText(text);
			referenceText = null;
			return;
		}
		accDefinition = null;
		displayReference(text);
	}

	private void displayReference(String text) {
		searchText = text;
		HashMap<Integer, Integer> pageNumbersMap = searchReferences(text);
		List<Integer> pageNumbersList = new ArrayList<Integer>();
		pageNumbersList.addAll(pageNumbersMap.keySet());
		Collections.sort(pageNumbersList);
		StringBuilder sb = new StringBuilder("<b>").append(text);

		if (pageNumbersList.isEmpty()) {
			sb.append(":</b><br>*** no reference ***");
		} else {
			sb.append("</b> is founded on these pages: <br>");
		}

		for (Integer pageNumber : pageNumbersList) {
			sb.append(pageNumber);
			if (pageNumbersMap.get(pageNumber) > 1) {
				sb.append('(').append(pageNumbersMap.get(pageNumber)).append(')');
			}
			sb.append(", ");
		}
		this.referenceText = sb.toString();
		displayText();
	}

	public HashMap<Integer, Integer> searchReferences(String searchText) {
		String[] searchWords = searchText.split(" ");
		HashMap<Integer, Integer> foundPageNumbers = new HashMap<Integer, Integer>();

		for (String searchWord : searchWords) {
			Iterator<Integer> pageNumbers = ds.search(searchWord);
			while (pageNumbers.hasNext()) {
				Integer pageNumber = pageNumbers.next();
				Integer count = foundPageNumbers.get(pageNumber);
				count = count == null ? 1 : count + 1;
				foundPageNumbers.put(pageNumber, count);
			}
		}
		return foundPageNumbers;
	}

	private void displayHttpText(String url) {
		this.accDefinition = WebHelper.getWordDefinition(url);
		displayText();
	}

	private void displayText() {
		String displayingText;
		if (accDefinition != null) {
			displayingText = accDefinition;
		} else if (referenceText != null) {
			displayingText = referenceText;
		} else {
			displayingText = "";
		}
		StringBuilder sb = new StringBuilder(String.format(DefinitionPanel.FONT_BEGIN, textPaneFontSize, textPaneFont));
		sb.append(displayingText).append(DefinitionPanel.FONT_END);

		referencesPane.setText(sb.toString());
		referencesPane.setCaretPosition(0);
	}

	private void setBorder() {
		Border innerBorder = BorderFactory.createTitledBorder("References");
		Border outterBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outterBorder, innerBorder));
	}

	public void setSmallerText() {
		if (textPaneFontSize > SMALLEST_TEXT_AREA_FONT_SIZE) {
			this.textPaneFontSize--;
		}
		displayText();
		repaint();
	}

	public void setLargerText() {
		if (textPaneFontSize < LARGEST_TEXT_AREA_FONT_SIZE) {
			this.textPaneFontSize++;
		}
		displayText();
		repaint();
	}

	public void setDefinitionFrame(DefinitionFrame definitionFrame) {
		this.definitionFrame = definitionFrame;
	}

	public void setReferencesFrame(ReferencesFrame referencesFrame) {
		this.referencesFrame = referencesFrame;
	}

	public void setRefBook(String refName) {
		this.refName = refName;
		if (referencePageFrame == null) {
			referencePageFrame = new ReferencePageFrame(refName);
			referencePageFrame.setReferencesFrame(referencesFrame);
			referencePageFrame.setDefinitionFrame(definitionFrame);
		}
		referencePageFrame.setRefBook(refName);
		loadRefsDocument();
	}
}
