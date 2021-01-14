package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.exceptions.ScrapingFailedException;
import ca.goudie.advisorinformationscraping.exceptions.UrlParseException;
import ca.goudie.advisorinformationscraping.models.common.FirmResult;
import ca.goudie.advisorinformationscraping.models.common.ScrapeResult;
import ca.goudie.advisorinformationscraping.utils.AisPhoneUtils;
import ca.goudie.advisorinformationscraping.utils.AisRegexUtils;
import ca.goudie.advisorinformationscraping.utils.AisUrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
public class GenericScraper implements Scraper {

	/**
	 * A list of possible paths that a website could be using for its employees
	 * page.
	 *
	 * An employees page is a page that contains information about relevant
	 * employees within the company.
	 *
	 * These pages may contain anchors for individual employees.
	 */
	private static final Collection<String> EMPLOYEE_PAGE_PATHS = new HashSet<>();

	public GenericScraper() {
		// Populate the list of employee page paths here.
		Collections.addAll(GenericScraper.EMPLOYEE_PAGE_PATHS,
				"/about-us/our-team/");
	}

	public ScrapeResult scrapeWebsite(
			final WebDriver driver, final String url
	) throws ScrapingFailedException {
		final ScrapeResult out = new ScrapeResult();
		final FirmResult firm = this.scrapeLandingPage(driver, url);

		this.scrapeEmployeePages(driver, firm);

		out.setFirm(firm);
		out.setIndividuals(new ArrayList<>());

		return out;
	}

	/**
	 * Scrapes the page that was given to us by the search link.
	 *
	 * We expect this page to provide information relevant specifically to the
	 * firm itself.
	 *
	 * @param driver
	 * @param url
	 * @return
	 * @throws ScrapingFailedException
	 */
	private FirmResult scrapeLandingPage(
			final WebDriver driver,
			final String url
	) throws ScrapingFailedException {
		driver.get(url);

		final FirmResult firm = new FirmResult();

		firm.setEmailAddress(this.findFirstEmailByAnchor(driver));
		firm.setPhoneNumber(this.findPhoneNumber(driver));

		this.formatFirmSource(firm, url);
		this.compareFirmEmailAndSource(firm, url);

		return firm;
	}

	/**
	 * Finds possible employee pages by looking at the paths and comparing it to a
	 * pre-determined list of suspected paths.
	 *
	 * @param driver
	 * @return
	 */
	private Collection<String> findEmployeePageLinks(final WebDriver driver) {
		final List<WebElement> anchors = driver.findElements(By.cssSelector("a"));
		// Using a hashset to avoid scraping the same page multiple times.
		final Collection<String> employeePageLinks = new HashSet<>();

		for (final WebElement anchor : anchors) {
			final String href = anchor.getAttribute("href");

			if (StringUtils.isNotBlank(href)) {
				try {
					final String path = AisUrlUtils.extractPath(href);

					if (GenericScraper.EMPLOYEE_PAGE_PATHS.contains(path)) {
						employeePageLinks.add(href);
					}
				} catch (UrlParseException e) {}
			}
		}

		return employeePageLinks;
	}

	private void scrapeEmployeePages(
			final WebDriver driver,
			final FirmResult firm
	) {
		final Collection<String> employeePageLinks =
				this.findEmployeePageLinks(driver);

		for (final String employeePageLink : employeePageLinks) {
			driver.get(employeePageLink);
		}
	}

	/**
	 * Anchor tags can be used to open a mail app to send an email. This is done
	 * by starting the anchor href value with 'mailto:'
	 * <p>
	 * Search the page for these anchors and return the first email address.
	 *
	 * @param driver
	 *
	 * @return
	 */
	private String findFirstEmailByAnchor(final WebDriver driver) {
		final List<WebElement> anchors = driver.findElements(By.cssSelector("a"));

		for (final WebElement anchor : anchors) {
			final String href = anchor.getAttribute("href");

			if (StringUtils.isNotBlank(href) &&
					href.toLowerCase().startsWith("mailto:")) {
				// Check the innerText for an email that is displayed to the user.
				final String innerText = anchor.getAttribute("innerText");
				final String innerEmail = AisRegexUtils.findEmail(innerText);

				if (StringUtils.isNotBlank(innerEmail)) {
					return innerEmail;
				}

				// We could not find an email in the innerText
				// Check what is after the mailto: instead and see if that is valid.

				final String hrefText = href.substring(7);
				final String hrefEmail = AisRegexUtils.findEmail(hrefText);

				if (StringUtils.isNotBlank(hrefEmail)) {
					return hrefEmail;
				}
			}
		}

		return null;
	}

	private String findPhoneNumber(final WebDriver driver) {
		final String anchorPhone = this.findFirstPhoneNumberByAnchor(driver);

		if (StringUtils.isNotBlank(anchorPhone)) {
			return anchorPhone;
		}

		final String fullPagePhone =
				AisPhoneUtils.findFirstPhoneNumber(driver.getPageSource());

		if (StringUtils.isNotBlank(fullPagePhone)) {
			return fullPagePhone;
		}

		return null;
	}

	/**
	 * Searches the page for any anchor tags that are indicated to contain
	 * telephone numbers.
	 *
	 * An anchor contains a phone number if the href value starts with 'tel:'
	 *
	 * @param driver
	 * @return
	 */
	private String findFirstPhoneNumberByAnchor(final WebDriver driver) {
		final List<WebElement> anchors = driver.findElements(By.cssSelector("a"));

		for (final WebElement anchor : anchors) {
			final String href = anchor.getAttribute("href");

			if (StringUtils.isNotBlank(href) &&
					href.toLowerCase().startsWith("tel:")) {
				// Check the value in the href first since it is more likely to be
				// accurate.
				// This is because numbers displayed to the user may have letters in
				// place of numbers.
				final String hrefText = href.substring(4);
				final String hrefPhone = AisPhoneUtils.findFirstPhoneNumber(hrefText);

				if (StringUtils.isNotBlank(hrefPhone)) {
					return hrefPhone;
				}

				// Blindly accept the phone number displayed to the user.
				return anchor.getAttribute("innerText");
			}
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
	 * @param url
	 */
	private void compareFirmEmailAndSource(
			final FirmResult firm,
			final String url
	) throws ScrapingFailedException {
		final String firmEmail = firm.getEmailAddress();

		if (StringUtils.isBlank(firmEmail)) {
			return;
		}

		// The email host is everything after the '@' character.
		final String emailHost = firmEmail.substring(firmEmail.indexOf('@') + 1);

		try {
			final String sourceHost = AisUrlUtils.extractHostname(url);

			// If the source host ends with the email host...
			if (sourceHost.endsWith(emailHost)) {
				// ...then the source is probably the firms own website.
				// We only check the end of the source host to account for internal
				// sites.
				firm.setFirmUrl(sourceHost);
			}
		} catch (UrlParseException e) {
			throw new ScrapingFailedException(e);
		}
	}

	/**
	 * Formats the URL that was used to search for the firm's general information.
	 * All that should remain is the host, port (if present), and path.
	 *
	 * @param firm
	 * @param url
	 */
	private void formatFirmSource(final FirmResult firm, final String url) {
		try {
			firm.setSource(AisUrlUtils.formatSource(url));
		} catch (UrlParseException e) {
			// Formatting the source failed.
			// Use the unchanged url as the source.
			firm.setSource(url);
		}
	}

}
