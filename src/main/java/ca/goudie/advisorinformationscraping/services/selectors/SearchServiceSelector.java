package ca.goudie.advisorinformationscraping.services.selectors;

import ca.goudie.advisorinformationscraping.services.searchers.GoogleSearchService;
import ca.goudie.advisorinformationscraping.services.searchers.ISearchService;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceSelector {

	private static final String GOOGLE_KEY = "G";

	@Autowired
	private GoogleSearchService googleSearchService;

	/**
	 * Selects the default searcher.
	 *
	 * @return
	 */
	public ISearchService selectSearcher() {
		return this.selectSearcher(null);
	}

	/**
	 * Selects a searcher using the given key.
	 *
	 * @param key
	 * @return
	 */
	public ISearchService selectSearcher(final String key) {
		switch (key) {
			default: {
				return this.googleSearchService;
			}
		}
	}

}
