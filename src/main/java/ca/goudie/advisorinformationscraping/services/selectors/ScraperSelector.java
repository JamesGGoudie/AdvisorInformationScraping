package ca.goudie.advisorinformationscraping.services.selectors;

import ca.goudie.advisorinformationscraping.exceptions.UrlParseException;
import ca.goudie.advisorinformationscraping.services.scrapers.BloombergScraper;
import ca.goudie.advisorinformationscraping.services.scrapers.IScraper;
import ca.goudie.advisorinformationscraping.services.scrapers.generic.GenericScraper;
import ca.goudie.advisorinformationscraping.utils.AisUrlUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScraperSelector {

	private static final String BLOOMBERG_HOST = "www.bloomberg.com";

	@Autowired
	private GenericScraper genericScraper;

	@Autowired
	private BloombergScraper bloombergScraper;

	/**
	 * Selects a scraper using the given link.
	 *
	 * Will check the link and see if it corresponds to a site that we have a
	 * specialized scraper for.
	 *
	 * If a specialized scraper does not exist, then we will return a generic
	 * scraper.
	 *
	 * @param link
	 * @return
	 */
	public IScraper selectScraper(final String link) {
		final String host;
		try {
			host = AisUrlUtils.extractHostname(link);
		} catch (UrlParseException e) {
			return this.genericScraper;
		}

		switch (host) {
			case ScraperSelector.BLOOMBERG_HOST: {
				return this.bloombergScraper;
			}
			default: {
				return this.genericScraper;
			}
		}
	}

}
