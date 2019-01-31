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
	private static final String XPATH_DICT_TERM = "//div[@id='dictionary-term']";
	private static final String XPATH_MAIN_CONTENT = "//div[@class='main-content']";

	public static List<String> getNysscpaTerms() throws Exception {
		WebDriver driver = new HtmlUnitDriver();
		driver.get(THE_NYSCPA_TERM_WEBSITE);
		List<WebElement> sections = driver.findElements(By.xpath(XPATH_TERM_TEXT));
		List<String> definedTerms = new ArrayList<String>();

		for (WebElement section : sections) {
			List<WebElement> terms = filterEmptyWebElements(section.findElements(By.xpath("//h3")));
			terms = driver.findElements(By.xpath(".//*"));
			for (WebElement term : terms) {
				System.out.println(term.getTagName() + ":" + term.getText());
			}
			terms = filterEmptyWebElements(section.findElements(By.xpath("//h3")));
			for (WebElement term : terms) {
				String text = term.getText();

				if (text == null || text.trim().isEmpty()) {
					continue;
				}
				StringBuilder sb = new StringBuilder(text).append(": ");
				List<WebElement> definitions = term.findElements(By.xpath("/following-sibling::p"));

				for (WebElement definition : definitions) {
					String defText = definition.getText();
					if (defText == null || defText.trim().isEmpty()) {
						continue;
					}
					sb.append(defText).append("<br> ");

				}
				definedTerms.add(sb.toString());
				LOGGER.info("*** {} ***", sb.toString());
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

		return sb.append("<br><br><b>See Also</b>: <br>").append(seeAlso).toString();
	}

	public static String getWordDefinition(String url, int fontSize, String font) {
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

		divs = driver.findElements(By.xpath(XPATH_DICT_TERM));
		if (!divs.isEmpty()) {
			return divs.get(0).getAttribute("innerHTML");
		}

		divs = driver.findElements(By.xpath(XPATH_MAIN_CONTENT));
		if (!divs.isEmpty()) {
			return divs.get(0).getAttribute("innerHTML");
		}

		return "*** NOT FOUND ***";
	}

	public static List<String> getAccountingToolsTerms() {
		char ch = 'a';
		String prefixUrl = "https://www.accountingtools.com/terms-";
		String xpath = "//div[@class='sqs-block-content']//li//a";
		List<String> termsWithUrls = new ArrayList<String>();
		WebDriver driver = new HtmlUnitDriver();

		while (ch != 'w') {
			String url = prefixUrl + ch;
			ch++;
			driver.get(url);
			List<WebElement> as = driver.findElements(By.xpath(xpath));
			for (WebElement a : as) {
				termsWithUrls.add(a.getText() + ": " + a.getAttribute("href"));
				LOGGER.info("*** {} ***", termsWithUrls.get(termsWithUrls.size() - 1));
			}
		}
		String url = prefixUrl + "xyz";
		driver.get(url);
		List<WebElement> as = driver.findElements(By.xpath(xpath));
		for (WebElement a : as) {
			termsWithUrls.add(a.getText() + ": " + a.getAttribute("href"));
			LOGGER.info("*** {} ***", termsWithUrls.get(termsWithUrls.size() - 1));
		}
		return termsWithUrls;
	}

	public static List<String> getAccountingCoachTerms() {
		char ch = 'A';
		String prefixUrl = "https://www.accountingcoach.com/terms/";
		List<String> termsWithUrls = new ArrayList<String>();
		WebDriver driver = new HtmlUnitDriver();
		while (ch != 'W') {
			String url = prefixUrl + ch;
			driver.get(url);
			String xpath = String.format("//h4[text()='Letter %c']/..//a[contains(@href,'accountingcoach.com/terms/')]",
					ch);
			List<WebElement> as = driver.findElements(By.xpath(xpath));
			for (WebElement a : as) {
				termsWithUrls.add(a.getText() + ": " + a.getAttribute("href"));
				LOGGER.info("*** {} ***", termsWithUrls.get(termsWithUrls.size() - 1));
			}
			ch++;

		}
		return termsWithUrls;
	}

}
