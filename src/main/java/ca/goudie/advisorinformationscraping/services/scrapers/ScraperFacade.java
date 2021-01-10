package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.exceptions.ScrapingFailedException;
import ca.goudie.advisorinformationscraping.models.common.ScrapeResult;
import ca.goudie.advisorinformationscraping.services.dihelpers.ScraperSelector;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScraperFacade {

	@Autowired
	private GenericScraper genericScraper;

	@Autowired
	private ScraperSelector scraperSelector;

	public ScrapeResult scrapeWebsite(
			final WebDriver driver, final String url
	) throws ScrapingFailedException {
		return scraperSelector.selectScraper(url).scrapeWebsite(driver, url);
	}

}
