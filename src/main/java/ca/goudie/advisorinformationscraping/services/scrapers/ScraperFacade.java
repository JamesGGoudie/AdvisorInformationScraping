package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.models.ScrapeResult;
import ca.goudie.advisorinformationscraping.services.dihelpers.SpecializedScraperSelector;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScraperFacade {

	@Autowired
	private GenericScraper genericScraper;

	@Autowired
	private SpecializedScraperSelector specializedScraperSelector;

	public ScrapeResult scrapeWebsite(
			final WebDriver driver, final String url
	) {
		if (this.specializedScraperSelector.hasSpecializedScraper(url)) {
			return this.useSpecializedScraper(driver, url);
		}

		return this.useGenericScraper(driver, url);
	}

	private ScrapeResult useGenericScraper(
			final WebDriver driver, final String url
	) {
		return this.genericScraper.scrapeWebsite(driver, url);
	}

	private ScrapeResult useSpecializedScraper(
			final WebDriver driver, final String url
	) {
		return this.specializedScraperSelector.selectSpecializedScraper(url)
				.scrapeWebsite(driver);
	}

}
