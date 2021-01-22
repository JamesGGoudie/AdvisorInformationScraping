package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import ca.goudie.advisorinformationscraping.services.scrapers.models.FirmResult;
import ca.goudie.advisorinformationscraping.exceptions.RunCancelException;
import ca.goudie.advisorinformationscraping.exceptions.ScrapeException;
import ca.goudie.advisorinformationscraping.services.ThreadService;
import ca.goudie.advisorinformationscraping.services.scrapers.IScraper;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Log4j2
@Service
public class GenericScraper implements IScraper {

	@Autowired
	private GenericEmailHelper emailHelper;

	@Autowired
	private GenericEmployeesPageHelper employeesPageHelper;

	@Autowired
	private GenericHrefHelper hrefHelper;

	@Autowired
	private GenericPhoneHelper phoneHelper;

	@Autowired
	private GenericScoreHelper scoreHelper;

	@Autowired
	private GenericSourceHelper sourceHelper;

	@Autowired
	private ThreadService threadService;

	public FirmResult scrapeWebsite(
			final WebDriver driver,
			final String url,
			final String countryCode
	) throws ScrapeException, RunCancelException {
		log.info("Beginning Generic Scrape of: " + url);

		final FirmResult firm = this.scrapeLandingPage(driver, url, countryCode);
		this.scrapeEmployeePages(driver, firm, countryCode);
		this.scoreHelper.calculateScores(firm);

		return firm;
	}

	/**
	 * Scrapes the page that was given to us by the search link.
	 *
	 * We expect this page to provide information relevant specifically to the
	 * firm itself.
	 *
	 * @param driver
	 * @param url
	 * @param countryCode
	 * @return
	 */
	private FirmResult scrapeLandingPage(
			final WebDriver driver,
			final String url,
			final String countryCode
	) {
		driver.get(url);

		final FirmResult firm = new FirmResult();

		firm.getEmails().addAll(this.emailHelper.findEmailsByAnchor(driver));
		firm.getPhones().addAll(this.phoneHelper.findPhones(driver, countryCode));

		firm.setSource(this.sourceHelper.formatSource(url));
		this.sourceHelper.compareFirmEmailAndSource(firm, url);

		return firm;
	}

	/**
	 * Searches for and scrapes Employee Pages.
	 *
	 * @param driver
	 * @param firm
	 * @param countryCode
	 */
	private void scrapeEmployeePages(
			final WebDriver driver,
			final FirmResult firm,
			final String countryCode
	) throws RunCancelException {
		final Collection<String> employeePageLinks =
				this.findEmployeePageLinks(driver, driver.getCurrentUrl());
		this.employeesPageHelper.scrapeEmployeePages(
				driver, firm, employeePageLinks, countryCode);
	}

	/**
	 * Finds possible employee pages by looking at the paths and comparing it to a
	 * pre-determined list of suspected paths.
	 *
	 * @param context
	 * @param currentUrl
	 * @return
	 */
	private Collection<String> findEmployeePageLinks(
			final SearchContext context,
			final String currentUrl
	) {
		log.info("Searching for employee pages");

		// Using a hashset to avoid scraping the same page multiple times.
		final Collection<String> out = new HashSet<>();
		final List<WebElement> anchors;

		try {
			anchors = context.findElements(By.cssSelector("a"));
		} catch (StaleElementReferenceException e) {
			// Context is stale, return empty collection.
			log.error(e);

			return out;
		}

		for (final WebElement anchor : anchors) {
			final String href;

			try {
				href = anchor.getAttribute("href");
			} catch (StaleElementReferenceException e) {
				// Anchor is stale; try next one
				log.error(e);

				continue;
			}

			if (this.employeesPageHelper.isAnchorEmployeesPage(anchor)) {
				log.info("Found Employee Page: " + href);
				out.add(href);
			}
		}

		log.info("Found " + out.size() + " Employee Pages");

		return this.hrefHelper.cleanLinks(out, currentUrl);
	}

}
