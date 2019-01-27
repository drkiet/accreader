package com.drkiet.accreader.util;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drkiettran.text.util.CommonUtils;

public class WebHelper {
	public static final String XPATH_ENTRY_CONTENT = "//div[contains(@class,'entry-content')]";
	private static final String XPATH_CONTENT = "//div[@id='content']";
	public static final String TRANSLATED_TEXT = "translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')";
	private static final Logger LOGGER = LoggerFactory.getLogger(WebHelper.class);

	private static final String XYZ_CHARS = "xyz";
	private static final String THE_NYSCPA_TERM_WEBSITE = "https://www.nysscpa.org/professional-resources/accounting-terminology-guide";
	private static final String THE_ACCOUNTING_TOOLS_DICTIONARY = "https://www.accountingtools.com/terms-%s";
	private static final String XPATH_TERM_TEXT = "//div[@id='main']/div[@class='sfContentBlock']";

	public static List<String> getAccountingTerms() throws Exception {
		WebDriver driver = new HtmlUnitDriver();
		driver.get(THE_NYSCPA_TERM_WEBSITE);
		List<WebElement> sections = driver.findElements(By.xpath(XPATH_TERM_TEXT));
		List<String> definedTerms = new ArrayList<String>();

		for (WebElement section : sections) {
			List<WebElement> terms = filterEmptyWebElements(section.findElements(By.tagName("h3")));
			List<WebElement> definitions = filterEmptyWebElements(section.findElements(By.tagName("p")));

			if (terms.size() != definitions.size()) {
				throw new Exception("*** PROBLEM ***");
			}

			for (int idx = 0; idx < terms.size(); idx++) {
				definedTerms.add(String.format("%s: %s", terms.get(idx).getText(), definitions.get(idx).getText()));
			}
		}

		return definedTerms;
	}

	private static List<WebElement> filterEmptyWebElements(List<WebElement> webElements) {
		List<WebElement> nonEmptyWebElements = new ArrayList<WebElement>();

		for (WebElement webElement : webElements) {
			if (!webElement.getText().trim().isEmpty()) {
				nonEmptyWebElements.add(webElement);
			}
		}

		return nonEmptyWebElements;
	}

	public static String getAccDefinitionForWord(String text) {
		String url = getUrl(text);
		WebDriver driver = new HtmlUnitDriver();
		String definition = makeDefinition(driver, url, text.trim());
		return definition;
	}

	public static String getUrl(String text) {
		String url;
		if (XYZ_CHARS.indexOf(text.substring(0, 1).toLowerCase()) >= 0) {
			url = String.format(WebHelper.THE_ACCOUNTING_TOOLS_DICTIONARY, XYZ_CHARS);
		} else {
			url = String.format(WebHelper.THE_ACCOUNTING_TOOLS_DICTIONARY, text.substring(0, 1).toLowerCase());
		}
		return url;
	}

	private static String makeDefinition(WebDriver driver, String url, String word) {
		driver.get(url);
		String xpath = String.format("//a[contains(%s,'%s')]", TRANSLATED_TEXT, CommonUtils.getLowerSingular(word));
		List<WebElement> as = driver.findElements(By.xpath(xpath));
		WebElement matched = null;
		StringBuilder seeAlso = new StringBuilder();

		for (WebElement a : as) {
			if (a.getText().equalsIgnoreCase(CommonUtils.getLowerSingular(word))) {
				matched = a;
			} else {
				seeAlso.append("<a href=\"").append(a.getAttribute("href")).append("\">");
				seeAlso.append(a.getText()).append("</a>, ");
			}
			LOGGER.info("found <a>: {}, {}", a.getText(), a.getAttribute("href"));
		}

		StringBuilder sb = new StringBuilder("<br><br><b>").append(word).append("</b>:<br>");

		if (matched == null) {
			sb.append("*** no definition ***");
		} else {
			sb.append("<a href=\"").append(matched.getAttribute("href")).append("\">");
			sb.append(matched.getAttribute("href")).append("</a>");
			sb.append(getWordDefinition(driver, matched));
		}

		if (seeAlso.length() == 0) {
			return sb.toString();
		}

		return sb.append("<br><br><b>See Also</b>: ").append(seeAlso).toString();
	}

	public static String getWordDefinition(String url) {
		WebDriver driver = new HtmlUnitDriver();
		driver.get(url);
		StringBuilder sb = new StringBuilder("<b>").append(url).append("</b><br><br>");
		return sb.append(getInnerHtml(driver)).toString();
	}

	private static String getWordDefinition(WebDriver driver, WebElement matched) {
		driver.get(matched.getAttribute("href"));
		return getInnerHtml(driver);
	}

	public static String getInnerHtml(WebDriver driver) {
		List<WebElement> divs = driver.findElements(By.xpath(XPATH_ENTRY_CONTENT));
		if (!divs.isEmpty()) {
			return divs.get(0).getAttribute("innerHTML");
		}

		divs = driver.findElements(By.xpath(XPATH_CONTENT));
		if (!divs.isEmpty()) {
			return divs.get(0).getAttribute("innerHTML");
		}
		return "*** NOT FOUND ***";
	}

}
