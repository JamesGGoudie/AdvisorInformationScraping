package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.exceptions.ScrapingFailedException;
import ca.goudie.advisorinformationscraping.models.common.Firm;
import ca.goudie.advisorinformationscraping.services.dihelpers.ScraperSelector;
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
			final WebDriver driver, final String url
	) throws ScrapingFailedException {
		return scraperSelector.selectScraper(url).scrapeWebsite(driver, url);
	}

}
