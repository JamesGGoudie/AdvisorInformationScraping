package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.models.common.FirmResult;
import ca.goudie.advisorinformationscraping.models.common.ScrapeResult;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

@Service
public class GenericScraper implements Scraper {

	public ScrapeResult scrapeWebsite(
			final WebDriver driver, final String url
	) {
		driver.get(url);
		this.processChildren(driver);

		ScrapeResult out = new ScrapeResult();
		FirmResult firm = new FirmResult();

		firm.setSource(url);

		out.setFirm(firm);

		return out;
	}

	private void processChildren(final SearchContext context) {
		// For every immediate child of the context...
		for (WebElement el : context.findElements(By.xpath("./*"))) {
			this.processChildren(el);
		}
	}

}
