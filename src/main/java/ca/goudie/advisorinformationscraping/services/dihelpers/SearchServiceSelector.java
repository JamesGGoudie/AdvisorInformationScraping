package ca.goudie.advisorinformationscraping.services.dihelpers;

import ca.goudie.advisorinformationscraping.services.search.GoogleSearchService;
import ca.goudie.advisorinformationscraping.services.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceSelector {

	@Autowired
	private GoogleSearchService googleSearchService;

	public SearchService selectSearchService() {
		return this.googleSearchService;
	}

	public SearchService selectSearchService(String key) {
		return this.googleSearchService;
	}

}
