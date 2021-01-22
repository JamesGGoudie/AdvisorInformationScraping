package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import ca.goudie.advisorinformationscraping.constants.GenericConstants;
import ca.goudie.advisorinformationscraping.exceptions.DomReadException;
import ca.goudie.advisorinformationscraping.exceptions.UrlParseException;
import ca.goudie.advisorinformationscraping.services.scrapers.models.EmployeeResult;
import ca.goudie.advisorinformationscraping.services.scrapers.models.FirmResult;
import ca.goudie.advisorinformationscraping.utils.AisRegexUtils;
import ca.goudie.advisorinformationscraping.utils.AisUrlUtils;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Log4j2
@Service
public class GenericPersonalPageHelper {

	@Autowired
	private GenericEmailHelper emailHelper;

	@Autowired
	private GenericEmployeesPageHelper employeesPageHelper;

	@Autowired
	private GenericHrefHelper hrefHelper;

	@Autowired
	private GenericPathsHelper pathsHelper;

	@Autowired
	private GenericPhoneHelper phoneHelper;

	@Autowired
	private GenericSourceHelper commonHelper;

	/**
	 * Scrapes an employee's personal page for information to attribute to the
	 * employee.
	 *
	 * @param driver
	 * @param firm
	 * @param employee
	 * @param countryCode
	 */
	void scrapePersonalPage(
			final WebDriver driver,
			final FirmResult firm,
			final EmployeeResult employee,
			final String countryCode
	) {
		if (StringUtils.isBlank(employee.getSource())) {
			return;
		}

		log.info("Scraping Personal Page");

		driver.get(employee.getSource());
		employee.setSource(this.commonHelper.formatSource(employee.getSource()));

		final WebElement personalBlock =
				this.findPersonalPageBlockByEmailAnchor(driver, firm);

		final Collection<String> phones =
				this.phoneHelper.findPhones(personalBlock, countryCode);

		for (final String phone : phones) {
			employee.getPhones().put(phone, GenericConstants.INIT_SCORE);
		}

		final Collection<String> emails =
				this.emailHelper.findEmailsByAnchor(personalBlock);

		for (final String email : emails) {
			employee.getEmails().put(email, GenericConstants.INIT_SCORE);
		}
	}

	/**
	 * Returns true if the anchor given likely represents a personal page.
	 *
	 * Uses a collection of known non-personal page anchors to speed up the
	 * process.
	 *
	 * @param anchor
	 * @param badHrefs
	 * @return
	 */
	boolean isAnchorPersonalPage(
			final WebElement anchor,
			final Collection<String> badHrefs
	) {
		try {
			if (!this.hrefHelper.doesHrefExist(anchor)) {
				return false;
			}
		} catch (DomReadException e) {
			// Couldn't read DOM; assume false
			log.error(e);

			return false;
		}

		final String href;

		try {
			 href = anchor.getAttribute("href");
		} catch (StaleElementReferenceException e) {
			// Couldn't find anchor; assume false
			log.error(e);

			return false;
		}

		if (badHrefs.contains(href)) {
			return false;
		}

		try {
			if (this.emailHelper.isEmailAnchor(anchor) ||
					this.phoneHelper.isPhoneAnchor(anchor)) {
				badHrefs.add(href);

				return false;
			}
		} catch (DomReadException e) {
			log.error(e);

			return false;
		}

		final String path;

		try {
			path = AisUrlUtils.extractPath(href);
		} catch (UrlParseException e) {
			// Couldn't parse HREF; assume false
			log.error(e);

			return false;
		}

		if (StringUtils.isBlank(path)) {
			badHrefs.add(href);

			return false;
		}

		final List<String> segments = AisRegexUtils.findPathSegments(path);

		if (segments.size() == 0) {
			badHrefs.add(href);

			return false;
		}

		final String finalSegment = segments.get(segments.size() - 1);

		// Ensure that the anchor isn't for an employee page.
		if (this.pathsHelper.isEmployeePageSegment(finalSegment)) {
			badHrefs.add(href);

			return false;
		}

		for (final String segment : segments) {
			if (this.pathsHelper.isEmployeePageSegment(segment)) {
				return true;
			}
		}

		badHrefs.add(href);

		return false;
	}

	/**
	 * Searches the given context for the employee's block.
	 *
	 * This block is expected to contain all of the information related to the
	 * employee of this page.
	 *
	 * This is done by searching for a relevant email anchor to find a starting
	 * point, and then recursively looking at the parent element until we find an
	 * element that has too many irrelevant anchors.
	 *
	 * @param context
	 * @param firm
	 * @return
	 */
	private WebElement findPersonalPageBlockByEmailAnchor(
			final SearchContext context,
			final FirmResult firm
	) {
		log.info("Searching for Personal Page Block by Email Anchor");

		final Collection<WebElement> anchors =
				this.emailHelper.findEmailAnchors(context);

		for (final WebElement anchor : anchors) {
			final String href;

			try {
				href = anchor.getAttribute("href").substring(7);
			} catch (StaleElementReferenceException e) {
				// Couldn't find anchor; try the next
				log.error(e);

				continue;
			}

			// If this email was already found for the firm...
			if (firm.getEmails().contains(href)) {
				// ...then disregard this anchor.
				continue;
			}

			WebElement currentNode = anchor;

			do {
				final WebElement parentNode;
				final Collection<WebElement> localAnchors;

				try {
					parentNode = currentNode.findElement(By.xpath(".."));
					localAnchors = parentNode.findElements(By.tagName("a"));
				} catch (StaleElementReferenceException e) {
					// Current node is missing; try the next anchor
					log.error(e);

					break;
				}

				int badAnchors = 0;

				// Look for any anchors that refer to non-relevant information.
				for (final WebElement localAnchor : localAnchors) {
					try {
						if (!(this.phoneHelper.isPhoneAnchor(localAnchor) ||
								this.emailHelper.isEmailAnchor(localAnchor))) {
							++badAnchors;
						}
					} catch (DomReadException e) {
						// Still other anchors to check.
						log.error(e);
					}
				}

				// Personal blocks can have all kinds of different anchors in them.
				// Some examples are external review sites and navigation buttons
				// We need to have a certain level of tolerance so that we don't return
				// too small of a block.
				if (badAnchors > 5) {
					break;
				}

				currentNode = parentNode;
			} while (!(currentNode.getTagName().equalsIgnoreCase("body") ||
					currentNode.getTagName().equalsIgnoreCase("head")));

			log.info("Found Personal Page Block with Email Anchor: " + href);

			return currentNode;
		}

		return null;
	}

}
