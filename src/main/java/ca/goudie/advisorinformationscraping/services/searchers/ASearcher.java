package ca.goudie.advisorinformationscraping.services.searchers;

import ca.goudie.advisorinformationscraping.exceptions.DomReadException;
import ca.goudie.advisorinformationscraping.exceptions.SearchException;
import ca.goudie.advisorinformationscraping.exceptions.UrlParseException;
import ca.goudie.advisorinformationscraping.utils.AisUrlUtils;

import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public abstract class ASearcher implements ISearcher {

	@Override
	public Collection<String> search(
			final WebDriver driver,
			final String query,
			final int resultsLimit,
			final Collection<String> blacklist
	) throws SearchException {
		this.performQuery(driver, query);

		return this.getSearchResults(driver, resultsLimit, blacklist);
	}

	/**
	 * Scrapes any results available for relevant links.
	 * If the current page does not have enough links, we will navigate to the
	 * next page, if it exists.
	 *
	 * @param driver
	 * @param resultsLimit
	 * @param blacklist
	 * @return
	 */
	private Collection<String> getSearchResults(
			final WebDriver driver,
			final int resultsLimit,
			final Collection<String> blacklist
	) throws SearchException {
		final Collection<String> goodHrefs = new ArrayList<>();
		final Collection<String> hosts = new HashSet<>();

		// Could have a while (true) here
		// Using for to be safe
		// Expecting at least one result per page
		for (int i = 0; i < resultsLimit; ++i) {
			final Collection<String> newHrefs = this.getSearchResultsOnPage(driver);
			final int resultsNeeded = resultsLimit - goodHrefs.size();
			goodHrefs.addAll(
					this.processHrefs(
							newHrefs,
							hosts,
							resultsNeeded,
							blacklist));

			try {
				// If we need more results and there is a next page to look at...
				if (goodHrefs.size() < resultsLimit && this.hasNextPage(driver)) {
					this.goToNextPage(driver);
				} else {
					break;
				}
			} catch (DomReadException e) {
				// Failed to find or click next-page button.
				break;
			}
		}

		return goodHrefs;
	}

	private Collection<String> processHrefs(
			final Collection<String> newHrefs,
			final Collection<String> hosts,
			final int resultsNeeded,
			final Collection<String> blacklist
	) {
		final Collection<String> goodHrefs = new ArrayList<>();

		for (final String href : newHrefs) {
			final String host;

			try {
				host = AisUrlUtils.extractHostname(href);
			} catch (UrlParseException e) {
				// Bad href value; skip
				continue;
			}

			if (blacklist.contains(host)) {
				// The host is in the blacklist and is not allowed to be scraped.
				continue;
			}

			if (!hosts.add(host)) {
				// The host was already in the collection of hosts from previous
				// links; skip
				continue;
			}

			goodHrefs.add(href);

			if (goodHrefs.size() >= resultsNeeded) {
				break;
			}
		}

		return goodHrefs;
	}

	abstract void performQuery(
			final WebDriver driver,
			final String query
	) throws SearchException;

	/**
	 * Scrapes the current search results page for any links that we can use.
	 *
	 * @param driver
	 * @throws SearchException
	 */
	abstract Collection<String> getSearchResultsOnPage(
			final WebDriver driver
	) throws SearchException;

	abstract boolean hasNextPage(final WebDriver driver) throws DomReadException;

	abstract void goToNextPage(final WebDriver driver) throws DomReadException;

}
