package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.exceptions.ScrapingFailedException;
import ca.goudie.advisorinformationscraping.models.common.FirmResult;
import ca.goudie.advisorinformationscraping.models.common.ScrapeResult;
import ca.goudie.advisorinformationscraping.utils.AisUrlUtils;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GenericScraper implements Scraper {

	/**
	 * Comprehensive regex for identifying email addresses shamelessly stolen from
	 * from the Chromium repository.
	 */
	private static final String EMAIL_REGEX =
			"(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\"" +
					".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|" +
					"(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))";

	public ScrapeResult scrapeWebsite(
			final WebDriver driver, final String url
	) throws ScrapingFailedException {
		driver.get(url);

		ScrapeResult out = new ScrapeResult();
		FirmResult firm = new FirmResult();

		firm.setEmailAddress(this.findEmailByMailTo(driver));
		firm.setPhoneNumber(this.findPhoneNumber(driver));

		firm.setSource(url);

		this.compareFirmEmailAndSource(firm);

		out.setFirm(firm);

		return out;
	}

	private void processChildren(final WebElement context, int depth) {
		// For every immediate child of the context...
		for (WebElement el : context.findElements(By.xpath("./*"))) {
			this.processChildren(el, depth + 1);
		}
	}

	/**
	 * Anchor tags can be used to open a mail app to send an email. This is done
	 * by starting the anchor href value with 'mailto:'
	 * <p>
	 * Search the page for these anchors to find email addresses.
	 *
	 * @param driver
	 *
	 * @return
	 */
	private String findEmailByMailTo(final WebDriver driver) {
		final List<WebElement> anchors = driver.findElements(By.cssSelector("a"));

		for (final WebElement anchor : anchors) {
			final String href = anchor.getAttribute("href");

			if (StringUtils.isNotBlank(href) && href.startsWith("mailto:")) {
				// Check the innerText for an email that is displayed to the user.
				final String innerText = anchor.getAttribute("innerText");

				Pattern emailPattern = Pattern.compile(GenericScraper.EMAIL_REGEX);
				Matcher innerMatcher = emailPattern.matcher(innerText);

				if (innerMatcher.find()) {
					return innerMatcher.group();
				}

				// We could not find an email in the innerText
				// Check what is in the mailto instead.

				final String hrefEmail = href.substring(7);

				Matcher hrefMatcher = emailPattern.matcher(hrefEmail);

				if (hrefMatcher.find()) {
					return hrefMatcher.group();
				}
			}
		}

		return null;
	}

	private String findPhoneNumber(final WebDriver driver) {
		Iterable<PhoneNumberMatch> numbers =
				PhoneNumberUtil.getInstance().findNumbers(driver.getPageSource(),
						null);

		for (final PhoneNumberMatch number : numbers) {
			return number.rawString();
		}

		return null;
	}

	/**
	 * Compares the email address of the firm to the URL that was used as the
	 * source of the information collected.
	 *
	 * If they match, then the URL is likely the URL for the firm.
	 * The URL will then by saved in the given firm object.
	 *
	 * If the email address is not present, then no changes will be made.
	 *
	 * @param firm
	 */
	private void compareFirmEmailAndSource(FirmResult firm)
			throws ScrapingFailedException {
		final String firmEmail = firm.getEmailAddress();

		if (StringUtils.isBlank(firmEmail)) {
			return;
		}

		// The email host is everything after the '@' character.
		final String emailHost = firmEmail.substring(firmEmail.indexOf('@') + 1);
		final String sourceHost = AisUrlUtils.extractHostname(firm.getSource());

		// If the source host ends with the email host...
		if (sourceHost.endsWith(emailHost)) {
			// ...then the source is probably the firms own website.
			// We only check the end of the source host to account for internal sites.
			firm.setFirmUrl(firm.getSource());
		}
	}

}
