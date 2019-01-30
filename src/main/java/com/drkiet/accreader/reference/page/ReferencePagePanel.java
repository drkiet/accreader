package com.drkiet.accreader.reference.page;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drkiet.accreader.definition.DefinitionFrame;
import com.drkiet.accreader.definition.DefinitionPanel;
import com.drkiet.accreader.reference.ReferencesFrame;
import com.drkiet.search.DocumentSearch;
import com.drkiettran.text.TextApp;
import com.drkiettran.text.model.Document;

public class ReferencePagePanel extends JPanel {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReferencePageFrame.class);

	private static final long serialVersionUID = -8244136736985618463L;
	public static final int SMALLEST_TEXT_AREA_FONT_SIZE = 3;
	public static final int LARGEST_TEXT_AREA_FONT_SIZE = 7;
	private JTextPane referencePagePane;
	private int textPaneFontSize = 4;
	private String textPaneFont = "Candara";
	private String refPageText = null;
	private ReferencesFrame referencesFrame;
	private DefinitionFrame definitionFrame;
	private Document document = null;
	private DocumentSearch ds;
	private String highlightedText;
	private Integer pageNumber;

	private String refName;

	public ReferencePagePanel(String refName) {
		this.refName = refName;

		if (refName != null && !refName.trim().isEmpty()) {
			loadRefsDocument();
		}
		referencePagePane = new JTextPane();
		referencePagePane.setCaretPosition(0);
		referencePagePane.setCaretColor(Color.WHITE);
		referencePagePane.setContentType("text/html");

		setLayout(new BorderLayout());
		add(new JScrollPane(referencePagePane), BorderLayout.CENTER);
		referencePagePane.addMouseListener(getMouseListner());
		setBorder();
	}

	private MouseListener getMouseListner() {
		return new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				String word = getWordAtCaret(referencePagePane);
				if (SwingUtilities.isRightMouseButton(e)) {
					LOGGER.info("definition for: {}", word);
					displayDefinitions(word);
				}
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
				if (referencePagePane.getSelectedText() != null) { // See if they selected something
					highlightedText = referencePagePane.getSelectedText().trim();
					LOGGER.info("highlighted text: {}", highlightedText);
					referencesFrame.setText(highlightedText);
				}
			}

		};
	}

	private void displayDefinitions(String word) {
		definitionFrame.setTitle(
				String.format("Document: %s %d", document.getBookFileName(), document.getCurrentPageNumber()));
		definitionFrame.setDefinition(word);
	}

	private static String getWordAtCaret(JTextComponent tc) {
		try {
			int caretPosition = tc.getCaretPosition();
			int start = Utilities.getWordStart(tc, caretPosition);
			int end = Utilities.getWordEnd(tc, caretPosition);
			return tc.getText(start, end - start);
		} catch (BadLocationException e) {
			System.err.println(e);
		}

		return null;
	}

	private void loadRefsDocument() {
		document = loadDocumentFromFile();
	}

	private Document loadDocumentFromFile() {
		TextApp textApp = new TextApp();

		Document document = textApp.getPages(refName);
		if (document == null) {
		} else {
			LOGGER.info("{} has {} pages", refName, document.getPageCount());
		}

		document.setBookFileName(refName);
		return document;
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

	private void displayPage() {
		StringBuilder sb = new StringBuilder(String.format(DefinitionPanel.FONT_BEGIN, textPaneFontSize, textPaneFont));
		sb.append(refPageText).append(DefinitionPanel.FONT_END);

		referencePagePane.setText(sb.toString());
		referencePagePane.setCaretPosition(0);
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
		displayPage();
		repaint();
	}

	public void setLargerText() {
		if (textPaneFontSize < LARGEST_TEXT_AREA_FONT_SIZE) {
			this.textPaneFontSize++;
		}
		displayPage();
		repaint();
	}

	public void setDefinitionFrame(DefinitionFrame definitionFrame) {
		this.definitionFrame = definitionFrame;
	}

	public void setPageNumber(Integer pageNumber, String foundText) {
		this.pageNumber = pageNumber;
		document.setPageNo(pageNumber);
		StringBuilder sb = new StringBuilder("<b>Page ").append(pageNumber).append("</b>:<br>");
		sb.append("<p>").append(getHighlightedPage(foundText)).append("</p>");
		refPageText = sb.toString();
		displayPage();
	}

	public String getHighlightedPage(String foundText) {
		String highlightedPage = document.getCurrentPage().getRtm().getText().replaceAll("\n", "<br>");

		String[] highlightedWords = foundText.split(" ");

		for (String highlightedWord : highlightedWords) {
			List<Integer> startHighlightedLocs = getStartHighlightedLocs(highlightedPage, highlightedWord);
			highlightedPage = highlightThePage(highlightedPage, startHighlightedLocs, highlightedWord.length());
			highlightedPage = highlightedPage.replaceAll(highlightedWord,
					String.format("<b><u>%s</u></b>", highlightedWord));
		}
		LOGGER.info("highlightedPage: {}", highlightedPage);
		return highlightedPage;
	}

	private String highlightThePage(String highlightedPage, List<Integer> startHighlightedLocs, int length) {
		StringBuilder sb = new StringBuilder();
		int startIdx = 0;

		for (Integer startHighlightedIndex : startHighlightedLocs) {
			sb.append(highlightedPage.substring(startIdx, startHighlightedIndex));
			sb.append("<b><u>");
			sb.append(highlightedPage.substring(startHighlightedIndex, startHighlightedIndex + length));
			sb.append("</u></b>");
			startIdx = startHighlightedIndex + length;
		}

		if (startIdx < highlightedPage.length()) {
			sb.append(highlightedPage.substring(startIdx));
		}
		return sb.toString();
	}

	private List<Integer> getStartHighlightedLocs(String highlightedPage, String highlightedWord) {
		List<Integer> startHighlightedLocs = new ArrayList<Integer>();
		String lowercasePage = highlightedPage.toLowerCase();
		String lowercaseWord = highlightedWord.toLowerCase();
		int startIndex = 0;
		while (startIndex >= 0) {
			startIndex = lowercasePage.indexOf(lowercaseWord, startIndex);
			if (startIndex >= 0) {
				startHighlightedLocs.add(startIndex);
				startIndex += highlightedWord.length();
			}
		}
		return startHighlightedLocs;
	}

	public void setReferencesFrame(ReferencesFrame referencesFrame) {
		this.referencesFrame = referencesFrame;
	}

	public void setRefName(String refName) {
		this.refName = refName;
	}
}
