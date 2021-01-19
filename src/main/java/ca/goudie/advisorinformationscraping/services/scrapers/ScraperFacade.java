package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.exceptions.ScrapeException;
import ca.goudie.advisorinformationscraping.dto.Firm;
import ca.goudie.advisorinformationscraping.services.selectors.ScraperSelector;
import ca.goudie.advisorinformationscraping.services.scrapers.generic.GenericScraper;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScraperFacade {

	@Autowired
	private GenericScraper genericScraper;

	@Autowired
	private ScraperSelector scraperSelector;

	public Firm scrapeWebsite(
			final WebDriver driver, final String url, final String countryCode
	) throws ScrapeException {
		return scraperSelector.selectScraper(url).scrapeWebsite(
				driver, url, countryCode);
	}

}
