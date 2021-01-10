package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.exceptions.ScrapingFailedException;
import ca.goudie.advisorinformationscraping.models.common.ScrapeResult;
import org.openqa.selenium.WebDriver;

public interface Scraper {

	ScrapeResult scrapeWebsite(final WebDriver driver, final String url)
			throws ScrapingFailedException;

}
