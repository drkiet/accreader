package com.drkiet.accreader.reader;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drkiet.accreader.reference.ReaderListener;
import com.drkiet.accreader.reference.ReaderListener.Command;
import com.drkiet.accreader.util.FileHelper;
import com.drkiettran.text.TextApp;
import com.drkiettran.text.model.Document;

/**
 * 
 * @author ktran
 *
 */
public class FormPanel extends JPanel {
	private static final Logger LOGGER = LoggerFactory.getLogger(FormPanel.class);

	private static final long serialVersionUID = 3506596135223108382L;
	private JLabel bookNameLabel;
	private JComboBox<String> bookNameComboBox;
	private JButton openButton;
	private JLabel speedLabel;
	private JLabel skipArticleLabel;
	private JCheckBox skipArticleCheckBox;
	private JTextField speedField;
	private JLabel searchTextLabel;
	private JTextField searchTextField;
	private JButton searchButton;
	private JButton setButton;
	private Integer speedWpm = 200;
	private String bookName;
	private String refName;
	private String text;
	private ReaderListener readerListener;
	private Document document = null;
	private String loadingError = "";
	private JButton goToPageNoButton;
	private JTextField pageNoTextField;
	private JLabel pageNoTextLabel;
	private JButton nextFindButton;
	private JComboBox<String> translationComboBox;
	private JLabel refNameLabel;
	private JComboBox<String> refNameComboBox;
	private JButton loadButton = null;

	public String getLoadingError() {
		return loadingError;
	}

	public Document getDocument() {
		return document;
	}

	public String getText() {
		return text;
	}

	public Integer getSpeedWpm() {
		return speedWpm;
	}

	public FormPanel() {
		Dimension dim = getPreferredSize();
		dim.width = 250;
		setPreferredSize(dim);
		makeComboBoxes();
		makeComponents();

		setListeners();

		Border innerBorder = BorderFactory.createTitledBorder("Settings");
		Border outterBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outterBorder, innerBorder));

		layoutComponents();
	}

	public void setListeners() {
		setButton.addActionListener((ActionEvent actionEvent) -> {
			speedWpm = Integer.valueOf(speedField.getText());
		});

		openButton.addActionListener((ActionEvent actionEvent) -> {
			bookName = (String) bookNameComboBox.getSelectedItem();

			TextApp textApp = new TextApp();

			document = textApp.getPages(FileHelper.getFQFileName(bookName));
			document.setBookFileName(bookName);
			if (document == null) {
				loadingError = "Unable to open " + bookName;
			} else {
				LOGGER.info("{} has {} pages", bookName, document.getPageCount());
				loadingError = "";
			}
			readerListener.invoke(Command.LOAD);
		});

		loadButton.addActionListener((ActionEvent actionEvent) -> {
			refName = (String) refNameComboBox.getSelectedItem();
			readerListener.invoke(Command.LOAD_REF);
		});

		searchButton.addActionListener((ActionEvent actionEvent) -> {
			readerListener.invoke(Command.SEARCH);
		});

		nextFindButton.addActionListener((ActionEvent actionEvent) -> {
			readerListener.invoke(Command.NEXT_FIND);
		});

		goToPageNoButton.addActionListener((ActionEvent actionEvent) -> {
			readerListener.invoke(Command.GOTO);
		});

		bookNameComboBox.addActionListener((ActionEvent actionEvent) -> {
			readerListener.invoke(Command.SELECT_BOOK);
		});
	}

	public void makeComponents() {
		speedLabel = new JLabel("Speed (wpm): ");
		speedField = new JTextField(10);
		searchTextLabel = new JLabel("Text");
		searchTextField = new JTextField(10);
		pageNoTextLabel = new JLabel("Page No.");
		pageNoTextField = new JTextField(10);
		speedField.setText("" + speedWpm);
		skipArticleLabel = new JLabel("Skip articles: ");

		skipArticleCheckBox = new JCheckBox();
		setButton = new JButton("Set");
		openButton = new JButton("Open");
		loadButton = new JButton("Load Ref.");
		searchButton = new JButton("Search");
		nextFindButton = new JButton("Next");
		goToPageNoButton = new JButton("Go to");
	}

	public void makeComboBoxes() {
		bookNameLabel = new JLabel("Select a book: ");
		bookNameComboBox = new JComboBox<String>();
		bookNameComboBox.setPrototypeDisplayValue("default text here");

		List<String> fileNames = getListOfFileNames();

		for (String fileName : fileNames) {
			bookNameComboBox.addItem(fileName);
		}

		ComboboxToolTipRenderer renderer = new ComboboxToolTipRenderer();
		bookNameComboBox.setRenderer(renderer);
		renderer.setTooltips(fileNames);

		// Reference books.

		refNameLabel = new JLabel("Ref. book: ");
		refNameComboBox = new JComboBox<String>();
		refNameComboBox.setPrototypeDisplayValue("default text here");

		for (String fileName : fileNames) {
			refNameComboBox.addItem(fileName);
		}

		renderer = new ComboboxToolTipRenderer();
		refNameComboBox.setRenderer(renderer);
		renderer.setTooltips(fileNames);
	}

	private List<String> getListOfFileNames() {
		return FileHelper.getFileNames(FileHelper.getAccReaderFolder());
	}

	public String getBookName() {
		return bookName;
	}

	private void layoutComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();

		//// FIRST ROW /////////////
		gc.gridy = 0;

		// Always do the following to avoid future confusion :)
		// Speed
		gc.weightx = 1;
		gc.weighty = 0.1;

		gc.gridx = 0;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(speedLabel, gc);

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.LINE_START;
		add(speedField, gc);

		//// next row /////////////
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = .2;

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(setButton, gc);

		// Always do the following to avoid future confusion :)
		// Skip Articles
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 0.1;

		gc.gridx = 0;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 0);
		add(skipArticleLabel, gc);

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.LINE_START;
		add(skipArticleCheckBox, gc);

		// Always do the following to avoid future confusion :)
		// Book Name:
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 0.1;

		gc.gridx = 0;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(bookNameLabel, gc);

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.LINE_START;
		add(bookNameComboBox, gc);

		//// next row /////////////
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = .2;

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(openButton, gc);

		// Always do the following to avoid future confusion :)
		// Reference Book Name:
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 0.1;

		gc.gridx = 0;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(refNameLabel, gc);

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.LINE_START;
		add(refNameComboBox, gc);

		//// next row /////////////
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = .2;

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(loadButton, gc);

		// Always do the following to avoid future confusion :)
		// Search Text:
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 0.1;

		gc.gridx = 0;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(searchTextLabel, gc);

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.LINE_START;
		add(searchTextField, gc);

		//// next row /////////////
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = .2; // 5;

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(searchButton, gc);

		//// next row /////////////
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = .2;

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(nextFindButton, gc);

		// Always do the following to avoid future confusion :)
		// Goto page:
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 0.1;

		gc.gridx = 0;
		gc.anchor = GridBagConstraints.LINE_END;
		gc.insets = new Insets(0, 0, 0, 5);
		add(pageNoTextLabel, gc);

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.LINE_START;
		add(pageNoTextField, gc);

		//// next row /////////////
		gc.gridy++;
		gc.weightx = 1;
		gc.weighty = 5;

		gc.gridx = 1;
		gc.insets = new Insets(0, 0, 0, 0);
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(goToPageNoButton, gc);

		disableSearch();
		disableGoto();
	}

	public void disableGoto() {
		pageNoTextField.setEnabled(false);
		goToPageNoButton.setEnabled(false);
	}

	public void enableGoto() {
		pageNoTextField.setEnabled(true);
		goToPageNoButton.setEnabled(true);
	}

	public void setReaderListener(ReaderListener readerListener) {
		this.readerListener = readerListener;
	}

	public void setFileName(String selectedFile) {
		bookNameComboBox.setSelectedItem(selectedFile);
	}

	public String getSearchText() {
		return searchTextField.getText();
	}

	public void enableSearch() {
		searchButton.setEnabled(true);
		nextFindButton.setEnabled(true);
		searchTextField.setEnabled(true);
	}

	public void disableSearch() {
		searchButton.setEnabled(false);
		nextFindButton.setEnabled(false);
		searchTextField.setEnabled(false);
	}

	public int getGotoPageNo() {
		if (!pageNoTextField.getText().trim().isEmpty()) {
			return Integer.valueOf(pageNoTextField.getText());
		}
		return -1;
	}

	public String getSelectedBookName() {
		return (String) bookNameComboBox.getSelectedItem();
	}

	public String getselectedTranslation() {
		return (String) translationComboBox.getSelectedItem();
	}

	public boolean skipArticle() {
		LOGGER.info("skip articles ... {}", skipArticleCheckBox.isSelected());
		return skipArticleCheckBox.isSelected();
	}

	public String getRefBook() {
		return refName;
	}
}
