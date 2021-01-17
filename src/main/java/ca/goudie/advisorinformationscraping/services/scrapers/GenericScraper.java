package ca.goudie.advisorinformationscraping.services.scrapers;

import ca.goudie.advisorinformationscraping.exceptions.UrlParseException;
import ca.goudie.advisorinformationscraping.models.common.Employee;
import ca.goudie.advisorinformationscraping.models.common.Firm;
import ca.goudie.advisorinformationscraping.utils.AisPhoneUtils;
import ca.goudie.advisorinformationscraping.utils.AisRegexUtils;
import ca.goudie.advisorinformationscraping.utils.AisUrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
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
	 * These pages may contain anchors for employee's personal pages.
	 */
	private static final Collection<String> EMPLOYEE_PAGE_PATHS = new HashSet<>();

	/**
	 * A list of employee titles that we are interested in.
	 *
	 * This allows us to ignore non-relevant employees.
	 */
	private static final Collection<String> EMPLOYEE_TITLES = new HashSet<>();

	public GenericScraper() {
		// Populate our static hash-sets here for simplicity.
		Collections.addAll(GenericScraper.EMPLOYEE_PAGE_PATHS,
				"our-team");

		Collections.addAll(GenericScraper.EMPLOYEE_TITLES,
				"chartered financial planner",
				"financial planning specialist",
				"trust specialist",
				"financial planning consultant");
	}

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

		firm.getEmails().addAll(this.findEmailsByAnchor(driver));
		firm.getPhones().addAll(
				this.findPhones(driver.findElement(By.tagName("body"))));

		firm.setSource(this.formatSource(url));
		this.compareFirmEmailAndSource(firm, url);

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
		final Collection<WebElement> employeeBlocks = new ArrayList<>();

		for (final String employeePageLink : employeePageLinks) {
			driver.get(employeePageLink);
			employeeBlocks.addAll(this.findEmployeePageBlocksByAnchors(driver));
		}

		for (final WebElement employeeBlock : employeeBlocks) {
			this.processEmployeeBlock(driver, employeeBlock, firm);
		}

		for (final Employee employee : firm.getEmployees()) {
			this.scrapePersonalPage(driver, firm, employee);
		}
	}

	private void processEmployeeBlock(
			final WebDriver driver,
			final WebElement employeeBlock,
			final Firm firm
	) {
		final Employee employee = new Employee();

		employee.setTitle(this.findEmployeeTitleInBlock(employeeBlock));

		if (StringUtils.isBlank(employee.getTitle())) {
			// The employee's title isn't in the list of known titles.
			// Assume that the employee is not relevant.
			return;
		}

		final Collection<WebElement> personalPageAnchors =
				this.findPersonalPageAnchors(employeeBlock);

		for (final WebElement anchor : personalPageAnchors) {
			final String href = anchor.getAttribute("href");
			final String cleanHref = this.cleanLink(href, driver.getCurrentUrl());

			employee.setSource(cleanHref);
			employee.setName(this.findEmployeeNameInUrl(cleanHref));

			// Only expecting one anchor, but break just in case
			break;
		}

		firm.getEmployees().add(employee);
	}

	private void scrapePersonalPage(
			final WebDriver driver,
			final Firm firm,
			final Employee employee
	) {
		driver.get(employee.getSource());
		employee.setSource(this.formatSource(employee.getSource()));

		final WebElement personalBlock =
				this.findPersonalPageBlockByEmailAnchor(driver, firm);

		employee.getPhones().addAll(this.findPhones(personalBlock));
		employee.getEmails().addAll(this.findEmailsByAnchor(personalBlock));
	}

	private Collection<WebElement> findEmployeePageBlocksByAnchors(
			final SearchContext context
	) {
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
				final WebElement parentNode =
						currentNode.findElement(By.xpath(".."));
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

	private WebElement findPersonalPageBlockByEmailAnchor(
			final SearchContext context,
			final Firm firm
	) {
		final Collection<WebElement> anchors = this.findEmailAnchors(context);

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
					if (!(this.isPhoneAnchor(localAnchor) ||
							this.isEmailAnchor(localAnchor))) {
						++badAnchors;
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

	private Collection<WebElement> findPersonalPageAnchors(
			final SearchContext context
	) {
		final List<WebElement> anchors = context.findElements(By.tagName("a"));
		final Collection<WebElement> validAnchors = new ArrayList<>();

		for (final WebElement anchor : anchors) {
			if (this.isAnchorPersonalPage(anchor)) {
				validAnchors.add(anchor);
			}
		}

		return validAnchors;
	}

	private boolean isAnchorEmployeesPage(final WebElement anchor) {
		if (!this.doesHrefExist(anchor)) {
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

		for (final String segment : segments) {
			if (GenericScraper.EMPLOYEE_PAGE_PATHS.contains(segment.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	private boolean isAnchorPersonalPage(final WebElement anchor) {
		if (!this.doesHrefExist(anchor)) {
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
		if (GenericScraper.EMPLOYEE_PAGE_PATHS.contains(
				finalSegment.toLowerCase())) {
			return false;
		}

		for (final String segment : segments) {
			if (GenericScraper.EMPLOYEE_PAGE_PATHS.contains(segment.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	private boolean doesHrefExist(final WebElement anchor) {
		final String href = anchor.getAttribute("href");

		return StringUtils.isNotBlank(href);
	}

	/**
	 * Tries to determine the name of the employee from the URL.
	 *
	 * We expect this to be the final segment of the path.
	 *
	 * Will disregard if the final segment contains numbers or specials symbols.
	 *
	 * @param url
	 * @return
	 */
	private String findEmployeeNameInUrl(final String url) {
		String path;

		try {
			path = AisUrlUtils.extractPath(url);
		} catch (UrlParseException e) {
			return null;
		}

		final List<String> pathSegments = AisRegexUtils.findPathSegments(path);

		if (pathSegments.size() == 0) {
			return null;
		}

		final String candidate = pathSegments.get(pathSegments.size() - 1);

		if (!AisRegexUtils.isPossiblyName(candidate)) {
			return null;
		}

		final String[] names = candidate.split("-");

		String out = "";

		for (final String name : names) {
			out += name.substring(0, 1).toUpperCase() + name.substring(1) + " ";
		}

		return out.trim();
	}

	private String findEmployeeTitleInBlock(final WebElement employeeBlock) {
		final Collection<String> tags = new HashSet<>();
		Collections.addAll(tags, "span");

		for (final String tag : tags) {
			final String title = this.findEmployeeTitleByTag(employeeBlock, tag);

			if (StringUtils.isNotBlank(title)) {
				return title;
			}
		}

		return null;
	}

	private String findEmployeeTitleByHeaders(final SearchContext context) {
		final Collection<String> headers = new HashSet<>();
		Collections.addAll(headers, "h1", "h2", "h3");

		for (final String header : headers) {
			final String title = this.findEmployeeTitleByTag(context, header);

			if (StringUtils.isNotBlank(title)) {
				return title;
			}
		}

		return null;
	}

	private String findEmployeeTitleByTag(
			final SearchContext context,
			final String tag
	) {
		final List<WebElement> els = context.findElements(By.tagName(tag));

		for (final WebElement el : els) {
			final List<WebElement> children = el.findElements(By.xpath("./*"));

			// We only want to check for titles in leaf tags since searching any
			// higher would give us less useful information.
			if (children.size() > 0) {
				continue;
			}

			final String innerText = el.getAttribute("innerText");

			for (final String title : GenericScraper.EMPLOYEE_TITLES) {
				if (innerText.toLowerCase().contains(title)) {
					// Return innerText instead of title because the employee's full title
					// may contain more than just one role.
					return innerText;
				}
			}
		}

		return null;
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

			if (this.isAnchorEmployeesPage(anchor)) {
				employeePageLinks.add(href);
			}
		}

		return this.cleanLinks(employeePageLinks, driver.getCurrentUrl());
	}

	/**
	 * Anchor tags can be used to open a mail app to send an email. This is done
	 * by starting the anchor href value with 'mailto:'
	 * <p>
	 * Search the page for these anchors and return the first email address.
	 *
	 * @param context
	 *
	 * @return
	 */
	private Collection<String> findEmailsByAnchor(final SearchContext context) {
		final Collection<WebElement> anchors = this.findEmailAnchors(context);
		final Collection<String> out = new HashSet<>();

		for (final WebElement anchor : anchors) {
			final String href = anchor.getAttribute("href");

			// Check the innerText for an email that is displayed to the user.
			final String innerText = anchor.getAttribute("innerText");
			final String innerEmail = AisRegexUtils.findFirstEmail(innerText);

			if (StringUtils.isNotBlank(innerEmail)) {
				out.add(innerEmail);

				continue;
			}

			// We could not find an email in the innerText
			// Check what is after the mailto: instead and see if that is valid.

			final String hrefText = href.substring(7);
			final String hrefEmail = AisRegexUtils.findFirstEmail(hrefText);

			if (StringUtils.isNotBlank(hrefEmail)) {
				out.add(hrefEmail);
			}
		}

		return out;
	}

	private boolean isEmailAnchor(
			final WebElement anchor
	) throws StaleElementReferenceException {
		final String href = anchor.getAttribute("href");

		return StringUtils.isNotBlank(href) &&
				href.toLowerCase().startsWith("mailto:");
	}

	private Collection<WebElement> findEmailAnchors(final SearchContext context) {
		final List<WebElement> anchors = context.findElements(By.tagName("a"));
		final Collection<WebElement> out = new HashSet<>();

		for (final WebElement anchor : anchors) {
			try {
				if (this.isEmailAnchor(anchor)) {
					out.add(anchor);
				}
			} catch (StaleElementReferenceException e) {
				continue;
			}
		}

		return out;
	}

	/**
	 * Finds all phone numbers in the current page.
	 *
	 * @param element
	 * @return
	 */
	private Collection<String> findPhones(final WebElement element) {
		final Collection<String> out = new HashSet<>();

		out.addAll(this.findPhonesByAnchor(element));
		out.addAll(AisPhoneUtils.findPhones(element.getAttribute("innerHTML")));

		return out;
	}

	/**
	 * Searches the page for any anchor tags that are indicated to contain
	 * telephone numbers.
	 *
	 * An anchor contains a phone number if the href value starts with 'tel:'
	 *
	 * @param context
	 * @return
	 */
	private Collection<String> findPhonesByAnchor(final SearchContext context) {
		final Collection<WebElement> anchors = this.findPhoneAnchors(context);
		final Collection<String> out = new HashSet<>();

		for (final WebElement anchor : anchors) {
			final String href = anchor.getAttribute("href");

			// Check the value in the href first since it is more likely to be
			// accurate.
			// This is because numbers displayed to the user may have letters in
			// place of numbers.
			final String hrefText = href.substring(4);
			final String hrefPhone = AisPhoneUtils.findFirstPhone(hrefText);

			if (StringUtils.isNotBlank(hrefPhone)) {
				out.add(hrefPhone);

				continue;
			}

			// Take whatever is displayed to the user instead.
			final String innerPhone = anchor.getAttribute("innerText");

			if (StringUtils.isNotBlank(innerPhone)) {
				out.add(innerPhone);
			}
		}

		return out;
	}

	private boolean isPhoneAnchor(final WebElement anchor) {
		final String href = anchor.getAttribute("href");

		return StringUtils.isNotBlank(href) &&
				href.toLowerCase().startsWith("tel:");
	}

	private Collection<WebElement> findPhoneAnchors(final SearchContext context) {
		final List<WebElement> anchors = context.findElements(By.tagName("a"));
		final Collection<WebElement> out = new HashSet<>();

		for (final WebElement anchor : anchors) {
			try {
				if (this.isPhoneAnchor(anchor)) {
					out.add(anchor);
				}
			} catch (StaleElementReferenceException e) {
				continue;
			}
		}

		return out;
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
			final Firm firm,
			final String url
	) {
		final Collection<String> firmEmails = firm.getEmails();

		for (final String firmEmail : firmEmails) {
			if (StringUtils.isBlank(firmEmail)) {
				return;
			}

			// The email host is everything after the '@' character.
			final String emailHost = firmEmail.substring(firmEmail.indexOf('@') + 1);

			String sourceHost;

			try {
				sourceHost = AisUrlUtils.extractHostname(url);
			} catch (UrlParseException e) {
				// Parsing failed, but there are other urls to try.
				continue;
			}

			// If the source host ends with the email host...
			if (sourceHost.endsWith(emailHost)) {
				// ...then the source is probably the firms own website.
				// We only check the end of the source host to account for internal
				// sites.
				firm.setFirmUrl(sourceHost);

				break;
			}
		}
	}

	/**
	 * Formats the given URL into something that can be presented by removing
	 * unnecessary information such as query parameters and scheme.
	 *
	 * @param url
	 */
	private String formatSource(final String url) {
		try {
			return AisUrlUtils.formatSource(url);
		} catch (UrlParseException e) {
			// Formatting the source failed.
			// Use the unchanged url as the source.
			return url;
		}
	}

	/**
	 * It is possible to have anchors that are missing the hostname.
	 *
	 * These anchors would use the hostname of the current page in a normal
	 * browser.
	 *
	 * Unfortunately, this doesn't work too well with Selenium, so we have to fix
	 * the links.
	 *
	 * @param links
	 * @param pageUrl
	 * @return
	 */
	private Collection<String> cleanLinks(
			final Collection<String> links,
			final String pageUrl
	) {
		String pageAuthority;

		try {
			pageAuthority = AisUrlUtils.removePath(pageUrl);
		} catch (UrlParseException e) {
			return links;
		}

		final Collection<String> fixedLinks = new HashSet<>();

		for (final String link : links) {
			try {
				// If the link is missing the hostname...
				if (!AisUrlUtils.hasAuthority(link)) {
					// Prepend the link with the page authority.
					fixedLinks.add(pageAuthority + link);
				} else {
					// No need to modify the link.
					fixedLinks.add(link);
				}
			} catch (UrlParseException e) {
				// Parsing failed, but let's keep the link just in case.
				fixedLinks.add(link);
			}
		}

		return fixedLinks;
	}

	private String cleanLink(final String link, final String pageUrl) {
		String pageAuthority;

		try {
			pageAuthority = AisUrlUtils.removePath(pageUrl);
		} catch (UrlParseException e) {
			return link;
		}

		try {
			// If the link is missing the hostname...
			if (!AisUrlUtils.hasAuthority(link)) {
				// Prepend the link with the page authority.
				return pageAuthority + link;
			} else {
				return link;
			}
		} catch (UrlParseException e) {
			return link;
		}
	}

}
