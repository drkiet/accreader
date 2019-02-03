package com.drkiet.accreader.reference;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import com.drkiet.accreader.definition.DefinitionFrame;
import com.drkiet.accreader.reference.ReaderListener.Command;
import com.drkiet.accreader.util.FileHelper;
import com.drkiet.accreader.util.ScreenPositions;

public class ReferencesFrame extends JFrame {

	private static final long serialVersionUID = 4689102811730742079L;
	private ReferencesPanel referencesPanel;
	private ReferencesToolbarPanel referencesToolbarPanel;
	private String refName = "";
	private String referenceText = "";

	public ReferencesFrame() {
		setLayout(new BorderLayout());
		setTitle("Refereces file: " + FileHelper.getFQRefencesFileName(refName));
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

	private void makeSmallerFont() {
		referencesPanel.setSmallerText();
	}

	private void makeLargerFont() {
		referencesPanel.setLargerText();
	}

	public void setText(String referenceText) {
		if (this.referenceText.equalsIgnoreCase(referenceText)) {
			return;
		}

		this.referenceText = referenceText;
		referencesPanel.setText(referenceText);
	}

	public void setDefinitionFrame(DefinitionFrame definitionFrame) {
		referencesPanel.setDefinitionFrame(definitionFrame);
		referencesPanel.setReferencesFrame(this);
	}

	public void setRefBook(String refName) {
		this.refName = refName;
		this.referenceText = "";
		referencesPanel.setRefBook(refName);
	}
}
