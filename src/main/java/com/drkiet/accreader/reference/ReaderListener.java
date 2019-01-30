package com.drkiet.accreader.reference;

public interface ReaderListener {
	public enum Command {
		START, STOP, RESET, RESTART, LOAD, LOAD_REF, START_AT, SMALLER_TEXT_FONT, LARGER_TEXT_FONT, PREVIOUS_PAGE, NEXT_PAGE,
		SMALLER_WORD_FONT, LARGER_WORD_FONT, HELP_PICTURE, BROWSE, SEARCH, GOTO, NEXTFIND, SELECT_BOOK,
		SELECT_TRANSLATION
	};

	void invoke(Command cmd);
}
