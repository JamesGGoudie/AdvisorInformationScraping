package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.exceptions.RunCancelException;
import ca.goudie.advisorinformationscraping.exceptions.ScrapeException;
import ca.goudie.advisorinformationscraping.services.scrapers.models.FirmResult;

import org.openqa.selenium.WebDriver;

public interface IScraper {

	FirmResult scrapeWebsite(
			final WebDriver driver,
			final String url,
			final String countryCode
	) throws ScrapeException, RunCancelException;

}
