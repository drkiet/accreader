package com.drkiet.accreader.definition;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import com.drkiet.accreader.reader.InfoPanel;
import com.drkiet.accreader.reference.ReaderListener.Command;
import com.drkiet.accreader.reference.ReferencesFrame;
import com.drkiet.accreader.util.ScreenPositions;

public class DefinitionFrame extends JFrame {

	private static final long serialVersionUID = 4689102811730742079L;
	private DefinitionPanel definitionPanel;
	private DefinitionToolbarPanel definitionToolbarPanel;
	private String word = "";

	public DefinitionFrame() {
		setLayout(new BorderLayout());
		setTitle("Definitions");
		setSize(600, 500);
		definitionPanel = new DefinitionPanel();
		definitionToolbarPanel = new DefinitionToolbarPanel();

		definitionToolbarPanel.setReaderListener((Command cmd) -> {
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

		add(definitionToolbarPanel, BorderLayout.NORTH);
		add(definitionPanel, BorderLayout.CENTER);
		setLocation(ScreenPositions.getBottomEast((int) getSize().getWidth(), (int) getSize().getHeight()));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private void makeSmallerFont() {
		definitionPanel.setSmallerText();
	}

	private void makeLargerFont() {
		definitionPanel.setLargerText();
	}

	public void setDefinition(String word) {
		if (this.word.equalsIgnoreCase(word)) {
			return;
		}

		this.word = word;
		definitionPanel.setDefinition(word.toLowerCase());
	}

	public void setReferencesFrame(ReferencesFrame referencesFrame) {
		definitionPanel.setReferencesFrame(referencesFrame);
	}

	public void setInfoPanel(InfoPanel infoPanel) {
	}
}
