package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import ca.goudie.advisorinformationscraping.exceptions.ScrapeException;
import ca.goudie.advisorinformationscraping.dto.Firm;
import ca.goudie.advisorinformationscraping.services.scrapers.IScraper;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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

	public Firm scrapeWebsite(
			final WebDriver driver,
			final String url,
			final String countryCode
	) throws ScrapeException {
		final Firm firm = this.scrapeLandingPage(driver, url, countryCode);
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
	private Firm scrapeLandingPage(
			final WebDriver driver,
			final String url,
			final String countryCode
	) {
		driver.get(url);

		final Firm firm = new Firm();

		firm.getEmails().addAll(this.emailHelper.findEmailsByAnchor(driver));
		firm.getPhones().addAll(this.phoneHelper.findPhones(driver, countryCode));

		firm.setSource(this.sourceHelper.formatSource(url));
		this.sourceHelper.compareFirmEmailAndSource(firm, url);

		return firm;
	}

	/**
	 * Searches for and scrapes employee pages.
	 *
	 * @param driver
	 * @param firm
	 * @param countryCode
	 */
	private void scrapeEmployeePages(
			final WebDriver driver,
			final Firm firm,
			final String countryCode
	) {
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
		// Using a hashset to avoid scraping the same page multiple times.
		final Collection<String> out = new HashSet<>();
		final List<WebElement> anchors;

		try {
			anchors = context.findElements(By.cssSelector("a"));
		} catch (StaleElementReferenceException e) {
			return out;
		}

		for (final WebElement anchor : anchors) {
			final String href;

			try {
				href = anchor.getAttribute("href");
			} catch (StaleElementReferenceException e) {
				continue;
			}

			if (this.employeesPageHelper.isAnchorEmployeesPage(anchor)) {
				out.add(href);
			}
		}

		return this.hrefHelper.cleanLinks(out, currentUrl);
	}

}
