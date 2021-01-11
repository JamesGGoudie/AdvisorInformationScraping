package ca.goudie.advisorinformationscraping.services.search;

import ca.goudie.advisorinformationscraping.exceptions.ScrapingFailedException;
import ca.goudie.advisorinformationscraping.utils.AisUrlUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class GoogleSearchService implements SearchService {

	@Override
	public Collection<String> search(
			final WebDriver driver, final String query, final int resultsLimit
	) {
		this.performQuery(driver, query);

		return this.getSearchResults(driver, resultsLimit);
	}

	private void performQuery(final WebDriver driver, final String query) {
		driver.get("https://www.google.ca");

		final WebElement queryEl = driver.findElement(By.name("q"));

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
	) {
		final Collection<String> links = new ArrayList<>();
		final Collection<String> hosts = new HashSet<>();

		// Could have a while (true) here
		// Using for to be safe
		// Expecting at least one result per page
		for (int i = 0; i < resultsLimit; ++i) {
			this.getSearchResultsOnPage(driver, links, hosts, resultsLimit);

			// If we need more results and there is a next page to look at...
			if (links.size() < resultsLimit && this.hasNextPage(driver)) {
				this.goToNextPage(driver);
			} else {
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
	) {
		// May contain junk like "People also search".
		// Filter them out by searching for this class.
		final List<WebElement> resultGroups = driver.findElements(By.className(
				"hlcw0c"));
		final List<WebElement> results = new ArrayList<>();

		if (resultGroups.size() > 0) {
			for (final WebElement resultGroup : resultGroups) {
				results.addAll(this.findResultItems(resultGroup));
			}
		} else {
			// If there were no result groups, then there was no junk.
			// Search the whole page for results instead.
			results.addAll(this.findResultItems(driver));
		}

		for (final WebElement result : results) {
			final WebElement anchor = result.findElement(By.tagName("a"));
			final String href = anchor.getAttribute("href");

			try {
				final String host = AisUrlUtils.extractHostname(href);

				if (!hosts.add(host)) {
					// The host was already in the collection of hosts from previous
					// links; skip
					continue;
				}
			} catch (URISyntaxException e) {
				// Bad href value; skip
				continue;
			}

			links.add(href);

			if (links.size() >= resultsLimit) {
				break;
			}
		}
	}

	private boolean hasNextPage(final WebDriver driver) {
		return this.findNextButton(driver) != null;
	}

	private void goToNextPage(final WebDriver driver) {
		final WebElement nextBtn = this.findNextButton(driver);
		nextBtn.click();
	}

	private List<WebElement> findResultItems(final SearchContext searchContext) {
		return searchContext.findElements(By.className("yuRUbf"));
	}

	private WebElement findNextButton(final WebDriver driver) {
		return driver.findElement(By.id("pnnext"));
	}

}
