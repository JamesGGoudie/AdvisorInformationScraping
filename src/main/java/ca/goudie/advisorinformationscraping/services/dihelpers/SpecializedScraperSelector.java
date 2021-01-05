package ca.goudie.advisorinformationscraping.services.dihelpers;

import ca.goudie.advisorinformationscraping.services.scrapers.BloombergScraper;
import ca.goudie.advisorinformationscraping.services.scrapers.SpecializedScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpecializedScraperSelector {

	@Autowired
	private BloombergScraper bloombergScraper;

	public SpecializedScraper selectSpecializedScraper(final String link) {
		return this.bloombergScraper;
	}

	public boolean hasSpecializedScraper(final String link) {
		switch (link) {
			default:
				return false;
		}
	}

}
