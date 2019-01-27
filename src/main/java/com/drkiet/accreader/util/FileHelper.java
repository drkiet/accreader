package com.drkiet.accreader.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tautua.markdownpapers.Markdown;
import org.tautua.markdownpapers.parser.ParseException;

import com.drkiettran.text.TextApp;

public class FileHelper {
	public static String loadTextFileIntoString(String fileName) {
		try (InputStream is = TextApp.class.getResourceAsStream(fileName)) {
			StringBuilder sb = new StringBuilder();

			for (;;) {
				int c = is.read();
				if (c < 0) {
					break;
				}
				sb.append((char) c);
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String loadFaqsFile() {

		try (InputStream is = TextApp.class.getResourceAsStream("/Faqs.md")) {
			Reader in = new InputStreamReader(is);
			Writer out = new StringWriter();
			Markdown md = new Markdown();
			try {
				md.transform(in, out);
			} catch (ParseException e) {
				e.printStackTrace();
				return "*** ERROR ***";
			}
			return out.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(FileHelper.class);

	public static List<String> getFileNames(String folderName) {
		LOGGER.info("accreader.folder: {}", folderName);
		File folder = new File(folderName);

		if (!folder.isDirectory()) {
			return null;
		}

		return getFileNames(folder);

	}

	private static List<String> getFileNames(File file) {
		LOGGER.info("get file names: {}", file.getName());
		List<String> fileNames = new ArrayList<String>();
		if (!file.isDirectory()) {
			fileNames.add(file.getName());
			return fileNames;
		}

		for (File f : file.listFiles()) {
			fileNames.addAll(getFileNames(f));
		}

		return fileNames;
	}

	public static List<String> getTermsFromHtml() {
		List<String> definedTerms = new ArrayList<String>();

		String html = loadTextFileIntoString("/terms.html");
		Document doc = Jsoup.parse(html);
		List<Element> nodes = doc.getAllElements();
		for (int idx = 0; idx < nodes.size(); idx++) {
			if ("h3".equalsIgnoreCase(nodes.get(idx).tagName()) && !nodes.get(idx).text().trim().isEmpty()) {
				String term = nodes.get(idx).text();
				String definition = "*** UNKNOWN ***";

				while (idx < nodes.size()) {
					if ("p".equalsIgnoreCase(nodes.get(idx).tagName())) {
						definition = nodes.get(idx).text();
						break;
					}
					idx++;
				}

				System.out.println(String.format("*** %s: %s", term, definition));
				definedTerms.add(String.format("*** %s: %s", term, definition));
			}

		}
		return definedTerms;
	}

	public static String getAccReaderFolder() {
		return System.getProperty("accreader.folder");
	}

	public static String getFQFileName(String bookName) {
		return String.format("%s%s%s", getAccReaderFolder(), File.separator, bookName);
	}

	public static String getFQRefencesFileName() {
		return getFQFileName(getRefencesFileName());
	}

	public static String getRefencesFileName() {
		return System.getProperty("accreader.references");
	}

	public static String getWorkspaceFolder() {
		return System.getProperty("accreader.workspace");
	}

	public static String getWSReferencesFileName() {
		return String.format("%s%s%s", getWorkspaceFolder(), File.separator, getRefencesFileName());
	}
}
