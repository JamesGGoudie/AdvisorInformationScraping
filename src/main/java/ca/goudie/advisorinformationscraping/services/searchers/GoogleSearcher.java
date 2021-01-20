package ca.goudie.advisorinformationscraping.services.searchers;

import ca.goudie.advisorinformationscraping.exceptions.DomReadException;
import ca.goudie.advisorinformationscraping.exceptions.SearchException;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class GoogleSearcher extends ASearcher {

	void performQuery(final WebDriver driver, final String query)
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

	Collection<String> getSearchResultsOnPage(
			final WebDriver driver
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
			throw new SearchException("Could not find any search results on page.");
		}

		final Collection<String> out = new ArrayList<>();

		for (final WebElement result : results) {
			final WebElement anchor;
			final String href;

			try {
				anchor = result.findElement(By.tagName("a"));
				href = anchor.getAttribute("href");

				out.add(href);
			} catch (StaleElementReferenceException e) {
				continue;
			}
		}

		return out;
	}

	boolean hasNextPage(final WebDriver driver) throws DomReadException {
		return this.findNextButton(driver) != null;
	}

	void goToNextPage(final WebDriver driver) throws DomReadException {
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
