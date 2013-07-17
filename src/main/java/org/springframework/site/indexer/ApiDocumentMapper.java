package org.springframework.site.indexer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchEntryMapper;

import java.util.Date;

public class ApiDocumentMapper implements SearchEntryMapper<Document> {

	public static final String START_OF_CLASS_DATA = "<!-- ======== START OF CLASS DATA ======== -->";
	public static final String END_OF_CLASS_DATA = "<!-- ========= END OF CLASS DATA ========= -->";

	public SearchEntry map(Document document) {
		if (document.baseUri().endsWith("allclasses-frame.html")) return null;

		String text = document.outerHtml();
		int start = text.indexOf(START_OF_CLASS_DATA) + START_OF_CLASS_DATA.length();
		int end = text.indexOf(END_OF_CLASS_DATA);
		String apiContent;
		String summary = null;
		if (start != -1 && end != -1) {
			apiContent = text.substring(start, end).trim();
			Elements ps = Jsoup.parse(apiContent, document.baseUri()).select("p");
			if (ps.size() > 0) {
				summary = ps.first().text();
			}
		} else {
			apiContent = text;
		}
		if (summary == null) {
			summary = apiContent.substring(0, Math.min(500, apiContent.length()));
		}
		SearchEntry entry = new SearchEntry();
		entry.setPublishAt(new Date(0L));
		entry.setRawContent(apiContent);
		entry.setSummary(summary);
		entry.setTitle(document.title());
		entry.setPath(document.baseUri());
		return entry;
	}
}