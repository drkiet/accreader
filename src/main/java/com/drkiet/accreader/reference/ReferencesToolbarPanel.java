package com.drkiet.accreader.reference;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.drkiet.accreader.reference.ReaderListener.Command;

public class ReferencesToolbarPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -6940468986941461194L;
	private JButton largerTextFontButton;
	private JButton smallerTextFontButton;
	private ReaderListener readerListener;

	private ImageIcon createIcon(String path) {
		URL url = getClass().getResource(path);

		if (url == null) {
			System.err.println("Unable to load image: " + path);
		}
		return new ImageIcon(url);
	}

	public ReferencesToolbarPanel() {
		setBorder(BorderFactory.createEtchedBorder());
		largerTextFontButton = new JButton();
		largerTextFontButton.setIcon(createIcon("/icons/Up16.gif"));
		largerTextFontButton.setToolTipText("Larger displaying text size");

		smallerTextFontButton = new JButton();
		smallerTextFontButton.setIcon(createIcon("/icons/Down16.gif"));
		smallerTextFontButton.setToolTipText("Smaller display text size");

		largerTextFontButton.addActionListener(this);
		smallerTextFontButton.addActionListener(this);

		add(largerTextFontButton);
		add(smallerTextFontButton);
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
			}
		}
	}
}
