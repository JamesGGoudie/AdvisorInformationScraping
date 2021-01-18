package ca.goudie.advisorinformationscraping.services.search;

import ca.goudie.advisorinformationscraping.exceptions.DomReadException;
import ca.goudie.advisorinformationscraping.exceptions.SearchException;
import ca.goudie.advisorinformationscraping.exceptions.UrlParseException;
import ca.goudie.advisorinformationscraping.utils.AisUrlUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class GoogleSearchService implements SearchService {

	@Override
	public Collection<String> search(
			final WebDriver driver, final String query, final int resultsLimit
	) throws SearchException {
		this.performQuery(driver, query);

		return this.getSearchResults(driver, resultsLimit);
	}

	private void performQuery(final WebDriver driver, final String query)
			throws SearchException {
		driver.get("https://www.google.ca");

		final WebElement queryEl;

		try {
			queryEl = driver.findElement(By.name("q"));
		} catch (StaleElementReferenceException e) {
			throw new SearchException("Could not find query element.");
		}

		queryEl.sendKeys(query);
		queryEl.submit();
	}

	/**
	 * Scrapes any results available for relevant links.
	 * If the current page does not have enough links, we will navigate to the
	 * next page, if it exists.
	 *
	 * @param driver
	 * @param resultsLimit
	 * @return
	 */
	private Collection<String> getSearchResults(
			final WebDriver driver, final int resultsLimit
	) throws SearchException {
		final Collection<String> links = new ArrayList<>();
		final Collection<String> hosts = new HashSet<>();

		// Could have a while (true) here
		// Using for to be safe
		// Expecting at least one result per page
		for (int i = 0; i < resultsLimit; ++i) {
			this.getSearchResultsOnPage(driver, links, hosts, resultsLimit);

			try {
				// If we need more results and there is a next page to look at...
				if (links.size() < resultsLimit && this.hasNextPage(driver)) {
					this.goToNextPage(driver);
				} else {
					break;
				}
			} catch (DomReadException e) {
				// Failed to find or click next-page button.
				break;
			}
		}

		return links;
	}

	/**
	 * Scrapes the current search results page for any links that we can use.
	 *
	 * @param driver
	 * @param links
	 * @param hosts
	 * @param resultsLimit
	 */
	private void getSearchResultsOnPage(
			final WebDriver driver,
			final Collection<String> links,
			final Collection<String> hosts,
			final int resultsLimit
	) throws SearchException {
		// May contain junk like "People also search".
		// Filter them out by searching for this class.
		List<WebElement> resultGroups;

		try {
			resultGroups = driver.findElements(By.className("hlcw0c"));
		} catch (StaleElementReferenceException e) {
			resultGroups = new ArrayList<>();
		}

		final List<WebElement> results = new ArrayList<>();

		if (resultGroups.size() > 0) {
			for (final WebElement resultGroup : resultGroups) {
				try {
					results.addAll(this.findResultItems(resultGroup));
				} catch (DomReadException e) {
					continue;
				}
			}
		} else {
			// If there were no result groups, then there was no junk.
			// Search the whole page for results instead.
			try {
				results.addAll(this.findResultItems(driver));
			} catch (DomReadException e) {}
		}

		if (results.size() == 0) {
			throw new SearchException("Could not find any search results.");
		}

		for (final WebElement result : results) {
			final WebElement anchor;
			final String href;

			try {
				anchor = result.findElement(By.tagName("a"));
				href = anchor.getAttribute("href");
			} catch (StaleElementReferenceException e) {
				continue;
			}

			final String host;

			try {
				host = AisUrlUtils.extractHostname(href);
			} catch (UrlParseException e) {
				// Bad href value; skip
				continue;
			}

			if (!hosts.add(host)) {
				// The host was already in the collection of hosts from previous
				// links; skip
				continue;
			}

			links.add(href);

			if (links.size() >= resultsLimit) {
				break;
			}
		}
	}

	private boolean hasNextPage(final WebDriver driver) throws DomReadException {
		return this.findNextButton(driver) != null;
	}

	private void goToNextPage(final WebDriver driver) throws DomReadException {
		final WebElement nextBtn = this.findNextButton(driver);
		nextBtn.click();
	}

	private List<WebElement> findResultItems(final SearchContext searchContext)
			throws DomReadException {
		try {
			return searchContext.findElements(By.className("yuRUbf"));
		} catch (StaleElementReferenceException e) {
			throw new DomReadException(e);
		}
	}

	private WebElement findNextButton(final WebDriver driver)
			throws DomReadException {
		try {
			return driver.findElement(By.id("pnnext"));
		} catch (StaleElementReferenceException e) {
			throw new DomReadException(e);
		}
	}

}
