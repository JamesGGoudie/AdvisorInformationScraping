package ca.goudie.advisorinformationscraping.services;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

	private static final int RESULTS_LIMIT = 20;

	public String search() {
		final WebDriver driver = this.initDriver();

		this.performQuery(driver, "test");
		final List<String> links = this.getSearchResults(driver);

		for (final String link : links) {
			System.out.println(link);
		}

		return driver.getPageSource();
	}

	private WebDriver initDriver() {
		System.setProperty("webdriver.chrome.driver",
				"C:/Program Files/Selenium/chromedriver.exe");

		final ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless",
				"--disable-gpu",
				"--window-size=1920,1200",
				"--ignore-certificate-errors",
				"--silent");

		return new ChromeDriver(options);
	}

	private void performQuery(final WebDriver driver, final String query) {
		driver.get("https://www.google.ca");

		final WebElement queryEl = driver.findElement(By.name("q"));

		queryEl.sendKeys(query);
		queryEl.submit();
	}

	private List<String> getSearchResults(final WebDriver driver) {
		final List<String> links = new ArrayList<>();

		// Could have a while (true) here
		// Using for to be safe
		// Expecting at least one result per page
		for (int i = 0; i < SearchService.RESULTS_LIMIT; ++i) {
			this.getSearchResultsOnPage(driver, links);

			// If we need more results and there is a next page to look at...
			if (links.size() < SearchService.RESULTS_LIMIT &&
					this.hasNextPage(driver)) {
				this.goToNextPage(driver);
			} else {
				break;
			}
		}

		return links;
	}

	private void getSearchResultsOnPage(
			final WebDriver driver, final List<String> links
	) {
		final List<WebElement>
				results =
				driver.findElements(By.className("yuRUbf"));

		for (final WebElement result : results) {
			// Any results that have extra classes are garbage like "People also
			// ask".
			// Ignore these
			if (!result.getAttribute("class").equals("yuRUbf")) {
				continue;
			}

			final WebElement anchor = result.findElement(By.tagName("a"));
			links.add(anchor.getAttribute("href"));

			if (links.size() >= SearchService.RESULTS_LIMIT) {
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
