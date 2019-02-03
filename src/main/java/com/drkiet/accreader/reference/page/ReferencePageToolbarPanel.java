package com.drkiet.accreader.reference.page;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.drkiet.accreader.reference.ReaderListener;
import com.drkiet.accreader.reference.ReaderListener.Command;

public class ReferencePageToolbarPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -6940468986941461194L;
	private JButton largerTextFontButton;
	private JButton smallerTextFontButton;
	private ReaderListener readerListener;
	private JButton nextPageButton;
	private JButton prevPageButton;
	private JButton nextFoundPageButton;
	private JButton prevFoundPageButton;
	private JButton nextExactMatchPageButton;
	private JButton prevExactMatchPageButton;

	private ImageIcon createIcon(String path) {
		URL url = getClass().getResource(path);

		if (url == null) {
			System.err.println("Unable to load image: " + path);
		}
		return new ImageIcon(url);
	}

	public ReferencePageToolbarPanel() {
		setBorder(BorderFactory.createEtchedBorder());
		largerTextFontButton = new JButton();
		largerTextFontButton.setIcon(createIcon("/icons/Up16.gif"));
		largerTextFontButton.setToolTipText("Larger displaying text size");

		smallerTextFontButton = new JButton();
		smallerTextFontButton.setIcon(createIcon("/icons/Down16.gif"));
		smallerTextFontButton.setToolTipText("Smaller display text size");

		nextPageButton = new JButton();
		nextPageButton.setIcon(createIcon("/icons/Next16.gif"));
		nextPageButton.setToolTipText("Next Page");

		prevPageButton = new JButton();
		prevPageButton.setIcon(createIcon("/icons/Previous16.gif"));
		prevPageButton.setToolTipText("Previous Page");

		nextFoundPageButton = new JButton();
		nextFoundPageButton.setIcon(createIcon("/icons/FastForward16.gif"));
		nextFoundPageButton.setToolTipText("Next Page with Found Text");

		prevFoundPageButton = new JButton();
		prevFoundPageButton.setIcon(createIcon("/icons/Rewind16.gif"));
		prevFoundPageButton.setToolTipText("Previous Page with Found Text");

		nextExactMatchPageButton = new JButton();
		nextExactMatchPageButton.setIcon(createIcon("/icons/StepForward16.gif"));
		nextExactMatchPageButton.setToolTipText("Next Page with Found Text");

		prevExactMatchPageButton = new JButton();
		prevExactMatchPageButton.setIcon(createIcon("/icons/StepBack16.gif"));
		prevExactMatchPageButton.setToolTipText("Previous Page with Found Text");

		largerTextFontButton.addActionListener(this);
		smallerTextFontButton.addActionListener(this);
		nextPageButton.addActionListener(this);
		prevPageButton.addActionListener(this);
		nextFoundPageButton.addActionListener(this);
		prevFoundPageButton.addActionListener(this);
		nextExactMatchPageButton.addActionListener(this);
		prevExactMatchPageButton.addActionListener(this);

		add(largerTextFontButton);
		add(smallerTextFontButton);
		add(prevPageButton);
		add(nextPageButton);
		add(prevFoundPageButton);
		add(nextFoundPageButton);
		add(prevExactMatchPageButton);
		add(nextExactMatchPageButton);
	}

	public void setReaderListener(ReaderListener readerListener) {
		this.readerListener = readerListener;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JButton clicked = (JButton) event.getSource();
		if (readerListener != null) {
			if (clicked == largerTextFontButton) {
				readerListener.invoke(Command.LARGER_TEXT_FONT);
			} else if (clicked == smallerTextFontButton) {
				readerListener.invoke(Command.SMALLER_TEXT_FONT);
			} else if (clicked == nextPageButton) {
				readerListener.invoke(Command.NEXT_PAGE);
			} else if (clicked == prevPageButton) {
				readerListener.invoke(Command.PREVIOUS_PAGE);
			} else if (clicked == nextFoundPageButton) {
				readerListener.invoke(Command.NEXT_FIND);
			} else if (clicked == prevFoundPageButton) {
				readerListener.invoke(Command.PREVIOUS_FIND);
			} else if (clicked == nextExactMatchPageButton) {
				readerListener.invoke(Command.NEXT_EXACT_MATCH);
			} else if (clicked == prevExactMatchPageButton) {
				readerListener.invoke(Command.PREVIOUS_EXACT_MATCH);
			}
		}
	}
}
