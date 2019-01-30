package com.drkiet.accreader.reference.page;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drkiet.accreader.definition.DefinitionFrame;
import com.drkiet.accreader.reader.InfoPanel;
import com.drkiet.accreader.reference.ReaderListener.Command;
import com.drkiet.accreader.reference.ReferencesFrame;
import com.drkiet.accreader.util.FileHelper;
import com.drkiet.accreader.util.ScreenPositions;

public class ReferencePageFrame extends JFrame {

	private static final long serialVersionUID = 4689102811730742079L;
	private ReferencePagePanel referencePagePanel;
	private ReferencePageToolbarPanel referencePageToolbarPanel;
	private DefinitionFrame definitionFrame;
	private InfoPanel infoPanel;
	private ReferencesFrame referencesFrame;
	private String refName = "";

	public ReferencePageFrame(String refName) {
		this.refName = refName;
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
		this.definitionFrame = definitionFrame;
		referencePagePanel.setDefinitionFrame(definitionFrame);
	}

	public void setInfoPanel(InfoPanel infoPanel) {
		this.infoPanel = infoPanel;
	}

	public void setReferencesFrame(ReferencesFrame referencesFrame) {
		this.referencesFrame = referencesFrame;
		referencePagePanel.setReferencesFrame(referencesFrame);
	}

	public void setRefBook(String refName) {
		this.refName = refName;
		referencePagePanel.setRefName(refName);
	}
}
