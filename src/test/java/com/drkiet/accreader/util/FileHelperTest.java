package com.drkiet.accreader.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class FileHelperTest {
	@Test
	public void shouldGetTermsFromHtmlFile() {
		List<String> definedTerms = FileHelper.getTermsFromHtml();
		assertThat(definedTerms.size(), greaterThan(0));
	}

	@Test
	public void shouldLoadDictionaryByFileName() throws IOException {
		HashMap<String, String> dict = FileHelper.loadDictionary("c:/book-catalog/accreader/",
				"terms-accounting-coach.dic");
		Iterator<String> it = dict.keySet().iterator();
		System.out.println(String.format("dictionary has %d terms", dict.size()));

		while (it.hasNext()) {
			String key = it.next();
			String value = dict.get(key);
			System.out.println(String.format("%s: %s", key, value));
		}
		assertThat(dict.size(), greaterThan(0));
	}
}
