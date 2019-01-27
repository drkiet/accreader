package com.drkiet.accreader.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.util.List;

import org.junit.Test;

public class FileHelperTest {
	@Test
	public void shouldGetTermsFromHtmlFile() {
		List<String> definedTerms = FileHelper.getTermsFromHtml();
		assertThat(definedTerms.size(), greaterThan(0));
	}
}
