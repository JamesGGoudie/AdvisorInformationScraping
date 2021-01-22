package ca.goudie.advisorinformationscraping.services.selectors;

import ca.goudie.advisorinformationscraping.constants.SearchEngineConstants;
import ca.goudie.advisorinformationscraping.services.searchers.GoogleSearcher;
import ca.goudie.advisorinformationscraping.services.searchers.ISearcher;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class SearcherSelector {

	@Autowired
	private GoogleSearcher googleSearcher;

	/**
	 * Selects the default searcher.
	 *
	 * @return
	 */
	public ISearcher selectSearcher() {
		return this.getDefault();
	}

	/**
	 * Selects a searcher using the given key.
	 *
	 * If the key is not recognized, using the default searcher.
	 *
	 * @param key
	 * @return
	 */
	public ISearcher selectSearcher(final String key) {
		switch (key) {
			case SearchEngineConstants.GOOGLE: {
				return this.googleSearcher;
			}
			default: {
				log.info("Unknown Search Engine: " + key + "; Using Default");
				return this.getDefault();
			}
		}
	}

	private ISearcher getDefault() {
		return this.googleSearcher;
	}

}
