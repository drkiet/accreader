package com.drkiet.accreader.reference;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drkiet.accreader.definition.DefinitionFrame;
import com.drkiet.accreader.reader.InfoPanel;
import com.drkiet.accreader.reference.ReaderListener.Command;
import com.drkiet.accreader.util.FileHelper;
import com.drkiet.accreader.util.ScreenPositions;
import com.drkiettran.text.model.Document;

public class ReferencesFrame extends JFrame {

	private static final long serialVersionUID = 4689102811730742079L;
	private ReferencesPanel referencesPanel;
	private ReferencesToolbarPanel referencesToolbarPanel;
	private DefinitionFrame definitionFrame;
	private Document document;
	private InfoPanel infoPanel;

	public ReferencesFrame() {
		setLayout(new BorderLayout());
		setTitle("Refereces file: " + FileHelper.getFQRefencesFileName());
		setSize(600, 500);
		referencesPanel = new ReferencesPanel();
		referencesToolbarPanel = new ReferencesToolbarPanel();

		referencesToolbarPanel.setReaderListener((Command cmd) -> {
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

		add(referencesToolbarPanel, BorderLayout.NORTH);
		add(referencesPanel, BorderLayout.CENTER);
		setLocation(ScreenPositions.getTopEast((int) getSize().getWidth(), (int) getSize().getHeight()));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ReferencesFrame.class);

	private void makeSmallerFont() {
		referencesPanel.setSmallerText();
	}

	private void makeLargerFont() {
		referencesPanel.setLargerText();
	}

	public void setText(String referenceText) {
		referencesPanel.setText(referenceText);
	}

	public void setDefinitionFrame(DefinitionFrame definitionFrame) {
		this.definitionFrame = definitionFrame;
		referencesPanel.setDefinitionFrame(definitionFrame);
	}

	public void setInfoPanel(InfoPanel infoPanel) {
		this.infoPanel = infoPanel;
	}
}
