package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.models.ScrapeResult;
import org.openqa.selenium.WebDriver;

public interface SpecializedScraper {

	ScrapeResult scrapeWebsite(final WebDriver driver);

}
