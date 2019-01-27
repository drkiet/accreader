package com.drkiet.accreader.reader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.Timer;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drkiet.accreader.help.HelpFrame;
import com.drkiet.accreader.reference.ReaderListener.Command;
import com.drkiet.accreader.util.FileHelper;
import com.drkiettran.text.model.Document;
import com.drkiettran.text.model.SearchResult;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = -5184507871687024902L;
	private static final Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);

	private TextPanel textPanel = new TextPanel();
	private Toolbar toolbar = new Toolbar();
	private FormPanel formPanel = new FormPanel();
	private JFileChooser fileChooser = new JFileChooser();
	private InfoPanel infoPanel = new InfoPanel();
	private JMenuBar menubar = new JMenuBar();

	private TextTimerTask textTimerTask = null;
	private Timer timer = null;

	private String bookName = null;
	private Document document = null;
	private List<SearchResult> searchResults = null;
	private String searchText = null;
	private String selectedBookName = null;
	private String selectedTranslation = null;

	public MainFrame() throws IOException {
		super("Accounting Reader");
		infoPanel.setFrame(this);
		textPanel.setInfoPanel(infoPanel);
		manageMenubar();
		manageListeners();
		manageLayout();
	}

	private void manageMenubar() {
		JMenu menu = new JMenu("File");
		JMenuItem menuItem = new JMenuItem("Exit");
		menuItem.addActionListener((event) -> System.exit(0));
		menu.add(menuItem);
		menubar.add(menu);

		menu = new JMenu("Help");
		menuItem = new JMenuItem("About");
		menuItem.addActionListener((event) -> displayAbout(event));
		menu.add(menuItem);
		menubar.add(menu);
		setJMenuBar(menubar);

	}

	private void displayAbout(ActionEvent event) {
		JTextPane textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setForeground(Color.BLACK);
		textPane.setBackground(Color.WHITE);

		UIManager.put("OptionPane.background", Color.white);
		UIManager.put("Panel.background", Color.white);

		textPane.setText(FileHelper.loadTextFileIntoString("/About.html"));
		JOptionPane.showMessageDialog(this, textPane, "About Accounting Reader", JOptionPane.INFORMATION_MESSAGE);
	}

	public void manageLayout() {
		setLayout(new BorderLayout());
		add(formPanel, BorderLayout.WEST);
		add(toolbar, BorderLayout.NORTH);
		add(textPanel, BorderLayout.CENTER);
		add(infoPanel, BorderLayout.SOUTH);

		setSize(800, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void manageListeners() {
		formPanel.setReaderListener((Command cmd) -> {
			processForm(cmd);
		});

		textPanel.setReaderListener((Command cmd) -> {
			processText(cmd);
		});

		textPanel.setMainFrame(this);
		textPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				processMouseClickedInTextPanel();
			}

		});

		toolbar.setReaderListener((Command cmd) -> {
			processButtonsOnToolbar(cmd);
		});
	}

	public void processButtonsOnToolbar(Command cmd) {
		switch (cmd) {
		case START_AT:
			startReadingAtCaret();
			// *** LET IT FALL THROUGH ***

		case START:
			startReading();
			textPanel.skipArticle(formPanel.skipArticle());
			if (!textPanel.isDoneReading()) {
				formPanel.enableSearch();
				formPanel.enableGoto();
			}
			break;

		case RESET:
			resetReading();
			stopReading();
			formPanel.disableSearch();
			formPanel.disableGoto();
			break;

		case STOP:
			stopReading();
			break;

		case LARGER_TEXT_FONT:
			makeLargerFont();
			break;

		case SMALLER_TEXT_FONT:
			makeSmallerFont();
			break;

		case PREVIOUS_PAGE:
			previousPage();
			break;

		case NEXT_PAGE:
			nextPage();
			break;

		case LARGER_WORD_FONT:
			textPanel.setLargerWordFont();
			break;

		case SMALLER_WORD_FONT:
			textPanel.setSmallerWordFont();
			break;

		case HELP_PICTURE:
			new HelpFrame();
			break;

		default:
			break;
		}
	}

	private void processMouseClickedInTextPanel() {
		if (isReading()) {
			stopReading();
		} else {
			startReading();
		}
	}

	private void processText(Command cmd) {
		switch (cmd) {
		case RESET:
			resetReading();
			break;
		default:
			break;
		}
	}

	private void processForm(Command cmd) {
		switch (cmd) {
		case LOAD:
			loadSelectedBook();
			break;
		case SELECT_BOOK:
			selectedBookName = formPanel.getSelectedBookName();
			LOGGER.info("Selected book {}", selectedBookName);
			break;
		case SELECT_TRANSLATION:
			selectedTranslation = formPanel.getselectedTranslation();
			LOGGER.info("Selected translation {}", selectedTranslation);
		case SEARCH:
			searchText = formPanel.getSearchText();
			searchText(searchText);
			searchTextInDocument(searchText);
			break;
		case NEXTFIND:
			nextFind();
			break;
		case GOTO:
			goToPage(formPanel.getGotoPageNo());
		default:
			break;
		}
	}

	private void nextFind() {
		if (searchResults == null || searchResults.size() == 0) {
			return;
		}

		for (int idx = document.getCurrentPageNumber(); idx < document.getPageCount(); idx++) {
			if (searchResults.get(idx).getNumberMatchedWords() > 0) {
				textPanel.goTo(idx);
				textPanel.search(searchText);
			}
		}
	}

	public void goToPage(int gotoPageNo) {
		if (gotoPageNo > 0) {
			textPanel.goTo(gotoPageNo);
		}
	}

	private void searchTextInDocument(String searchText) {
		if (document == null) {
			return;
		}

		searchResults = document.search(searchText);
		StringBuilder sb = new StringBuilder("\nSearch results: <br><br>");
		for (int idx = 0; idx < searchResults.size(); idx++) {
			SearchResult sr = searchResults.get(idx);
			if (sr.getNumberMatchedWords() > 0) {
				sb.append("page ").append(idx + 1);
				sb.append(" has ").append(sr.getNumberMatchedWords()).append(" ").append(searchText).append("<br>");
			}
		}

		infoPanel.addText(sb.toString());
	}

	private void previousPage() {
		textPanel.previousPage();
		stopReading();
	}

	private void nextPage() {
		textPanel.nextPage();
		stopReading();
	}

	public void browseDirectoryForFile() {
		if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
			formPanel.setFileName(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}

	public void loadSelectedBook() {
		document = formPanel.getDocument();
		bookName = formPanel.getSelectedBookName();
		infoPanel.setBookName(bookName);
		StringBuilder sb = new StringBuilder();

		if (document != null) {
			textPanel.loadTextFromFile(document);
			formPanel.enableSearch();
			formPanel.enableGoto();

			// @formatter:off
			sb.append('\n')
			  .append(bookName)
			  .append(" is loaded successfully!\n")
			  .append("This book has ")
			  .append(document.getPageCount())
			  .append(" pages.\n");
			// @formatter:on
			infoPanel.addText(sb.toString());

		} else {
			textPanel.resetReading();
			sb.append('\n').append(bookName).append(" fails to load!\n");
			infoPanel.addText(sb.toString());
		}
	}

	public void startReadingAtCaret() {
		textPanel.startReadingAt();
	}

	public void makeSmallerFont() {
		textPanel.setSmallerTextFont();
	}

	public void makeLargerFont() {
		textPanel.setLargerTextFont();
	}

	public void resetReading() {
		document = null;
		textPanel.resetReading();
	}

	public void stopReading() {
		if (isReading()) {
			timer.cancel();
			textTimerTask = null;
			timer = null;
			textPanel.stopReading();
		}
	}

	public void startReading() {
		if (!isReading() && document != null) {
			textTimerTask = new TextTimerTask();
			textTimerTask.register(textPanel);
			timer = new Timer();
			int speedWpm = formPanel.getSpeedWpm();
			timer.schedule(textTimerTask, 0, (60 * 1000) / speedWpm);
			textPanel.startReading();
		}
	}

	private boolean isReading() {
		return textTimerTask != null;
	}

	private void searchText(String searchText) {
		textPanel.search(searchText);
	}

}
