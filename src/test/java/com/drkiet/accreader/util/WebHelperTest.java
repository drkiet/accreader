package com.drkiet.accreader.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.util.List;

import org.junit.Test;

public class WebHelperTest {

	@Test
	public void shouldGetAllAccountTerms() throws Exception {
		List<String> terms = WebHelper.getNysscpaTerms();
		terms.stream().forEach(definedTerm -> System.out.println(definedTerm));
		assertThat(terms.size(), greaterThan(0));
	}

	@Test
	public void shouldGetAccountWords() {
		List<String> terms = WebHelper.getAccountingToolsTerms();
		terms.stream().forEach(definedTerm -> System.out.println(definedTerm));
		assertThat(terms.size(), greaterThan(0));
	}

	@Test
	public void shouldGetAccountingCoachTerms() {
		List<String> terms = WebHelper.getAccountingCoachTerms();
		terms.stream().forEach(definedTerm -> System.out.println(definedTerm));
		assertThat(terms.size(), greaterThan(0));
	}
}
