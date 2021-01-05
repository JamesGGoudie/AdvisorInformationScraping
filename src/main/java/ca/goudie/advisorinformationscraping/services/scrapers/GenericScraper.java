package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.models.FirmResult;
import ca.goudie.advisorinformationscraping.models.ScrapeResult;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

@Service
public class GenericScraper {

	public ScrapeResult scrapeWebsite(
			final WebDriver driver, final String url
	) {
		driver.get(url);

		System.out.println("========================================");
		System.out.println(url);

		this.processChildren(driver, 0);

		System.out.println("========================================");

		ScrapeResult out = new ScrapeResult();
		FirmResult firm = new FirmResult();

		firm.setSource(driver.getPageSource());

		out.setFirm(firm);

		return out;
	}

	private void processChildren(SearchContext context, int depth) {
		for (WebElement el : context.findElements(By.xpath("./*"))) {
			System.out.println(depth + ": " + el.getTagName());
			this.processChildren(el, depth + 1);
		}
	}

}
