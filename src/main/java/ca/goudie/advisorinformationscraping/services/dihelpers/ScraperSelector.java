package ca.goudie.advisorinformationscraping.services.dihelpers;

import ca.goudie.advisorinformationscraping.enums.KnownHost;
import ca.goudie.advisorinformationscraping.exceptions.ScrapingFailedException;
import ca.goudie.advisorinformationscraping.exceptions.UrlParseError;
import ca.goudie.advisorinformationscraping.services.scrapers.GenericScraper;
import ca.goudie.advisorinformationscraping.services.scrapers.Scraper;
import ca.goudie.advisorinformationscraping.utils.AisUrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScraperSelector {

	@Autowired
	private GenericScraper genericScraper;

	public Scraper selectScraper(final String link)
			throws ScrapingFailedException {
		try {
			final KnownHost host = KnownHost.getEnum(AisUrlUtils.extractHostname(
					link));

			// If the host enum is null...
			if (host == null) {
				// ...then the link doesn't correspond to any known hosts.
				// Use the generic scraper to compensate.
				return this.genericScraper;
			} else {
				return host.getScraper();
			}
		} catch (UrlParseError e) {
			throw new ScrapingFailedException(e);
		}
	}

}
