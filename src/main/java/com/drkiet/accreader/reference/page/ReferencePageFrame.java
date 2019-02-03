package com.drkiet.accreader.reference.page;

import java.awt.BorderLayout;
import java.util.HashMap;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drkiet.accreader.definition.DefinitionFrame;
import com.drkiet.accreader.reference.ReaderListener.Command;
import com.drkiet.accreader.reference.ReferencesFrame;
import com.drkiet.accreader.util.FileHelper;
import com.drkiet.accreader.util.ScreenPositions;

public class ReferencePageFrame extends JFrame {

	private static final long serialVersionUID = 4689102811730742079L;
	private ReferencePagePanel referencePagePanel;
	private ReferencePageToolbarPanel referencePageToolbarPanel;

	public ReferencePageFrame(String refName) {
		setLayout(new BorderLayout());
		setTitle("Refereces file: " + FileHelper.getFQRefencesFileName(refName));
		setSize(600, 500);
		referencePagePanel = new ReferencePagePanel(FileHelper.getFQRefencesFileName(refName));
		referencePageToolbarPanel = new ReferencePageToolbarPanel();

		referencePageToolbarPanel.setReaderListener((Command cmd) -> {
			switch (cmd) {

			case LARGER_TEXT_FONT:
				makeLargerFont();
				break;

			case SMALLER_TEXT_FONT:
				makeSmallerFont();
				break;

			case NEXT_PAGE:
				nextPage();
				break;

			case PREVIOUS_PAGE:
				previousPage();
				break;

			case NEXT_FIND:
				nextFindPage();
				break;

			case PREVIOUS_FIND:
				previousFindPage();
				break;
			
			case NEXT_EXACT_MATCH:
				nextExactMatchPage();
				break;
				
			case PREVIOUS_EXACT_MATCH:
				prevExactMatchPage();
				break;
				
			default:
				break;
			}
		});

		add(referencePageToolbarPanel, BorderLayout.NORTH);
		add(referencePagePanel, BorderLayout.CENTER);
		setLocation(ScreenPositions.getCenterEast((int) getSize().getWidth(), (int) getSize().getHeight()));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private void prevExactMatchPage() {
		referencePagePanel.previousExactMatchPage();
		
	}

	private void nextExactMatchPage() {
		referencePagePanel.nextExactMatchPage();	
	}

	private void previousFindPage() {
		referencePagePanel.previousFindPage();
	}

	private void nextFindPage() {
		referencePagePanel.nextFindPage();
	}

	private void previousPage() {
		referencePagePanel.previousPage();
	}

	private void nextPage() {
		referencePagePanel.nextPage();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ReferencePageFrame.class);

	private void makeSmallerFont() {
		referencePagePanel.setSmallerText();
	}

	private void makeLargerFont() {
		referencePagePanel.setLargerText();
	}

	public void setPageNumber(Integer pageNumber, String searchText) {
		referencePagePanel.setPageNumber(pageNumber, searchText);
	}

	public void setDefinitionFrame(DefinitionFrame definitionFrame) {
		referencePagePanel.setDefinitionFrame(definitionFrame);
	}

	public void setReferencesFrame(ReferencesFrame referencesFrame) {
		referencePagePanel.setReferencesFrame(referencesFrame);
	}

	public void setRefBook(String refName) {
		referencePagePanel.setRefName(refName);
	}

	public boolean exactMatchFoundOnPage(Integer pageNumber, String searchText) {
		return referencePagePanel.exactMatchFoundOnPage(pageNumber, searchText);
	}

	public void setFoundPageMap(HashMap<Integer, Integer> pageNumbersMap) {
		referencePagePanel.setFoundPageMap(pageNumbersMap);
	}
}
