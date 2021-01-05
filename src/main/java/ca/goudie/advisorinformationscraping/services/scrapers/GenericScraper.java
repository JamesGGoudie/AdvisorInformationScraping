package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.models.ScrapeResult;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

@Service
public class GenericScraper {

	public ScrapeResult scrapeWebsite(
			final WebDriver driver, final String url
	) {
		return null;
	}

}
