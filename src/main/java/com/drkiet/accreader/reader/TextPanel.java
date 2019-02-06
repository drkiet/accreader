package com.drkiet.accreader.reader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import org.elasticsearch.common.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drkiet.accreader.definition.DefinitionFrame;
import com.drkiet.accreader.reference.ReaderListener;
import com.drkiet.accreader.reference.ReaderListener.Command;
import com.drkiet.accreader.reference.ReferencesFrame;
import com.drkiet.accreader.util.FileHelper;
import com.drkiettran.text.ReadingTextManager;
import com.drkiettran.text.model.Document;
import com.drkiettran.text.model.Page;
import com.drkiettran.text.model.SearchResult;
import com.drkiettran.text.model.Word;

/**
 * TextPanel consists of three main components:
 * <ul>
 * <li>Reading word Label (Label)
 * <li>Reading text area (TextArea)
 * <li>Information area (Label)
 * </ul>
 * 
 * @author ktran
 *
 */
public class TextPanel extends JPanel {
	private static final Logger LOGGER = LoggerFactory.getLogger(TextPanel.class);

	public static final String LINE_INFO = " *** line: ";
	public static final int DEFAULT_DISPLAYING_FONT_SIZE = 60;
	public static final int DEFAULT_TEXT_AREA_FONT_SIZE = 18;
	public static final int SMALLEST_DISPLAYING_FONT_SIZE = 20;
	public static final int SMALLEST_TEXT_AREA_FONT_SIZE = 10;
	public static final int LARGEST_DISPLAYING_FONT_SIZE = 100;
	public static final int LARGEST_TEXT_AREA_FONT_SIZE = 32;
	public static final long serialVersionUID = -825536523977292110L;

	private JTextArea textArea;
	private JLabel displayingWordLabel;
	private String readingText = null;
	private JLabel infoLabel;
	private JLabel titleLabel;

	private ReferencesFrame referencesFrame = new ReferencesFrame();
	private DefinitionFrame definitionFrame = new DefinitionFrame();

	private List<Object> highlightedWords = new ArrayList<Object>();
	private Object highlightSelectedWord = null;
	private Object highlightedWord = null;

	private ReaderListener readerListener;

	private String displayingFontName = "Candara";
	private String infoFontName = "Candara";
	private int displayingWordFontSize = DEFAULT_DISPLAYING_FONT_SIZE;
	private int infoFontSize = 12;
	private String textAreaFontName = "Candara";
	private int textAreaFontSize = DEFAULT_TEXT_AREA_FONT_SIZE;
	private int defaultBlinkRate = 0;

	private Document document = null;
	private ReadingTextManager readingTextManager = null;
	private boolean doneReading = true;
	private Word selectedWord = null;
	private Word wordAtMousePos = null;
	private SearchResult searchResult = null;
	private String searchText;
	private List<String> currentTextAreaByLines = null;
	private int currentLineNumber;
	private String highlightedText;

	private InfoPanel infoPanel;

	public boolean isDoneReading() {
		return doneReading;
	}

	private void resetState() {
		document = null;
		readingText = null;
		doneReading = true;
		selectedWord = null;
		wordAtMousePos = null;
		searchResult = null;
		searchText = null;
		currentTextAreaByLines = null;
		currentLineNumber = 0;
		infoLabel.setText(null);
		displayingWordLabel.setText(null);
	}

	public TextPanel() {
		arrangeFixedComponents();
		setBorder("Reader");
		arrangeLayout();
		setObjectLinks();
	}

	private void setObjectLinks() {
		definitionFrame.setReferencesFrame(referencesFrame);
		referencesFrame.setDefinitionFrame(definitionFrame);
		definitionFrame.setInfoPanel(infoPanel);
	}

	private void arrangeFixedComponents() {
		displayingWordLabel = new JLabel();
		displayingWordLabel.setFont(new Font(displayingFontName, Font.PLAIN, displayingWordFontSize));
		displayingWordLabel.setHorizontalAlignment(JLabel.CENTER);
		textArea = new JTextArea();
		infoLabel = new JLabel("");
		infoLabel.setFont(new Font(infoFontName, Font.PLAIN, infoFontSize));
		titleLabel = new JLabel("Title:");
	}

	private void arrangeLayout() {
		setLayout(new BorderLayout());
		add(displayingWordLabel, BorderLayout.NORTH);
		addWelcomePane();
		add(titleLabel, BorderLayout.SOUTH);
		add(infoLabel, BorderLayout.SOUTH);

	}

	private void addWelcomePane() {
		removeComponentOnCenterLayout();
		JTextPane textPane = new JTextPane();
		textPane.setFont(new Font("Candara", Font.PLAIN, 12));
		textPane.setContentType("text/html");
		textPane.setForeground(Color.GREEN);
		textPane.setBackground(new Color(245, 245, 245));

		textPane.setText(FileHelper.loadTextFileIntoString("/About.html"));

		add(textPane, BorderLayout.CENTER);
	}

	public void removeComponentOnCenterLayout() {
		BorderLayout layout = (BorderLayout) getLayout();
		Component comp = layout.getLayoutComponent(BorderLayout.CENTER);
		if (comp != null) {
			remove(comp);
		}
	}

	private void addTextPane() {
		removeComponentOnCenterLayout();
		makeTextArea();
		add(new JScrollPane(textArea), BorderLayout.CENTER);
	}

	private void setBorder(String bookName) {
		Border innerBorder = BorderFactory.createTitledBorder(bookName);
		Border outterBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outterBorder, innerBorder));
	}

	private void makeTextArea() {
		defaultBlinkRate = textArea.getCaret().getBlinkRate();
		textArea.setCaretPosition(0);
		textArea.setCaretColor(Color.white);
		textArea.setFont(new Font(textAreaFontName, Font.PLAIN, textAreaFontSize));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);

		textArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				readerListener.invoke(Command.RESTART);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				readerListener.invoke(Command.RESTART);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				readerListener.invoke(Command.RESTART);
			}

		});

		textArea.addMouseMotionListener(getMouseMotionListener());
		textArea.addMouseListener(getMouseListner());

	}

	private MouseListener getMouseListner() {

		return new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				SwingUtilities.isLeftMouseButton(e);
				SwingUtilities.isRightMouseButton(e);
				SwingUtilities.isMiddleMouseButton(e);
				if (readingTextManager != null) {
					int caretPos = textArea.getCaretPosition();
					selectedWord = readingTextManager.getWordAt(textArea.getCaretPosition());
					if (SwingUtilities.isRightMouseButton(e)) {
						displayDefinitions(selectedWord.getTransformedWord().replaceAll("'", ""));
					}
					try {
						highlightSelectedWord = highlight(selectedWord.getTransformedWord(),
								selectedWord.getIndexOfText(), Color.GRAY, highlightSelectedWord);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					search(selectedWord.getTransformedWord().replaceAll("'", ""));
					textArea.setCaretPosition(caretPos);
				}
			}

			@Override
			public void mousePressed(MouseEvent event) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (textArea.getSelectedText() != null) { // See if they selected something
					highlightedText = textArea.getSelectedText().trim();
					displayReferences(highlightedText);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {
				wordAtMousePos = null;
			}
		};
	}

	private MouseMotionListener getMouseMotionListener() {
		return new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent event) {
				processToolTip(event);
			}

			@Override
			public void mouseDragged(MouseEvent event) {
			}
		};

	}

	public boolean mouseOverWord(int caretPos) {
		return caretPos >= wordAtMousePos.getIndexOfText()
				&& caretPos <= wordAtMousePos.getIndexOfText() + wordAtMousePos.getTransformedWord().length();
	}

	public void processToolTip(MouseEvent event) {
		int viewToModel = textArea.viewToModel(event.getPoint());
		if (viewToModel != -1) {
			try {
				String labelText = infoLabel.getText();
				int idx = labelText.indexOf(LINE_INFO);
				if (idx >= 0) {
					labelText = labelText.substring(0, idx);
				}
				infoLabel.setText(labelText + LINE_INFO + (1 + textArea.getLineOfOffset(viewToModel)));
				textArea.setCaretPosition(textArea.viewToModel(event.getPoint()));

				String curWord = "--";
				int caretPos = textArea.getCaretPosition();

				if (readingTextManager != null) {
					wordAtMousePos = readingTextManager.getWordAt(textArea.getCaretPosition());
					if (wordAtMousePos != null && mouseOverWord(caretPos)) {
						curWord = wordAtMousePos.getTransformedWord();
						SearchResult sr = readingTextManager.search(curWord);
						String tip = String.format("<html><p><font color=\"#800080\" "
								+ "size=\"4\" face=\"Verdana\">%d '%s's found" + "</font></p></html>",
								sr.getNumberMatchedWords(), curWord);
						textArea.setToolTipText(tip);
					}
				}

				// LOGGER.debug("{}. caret: {}; word: {}", getCurrentLineNumber(),
				// textArea.getCaretPosition(), curWord);
				repaint();
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}

	}

	private int getCurrentLineNumber() {
		try {
			return textArea.getLineOfOffset(textArea.getCaretPosition()) + 1;
		} catch (BadLocationException e) {
			LOGGER.info("Bad Location Exception: " + e);
		}
		return -1;
	}

	private String getCurrentLineText(int currentLineNumber) {
		if (currentLineNumber <= currentTextAreaByLines.size() - 1) {
			return currentTextAreaByLines.get(currentLineNumber);
		} else {
			return currentTextAreaByLines.get(currentTextAreaByLines.size() - 1);
		}
	}

	private void displayDefinitions(String word) {
		definitionFrame.setTitle(
				String.format("Document: %s %d", document.getBookFileName(), document.getCurrentPageNumber()));
		definitionFrame.setDefinition(word);
	}

	public void displayReferences(String referenceText) {
		definitionFrame.setTitle(
				String.format("Document: %s %d", document.getBookFileName(), document.getCurrentPageNumber()));
		referencesFrame.setText(referenceText);
	}

	public void updateInfoLabel() {
		String labelText = infoLabel.getText();
		int idx = labelText.indexOf(LINE_INFO);
		String cursorInfo;

		if (idx >= 0) {
			labelText = labelText.substring(0, idx);
		}

		if (document != null) {
			cursorInfo = String.format("%s %d", LINE_INFO, document.getCurrentPageNumber());
		} else {
			cursorInfo = LINE_INFO;
		}

		infoLabel.setText(labelText + cursorInfo);
		LOGGER.info("line {}: {}", currentLineNumber, getCurrentLineText(currentLineNumber));
	}

	public void resetReading() {
		LOGGER.info("Reset reading ...");
		addWelcomePane();
		resetState();
		repaint();
	}

	public String[] THE_ARTICLES = { "the", "an", "a" };
	public List<String> ARTICLES = Arrays.asList(THE_ARTICLES);

	private boolean skipArticle;

	public void skipArticle(boolean skipArticle) {
		this.skipArticle = skipArticle;
	}

	public void nextWord() throws BadLocationException {
		String wordToRead = getNextWord();

		if (wordToRead == null) {
			doneReading = true;
			return;
		}
		wordToRead = wordToRead.trim();

		if (skipArticle && ARTICLES.contains(wordToRead.toLowerCase())) {
			wordToRead = getNextWord();
		}

		LOGGER.info("wordtoread: {}", wordToRead);
		if (wordToRead != null) {
			if (wordToRead.isEmpty()) {
				return;
			}

			highlightedWord = highlight(wordToRead, readingTextManager.getCurrentCaret(), Color.PINK, highlightedWord);
			textArea.requestFocus();
			displayingWordLabel.setText(wordToRead);
			displayReadingInformation();
		} else {
			displayReadingInformation();
			doneReading = true;
		}
		repaint();
	}

	public void displayText() {
		currentTextAreaByLines = Arrays.asList(readingTextManager.getReadingText().split("\n"));
		textArea.setText(readingTextManager.getReadingText());
	}

	private Object highlight(String wordToRead, int caret, Color color, Object highlightedWord)
			throws BadLocationException {
		textArea.setCaretPosition(caret);
		Highlighter hl = textArea.getHighlighter();
		HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(color);
		int p0 = textArea.getCaretPosition();
		int p1 = p0 + wordToRead.length();
		if (highlightedWord != null) {
			hl.removeHighlight(highlightedWord);
		}
		return hl.addHighlight(p0, p1, painter);
	}

	private void unHighlight(Object highlightedWord) {
		textArea.getHighlighter().removeHighlight(highlightedWord);
	}

	private void displayReadingInformation() {
		int wordsFromBeginning = readingTextManager.getWordsFromBeginning();
		int totalWords = readingTextManager.getTotalWords();
		int readingPercentage = 0;

		if (totalWords != 0) {
			readingPercentage = (100 * wordsFromBeginning) / totalWords;
		}

		String docInfo = "";
		if (document != null) {
			docInfo = String.format("Page %d of %d", document.getCurrentPageNumber(), document.getPageCount());
		}

		infoLabel.setText(
				String.format("%s: %d of %d words (%d%%)", docInfo, wordsFromBeginning, totalWords, readingPercentage));
		infoLabel.setForeground(Color.BLUE);
	}

	private void displaySearchResult() {
		infoLabel.setText(String.format("found %d '%s's", searchResult.getNumberMatchedWords(), searchText));
		infoLabel.setForeground(Color.BLUE);
	}

	private String getNextWord() {
		return readingTextManager.getNextWord();
	}

	public void setReaderListener(ReaderListener readerListener) {
		this.readerListener = readerListener;
	}

	public void stopReading() {
		textArea.setCaret(new DefaultCaret());
		textArea.getCaret().setBlinkRate(defaultBlinkRate);
		textArea.setCaretPosition(readingTextManager.getCurrentCaret());
		textArea.requestFocus();
		this.doneReading = false;
	}

	public void startReading() {
		if (document == null) {
			return;
		}

		textArea.setCaret(new FancyCaret());

		if (document != null && readingTextManager == null) {
			displayPageText(document.getCurrentPage());
		}

		if (readingTextManager != null) {
			textArea.setCaretPosition(readingTextManager.getCurrentCaret());
		} else {
			textArea.setCaretPosition(0);
		}

		doneReading = false;
		textArea.requestFocus();
	}

	public void setCurrentCaretAt() {
		if (readingTextManager != null) {
			readingTextManager.setCurrentCaret(textArea.getCaretPosition());
		}
	}

	public void search(String searchText) {
		if (readingTextManager == null) {
			return;
		}

		for (Object highlightedWord : highlightedWords) {
			unHighlight(highlightedWord);
		}

		this.searchText = searchText;
		this.searchResult = readingTextManager.search(searchText);
		Hashtable<Integer, String> matchedWords = searchResult.getMatchedWords();
		highlightedWords = new ArrayList<Object>();

		for (Integer idx : matchedWords.keySet()) {
			try {
				LOGGER.debug("highlighted at /{}/", matchedWords.get(idx));
				highlightedWords.add(highlight(matchedWords.get(idx), idx, Color.GREEN, null));
			} catch (BadLocationException e) {
				LOGGER.error("Bad location exception: {}", e);
			}
		}
		displaySearchResult();
		referencesFrame.setText(searchText);
		definitionFrame.setDefinition(searchText);
	}

	public void setInfo(String info) {

		if (readingTextManager != null) {
			infoLabel.setText(info);
			infoLabel.setForeground(Color.BLUE);
			textArea.setCaretPosition(readingTextManager.getCurrentCaret());
			textArea.requestFocus();
		} else {
			infoLabel.setText("Start reading first!");
			infoLabel.setForeground(Color.RED);
		}
		repaint();
	}

	public void startReadingAt() {
		if (document != null && readingTextManager != null) {
			readingTextManager.setCurrentCaret(selectedWord.getIndexOfText());
			startReading();
		}
	}

	public void loadTextFromFile(Document document) {
		this.document = document;
		setBorder((String) document.getBookFileName());
		addTextPane();
		displayPageText(document.getCurrentPage());
	}

	public void previousPage() {
		if (document != null) {
			Page page = document.previousPage();
			if (page != null) {
				displayPageText(page);
			}
		}
	}

	public void nextPage() {
		if (document != null) {
			Page page = document.nextPage();
			if (page != null) {
				displayPageText(page);
			}
		}
	}

	public void displayPageText(Page page) {
		readingTextManager = page.getRtm();

		readingText = readingTextManager.getReadingText();

		if (emptyReadingText()) {
			textArea.setText("*** PAGE IS EMPTY! ***");
		} else {
			displayText();
		}

		textArea.setCaretPosition(0);
		displayReadingInformation();

		if (!Strings.isNullOrEmpty(searchText)) {
			search(searchText);
		}
		repaint();
	}

	private boolean emptyReadingText() {
		for (int idx = 0; idx < readingText.length(); idx++) {
			if (Character.isAlphabetic(readingText.charAt(idx)) || Character.isDigit(readingText.charAt(idx))) {
				return false;
			}
		}
		return true;
	}

	public void goTo(int gotoPageNo) {
		if (document != null) {
			document.setPageNo(gotoPageNo);
			displayPageText(document.getCurrentPage());
		}
	}

	public void setLargerTextFont() {
		if (textAreaFontSize < LARGEST_TEXT_AREA_FONT_SIZE) {
			this.textAreaFontSize++;
		}
		textArea.setFont(new Font(textAreaFontName, Font.PLAIN, textAreaFontSize));
		repaint();
	}

	public void setSmallerTextFont() {
		if (textAreaFontSize > SMALLEST_TEXT_AREA_FONT_SIZE) {
			this.textAreaFontSize--;
		}
		textArea.setFont(new Font(textAreaFontName, Font.PLAIN, textAreaFontSize));
		repaint();
	}

	public void setLargerWordFont() {
		if (displayingWordFontSize < LARGEST_DISPLAYING_FONT_SIZE) {
			this.displayingWordFontSize++;
		}
		displayingWordLabel.setFont(new Font(displayingFontName, Font.PLAIN, displayingWordFontSize));
		repaint();
	}

	public void setSmallerWordFont() {
		if (displayingWordFontSize > SMALLEST_DISPLAYING_FONT_SIZE) {
			this.displayingWordFontSize--;
		}
		displayingWordLabel.setFont(new Font(displayingFontName, Font.PLAIN, displayingWordFontSize));
		repaint();
	}

	public void setInfoPanel(InfoPanel infoPanel) {
		this.infoPanel = infoPanel;
	}

	public void setMainFrame(MainFrame mainFrame) {
	}

	public void setRefBook(String refName) {
		referencesFrame.setRefBook(refName);
	}
}
