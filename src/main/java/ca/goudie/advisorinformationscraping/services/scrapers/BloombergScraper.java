package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.exceptions.ScrapingFailedException;
import ca.goudie.advisorinformationscraping.models.FirmResult;
import ca.goudie.advisorinformationscraping.models.ScrapeResult;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * https://www.bloomberg.com
 */
@Service
public class BloombergScraper implements Scraper {

	private static final String CAPTCHA_TITLE = "Bloomberg - Are you a robot?";
	private static final String CRITICAL_COOKIE = "_px2";
	private static final String
			CAPTCHA_EXCEPTION_MESSAGE =
			"Could not beat Bloomberg Captcha";
	private static final int MAX_COOKIE_WAIT_TIME = 10;
	private static final int POST_COOKIE_WAIT_TIME = 1;

	@Override
	public ScrapeResult scrapeWebsite(
			final WebDriver driver, final String url
	) throws ScrapingFailedException {
		driver.get(url);

		this.checkAndCircumventCaptcha(driver, url);

		ScrapeResult out = new ScrapeResult();
		FirmResult firm = new FirmResult();

		firm.setSource(driver.getPageSource());

		out.setFirm(firm);

		return out;
	}

	/**
	 * Bloomberg has some kind of system to detect bots and stop them with a
	 * captcha, but I think that I've found a way around it.
	 *
	 * From my understanding, we require a specific cookie to bypass the captcha.
	 * This cookie comes from a request fired when the captcha screen finishes
	 * loading.
	 * Once we have the cookie, if we re-navigate to the given URL, we should be
	 * able to reach the page as intended.
	 *
	 * This isn't fool proof, obviously, but it's the best that I could come up
	 * with.
	 *
	 * @param driver
	 * @throws ScrapingFailedException
	 */
	private void checkAndCircumventCaptcha(
			final WebDriver driver, final String url
	) throws ScrapingFailedException {
		// Check the title to see if we were stopped by the captcha.
		if (driver.getTitle().equals(BloombergScraper.CAPTCHA_TITLE)) {
			// To bypass the captcha, we need a cookie from a pending request.
			// Wait until we have the cookie or until a enough time has passed.
			for (int i = 0; i < BloombergScraper.MAX_COOKIE_WAIT_TIME; ++i) {
				if (driver.manage().getCookieNamed(BloombergScraper.CRITICAL_COOKIE) !=
						null) {
					break;
				}

				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					throw new ScrapingFailedException(e);
				}
			}

			// For some reason, we need to wait some more after we have the cookie
			// before we re-navigate.
			try {
				TimeUnit.SECONDS.sleep(BloombergScraper.POST_COOKIE_WAIT_TIME);
			} catch (InterruptedException e) {
				throw new ScrapingFailedException(e);
			}

			// Now that we have the cookie, we need to re-navigate to the website.
			// The captcha screen has a different URL, so refreshing wouldn't work.
			driver.get(url);

			if (driver.getTitle().equals(BloombergScraper.CAPTCHA_TITLE)) {
				// Got caught by the Captcha again.
				throw new ScrapingFailedException(
						BloombergScraper.CAPTCHA_EXCEPTION_MESSAGE);
			}
		}
	}

}
