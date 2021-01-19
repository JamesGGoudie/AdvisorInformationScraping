package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.exceptions.ScrapeException;
import ca.goudie.advisorinformationscraping.models.common.Firm;
import org.openqa.selenium.WebDriver;

public interface IScraper {

	Firm scrapeWebsite(
			final WebDriver driver,
			final String url,
			final String countryCode
	) throws ScrapeException;

}
