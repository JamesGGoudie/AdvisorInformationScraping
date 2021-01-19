package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import ca.goudie.advisorinformationscraping.exceptions.DomReadException;
import ca.goudie.advisorinformationscraping.exceptions.UrlParseException;
import ca.goudie.advisorinformationscraping.dto.Employee;
import ca.goudie.advisorinformationscraping.dto.Firm;
import ca.goudie.advisorinformationscraping.utils.AisRegexUtils;
import ca.goudie.advisorinformationscraping.utils.AisUrlUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
@Service
public class GenericEmployeesPageHelper {

	@Autowired
	private GenericHrefHelper hrefHelper;

	@Autowired
	private GenericNameHelper nameHelper;

	@Autowired
	private GenericPathsHelper pathsHelper;

	@Autowired
	private GenericPersonalPageHelper personalPageHelper;

	@Autowired
	private GenericTitleHelper titleHelper;

	/**
	 * Searches for and scrapes employee pages.
	 *
	 * @param driver
	 * @param firm
	 * @param employeePageLinks
	 * @param countryCode
	 */
	void scrapeEmployeePages(
			final WebDriver driver,
			final Firm firm,
			final Collection<String> employeePageLinks,
			final String countryCode
	) {
		log.info("Scraping Employee Pages");

		final Collection<WebElement> employeeBlocks = new ArrayList<>();

		for (final String employeePageLink : employeePageLinks) {
			driver.get(employeePageLink);
			employeeBlocks.addAll(this.findEmployeePageBlocksByAnchors(driver));
		}

		final String currentUrl = driver.getCurrentUrl();

		for (final WebElement employeeBlock : employeeBlocks) {
			this.processEmployeeBlock(employeeBlock, firm, currentUrl);
		}

		for (final Employee employee : firm.getEmployees()) {
			this.personalPageHelper.scrapePersonalPage(
					driver, firm, employee, countryCode);
		}
	}

	/**
	 * Scrapes an employee page looking for blocks of the individual employees
	 * displayed to the user on that page.
	 *
	 * This is done by searching for anchors of the employees' personal pages.
	 *
	 * @param context
	 * @return
	 */
	Collection<WebElement> findEmployeePageBlocksByAnchors(
			final SearchContext context
	) {
		log.info("Searching for Employee Page Blocks by Anchors");

		final Collection<WebElement> employeeBlocks = new ArrayList<>();
		final Collection<WebElement> anchors =
				this.findPersonalPageAnchors(context);

		for (final WebElement anchor : anchors) {
			WebElement currentNode = anchor;

			// Loop over the anchors parents until we find one with other anchor tags
			// that are not for emails or addresses.
			// After this, the current node should represent the block of information
			// for a single employee.
			do {
				final WebElement parentNode;

				try {
					parentNode = currentNode.findElement(By.xpath(".."));
				} catch (StaleElementReferenceException e) {
					break;
				}

				final Collection<WebElement> localAnchors =
						this.findPersonalPageAnchors(parentNode);

				if (localAnchors.size() > 1) {
					break;
				}

				currentNode = parentNode;
			} while (!currentNode.getTagName().equalsIgnoreCase("body"));

			employeeBlocks.add(currentNode);
		}

		return employeeBlocks;
	}

	/**
	 * Searches for anchors that are likely for personal employee pages.
	 *
	 * @param context
	 * @return
	 */
	Collection<WebElement> findPersonalPageAnchors(
			final SearchContext context
	) {
		final Collection<WebElement> out = new ArrayList<>();
		final List<WebElement> anchors;

		try {
			anchors = context.findElements(By.tagName("a"));
		} catch (StaleElementReferenceException e) {
			return out;
		}

		for (final WebElement anchor : anchors) {
			if (this.personalPageHelper.isAnchorPersonalPage(anchor)) {
				out.add(anchor);
			}
		}

		return out;
	}

	/**
	 * Returns true if the anchor likely redirects the user to a page with a list
	 * of employees.
	 *
	 * @param anchor
	 * @return
	 */
	boolean isAnchorEmployeesPage(final WebElement anchor) {
		try {
			if (!this.hrefHelper.doesHrefExist(anchor)) {
				return false;
			}
		} catch (DomReadException e) {
			return false;
		}

		final String href;

		try {
			href = anchor.getAttribute("href");
		} catch (StaleElementReferenceException e) {
			return false;
		}

		final String path;

		try {
			path = AisUrlUtils.extractPath(href);
		} catch (UrlParseException e) {
			return false;
		}

		if (StringUtils.isBlank(path)) {
			return false;
		}

		final List<String> segments = AisRegexUtils.findPathSegments(path);

		for (final String segment : segments) {
			if (this.pathsHelper.isEmployeePageSegment(segment)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Scrapes a small section of the current page, an employee block, searching
	 * for any relevant information.
	 *
	 * This information is stored in a newly created employee that is added to the
	 * firm.
	 *
	 * This employee will only be saved if the title given is one of interest.
	 *
	 * @param employeeBlock
	 * @param firm
	 * @param currentUrl
	 */
	private void processEmployeeBlock(
			final WebElement employeeBlock,
			final Firm firm,
			final String currentUrl
	) {
		log.info("Processing Employee Block");

		final Employee employee = new Employee();

		employee.setTitle(this.titleHelper.findEmployeeTitleInBlock(employeeBlock));

		if (StringUtils.isBlank(employee.getTitle())) {
			// The employee's title isn't in the list of known titles.
			// Assume that the employee is not relevant.
			return;
		}

		final Collection<WebElement> personalPageAnchors =
				this.findPersonalPageAnchors(employeeBlock);

		for (final WebElement anchor : personalPageAnchors) {
			final String href;

			try {
				href = anchor.getAttribute("href");
			} catch (StaleElementReferenceException e) {
				continue;
			}

			final String cleanHref =
					this.hrefHelper.cleanLink(href, currentUrl);

			employee.setSource(cleanHref);
			employee.setName(this.nameHelper.findEmployeeNameInUrl(cleanHref));

			// Only expecting one anchor, but break just in case
			break;
		}

		firm.getEmployees().add(employee);
	}

}
