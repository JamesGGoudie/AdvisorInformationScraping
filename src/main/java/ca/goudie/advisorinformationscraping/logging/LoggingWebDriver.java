package ca.goudie.advisorinformationscraping.logging;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Set;

@Log4j2
public class LoggingWebDriver implements WebDriver {

	private final WebDriver driver;

	public LoggingWebDriver(final WebDriver driver) {
		this.driver = driver;
	}

	@Override
	public void get(String s) {
		log.info("Getting: " + s);
		this.driver.get(s);
	}

	@Override
	public String getCurrentUrl() {
		final String currentUrl = this.driver.getCurrentUrl();
		log.info("Current URL: " + currentUrl);

		return currentUrl;
	}

	@Override
	public String getTitle() {
		final String title = this.driver.getTitle();
		log.info("Title: " + title);

		return title;
	}

	@Override
	public List<WebElement> findElements(By by) {
		log.info("Finding Elements " + by);
		List<WebElement> foundElements = this.driver.findElements(by);
		log.info("Found " + foundElements.size() + " Elements");

		return foundElements;
	}

	@Override
	public WebElement findElement(By by) {
		log.info("Finding Element " + by);
		WebElement foundElement = this.driver.findElement(by);

		if (foundElement == null) {
			log.info("No Element Found");
		} else {
			log.info("Found an Element");
		}

		return foundElement;
	}

	@Override
	public String getPageSource() {
		log.info("Getting Page Source");

		return this.driver.getPageSource();
	}

	@Override
	public void close() {
		this.driver.close();
	}

	@Override
	public void quit() {
		this.driver.quit();
	}

	@Override
	public Set<String> getWindowHandles() {
		return this.driver.getWindowHandles();
	}

	@Override
	public String getWindowHandle() {
		return this.driver.getWindowHandle();
	}

	@Override
	public TargetLocator switchTo() {
		return this.driver.switchTo();
	}

	@Override
	public Navigation navigate() {
		return this.driver.navigate();
	}

	@Override
	public Options manage() {
		return this.driver.manage();
	}

}
