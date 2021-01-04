package ca.goudie.advisorinformationscraping.services.search;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleSearchService implements SearchService {

	@Override
	public List<String> search(final WebDriver driver, final int resultsLimit) {
		this.performQuery(driver, "test");

		return this.getSearchResults(driver, resultsLimit);
	}

	private void performQuery(final WebDriver driver, final String query) {
		driver.get("https://www.google.ca");

		final WebElement queryEl = driver.findElement(By.name("q"));

		queryEl.sendKeys(query);
		queryEl.submit();
	}

	private List<String> getSearchResults(
			final WebDriver driver,
			final int resultsLimit
	) {
		final List<String> links = new ArrayList<>();

		// Could have a while (true) here
		// Using for to be safe
		// Expecting at least one result per page
		for (int i = 0; i < resultsLimit; ++i) {
			this.getSearchResultsOnPage(driver, links, resultsLimit);

			// If we need more results and there is a next page to look at...
			if (links.size() < resultsLimit && this.hasNextPage(driver)) {
				this.goToNextPage(driver);
			} else {
				break;
			}
		}

		return links;
	}

	private void getSearchResultsOnPage(
			final WebDriver driver,
			final List<String> links,
			final int resultsLimit
	) {
		// May contain junk like "People also search".
		// Filter them out by searching for this class.
		final List<WebElement> resultGroups = driver.findElements(By.className(
				"hlcw0c"));
		final List<WebElement> results = new ArrayList<>();

		if (resultGroups.size() > 0) {
			for (final WebElement resultGroup : resultGroups) {
				results.addAll(resultGroup.findElements(By.className("yuRUbf")));
			}
		} else {
			// If there were no result groups, then there was no junk.
			// Search the whole page for results instead.
			results.addAll(driver.findElements(By.className("yuRUbf")));
		}

		for (final WebElement result : results) {
			final WebElement anchor = result.findElement(By.tagName("a"));
			links.add(anchor.getAttribute("href"));

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

	private WebElement findNextButton(final WebDriver driver) {
		return driver.findElement(By.id("pnnext"));
	}

}
