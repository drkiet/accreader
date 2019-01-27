package com.drkiet.accreader;

import java.io.IOException;

import javax.swing.SwingUtilities;

import com.drkiet.accreader.reader.MainFrame;

/**
 * Accounting Reader - branched off from the Speed Reader project I developed
 *
 */
public class AccountingReader {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame mainFrame = new MainFrame();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
