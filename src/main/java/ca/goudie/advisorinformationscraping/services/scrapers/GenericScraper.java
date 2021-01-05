package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.models.FirmResult;
import ca.goudie.advisorinformationscraping.models.ScrapeResult;
import org.openqa.selenium.*;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class GenericScraper {

	public ScrapeResult scrapeWebsite(
			final WebDriver driver, final String url
	) {
		System.out.println("========================================");
		System.out.println(url);
		driver.get(url);

		for (int i = 0; i < 10; ++i) {
			if (driver.manage().getCookieNamed("_pxde") != null &&
					driver.manage().getCookieNamed("_px3") != null &&
					driver.manage().getCookieNamed("_px2") != null &&
					driver.manage().getCookieNamed("_pxvid") != null &&
					driver.manage().getCookieNamed("_pxff_rf") != null) {
				break;
			}

			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println(i);
		}

		Set<Cookie> cookies = driver.manage().getCookies();
		for (Cookie cookie : cookies) {
			System.out.println(cookie.getName() + ": " + cookie.getValue());
		}

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("========================================");

		driver.get(url);
		this.processChildren(driver, 0);

		System.out.println("========================================");

		ScrapeResult out = new ScrapeResult();
		FirmResult firm = new FirmResult();

		firm.setSource(driver.getPageSource());

		out.setFirm(firm);

		return out;
	}

	private void checkBloombergCookies(WebDriver driver) {

	}

	private void processChildren(SearchContext context, int depth) {
		// For every immediate child of the context...
		for (WebElement el : context.findElements(By.xpath("./*"))) {
			System.out.println(depth + ": " + el.getTagName());
			this.processChildren(el, depth + 1);
		}
	}

}
