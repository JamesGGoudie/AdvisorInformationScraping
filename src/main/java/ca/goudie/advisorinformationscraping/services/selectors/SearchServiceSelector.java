package ca.goudie.advisorinformationscraping.services.selectors;

import ca.goudie.advisorinformationscraping.services.searchers.GoogleSearcher;
import ca.goudie.advisorinformationscraping.services.searchers.ISearcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceSelector {

	private static final String GOOGLE_KEY = "G";

	@Autowired
	private GoogleSearcher googleSearcher;

	/**
	 * Selects the default searcher.
	 *
	 * @return
	 */
	public ISearcher selectSearcher() {
		return this.selectSearcher(null);
	}

	/**
	 * Selects a searcher using the given key.
	 *
	 * @param key
	 * @return
	 */
	public ISearcher selectSearcher(final String key) {
		switch (key) {
			default: {
				return this.googleSearcher;
			}
		}
	}

}
