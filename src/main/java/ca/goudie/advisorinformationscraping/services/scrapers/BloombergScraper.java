package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.models.ScrapeResult;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

@Service
public class BloombergScraper implements SpecializedScraper {

	@Override
	public ScrapeResult scrapeWebsite(
			final WebDriver driver
	) {
		// CAPTCHA Title: Bloomberg - Are you a robot?
		// https://www.bloomberg.com/profile/company/1297756D:LN
		return null;
	}

}
