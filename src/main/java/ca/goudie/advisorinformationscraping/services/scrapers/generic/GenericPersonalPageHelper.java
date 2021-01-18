package ca.goudie.advisorinformationscraping.services.scrapers.generic;

import ca.goudie.advisorinformationscraping.constants.GenericConstants;
import ca.goudie.advisorinformationscraping.exceptions.DomReadException;
import ca.goudie.advisorinformationscraping.exceptions.UrlParseException;
import ca.goudie.advisorinformationscraping.models.common.Employee;
import ca.goudie.advisorinformationscraping.models.common.Firm;
import ca.goudie.advisorinformationscraping.utils.AisRegexUtils;
import ca.goudie.advisorinformationscraping.utils.AisUrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

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
	 */
	void scrapePersonalPage(
			final WebDriver driver,
			final Firm firm,
			final Employee employee
	) {
		driver.get(employee.getSource());
		employee.setSource(this.commonHelper.formatSource(employee.getSource()));

		final WebElement personalBlock =
				this.findPersonalPageBlockByEmailAnchor(driver, firm);

		final Collection<String> phones =
				this.phoneHelper.findPhones(personalBlock);

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
	 * @param anchor
	 * @return
	 */
	boolean isAnchorPersonalPage(final WebElement anchor) {
		if (!this.hrefHelper.doesHrefExist(anchor)) {
			return false;
		}

		final String href = anchor.getAttribute("href");

		String path;

		try {
			path = AisUrlUtils.extractPath(href);
		} catch (UrlParseException e) {
			return false;
		}

		if (StringUtils.isBlank(path)) {
			return false;
		}

		final List<String> segments = AisRegexUtils.findPathSegments(path);

		if (segments.size() == 0) {
			return false;
		}

		final String finalSegment = segments.get(segments.size() - 1);

		// Ensure that the anchor isn't for an employee page.
		if (this.pathsHelper.isEmployeePageSegment(finalSegment)) {
			return false;
		}

		for (final String segment : segments) {
			if (this.pathsHelper.isEmployeePageSegment(segment)) {
				return true;
			}
		}

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
			final Firm firm
	) {
		final Collection<WebElement> anchors =
				this.emailHelper.findEmailAnchors(context);

		for (final WebElement anchor : anchors) {
			final String href = anchor.getAttribute("href").substring(7);

			// If this email was already found for the firm...
			if (firm.getEmails().contains(href)) {
				// ...then disregard this anchor.
				continue;
			}

			WebElement currentNode = anchor;

			do {
				final WebElement parentNode =
						currentNode.findElement(By.xpath(".."));
				final Collection<WebElement> localAnchors =
						parentNode.findElements(By.tagName("a"));

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
						continue;
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
			} while (!currentNode.getTagName().equalsIgnoreCase("body"));

			return currentNode;
		}

		return null;
	}

}
