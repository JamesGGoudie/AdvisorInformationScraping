package ca.goudie.advisorinformationscraping.services.dihelpers;

import ca.goudie.advisorinformationscraping.services.searchers.GoogleSearchService;
import ca.goudie.advisorinformationscraping.services.searchers.ISearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceSelector {

	@Autowired
	private GoogleSearchService googleSearchService;

	public ISearchService selectSearchService() {
		return this.googleSearchService;
	}

	public ISearchService selectSearchService(final String key) {
		return this.googleSearchService;
	}

}
