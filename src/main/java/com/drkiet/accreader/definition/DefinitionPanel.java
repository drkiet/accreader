package com.drkiet.accreader.definition;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drkiet.accreader.reference.ReferencesFrame;
import com.drkiet.accreader.reference.ReferencesPanel;
import com.drkiet.accreader.util.FileHelper;
import com.drkiet.accreader.util.WebHelper;
import com.drkiettran.text.util.CommonUtils;

public class DefinitionPanel extends JPanel {
	private static final long serialVersionUID = 8683655130181562963L;
	public static final String FONT_BEGIN = "<font size=\"%d\" face=\"%s\">";
	public static final String FONT_END = "</font>";

	private JTextPane definitionPane;
	private String definition = null;
	private Integer textPaneFontSize = 4;
	private String textPaneFont = "Candara";
	private String accDefinition;
	private String highlightedText = "";
	private ReferencesFrame referencesFrame;
	private HashMap<String, String> toolsDict;
	private HashMap<String, String> coachDict;
	private List<String> toolsKeys = new ArrayList<String>();
	private List<String> coachKeys = new ArrayList<String>();
	private Object dictDefinitions;

	private static final String[] DICTIONARY_SITE = { "terms-accounting-tools.dic", "terms-accounting-coach.dic" };

	public DefinitionPanel() {
		loadDictionaries();
		definitionPane = new JTextPane();
		definitionPane.setCaretPosition(0);
		definitionPane.setCaretColor(Color.WHITE);
		definitionPane.setContentType("text/html");

		setLayout(new BorderLayout());
		add(new JScrollPane(definitionPane), BorderLayout.CENTER);
		definitionPane.addMouseListener(getMouseListner());
		setBorder();
	}

	public void loadDictionaries() {
		try {
			toolsDict = FileHelper.loadDictionary(FileHelper.getWorkspaceFolder(), DICTIONARY_SITE[0]);
			coachDict = FileHelper.loadDictionary(FileHelper.getWorkspaceFolder(), DICTIONARY_SITE[1]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Iterator<String> keys = toolsDict.keySet().iterator();
		while (keys.hasNext()) {
			toolsKeys.add(keys.next());
		}

		keys = coachDict.keySet().iterator();
		while (keys.hasNext()) {
			coachKeys.add(keys.next());
		}

		Collections.sort(toolsKeys);
		Collections.sort(coachKeys);
	}

	public void setDefinition(String text) {
		this.definition = CommonUtils.getDefinitionForWord(text);
		this.accDefinition = WebHelper.getAccDefinitionForWord(text);
		this.dictDefinitions = getListOfTermsContains(text);
		displayDefinition();
	}

	private String getListOfTermsContains(String text) {
		StringBuilder sb = new StringBuilder("<br><br><b>").append(DICTIONARY_SITE[0]).append("</b>: <br>");

		for (String key : toolsKeys) {
			if (key.contains(text)) {
				sb.append(String.format("<a href=\"%s\">", toolsDict.get(key))).append(key).append("</a><br>");
			}
		}

		sb.append("<br>").append("<b>").append(DICTIONARY_SITE[1]).append("</b>: <br>");
		for (String key : coachKeys) {
			if (key.contains(text)) {
				sb.append(String.format("<a href=\"%s\">", coachDict.get(key))).append(key).append("</a><br>");
			}
		}
		return sb.toString();
	}

	public void displayDefinition() {
		StringBuilder sb = new StringBuilder("<html>");
		sb.append(String.format(FONT_BEGIN, textPaneFontSize, textPaneFont)).append(definition).append(FONT_END);
		sb.append(String.format(FONT_BEGIN, textPaneFontSize, textPaneFont)).append(accDefinition).append(FONT_END);
		sb.append(String.format(FONT_BEGIN, textPaneFontSize, textPaneFont)).append(dictDefinitions).append(FONT_END);

		definitionPane.setText(sb.toString());
		definitionPane.setCaretPosition(0);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DefinitionPanel.class);

	private MouseListener getMouseListner() {

		return new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				String urlClicked = null;
				List<Element> contentsWithUrls = new ArrayList<Element>();

				for (Element el : definitionPane.getDocument().getRootElements()) {
					findContentsWithUrls(el, contentsWithUrls);
				}

				int caretPos = definitionPane.getCaretPosition();
				Element foundEl = null;

				for (Element el : contentsWithUrls) {
					LOGGER.info("{} - {}, {}", caretPos, el.getStartOffset(), el.getEndOffset());

					if (caretPos >= el.getStartOffset() && caretPos <= el.getEndOffset()) {
						foundEl = el;
						break;
					}
				}

				if (foundEl == null) {
					return;
				}

				Enumeration<?> attrs = foundEl.getAttributes().getAttributeNames();

				while (attrs.hasMoreElements()) {
					Object attrName = attrs.nextElement();
					if ("a".equals(attrName.toString())) {
						String href = foundEl.getAttributes().getAttribute(attrName).toString();
						urlClicked = href.substring(href.indexOf("=") + 1);
						break;
					}
				}
				LOGGER.info("urlClicked: {}", urlClicked);
				referencesFrame.setText(urlClicked.trim());

			}

			private void findContentsWithUrls(Element el, List<Element> contentsWithUrls) {
				if (el.getElementCount() > 0) {
					for (int idx = 0; idx < el.getElementCount(); idx++) {
						findContentsWithUrls(el.getElement(idx), contentsWithUrls);
					}
				}
				Enumeration<?> attrs = el.getAttributes().getAttributeNames();

				while (attrs.hasMoreElements()) {
					Object attrName = attrs.nextElement();
					if ("a".equals(attrName.toString())) {
						contentsWithUrls.add(el);
						LOGGER.info("FOUND -- {}: {}", attrName, el.getAttributes().getAttribute(attrName));
						break;
					}
					LOGGER.info("-- {}: {}", attrName, el.getAttributes().getAttribute(attrName));
				}
			}

			@Override
			public void mousePressed(MouseEvent event) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				for (Element element : definitionPane.getDocument().getRootElements()) {
					printElement(element);
				}

				try {
					int len = definitionPane.getSelectionEnd() - definitionPane.getSelectionStart();
					highlightedText = definitionPane.getDocument().getText(definitionPane.getSelectionStart(), len)
							.trim();
					LOGGER.info("HIGHLIGHTED: {}", highlightedText);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			private void printElement(Element el) {
				LOGGER.info(">> offset {}({}, {})", el.getName(), el.getStartOffset(), el.getEndOffset());
				Enumeration<?> attrs = el.getAttributes().getAttributeNames();

				while (attrs.hasMoreElements()) {
					Object attrName = attrs.nextElement();
					LOGGER.info("-- {}: {}", attrName, el.getAttributes().getAttribute(attrName));
				}

				if (el.getElementCount() == 0) {
					return;
				}

				for (int idx = 0; idx < el.getElementCount(); idx++) {
					printElement(el.getElement(idx));
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		};
	}

	private void setBorder() {
		Border innerBorder = BorderFactory.createTitledBorder("Definition");
		Border outterBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		setBorder(BorderFactory.createCompoundBorder(outterBorder, innerBorder));
	}

	public void setSmallerText() {
		if (textPaneFontSize > ReferencesPanel.SMALLEST_TEXT_AREA_FONT_SIZE) {
			this.textPaneFontSize--;
		}
		displayDefinition();
		repaint();
	}

	public void setLargerText() {
		if (textPaneFontSize < ReferencesPanel.LARGEST_TEXT_AREA_FONT_SIZE) {
			this.textPaneFontSize++;
		}
		displayDefinition();

		repaint();
	}

	public void setReferencesFrame(ReferencesFrame referencesFrame) {
		this.referencesFrame = referencesFrame;
	}
}
