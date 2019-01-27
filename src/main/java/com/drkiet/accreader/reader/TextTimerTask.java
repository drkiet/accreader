package com.drkiet.accreader.reader;

import java.util.TimerTask;

import javax.swing.text.BadLocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Establishing an asynchronous timer awaken periodically until the reading is
 * done.
 * 
 * @author Kiet T. Tran, Ph.D (c) 2019
 *
 */
public class TextTimerTask extends TimerTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(TextTimerTask.class);
	private TextPanel textPanel;

	public void register(TextPanel textPanel) {
		LOGGER.info("Reading timer starts ...");
		this.textPanel = textPanel;
	}

//	@Override
//	public boolean cancel() {
//		LOGGER.info("Reading timer is cancelled ...");
//		return true;
//	}

	@Override
	public void run() {
		try {
			textPanel.nextWord();
		} catch (BadLocationException e) {
			LOGGER.info("Bad location exception: {}", e.getMessage());
		} finally {
			if (textPanel.isDoneReading()) {
				LOGGER.info("Reading is completed!");
				cancel();
			}
		}
	}

}
