package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import ca.goudie.advisorinformationscraping.models.common.Firm;
import ca.goudie.advisorinformationscraping.services.scrapers.Scraper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class GenericScraper implements Scraper {

	@Autowired
	private GenericEmailHelper emailHelper;

	@Autowired
	private GenericEmployeesPageHelper employeesPageHelper;

	@Autowired
	private GenericHrefHelper hrefHelper;

	@Autowired
	private GenericPhoneHelper phoneHelper;

	@Autowired
	private GenericSourceHelper sourceHelper;

	public Firm scrapeWebsite(
			final WebDriver driver, final String url
	) {
		final Firm firm = this.scrapeLandingPage(driver, url);

		this.scrapeEmployeePages(driver, firm);

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
	 * @return
	 */
	private Firm scrapeLandingPage(
			final WebDriver driver,
			final String url
	) {
		driver.get(url);

		final Firm firm = new Firm();

		firm.getEmails().addAll(this.emailHelper.findEmailsByAnchor(driver));
		firm.getPhones().addAll(
				this.phoneHelper.findPhones(driver.findElement(By.tagName("body"))));

		firm.setSource(this.sourceHelper.formatSource(url));
		this.sourceHelper.compareFirmEmailAndSource(firm, url);

		return firm;
	}

	/**
	 * Searches for and scrapes employee pages.
	 *
	 * @param driver
	 * @param firm
	 */
	private void scrapeEmployeePages(
			final WebDriver driver,
			final Firm firm
	) {
		final Collection<String> employeePageLinks =
				this.findEmployeePageLinks(driver);
		this.employeesPageHelper.scrapeEmployeePages(
				driver, firm, employeePageLinks);
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

			if (this.employeesPageHelper.isAnchorEmployeesPage(anchor)) {
				employeePageLinks.add(href);
			}
		}

		return this.hrefHelper.cleanLinks(
				employeePageLinks, driver.getCurrentUrl());
	}

}
