package com.drkiet.accreader.util;

import org.junit.Test;
import static org.hamcrest.Matchers.greaterThan;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class WebHelperTest {

	@Test
	public void shouldGetAllAccountTerms() throws Exception {
		List<String> terms = WebHelper.getAccountingTerms();
		terms.stream().forEach(definedTerm -> System.out.println(definedTerm));
		assertThat(terms.size(), greaterThan(0));
	}
}
