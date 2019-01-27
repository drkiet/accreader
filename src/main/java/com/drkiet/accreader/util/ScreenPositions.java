package com.drkiet.accreader.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

public class ScreenPositions {
	private static final Dimension SCREENSIZE = Toolkit.getDefaultToolkit().getScreenSize();

	public static Point getTopEast(int width, int height) {
		return new Point(SCREENSIZE.width - width, 0);
	}

	public static Point getBottomEast(int width, int height) {
		return new Point(SCREENSIZE.width - width, SCREENSIZE.height - height);
	}

	public static Point getCenterEast(int width, int height) {
		return new Point(SCREENSIZE.width - width, (SCREENSIZE.height / 2) - height / 2);
	}

	public static Point getTopWest(int width, int height) {
		return new Point(0, 0);
	}

	public static Point getCenterWest(int width, int height) {
		return new Point(0, (SCREENSIZE.height / 2) - height / 2);
	}

	public static Point getBottomWest(int width, int height) {
		return new Point(0, SCREENSIZE.height - height);
	}

}
